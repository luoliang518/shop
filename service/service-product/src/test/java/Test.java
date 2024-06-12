import io.swagger.models.auth.In;

import java.util.concurrent.CompletableFuture;

/**
 * 异步编排测试类
 */
public class Test {

    /**
     * 案例01
     * @param args
     */
    public static void main1(String[] args) throws Exception{
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("子线程: 异步编排的runAsync" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //获取任务的执行结果: 阻塞调用线程
        System.out.println(future.get());
        System.out.println("主线程结束干活");
    }

    /**
     * supplyAsync
     * @param args
     * @throws Exception
     */
    public static void main2(String[] args) throws Exception{
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("子线程: 异步编排的runAsync" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "10086";
        });
        //获取任务的执行结果: 阻塞调用线程
        System.out.println(future.get());
        System.out.println("主线程结束干活");
    }

    /**
     *
     * @param args
     */
    public static void main3(String[] args) throws Exception{
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        for (int i = 0; i < 10; i++) {
            //任务第一步
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "任务第一步执行");
                return 1;
            });
            //第二步: thenRun任务可能由调用线程执行,也可能由子线程执行
            //thenRunAsync: 任务只可能是由子线程执行的
            future.thenRunAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "任务第二步执行");
            });
            //阻塞
            future.get();
        }
        System.out.println("主线程结束干活");
    }

    /**
     *
     * @param args
     */
    public static void main4(String[] args) throws Exception{
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        for (int i = 0; i < 10; i++) {
            //任务第一步
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "任务第一步执行");
//                return 1;
            });
            //第二步: thenRun任务可能由调用线程执行,也可能由子线程执行
            //thenRunAsync: 任务只可能是由子线程执行的
            future.thenAcceptAsync((a)->{
                System.out.println("接受第一步的结果为: " + a);
                System.out.println(Thread.currentThread().getName() + "任务第二步执行");
            });
            //阻塞
            future.get();
        }
        System.out.println("主线程结束干活");
    }

    public static void main5(String[] args) throws Exception{
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        for (int i = 0; i < 10; i++) {
            //任务第一步
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "任务第一步执行");
                return 10;
            }).thenApplyAsync((a) ->{
                System.out.println(Thread.currentThread().getName() + "任务第二步执行");
                return a*100;
            });
            //阻塞
            System.out.println(future.get());
        }
        System.out.println("主线程结束干活");
    }

    public static void main6(String[] args) throws Exception{
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        //任务第一步
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "任务第一步执行");
            int i = 1/0;
            return 10;
        }).whenCompleteAsync((a, b) -> {
            System.out.println("第一个参数:" + a);
            System.out.println("第二个参数:" + b);
        });
        //阻塞
        System.out.println("最终结果:" + future.get());

        System.out.println("主线程结束干活");
    }
    public static void main7(String[] args) throws Exception{
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        //任务第一步
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "任务第一步执行");
            int i = 1 / 0;
            return 10;
        }).exceptionally((a) -> {
            System.out.println(a);
            return 1;
        });
        //阻塞
        System.out.println("最终结果:" + future.get());

        System.out.println("主线程结束干活");
    }

    public static void main8(String[] args) throws Exception{
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        //任务第一步
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "任务第一步执行");
            return 10;
        }).handleAsync((a, b) -> {
            if(b != null){
                return -1;
            }
            System.out.println("第一个参数:" + a);
            System.out.println("第二个参数:" + b);
            return a;
        });
        //阻塞
        System.out.println("最终结果:" + future.get());

        System.out.println("主线程结束干活");
    }

    public static void main(String[] args) {
        System.out.println("主线程开始干活" + Thread.currentThread().getName());
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            return 10;
        });
        //加
        CompletableFuture<Void> future1 = future.thenAcceptAsync((a) -> {
            try {
                Thread.sleep(1000);
                System.out.println(a + 10);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        //减
        CompletableFuture<Void> future2 = future.thenAcceptAsync((a) -> {
            try {
                Thread.sleep(2000);
                System.out.println(a - 10);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        //乘
        CompletableFuture<Void> future3 = future.thenAcceptAsync((a) -> {

            try {
                Thread.sleep(3000);
                System.out.println(a * 10);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        //除
        CompletableFuture<Void> future4 = future.thenAcceptAsync((a) -> {

            try {
                Thread.sleep(4000);
                System.out.println(a / 10);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
//        任何一个任务执行完成,全部结束
//        CompletableFuture.anyOf(future1, future2, future3, future4).join();

        //全部任务必须全部执行完才能结束主线程
        CompletableFuture.allOf(future1, future2, future3, future4).join();
        System.out.println("主线程结束干活");
    }

}
