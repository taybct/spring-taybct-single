```bash
#!/bin/bash
mvn archetype:create-from-project -Darchetype.properties=./archetype.properties
cd target/generated-sources/archetype
```

# 因为项目复杂的特殊性，这里需要在这个目录下面替换一些值

在 target 目录下全局替换

```xml
<groupId>${groupId}</groupId>
            <artifactId>${rootArtifactId}-tool
```
为
```xml
<groupId>io.github.taybct</groupId>
            <artifactId>spring-taybct-tool
```

替换
```xml
<groupId>${groupId}</groupId>
                <artifactId>${rootArtifactId}-tool
```
为
```xml
<groupId>io.github.taybct</groupId>
                <artifactId>spring-taybct-tool
```

将 <artifactId>spring-taybct-tools-dependencies</artifactId> 的版本号改成 ${spring-taybct-tools.version}
```xml
<dependency>
    <groupId>io.github.taybct</groupId>
    <artifactId>spring-taybct-tools-dependencies</artifactId>
    <version>${spring-taybct-tools.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

替换`${package}.taybct.tool.`为 `io.github.taybct.tool.`

替换`${package}.tool.`为 `io.github.taybct.tool.`

如果是单体架构的，有配置文件的，在 target 目录下查找 `io.github.taybct` 勾选 '*.yml' ，看看配置里面有哪些包名是以这个开头的，
只要不是 `io.github.taybct.tool.` 的，都替换成 `${package}`



```bash
# 加载到本地
mvn install
# 推送到仓库
mvn deploy
```