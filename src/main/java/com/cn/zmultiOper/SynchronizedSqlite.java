package com.cn.zmultiOper;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cn.sqlite.SQLiteJDBC;

/**
 * 
 * @author
 *
 */
public class SynchronizedSqlite implements Runnable{

	private static final SQLiteJDBC operateSQLiteJDBC = new SQLiteJDBC();

	@SuppressWarnings("unused")
	private String name;
	public SynchronizedSqlite(String name) {
        this.name = name;
    }

	@Override
	public void run() {
//		SQLite只支持库级锁，库级锁意味着什么？——意味着同时只能允许一个写操作，
//		也就是说，即事务T1在A表插入一条数据，事务T2在B表中插入一条数据，
//		这两个操作不能同时进行，即使你的机器有100个CPU，也无法同时进行，而只能顺序进行。
//		表级都不能并行，更别说元组级了——这就是库级锁。但是，SQLite尽量延迟申请X锁，直到数据块真正写盘时才申请X锁，这是非常巧妙而有效的。
		operateSQLiteJDBC.operationDB();
	}

	public static void main(String[] args) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 3, 20, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>());
		threadPoolExecutor.execute(new SynchronizedSqlite("a"));
		threadPoolExecutor.execute(new SynchronizedSqlite("b"));
		threadPoolExecutor.execute(new SynchronizedSqlite("c"));
		threadPoolExecutor.shutdown();
	}
}
