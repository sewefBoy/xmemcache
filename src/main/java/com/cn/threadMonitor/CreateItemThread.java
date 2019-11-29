package com.cn.threadMonitor;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建报文文件的子线程，即一个任务一个线程!!
 * @author xch
 *
 */
public class CreateItemThread extends Thread {

	private String str_taskid = null; //任务id

	private Logger logger = LoggerFactory.getLogger(getClass());

	//构造方法
	public CreateItemThread(String _taskid) {
		this.str_taskid = _taskid; //
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			//第一步就先注册缓存,表示XX机构、XX日期的压缩任务正在执行!!
			Monitor.getInstance().putCreateFileTaskRuning(str_taskid);

			//实际压缩,耗时操作,会阻塞在这里
			long ll_1 = System.currentTimeMillis();
			logger.info("开始创建报文件[" + str_taskid + "]..."); //

			String str_isSimulation = System.getProperty("isSimulation"); //是否模拟??
			if ("Y".equals(str_isSimulation)) {
				Random random = new Random();
				int li_count = 2 + random.nextInt(15); //
				for (int i = 0; i < li_count; i++) {
					//witeTempTarZipFile(str_zipFilePath + str_fileName); //先写文件,文件总是先创建,但不断增长的!
					Thread.currentThread().sleep(1 * 1000); //在Windows环境下无法实际执行tar,用sleep 15秒代替,测试效果
				}
			} else {
				//再创建一个工具类，创建文件的实际逻辑在工具类中,因为创建报文的逻辑复杂,所以单独搞一个类,这样可以让控制线程的这部分代码简洁，更容易维护!
				new CreateUtil(str_taskid).createTwoFile();
			}

			long ll_2 = System.currentTimeMillis();
			logger.info("创建报文件[" + str_taskid + "]结束!,耗时【" + (ll_2 - ll_1) + "】毫秒"); //

		} catch (Throwable _ex) {
			_ex.printStackTrace(); //
		} finally {
			//最后一定要移除缓存标记，否则就控制不准了!
			Monitor.getInstance().removeCreateFileTaskRuning(str_taskid);
		}
	}
}
