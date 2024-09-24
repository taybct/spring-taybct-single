package io.github.mangocrisp.demo;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * xxx
 *
 * @author xijieyin <br> 2023/3/2 上午10:36
 */
public class DemoTest {

    @Test
    public void test1() {
        PathMatcher pathMatcher = new AntPathMatcher();
        System.out.println(pathMatcher.match("/{v}/user/{id}", "/v1/user/1"));
    }

    @Test
    public void test2() {
        PathMatcher pathMatcher = new AntPathMatcher();
        System.out.println(pathMatcher.match("DELETE:/{v}/system/dictType/batch", "DELETE:/v2/system/dictType/batch"));
    }

}
