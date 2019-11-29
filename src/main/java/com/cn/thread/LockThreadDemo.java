package com.cn.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 三个线程按次序轮流打印a,b,c Lock实现
 * @author 
 */
public class LockThreadDemo implements Runnable{
    private static Lock lock = new ReentrantLock();

    private static Integer currentCount = 0;

    private static final Integer MAX_COUNT = 30;

    private static String [] chars = {"a", "b", "c"};

    private String name;

    public LockThreadDemo(String name) {
        this.name =  name;
    }

    @Override
    public void run() {
        while(currentCount<MAX_COUNT){
            //lock() 与 unlock() 必须和 try...finally 配套使用 避免出现异常不解锁
            try{
                lock.lock();
                while(this.name.equals(chars[currentCount%3])&&currentCount<MAX_COUNT){
                    printAndPlusOne(this.name);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    public void printAndPlusOne(String name){
        System.out.println(name + "\t" + currentCount);
        currentCount ++;
    }

    public static void main(String [] args){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 3, 20, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        threadPoolExecutor.execute(new LockThreadDemo("a"));
        threadPoolExecutor.execute(new LockThreadDemo("b"));
        threadPoolExecutor.execute(new LockThreadDemo("c"));
        threadPoolExecutor.shutdown();
    }
}

