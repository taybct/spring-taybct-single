package io.github.taybct.demo;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 * JDK 21 新特性
 * </pre>
 *
 * @author XiJieYin
 * @since 2025/11/25 17:27
 */
public class JDK21Test {


    /**
     * 主线程会等待子线程完成后再结束
     */
    @Test
    public void testVirtualThreads(){


        System.out.println("Main thread started.");

        // 创建一个虚拟线程池，每个任务将由一个新的虚拟线程执行
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // 提交多个任务
            for (int i = 0; i < 5; i++) {
                int taskNumber = i;
                executor.submit(() -> { // executor.submit() 提交任务时，这个执行器会为每个任务创建一个新的虚拟线程来执行它
                    System.out.println("Executing task " + taskNumber + " in Virtual Thread.");
                    System.out.println("Thread: " + Thread.currentThread().getName());
                    try {
                        // 模拟一些工作
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Task " + taskNumber + " interrupted.");
                    }
                    System.out.println("Task " + taskNumber + " completed.");
                });
            }
        } // try-with-resources 会自动关闭 ExecutorService

        System.out.println("All tasks submitted and ExecutorService closed.");
        System.out.println("Main thread finished.");
    }

    /**
     * 主线程提交子线程后，直接结束，不等待子线程完成
     */
    @Test
    public void testVirtualThreads2(){

        System.out.println("Main thread started.");

        // 1. 创建虚拟线程池（不使用 try-with-resources，避免自动关闭）
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        // 2. 提交子任务（虚拟线程执行）
        for (int i = 0; i < 5; i++) {
            int taskNumber = i;
            executor.submit(() -> {
                System.out.println("子任务 " + taskNumber + " 开始执行（线程：" + Thread.currentThread().getName() + "）");
                try {
                    Thread.sleep(1000); // 子任务模拟1秒工作
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("子任务 " + taskNumber + " 被中断");
                }
                System.out.println("子任务 " + taskNumber + " 执行完成");
            });
        }

        // 3. 主线程提交任务后，直接继续执行自己的逻辑（不等待子线程）
        System.out.println("主线程已提交所有子任务，开始执行自己的业务逻辑...");
        // 模拟主线程自己的工作（比如处理其他业务、返回响应等）
        try {
            Thread.sleep(500); // 主线程工作500ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("主线程自己的业务逻辑执行完成！");

        // 4. （可选）主线程完成后，手动关闭线程池（不阻塞主线程）
        // shutdown() 会拒绝新任务，但会等待已提交任务完成后关闭线程池
        // 这里调用后主线程会直接退出，线程池在后台等待子任务完成后回收资源
        executor.shutdown();

        // 主线程直接结束（不会等子线程完成）
        System.out.println("主线程结束！");
    }

    /**
     * 扩展模式匹配，支持直接解构记录（Record）类型，提取字段值
     */
    @Test
    public void testRecordPatterns(){
        record Point(int x, int y) {}

        Object obj = new Point(3, 4);

        // 解构记录，无需手动调用 x()/y()
        if (obj instanceof Point(int x, int y)) {
            System.out.println(x + y);
        }
    }

    sealed interface Shape {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}

    double area(Shape s) {
        return switch (s) {
            case Circle(double r) -> Math.PI * r * r;
            case Square(double e) -> e * e;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * switch 语句支持类型模式、记录模式、常量模式等，避免 instanceof + 强制转换的冗余代码。
     * 增强 switch 的表达能力，简化类型判断逻辑，减少出错概率
     */
    @Test
    public void testPatternMatchingForSwitch(){
        double area = area(new Square(1.1));
        System.out.println("Area: " + area);
    }

    /**
     * 基于虚拟线程，提供 StructuredTaskScope API，实现 “任务树” 管理，确保子任务与父任务生命周期一致（父任务取消 / 异常时，子任务自动取消）
     * 解决传统并发中 “任务泄漏”“取消传播困难” 问题，让并发代码更易维护。
     */
    @Test
    public void testStructuredConcurrency(){
//        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
//            Future<Integer> f1 = scope.fork(() -> task1());
//            Future<Integer> f2 = scope.fork(() -> task2());
//            scope.join(); // 等待所有子任务完成，或任一失败则取消其他任务
//            return f1.resultNow() + f2.resultNow();
//        }
    }

    private Object task2() {
        return 2;
    }

    private Object task1() {
        return 1;
    }

    /**
     * 引入模板表达式，支持安全地嵌入变量 / 表达式到字符串中，替代 String.format() 和字符串拼接
     */
    @Test
    public void testStringTemplates(){
//        String name = "Alice";
//        int age = 25;
//        String info = STR."Name: \{name}, Age: \{age}"; // 等价于 "Name: Alice, Age: 25"
    }

    @Test
    public void testVectorAPI(){
//        VectorSpecies<Integer> vectorSpecies = IntVector.SPECIES_256;
//        var a = vectorSpecies.fromArray(new int[]{1,2,3,4,5,6,7,8}, 0);
//        var b = vectorSpecies.fromArray(new int[]{8,7,6,5,4,3,2,1}, 0);
//        var sum = a.add(b); // 向量级加法，一次处理8个int
//        sum.intoArray(result); // 结果写入数组
    }

}
