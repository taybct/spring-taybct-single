#!/bin/ash
java=java
jar=$1
vm=-Dfile.encoding=utf-8 \
-Dmaven.wagon.http.ssl.insecure=true \
-Dmaven.wagon.http.ssl.allowall=true \
--add-opens java.base/java.util=ALL-UNNAMED \
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens java.base/java.lang.io=ALL-UNNAMED \
-Xms8g -Xmx8g \
-XX:MaxMetaspaceSize=512m \
-XX:+UseZGC \
-XX:MaxGCPauseMillis=150 \
-XX:ReservedCodeCacheSize=256m \
-XX:+UseCodeCacheFlushing \
-Xlog:gc*,gc+age=trace,safepoint:file=ac/gc.log:time,uptime,level,tags:filecount=10,filesize=10M \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=./java_pid%p.hprof \
-XX:NativeMemoryTracking=detail
params=--spring.profiles.active=dev \
--spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 \
--spring.cloud.nacos.config.server-addr=127.0.0.1:8848 \
--spring.cloud.nacos.username=nacos \
--spring.cloud.nacos.password=nacos \
--server.port=8080
$java $vm -jar $jar $params