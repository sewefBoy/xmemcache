package test;


import com.cn.sqlite.SQLiteJDBC;
import com.cn.threadMonitor.Monitor;

public class Test {
	public static void main(String[] args) {
		//----------------------------sql执行完成共插入100000信息-------------------------------------
		//Operation database successfully ,consume time:66002
//		SQLiteJDBC sqlite = new SQLiteJDBC();
//		sqlite.operationDB();
		
		
		
		//redis
		
		
		//Monitor
		String[] _taskIds = new String[200];
		for(int i=0; i< _taskIds.length; i++) {
			_taskIds[i] = String.valueOf(i);
		}
		Monitor.getInstance().addCreateWaitTaskOrStart(_taskIds);
		
	}
}
