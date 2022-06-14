package com.suparking.icbc.threadpool;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadPoolExecutor extends ThreadPoolExecutor{

    private String poolName;
    public NamedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveSecond, String poolName){
        /**
         * 设置 线程池队列大小为 最大线程数的10倍,如果实际业务中线程数超过设置的值,就会存日志,同时增加机器
         */
        super(corePoolSize,maximumPoolSize,keepAliveSecond,TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10*maximumPoolSize),
                new NamedThreadFactory(poolName));
        this.poolName = poolName;

        /**
         * 如果线程池中的内存与队列不足,则java提供了四种预定义的处理程序策略
         * 默认的: ThreadPoolExecutor.AbortPolicy 处理程序遭到拒绝将抛出运行时RejectedExecutionException
         * ThreadPoolExecutor.CallerRunsPolicy 线程调用运行时该任务的execute本身,此策略提供简单的反馈控制机制,
         * 能够减缓新任务的提交速度
         * ThreadPoolExecutor.DiscardPolicy 不能执行的任务将被删除,同时写日志
         */
        setRejectedExecutionHandler(new DiscardPolicy()
        {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e){
                StackTraceElement stackTraceElement[] = Thread.currentThread().getStackTrace();
                for (StackTraceElement ste:stackTraceElement)
                {
                }
            }
        });

    }

    public NamedThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                   long keepAliveSecond){
        super(corePoolSize,maximumPoolSize,keepAliveSecond,TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10*corePoolSize));
        setRejectedExecutionHandler(new DiscardPolicy(){
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e){
                StackTraceElement stes[] = Thread.currentThread().getStackTrace();
                for (StackTraceElement ste:stes)
                {
                }
            }
        });
    }

    @Override
    public void execute(Runnable command){
        /**
         * 队列任务已满,请求不在响应
         */
        if (super.getCorePoolSize() * 10  - this.getQueue()
                .remainingCapacity()>100){
        }
        else
        {
            super.execute(command);
        }
    }

    /**
     * 默认线程池工厂
     */
    static class NamedThreadFactory implements ThreadFactory
    {

        /**
         * 重写 ThreadFactory 并且可以为线程设置名称
         */
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(final String name){
            SecurityManager securityManager = System.getSecurityManager();
            group = (securityManager!=null)?securityManager.getThreadGroup():
                    Thread.currentThread().getThreadGroup();

            namePrefix = name + "-pool-" + poolNumber.getAndIncrement()+"-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group,r,
                    namePrefix+threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
