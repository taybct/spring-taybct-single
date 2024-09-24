#!/bin/bash
mvn archetype:create-from-project -Darchetype.properties=./archetype.properties
cd target/generated-sources/archetype
# 加载到本地
mvn install
# 推送到仓库
mvn deploy