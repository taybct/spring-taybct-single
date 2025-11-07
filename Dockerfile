# 设置镜像基础，jdk17
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:21
# 维护人员信息
MAINTAINER <15014633363@163.com>
# maven 打包 buildArgs 指定项目 jar 包位置名称
ARG JAR_FILE
# 设定时区
ENV TZ=Asia/Shanghai
# JVM 参数
ENV JVM_OPTS="\
-Dfile.encoding=utf-8 \
-Dmaven.wagon.http.ssl.insecure=true \
-Dmaven.wagon.http.ssl.allowall=true \
--add-opens java.base/java.util=ALL-UNNAMED \
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens java.base/java.lang.io=ALL-UNNAMED"
# 声明 Jar 运行参数的环境变量（默认空，可动态传入，如 --spring.profiles.active=prod，注意：--server.port=8080 是固定值，因为要暴露端口出去）
ENV JAR_OPTS=""
# 声明 Jar 包运行端口的环境变量（默认8080，可动态传入）
ENV JAR_PORT=8080
#设置镜像对外暴露端口（因为也不知道 jar 包最终运行的是什么端口这里就不指定了运行的时候自己写命令去运行）
EXPOSE $JAR_PORT
# 设置后续指令的工作目录
WORKDIR /taybct
# 将指定的 jar 放置在根目录下，命名为 app.jar，推荐使用绝对路径
ADD $JAR_FILE jar/app.jar
# 执行多条命令的 shell 文件
ADD ./run.sh run/run.sh
# 设置时区 && 创建 spring boot jar 包启动的配置文件夹 && 授权可执行文件   这里尽量一行搞定，因为每多一个 RUN 指令 Docker 就多套一层
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ >/etc/timezone && mkdir jar/config && chmod +x run/run.sh
#执行启动命令
ENTRYPOINT ["run/run.sh", "/taybct/jar", "./app.jar"]