package com.cn.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author
 *
 */
public class SynchronizedThreadDemo implements Runnable{
	private static String[] chars = { "a", "b", "c" };

	private static final OperateInteger operateInteger = new OperateInteger(0, 30);

	private String name;

	public SynchronizedThreadDemo(String name) {
        this.name = name;
    }

	@Override
	public void run() {
		while (operateInteger.getCurrentCount() < operateInteger.getMaxCount()) {
			synchronized (operateInteger) {
				if (operateInteger.getCurrentCount() < operateInteger.getMaxCount()) {
					if (this.name.equals(chars[operateInteger.getCurrentCount() % 3])) {
						operateInteger.printAndPlusOne();
						operateInteger.notifyAll();
					} else {
						try {
							operateInteger.wait();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}

	}

	public static void main(String[] args) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 3, 20, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>());
		threadPoolExecutor.execute(new SynchronizedThreadDemo("a"));
		threadPoolExecutor.execute(new SynchronizedThreadDemo("b"));
		threadPoolExecutor.execute(new SynchronizedThreadDemo("c"));
		threadPoolExecutor.shutdown();
	}

	private static class OperateInteger {
		private int currentCount;

		private int maxCount;

		private void printAndPlusOne() {
			System.out.println(chars[currentCount % 3] + "\t" + currentCount);
			currentCount++;
		}

		public OperateInteger(int currentCount, int maxCount) {
			this.currentCount = currentCount;
			this.maxCount = maxCount;
		}

		public int getCurrentCount() {
			return currentCount;
		}

		public int getMaxCount() {
			return maxCount;
		}
	}
}
