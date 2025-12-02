# Spring TayBct Single

[![GitHub license](https://img.shields.io/github/license/taybct/spring-taybct-single?style=flat)](./LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/taybct/spring-taybct-single?color=fa6470&style=flat)](https://github.com/taybct/spring-taybct-single/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/taybct/spring-taybct-single?style=flat)](https://github.com/taybct/spring-taybct-single/network/members)

## 介绍
Spring TayBct Single 是一个 Spring 业务组件基础集成的基础业务（多模块单体架构），对一些常用的系统管理，
用户体系等基础功能做了基础的常用的简易的集成，并且提供一些业务开发过程中常用的功能模块集成，开箱即用。

## 软件架构

- 基于 spring boot 开发，版本和 spring boot 大版本基本同步，例如 spring-taybct 3.5.x -> spring-boot 3.5.x
- 适配一些 spring 相关的基础组件的简单基础集成
- 系统管理等一些基础通用业务模块的基础简易集成
- 本项目是单体架构，后续可以使用 `Spring Cloud Gateway` + `dubbo` 变成 [微服务](/taybct/spring-taybct-cloud)

![framework](./assets/images/framework.png)
构架图

## 演示

![monitor-application-list.png](./assets/images/monitor-application-list.png)
应用列表

![api-doc.png](./assets/images/api-doc.png)
接口文档

![swagger-ui.png](./assets/images/swagger-ui.png)
接口文档

![nacos-serve-list.png](./assets/images/nacos-serve-list.png)
Nacos 服务列表

![druid-view.png](./assets/images/druid-view.png)
Druid 数据库监控

![e-r.png](./assets/images/E-R.png)
![user-role-perm.png](./assets/images/user-role-perm.png)
基于用户角色权限管理

![onlyoffice-page.png](./assets/images/onlyoffice-page.png)
OnlyOffice 文档编辑器

![scheduling.png](./assets/images/scheduling.png)
分布式任务调度

![logic-flow-design.png](./assets/images/logic-flow-design.png)
流程设计

![form-create.png](./assets/images/form-create.png)
动态表单

## 示例

- [示例项目（PureAdmin）](https://mangocrisp.top/pureadmin)

> 本项目为纯后端项目，以上展示的前端界面是 [vue-pure-admin（Mango Crisp）](https://github.com/mangocrisp/vue-pure-admin) 基于本项目
> 和 [vue-pure-admin](https://github.com/pure-admin/vue-pure-admin) 开发的前端示例项目

## JavaDoc 口文档

<a href="https://mangocrisp.top/javadoc/spring-taybct-tools-doc/index.html" target="_blank" >spring-taybct-tools 接口文档</a>

<a href="https://mangocrisp.top/javadoc/spring-taybct-doc/index.html" target="_blank" >spring-taybct 接口文档</a>

### 仓库模板

直接使用本仓库模板创建仓库即可

### 本地创建

[快速开始](https://mangocrisp.top/code/taybct/3.5.x/get-started/)

### Fork 本仓库

为了方便后续的更新和维护，你可以 Fork 本仓库到你自己的代码仓库

## 使用说明

1.  是运行的代码
2. **启动 VM 参数**

```bash
-Dmaven.wagon.http.ssl.insecure=true
-Dmaven.wagon.http.ssl.allowall=true
--add-opens
java.base/java.lang=ALL-UNNAMED
--add-opens
java.base/java.util=ALL-UNNAMED
--add-opens
java.base/java.nio=ALL-UNNAMED
--add-opens
java.base/sun.nio.ch=ALL-UNNAMED
--add-opens
java.base/java.lang.reflect=ALL-UNNAMED
# 基础内存设置
-Xms8g -Xmx8g
-XX:MaxMetaspaceSize=512m
# 使用ZGC
-XX:+UseZGC
-XX:MaxGCPauseMillis=150
# 解决CodeCache问题
-XX:ReservedCodeCacheSize=256m
-XX:+UseCodeCacheFlushing
# 日志与诊断
-Xlog:gc*,gc+age=trace,safepoint:file=ac/gc.log:time,uptime,level,tags:filecount=10,filesize=10M
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./java_pid%p.hprof
-XX:NativeMemoryTracking=detail
```

> JVM 参数根据实际情况调整

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
5. 本项目属于集成平时开发的经验避免重复造轮子，所以如果在开发过程中遇到了本项目里面的一些 BUG，请及时在你的运行模块里面，新建
   BUG 类同包同名的类来强制重写本项目的代码，等到有空了把这些
   BUG，和你修复好的文件一起提交给本项目，将会是对本项目最伟大的贡献，感激不尽！另外，如果有好的点子，或者想法，也欢迎往本项目这儿疯狂
   PR.

> 本项目是基于 [Spring Taybct](../../../spring-taybct) 开发的多模块单体架构业务项目，如果你的业务希望在微服务和单体架构下都能运行，可以去这里看看

## 免责声明

本项目所有依赖包都是互联网能找到的，不能保证没有漏洞，源码也不能百分百保证没有 
BUG，谁都不能保证，所以，如果用于生产环境，出现了什么问题，本项目方不负任何责任，但是可以提供友情技术帮助和支持，你可以在项目的 
Issues 提出你的问题