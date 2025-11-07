#!/bin/bash
# 镜像仓库地址
registry_url="ghcr.io"
# 镜像组名
group_name="taybct"
# 镜像版本
image_version="3.5.0"
# 需要 build 哪些 jar 包的名都往这个数组里面放
apps=('spring-taybct-single')
for(( i=0;i<${#apps[@]};i++)) ; do
    ./docker-build.sh -u "$registry_url" -g "$group_name" -n "${apps[$i]}" -v "$image_version"
done
