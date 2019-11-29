package com.cn.sqlite;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.domain.User;
import com.cn.util.YuFormatUtil;

public class SQLiteJDBC {
	private Logger logger = LoggerFactory.getLogger(SQLiteJDBC.class);
	private YuFormatUtil bsUtil = new YuFormatUtil();
	
	public void operationDB() {
		logger.debug("current thread is name :"+Thread.currentThread().getName());
		long startTime = System.currentTimeMillis();
		// 创建会话
		Connection conn = bsUtil.getSqliteConn();
		// 增
		List<User> lists = new ArrayList<User>();
		for (int i = 0; i < 100000; i++) {
			String id = UUID.randomUUID().toString();
			User user = new User(id, "xiaoming" + i, LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), i%2==0?1:0, "beijing");
			lists.add(user);
		}
		bsUtil.saveEntityJdbcBatch(conn, lists);
		long endTime = System.currentTimeMillis();
		logger.debug("Operation database successfully ,consume time:"+(endTime - startTime));
	}

}
