package com.cn.threadMonitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CreateUtil {
	private String taskId;

	public CreateUtil(String taskId) {
		this.taskId = taskId;
	}

	public void createTwoFile() {
		FileWriter out;
		try {
			out = new FileWriter("D://file.txt", true);
			BufferedWriter bw = new BufferedWriter(out);
			bw.write(taskId);
			bw.newLine();
			bw.flush();
			out.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
