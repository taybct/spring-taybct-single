package io.github.taybct.demo;

import io.github.taybct.tool.core.util.sm.SM2Coder;
import io.github.taybct.tool.core.util.sm.SM2Properties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <pre>
 * 无鉴权访问测试
 * </pre>
 *
 * @author XiJieYin
 * @since 2024/12/2 15:05
 */
@SpringBootTest
@Slf4j
public class TestNoAuthAccess {

    @SneakyThrows
    @Test
    public void test1() {
        String jsonStr = """
                {
                    "uid":"1",
                    "nbf":1732851466,
                    "grant_type":"gx_cloud",
                    "user_name":"root",
                    "scope":["all"],
                    "atm":"username",
                    "exp":1732855066,
                    "iat":1732851466,
                    "jti":"ae8109f4257847b68747c644d31c43f4",
                    "client_id":"gx_cloud_pc",
                    "authorities":["ROOT"],
                    "tni":"000000"
                }""";

        SM2Coder.ini(new SM2Properties());
        String payload = SM2Coder.encryptBase64StringByPublicKey(jsonStr);
        System.out.printf("payload:%s", payload);

        //可以直接使用 SM2 加密成一个字符串，然后加入到请求头：

        //payload: xxx
    }

    @SneakyThrows
    @Test
    public void test2() {
        SM2Coder.ini(new SM2Properties());
//        String enStr = SM2Coder.encryptBase64StringByPublicKey("123456");
//        System.out.println(enStr);
        String enStr = "BCUu5u/uzuoSEQ7p+HFI6hjEEZv5ilKGXxTX76o7uR1IadEPjD2x9fForNweJ9209CsZnIzkvm8sLF82Sa09gtKKPuyYsZMzV7Nfd0Ek1hkoYWxqqmQqONWdnHXVppBUsUbOr/D7Fu+DW0ueG9brgc5W266Ell9wuS8lvP1BpsjW5MtVOBX/eSs/jbbtL7FRY/2WzGCIZs3Q49HcDxGiEjP2KGOZXT4yUR9VoSuvluHUHB0WNwYVBwOkknv79ORcKCN4QBJnxBMJkv1IdKOdNJeOsAjx5z0b2LVoMQYJnfrZ3DQ6rzpBv01wQKYLG6W9WzaGYhFfW7wyeePJLLNNfKkKBmt2TzOWrYn50chsoT7w1AwUPc7S3ybnjQYv5kHYtv0oi4pfSIUph5e+GRJH4ElGrCYiZT5ma+aS3vp1DJezUoLZ/QNdMZXGsU2egrsJ";
        String deStr = SM2Coder.decryptBase64StringByPrivateKey(enStr);
        System.out.println(deStr);
    }

}
