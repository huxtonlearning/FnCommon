#!/bin/sh

# File lưu phiên bản hiện tại
VERSION_FILE=".version"

# Lấy phiên bản mới nhất từ Git tag (bỏ tiền tố "v" nếu có)
LATEST_GIT_VERSION=$(git tag --sort=-v:refname | grep -Eo '[0-9]+\.[0-9]+\.[0-9]+' | head -n 1)

# Nếu không có tag nào, gán mặc định 0.0.1
if [ -z "$LATEST_GIT_VERSION" ]; then
  LATEST_GIT_VERSION="0.0.1"
fi

# Kiểm tra file .version tồn tại
if [ ! -f "$VERSION_FILE" ]; then
  echo "VERSION=$LATEST_GIT_VERSION" > "$VERSION_FILE"
fi

# Đọc phiên bản hiện tại từ file (dùng grep -E thay vì -P)
CURRENT_VERSION=$(grep -Eo 'VERSION=[0-9]+\.[0-9]+\.[0-9]+' "$VERSION_FILE" | cut -d '=' -f2)

# Nếu không đọc được, sử dụng phiên bản từ Git
if [ -z "$CURRENT_VERSION" ]; then
  CURRENT_VERSION="$LATEST_GIT_VERSION"
fi

# Tách các phần của phiên bản (X.Y.Z)
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"

# Tăng phiên bản
PATCH=$((PATCH + 1))

if [ "$PATCH" -ge 10 ]; then
  PATCH=0
  MINOR=$((MINOR + 1))

  if [ "$MINOR" -ge 10 ]; then
    MINOR=0
    MAJOR=$((MAJOR + 1))
  fi
fi

NEW_VERSION="$MAJOR.$MINOR.$PATCH"

# Kiểm tra nếu tag đã tồn tại
if git tag | grep -q "v$NEW_VERSION"; then
  echo "⚠️ Version $NEW_VERSION already exists. Skipping tag creation."
else
  # Ghi phiên bản mới vào file .version
  echo "VERSION=$NEW_VERSION" > "$VERSION_FILE"

  # Commit thay đổi
  git add .
  git commit -m "Auto-increment version to $NEW_VERSION"

  # Push code lên nhánh main
  git push origin main

  # Tạo tag mới
  git tag -a "$NEW_VERSION" -m "Release $NEW_VERSION"

  # Push tag lên remote
  git push origin "$NEW_VERSION"
fi

echo "✅ Deployment completed with version $NEW_VERSION"
