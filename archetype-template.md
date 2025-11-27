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
然后替换 `${package}.tool` 为 `io.github.taybct.tool`
然后在实际使用的时候替换 `${package}` 为 `group id`

Gradle 的替换：

将项目里的 gradle.properties 直接丢到生成的 archetype 目录下，注意，一共有三个 gradle.properties 文件，分别在：
- target/generated-sources/archetype/src/main/resources/archetype-resources
- target/generated-sources/archetype/target/classes/archetype-resources
- target/generated-sources/archetype/target/test-classes/projects/basic/project/basic

需要替换里面的 

- taybct-project.groupId=${package}
- taybct-project.version=${version}
- taybct-project.organization.name=${package}
- taybct-project.docker.registry.name=${artifactId}

替换 settings.gradle 文件里面的

include `:spring-taybct` 为 `:${package}`

因为是有多个子模块引用的，所以需要全局替换所有的 *.gradle 文件里面的
注意，因为 tools 也是这个开头，不要替换错了

根目录的 build.gradle 里面需要替换

```groovy
def nonBootModules = [
        '${package}-common',
        '${package}-api'
]
```

```bash
# 加载到本地
mvn install
# 推送到仓库
mvn deploy
```