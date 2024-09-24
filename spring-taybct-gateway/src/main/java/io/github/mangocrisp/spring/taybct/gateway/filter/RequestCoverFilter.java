package io.github.mangocrisp.spring.taybct.gateway.filter;

import io.github.mangocrisp.spring.taybct.common.prop.SecureProp;
import io.github.mangocrisp.spring.taybct.gateway.prop.GatewayProp;
import io.github.mangocrisp.spring.taybct.gateway.support.GatewayContext;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 请求内容存储 处理请求内容 内容放在 gatewayContext 中
 *
 * @author xijieyin <br> 2022/8/5 20:47
 * @since 1.0.0
 */
@AutoConfiguration
@Slf4j
@RequiredArgsConstructor
public class RequestCoverFilter implements GlobalFilter, Ordered {

    final GatewayProp gatewayProp;
    final SecureProp secureProp;

    /**
     * default HttpMessageReader
     */
    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();

    /**
     * 读取 form data 数据
     */
    private Mono<Void> readFormData(ServerWebExchange exchange, GatewayFilterChain chain,
                                    GatewayContext gatewayContext) {
        final ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        PathMatcher pathMatcher = new AntPathMatcher();

        // 需要验证码的接口
        for (String url : secureProp.getCaptchaUrls()) {
            if (pathMatcher.match(url, request.getURI().getPath())) {
                return cacheBody(exchange, chain, gatewayContext, headers, request);
            }
        }

        // 指定需要转换缓存请求 body 的接口
        for (String url : gatewayProp.getRequestCoverUrls()) {
            if (pathMatcher.match(url, request.getURI().getPath())) {
                return cacheBody(exchange, chain, gatewayContext, headers, request);
            }
        }
        return chain.filter(exchange);
    }

    private static Mono<Void> cacheBody(ServerWebExchange exchange, GatewayFilterChain chain, GatewayContext gatewayContext, HttpHeaders headers, ServerHttpRequest request) {
        return exchange.getFormData().doOnNext(multiValueMap -> {
            gatewayContext.setFormData(multiValueMap);
            log.debug("[GatewayContext]Read FormData:{}", multiValueMap);
        }).then(Mono.defer(() -> {
            Charset charset = Objects.requireNonNull(headers.getContentType()).getCharset();
            charset = charset == null ? StandardCharsets.UTF_8 : charset;
            String charsetName = charset.name();
            MultiValueMap<String, String> formData = gatewayContext.getFormData();
            /*
              formData is empty just return
             */
            if (null == formData || formData.isEmpty()) {
                return chain.filter(exchange);
            }
            StringBuilder formDataBodyBuilder = new StringBuilder();
            String entryKey;
            List<String> entryValue;
            try {
                /*
                  repackage form data
                 */
                for (Map.Entry<String, List<String>> entry : formData.entrySet()) {
                    entryKey = entry.getKey();
                    entryValue = entry.getValue();
                    if (entryValue.size() > 1) {
                        for (String value : entryValue) {
                            formDataBodyBuilder.append(entryKey).append("=")
                                    .append(URLEncoder.encode(value, charsetName)).append("&");
                        }
                    } else {
                        formDataBodyBuilder.append(entryKey).append("=")
                                .append(URLEncoder.encode(entryValue.get(0), charsetName)).append("&");
                    }
                }
            } catch (UnsupportedEncodingException e) {
                // ignore URLEncode Exception
            }
            /*
             * substring with the last char '&'
             */
            String formDataBodyString = "";
            if (formDataBodyBuilder.length() > 0) {
                formDataBodyString = formDataBodyBuilder.substring(0, formDataBodyBuilder.length() - 1);
            }
            /*
             * get data bytes
             */
            byte[] bodyBytes = formDataBodyString.getBytes(charset);
            int contentLength = bodyBytes.length;
            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
                /**
                 * change content-length
                 */
                @Override
                public HttpHeaders getHeaders() {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(super.getHeaders());
                    if (contentLength > 0) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                    return httpHeaders;
                }

                /**
                 * read bytes to Flux<Databuffer>
                 */
                @Override
                public Flux<DataBuffer> getBody() {
                    return DataBufferUtils.read(new ByteArrayResource(bodyBytes),
                            new NettyDataBufferFactory(ByteBufAllocator.DEFAULT), contentLength);
                }
            };
            ServerWebExchange mutateExchange = exchange.mutate().request(decorator).build();
            log.debug("[GatewayContext]Rewrite Form Data :{}", formDataBodyString);

            return chain.filter(mutateExchange);
        }));
    }

    /**
     * 读取 json 请求体数据
     */
    private Mono<Void> readBody(ServerWebExchange exchange, GatewayFilterChain chain, GatewayContext gatewayContext) {
        /*
         * join the body
         */
        return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            /*
             * read the body Flux<DataBuffer>, and release the buffer
             * //TODO when SpringCloudGateway Version Release To G.SR2,this can be update with the new version's feature
             * see PR https://github.com/spring-cloud/spring-cloud-gateway/pull/1095
             */
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                DataBufferUtils.retain(buffer);
                return Mono.just(buffer);
            });
            /*
             * repackage ServerHttpRequest
             */
            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedFlux;
                }
            };
            /*
             * mutate exchage with new ServerHttpRequest
             */
            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
            /*
             * read body string with default messageReaders
             */
            return ServerRequest.create(mutatedExchange, MESSAGE_READERS).bodyToMono(String.class)
                    .doOnNext(objectValue -> {
                        gatewayContext.setCacheBody(objectValue);
                        log.debug("[GatewayContext]Read JsonBody:{}", objectValue);
                    }).then(chain.filter(mutatedExchange));
        });
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /*
         * save request path and serviceId into gateway context
         */
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        GatewayContext gatewayContext = new GatewayContext();
        String path = request.getPath().pathWithinApplication().value();
        gatewayContext.setPath(path);
        gatewayContext.getFormData().addAll(request.getQueryParams());
        gatewayContext.setIpAddress(String.valueOf(request.getRemoteAddress()));
        HttpHeaders headers = request.getHeaders();
        gatewayContext.setHeaders(headers);
        log.debug("HttpMethod:{},Url:{}", request.getMethod(), request.getURI().getRawPath());

        /// 注意，因为webflux的响应式编程 不能再采取原先的编码方式 即应该先将gatewayContext放入exchange中，否则其他地方可能取不到
        /*
         * save gateway context into exchange
         */
        exchange.getAttributes().put(GatewayContext.CACHE_GATEWAY_CONTEXT, gatewayContext);

        // 处理参数
        MediaType contentType = headers.getContentType();
        long contentLength = headers.getContentLength();
        if (contentLength > 0) {
            if (MediaType.APPLICATION_JSON.equals(contentType) || MediaType.APPLICATION_JSON_UTF8.equals(contentType)) {
                return readBody(exchange, chain, gatewayContext);
            }
            if (MediaType.APPLICATION_FORM_URLENCODED.equals(contentType)) {
                return readFormData(exchange, chain, gatewayContext);
            }
        }

        // TODO 多版本划区域控制后期实现
        log.debug("[GatewayContext]ContentType:{},Gateway context is set with {}", contentType, gatewayContext);
        return chain.filter(exchange);
    }

}