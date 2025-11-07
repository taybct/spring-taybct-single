#!/bin/bash
# 镜像仓库地址
registry_url="ghcr.io"
# 镜像组名
group_name="taybct"
# 镜像版本
image_version="3.5.0"
# 需要 build 哪些 jar 包的名都往这个数组里面放
# 有哪些应用
apps=('spring-taybct-single')
# 这些应用的各自的端口,和上面的 jar 包名的顺序对应不要搞错了
port_arr=('9102')
# docker run 运行参数
run_args="-v=D:/dev/tools/docker/spring-taybct-single/jar/run.sh:/taybct/run/run.sh -v=D:/dev/tools/docker/spring-taybct-single/jar/config:/taybct/jar/config -v=D:/dev/tools/docker/spring-taybct-single/jar/logs:/taybct/jar/logs"
# docker run 运行时的环境变量
#evn_vars="JAR_OPTS='--server.port=8080'"
evn_vars="JAR_OPTS=''"
for(( i=0;i<${#apps[@]};i++)) ; do
    ./docker-run.sh -u "$registry_url" -g "$group_name" -n "${apps[$i]}" -v "$image_version" -p "${port_arr[$i]}:${port_arr[$i]}" -a "$run_args" -e "$evn_vars -e=JAR_PORT=${port_arr[$i]}"
done
