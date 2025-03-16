#!/bin/sh

# Định dạng phiên bản hợp lệ (X.Y.Z)
VERSION_FILE=".version"

# Kiểm tra file .version tồn tại
if [ ! -f "$VERSION_FILE" ]; then
  echo "VERSION=0.0.1" > $VERSION_FILE
fi

# Đọc phiên bản hiện tại từ file
CURRENT_VERSION=$(grep -oP '(?<=VERSION=)[0-9]+\.[0-9]+\.[0-9]+' "$VERSION_FILE")

# Nếu không đọc được, gán mặc định 0.0.1
if [ -z "$CURRENT_VERSION" ]; then
  CURRENT_VERSION="0.0.1"
fi

# Tách các phần của version (X.Y.Z)
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"

# Tăng số PATCH lên 1
PATCH=$((PATCH + 1))
NEW_VERSION="$MAJOR.$MINOR.$PATCH"

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

echo "✅ Deployment completed with version $NEW_VERSION"