package io.github.mangocrisp.demo;

import io.github.mangocrisp.spring.taybct.tool.core.result.R;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;

/**
 * @author XiJieYin <br> 2023/8/10 14:58
 */
@SpringBootTest
public class MonoDemo {

    @Test
    public void test1() {
        Mono<R<String>> demo = Mono.just(R.data("hello Mono!"));
        demo.subscribe(r -> System.out.println(r.getData()));
    }

    @Test
    public void test2() {
        Flux.create(t -> {
            t.next("create");
            t.next("create1");
            t.complete();
        }).subscribe(System.out::println);
    }

    @Test
    public void defer() {
        Flux.defer(() -> Flux.just("just", "just1", "just2"))
                .subscribe(System.out::println);
    }

    @Test
    public void interval() {
        Flux.interval(Duration.of(500, ChronoUnit.MILLIS))
                .map(l -> "just")
                .subscribe(System.out::println);
        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void error() {
        Flux.error(new RuntimeException())
                .subscribe(System.out::println);
    }

    @Test
    public void zipWith() {
//        Flux.just("a", "b").zipWith(Flux.just("c", "d")).subscribe(System.out::println);
        Flux.just("a", "b", "f").zipWith(Flux.just("c", "d", "e"), (s1, s2) -> String.format("%s-%s", s1, s2)).subscribe(System.out::println);
    }

    @Test
    public void take() {
//        Flux.range(1, 1000).take(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeLast(10).subscribe(System.out::println);
//        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(System.out::println);
//        Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(System.out::println);
    }

    @Test
    public void reduce() {
//        Flux.range(1, 100).reduce((x, y) -> x + y).subscribe(System.out::println);
        Flux.range(1, 100).reduceWith(() -> 100, (x, y) -> x + y).subscribe(System.out::println);
    }
}
