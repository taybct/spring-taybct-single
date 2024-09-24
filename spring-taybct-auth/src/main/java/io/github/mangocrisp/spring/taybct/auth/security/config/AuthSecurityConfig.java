package io.github.mangocrisp.spring.taybct.auth.security.config;

import cn.hutool.core.util.ArrayUtil;
import io.github.mangocrisp.spring.taybct.auth.security.filter.AuthFilter;
import io.github.mangocrisp.spring.taybct.auth.security.handle.IUserDetailsHandle;
import io.github.mangocrisp.spring.taybct.auth.security.handle.deft.PropertiesUserDetailsHandle;
import io.github.mangocrisp.spring.taybct.auth.security.prop.UserConfig;
import io.github.mangocrisp.spring.taybct.auth.security.service.CustomizeUserDetailsServiceImpl;
import io.github.mangocrisp.spring.taybct.auth.security.service.ICustomizeUserDetailsService;
import io.github.mangocrisp.spring.taybct.auth.security.service.ThirdUserDetailsService;
import io.github.mangocrisp.spring.taybct.auth.security.util.ResponseHandler;
import io.github.mangocrisp.spring.taybct.common.prop.SecureProp;
import io.github.mangocrisp.spring.taybct.tool.core.constant.ISysParamsObtainService;
import io.github.mangocrisp.spring.taybct.tool.core.exception.handler.IGlobalExceptionReporter;
import io.github.mangocrisp.spring.taybct.tool.core.exception.handler.IGlobalPrinter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.security.KeyPair;
import java.util.stream.Collectors;

/**
 * @author xijieyin <br> 2022/12/27 14:18
 */
@AutoConfiguration
@EnableWebSecurity
@Slf4j
public class AuthSecurityConfig {

    /**
     * A Spring Security filter chain for authentication.<br>
     * 这个也是个Spring Security的过滤器链，用于Spring Security的身份认证。
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    @Order(3)
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http
            , SecureProp secureProp
            , JwtDecoder jwtDecoder
            , JwtAuthenticationConverter jwtAuthenticationConverter
            , AuthorizationManager<RequestAuthorizationContext> authorizationManager
            , RedisTemplate<Object, Object> redisTemplate
            , ISysParamsObtainService sysParamsObtainService
            , KeyPair keyPair
            , ResponseHandler responseHandler
            , IUserDetailsHandle userDetailsHandle)
            throws Exception {
        // 禁用 csrf，跨域
        http.csrf().disable().cors();
        // X-Frame-Options，这个是为了安全考虑，可以不让别人把我们的网页嵌入 IFrame，如果禁用就可以嵌入
        // http.headers().frameOptions().disable();
        http.authorizeHttpRequests((authorize) -> {
                            try {
                                authorize
                                        // 黑名单
                                        .requestMatchers(ArrayUtil.toArray(secureProp.getBlackList().getUrls(), String.class)).denyAll()
                                        // 白名单
                                        .requestMatchers(ArrayUtil.toArray(secureProp.getIgnore().getUrls()
                                                        .stream().map(AntPathRequestMatcher::new).collect(Collectors.toList())
                                                , AntPathRequestMatcher.class)).permitAll()
                                        .anyRequest()
                                        //.authenticated()
                                        .access(authorizationManager)
                                        .and()
                                        .exceptionHandling(handle -> handle.accessDeniedHandler(responseHandler)
                                                .authenticationEntryPoint(responseHandler));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                // 默认表单登录
//                .formLogin(withDefaults());
                .formLogin(formLoginConfigurer -> formLoginConfigurer.loginPage("/login")
                        .permitAll())
                // Accept access tokens for User Info and/or Client Registration
//                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .httpBasic();

        // 过滤权限，解密 token 之后把 payload 放到 request 里面方便后面去获取用户
        http.addFilterAfter(new AuthFilter(redisTemplate, sysParamsObtainService, keyPair), AuthorizationFilter.class);
        // 这里要先 build 才能获取到里面 configure 好的配置
        DefaultSecurityFilterChain build = http.build();

        configResponseHandler(http, responseHandler);

        return build;
    }

    @Bean
    public ResponseHandler responseHandler(IGlobalExceptionReporter globalExceptionReporter
            , IGlobalPrinter globalPrinter) {
        return new ResponseHandler(globalExceptionReporter, globalPrinter);
    }

    /**
     * 配置结果处理器
     *
     * @param http            配置好的过滤器
     * @param responseHandler 结果处理器
     */
    private static void configResponseHandler(HttpSecurity http, ResponseHandler responseHandler) {
        responseHandler.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        // responseHandler.setAuthenticationSuccessHandler(responseHandler);
        // responseHandler.setAuthenticationFailureHandler(responseHandler);
        // responseHandler.setAuthenticationDetailsSource();
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            responseHandler.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            responseHandler.setRememberMeServices(rememberMeServices);
        }
        SecurityContextConfigurer securityContextConfigurer = http.getConfigurer(SecurityContextConfigurer.class);
        if (securityContextConfigurer != null) {
            SecurityContextRepository securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
            if (securityContextRepository == null) {
                securityContextRepository = new DelegatingSecurityContextRepository(new RequestAttributeSecurityContextRepository()
                        , new HttpSessionSecurityContextRepository());
            }
            responseHandler.setSecurityContextRepository(securityContextRepository);
        }
        responseHandler.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy(http));
    }

    public static SecurityContextHolderStrategy getSecurityContextHolderStrategy(HttpSecurity http) {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        String[] names = context.getBeanNamesForType(SecurityContextHolderStrategy.class);
        if (names.length == 1) {
            return context.getBean(SecurityContextHolderStrategy.class);
        } else {
            return SecurityContextHolder.getContextHolderStrategy();
        }
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationManager.class)
    public AuthorizationManager<RequestAuthorizationContext> authorizationManager(
            RedisTemplate<Object, Object> redisTemplate
            , ISysParamsObtainService sysParamsObtainService
            , SecureProp secureProp
    ) {
        return new SingleAuthorizationManager(redisTemplate, sysParamsObtainService, secureProp);
    }

    /**
     * 用户操作处理类
     *
     * @param userConfig 用户配置
     * @return IUserDetailsHandle
     */
    @Bean
    @ConditionalOnMissingBean(IUserDetailsHandle.class)
    public IUserDetailsHandle userDetailsHandle(UserConfig userConfig) {
        return new PropertiesUserDetailsHandle(userConfig);
    }

    /**
     * An instance of UserDetailsService for retrieving users to authenticate.<br>
     * 配置用户信息，或者配置用户数据来源，主要用于用户的检索。
     * <br>
     * 这个方法是要给 oauth2.1 的默认方法去调用使用用户名获取用户信息的，那做为一个喜欢自定义的程序员，怎么可能只满足于使用用户名查询用户信息呢？哈哈
     *
     * @return UserDetailsService
     */
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder
            , IUserDetailsHandle userDetailsHandle) {
        return new ThirdUserDetailsService(userDetailsHandle, passwordEncoder::encode);
    }

    /**
     * 自定义的用户查询
     *
     * @param passwordEncoder   加密器
     * @param userDetailsHandle 用户查询处理器
     * @return ICustomUserDetailsService
     */
    @Bean
    @ConditionalOnMissingBean(ICustomizeUserDetailsService.class)
    public ICustomizeUserDetailsService customizeUserDetailsService(PasswordEncoder passwordEncoder
            , IUserDetailsHandle userDetailsHandle) {
        return new CustomizeUserDetailsServiceImpl(userDetailsHandle, passwordEncoder::encode);
    }
}
