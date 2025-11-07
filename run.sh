#!/bin/sh
# 切换到 Jar 包目录（接收第一个参数：/taybct/jar）
cd "$1" || exit  # 若目录不存在则退出，避免后续错误

# 执行 Jar 包，使用环境变量中的 JVM 参数和 Jar 参数
java $JVM_OPTS -jar "$2" --server.port=$JAR_PORT $JAR_OPTS