#!/bin/sh

# Lấy danh sách tag, lọc đúng định dạng version (X.Y.Z), sắp xếp theo thứ tự phiên bản thực sự
LATEST_GIT_VERSION=$(git tag | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | sort -V | tail -n 1)

# Nếu không có tag nào, gán mặc định là 0.0.0
if [ -z "$LATEST_GIT_VERSION" ]; then
  LATEST_GIT_VERSION="0.0.0"
fi

echo "🔍 Latest version detected: $LATEST_GIT_VERSION"

# Tách các phần của phiên bản (X.Y.Z)
IFS='.' read -r MAJOR MINOR PATCH <<< "$LATEST_GIT_VERSION"

# Tăng phiên bản PATCH
PATCH=$((PATCH + 1))

# Tạo phiên bản mới
NEW_VERSION="$MAJOR.$MINOR.$PATCH"

# Kiểm tra nếu tag đã tồn tại
if git tag | grep -q "^$NEW_VERSION$"; then
  echo "⚠️ Version $NEW_VERSION already exists. Skipping tag creation."
  exit 1
fi

# Tạo tag mới
git tag -a "$NEW_VERSION" -m "Release $NEW_VERSION"

# Push tag lên remote
git push origin "$NEW_VERSION"

echo "✅ Deployment completed with version $NEW_VERSION"
