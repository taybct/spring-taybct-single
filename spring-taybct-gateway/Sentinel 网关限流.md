# 官网

[https://sentinelguard.io/zh-cn/](https://sentinelguard.io/zh-cn/)

#限流规则

- resource 资源名，资源名是限流规则的作用对象
- count 限流阈值
- grade 限流阈值类型，QPS 模式（1）或并发线程数模式（0） QPS 模式
- limitApp 流控针对的调用来源 default，代表不区分调用来源
- strategy 调用关系限流策略：直接、链路、关联 根据资源本身（直接）
- controlBehavior 流控效果（直接拒绝 / 排队等待 / 慢启动模式），不支持按调用关系限流 直接拒绝
- clusterMode 是否集群限流 否

# 启动配置

-Dcsp.sentinel.app.type=1 -Dcsp.sentinel.dashboard.server=localhost:8090 -Dproject.name=gateway-sentinel

-Dcsp.sentinel.app.type=1 Sentinel 网关类型  
-Dcsp.sentinel.dashboard.server=localhost:8090 限流控制服务地址  
-Dproject.name=gateway-sentinel 注册到限流服务控制的项目名称

# @SentinelResource

用于定义资源，并提供可选的异常处理和 fallback 配置项。 @SentinelResource 注解包含以下属性：

- value：资源名称，必需项（不能为空）
- entryType：entry 类型，可选项（默认为 EntryType.OUT）
- blockHandler / blockHandlerClass: blockHandler对应处理 BlockException 的函数名称，可选项。blockHandler 函数访问范围需要是
  public，返回类型需要与原方法相匹配，参数类型需要和原方法相匹配并且最后加一个额外的参数，类型为
  BlockException。blockHandler 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 blockHandlerClass
  为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
- fallback：fallback 函数名称，可选项，用于在抛出异常的时候提供 fallback 处理逻辑。fallback 函数可以针对所有类型的异常（除了
  exceptionsToIgnore 里面排除掉的异常类型）进行处理。fallback 函数签名和位置要求：
    - 返回值类型必须与原函数返回值类型一致；
    - 方法参数列表需要和原函数一致，或者可以额外多一个 Throwable 类型的参数用于接收对应的异常。
    - fallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass 为对应的类的 Class
      对象，注意对应的函数必需为 static 函数，否则无法解析。
    - defaultFallback（since 1.6.0）：默认的 fallback 函数名称，可选项，通常用于通用的 fallback 逻辑（即可以用于很多服务或方法）。默认
      fallback 函数可以针对所有类型的异常（除了 exceptionsToIgnore 里面排除掉的异常类型）进行处理。若同时配置了 fallback 和
      defaultFallback，则只有 fallback 会生效。defaultFallback 函数签名要求：
    - 返回值类型必须与原函数返回值类型一致；
    - 方法参数列表需要为空，或者可以额外多一个 Throwable 类型的参数用于接收对应的异常。
- defaultFallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass 为对应的类的 Class
  对象，注意对应的函数必需为 static 函数，否则无法解析。
- exceptionsToIgnore（since 1.6.0）：用于指定哪些异常被排除掉，不会计入异常统计中，也不会进入 fallback 逻辑中，而是会原样抛出。

> _若 blockHandler 和 fallback 都进行了配置，则被限流降级而抛出 BlockException 时只会进入 blockHandler 处理逻辑。若未配置
blockHandler、fallback 和 defaultFallback，则被限流降级时会将 BlockException 「直接抛出」（若方法本身未定义 throws
BlockException 则会被 JVM 包装一层 UndeclaredThrowableException）_

# 配置文件配置

- datasource: 目前支持 redis、apollo、zk、file、nacos，选什么类型的数据源就配置相应的key即可。
- data-id：可以设置成 ${spring.application.name}，方便区分不同应用的配置。
- rule-type：表示数据源中规则属于哪种类型，如 flow、degrade、param-flow、gw-flow等。
- data-type：指配置项的内容格式，Spring Cloud Alibaba Sentine l提供了JSON 和 XML 两种格式，如需要自定义，则可以将值配置为
  custom，并配置 converter-class 指向 converter 类。
