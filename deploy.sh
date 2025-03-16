#!/bin/bash

# Kiểm tra nếu người dùng không nhập tag
if [ -z "$1" ]; then
  echo "Usage: sh deploy.sh <version>"
  exit 1
fi

git add .
git commit -m"deploy"
git push origin main

VERSION=$1

# Cập nhật version vào file ENV
echo "VERSION=$VERSION" > .version

# Commit thay đổi
git add .
git commit -m "Update version to $VERSION"

# Tạo tag
git tag -a "$VERSION" -m "Release $VERSION"

# Push code và tag lên remote
git push origin main
git push origin "$VERSION"

echo "Deployment completed with version $VERSION"