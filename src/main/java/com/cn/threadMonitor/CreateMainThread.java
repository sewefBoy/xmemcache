package com.cn.threadMonitor;

/**
 * 创建报文文件的主线程! 在主线程中轮循扫描队列中的任务,如果子线程数还没有越界，则创建几个子线程，并加入计算!
 * 
 * @author xch
 *
 */
public class CreateMainThread extends Thread {

	private int li_maxCreateFileTaskLimit = 15; // 最多只能同时10个创建报文任务在跑!

	public CreateMainThread() {
	}

	/**
	 * 线程逻辑
	 */
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			Monitor.getInstance().isCreateMainThreadRuning = true; // 标记主线程正在跑!

			// 死循环,跑完队列所有任务
			while (true) {
				int li_waitTaskSize = Monitor.getInstance().getWaitTaskSize(); // 目前队列的大小,即排队等待处理的任务..

				// 如果队列中的任务为0，则才退出循环,即所有待办任务都干完了!!
				if (li_waitTaskSize == 0) {
					break; //
				}

				// 正在创建报文文件的线程数量!
				int li_creteFileTaskRuning = Monitor.getInstance().getCreateFileTaskSize(); //
				if (li_creteFileTaskRuning >= li_maxCreateFileTaskLimit) { // 如果运行的任务，达到上限，则进行控制，不做任何处理
					System.out.println("正在创建报文的任务数量【" + li_creteFileTaskRuning + "】达到上限【" + li_maxCreateFileTaskLimit
							+ "】,不再增加新的任务.."); //
				} else {
					// 可以跑的数量!上限减去正在运行的，比如上限是30,正在跑的是20个,则还可以新加10个任务.
					// 第一次就是直接同时跑30个
					int li_cando = li_maxCreateFileTaskLimit - li_creteFileTaskRuning;
					for (int i = 0; i < li_cando; i++) {
						String str_taskid = Monitor.getInstance().getWaitTask(); //
						if (str_taskid == null) { // 如果为空,则表示队列空了,直接退出!
							break;
						}

						// 创建一个子线程!
						new CreateItemThread(str_taskid).start(); //
					}
				}

				Thread.currentThread().sleep(1); // 每两秒钟轮循一次!
			}
		} catch (Throwable _ex) {
			_ex.printStackTrace();
		} finally {
			Monitor.getInstance().isCreateMainThreadRuning = false; // 标记主线程不在跑,即已结束!
		}
	}
}
