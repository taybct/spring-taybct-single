package io.github.mangocrisp.spring.taybct.auth.exception;

import io.github.mangocrisp.spring.taybct.auth.security.util.ResponseHandler;
import io.github.mangocrisp.spring.taybct.tool.core.exception.handler.IGlobalExceptionReporter;
import io.github.mangocrisp.spring.taybct.tool.core.exception.handler.IGlobalPrinter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

/**
 * 可以返回 JSON 到前端的异常处理器
 *
 * @author xijieyin <br> 2022/12/30 15:06
 */
public class JsonExceptionAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private final IGlobalExceptionReporter globalExceptionReporter;
    private final IGlobalPrinter globalPrinter;

    /**
     * @param loginFormUrl            URL where the login page can be found. Should either be
     *                                relative to the web-app context path (include a leading {@code /}) or an absolute
     *                                URL.
     * @param globalExceptionReporter 异常记录
     * @param globalPrinter           异常输出
     */
    public JsonExceptionAuthenticationEntryPoint(String loginFormUrl
            , IGlobalExceptionReporter globalExceptionReporter
            , IGlobalPrinter globalPrinter) {
        super(loginFormUrl);
        this.globalExceptionReporter = globalExceptionReporter;
        this.globalPrinter = globalPrinter;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (authException instanceof JsonResponseException || authException instanceof AccountStatusException) {
            new ResponseHandler(globalExceptionReporter, globalPrinter).onAuthenticationFailure(
                    request
                    , response
                    , authException
            );
        } else {
            super.commence(request, response, authException);
        }
    }
}
