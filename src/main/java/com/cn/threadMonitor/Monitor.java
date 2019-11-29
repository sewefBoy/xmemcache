package com.cn.threadMonitor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Monitor {

	// 静态实例!!
	private static Monitor monitor = null; //

	// 创建报文的主线程是否在运行!
	public boolean isCreateMainThreadRuning = false; //

	// 生成报文的任务队列,必须使用线程案例的队列对象!
	private ConcurrentLinkedQueue<String> waitTaskQueue = new ConcurrentLinkedQueue<String>(); //

	// 正在创建报文件的任务大小!
	private ConcurrentHashMap<String, String> createTaskMap = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, String> taskCountMap = new ConcurrentHashMap<String, String>();

	// 默认构造方法是private,别人无法创建!
	private Monitor() {
	}

	// 取得等待队列中的一个新任务
	public String getWaitTask() {
		return waitTaskQueue.poll();
	}

	// 计算等待队列的大小!
	public int getWaitTaskSize() {
		return waitTaskQueue.size(); //
	}

	// 加入新的子任务标记
	public void putCreateFileTaskRuning(String _taskId) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
		createTaskMap.put(_taskId, timestamp); //
	}

	// 删除创建文件的标记
	public void removeCreateFileTaskRuning(String _taskId) {
		createTaskMap.remove(_taskId); //
		taskCountMap.remove(_taskId); //
	}

	// 记录总数
	public void putCreateFileTaskCount(String _taskId, String _count) {
		taskCountMap.put(_taskId, _count); //
	}

	// 删除创建文件的标记
	public String getCreateFileTaskCount(String _taskId) {
		return taskCountMap.get(_taskId); //
	}

	// 计算正在创建的任务的大小..
	public int getCreateFileTaskSize() {
		return createTaskMap.size(); //
	}

	// 加入任务队列,或者启动主线程,即如果主线程已启动，则直接加入队列
	// 如果主线程还没有启动,则启动之!
	// 这个方法必须加synchronized同步控制，否则可能会启动多个主线程!
	// 必须要保证主线程永远只有一个! 也可以在系统启动时主线程就自动启动.
	public synchronized String addCreateWaitTaskOrStart(String[] _taskids) {
		String[] str_waitIds = waitTaskQueue.toArray(new String[0]); // 必须先立即克隆复制出当前的等待队列!不能循环判断,因为队列大小本身随时在变!
		String[] str_runingIds = createTaskMap.keySet().toArray(new String[0]); // 正好运行的!

		ArrayList<String> allIdList = new ArrayList<String>();
		for (int i = 0; i < str_waitIds.length; i++) {
			allIdList.add(str_waitIds[i]); //
		}

		for (int i = 0; i < str_runingIds.length; i++) {
			allIdList.add(str_runingIds[i]); //
		}

		int li_alreadyCount = 0;
		int li_canAddCount = 0;
		for (int i = 0; i < _taskids.length; i++) {
			if (allIdList.contains(_taskids[i])) { // 如果已经在运行了,则不加入!
				li_alreadyCount = li_alreadyCount + 1;
			} else {
				waitTaskQueue.add(_taskids[i]); // 加入待办队列,★★★关键代码★★★
				li_canAddCount = li_canAddCount + 1;
			}
		}

		// 如果主线程没有启动,则启动主线程.即有可能曾经主线程已经跑结束了,则再次启动
		// 这样保证主线程永远只会有一个!
		if (!isCreateMainThreadRuning) {
			new CreateMainThread().start(); //
		}

		return "预计启动个[" + _taskids.length + "]任务,其中[" + li_alreadyCount + "]个已启动,实际启动[" + li_canAddCount + "]个!"; //
	}


	// 得到所有正在压缩的任务清单
	public String[][] getAllCreateFileTaskInfo() {
		String[] str_keys = createTaskMap.keySet().toArray(new String[0]);
		String[][] str_datas = new String[str_keys.length][2];
		for (int i = 0; i < str_datas.length; i++) {
			str_datas[i][0] = str_keys[i];
			str_datas[i][1] = createTaskMap.get(str_keys[i]);
		}
		return str_datas;
	}

	// 创建实例
	public static synchronized Monitor getInstance() {
		if (monitor != null) {
			return monitor;
		}
		monitor = new Monitor();
		return monitor;
	}

}
