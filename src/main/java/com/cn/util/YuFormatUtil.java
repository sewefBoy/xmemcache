package com.cn.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;


public class YuFormatUtil {
	private Logger logger = LoggerFactory.getLogger(YuFormatUtil.class);

	public Connection getSqliteConn () {
		Connection conn = null;
		long startTime = System.currentTimeMillis();
		try {
			// 加载驱动
			Class.forName("org.sqlite.JDBC");
			// 创建连接对象
			conn = DriverManager.getConnection("jdbc:sqlite:test.db");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		long endTime = System.currentTimeMillis();
		logger.debug("Opened database successfully ,consume time:"+(endTime - startTime));
		return conn;
	}
	
	public XMemcachedClient getMemcachedClient () {
		XMemcachedClient memcachedClient = null;
		XMemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses("192.168.239.128:11211"));
		long startTime = System.currentTimeMillis();
		try {
			memcachedClient = (XMemcachedClient) builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		logger.debug("Opened database successfully ,consume time:"+(endTime - startTime));
		return memcachedClient;
	}
	
	
	public <T> void uptEntityJdbcBatch(Connection conn, Collection<T> saveObjs,List<String> fields,List<String> uptfields) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder uptSql = new StringBuilder("update ").append(
					tableNm).append(" set ");
			if(uptfields!=null&&uptfields.size()>0){
				int i = 0;
				for(String uptfield: uptfields){
					uptSql.append(fieldNms.get(uptfield));
					uptSql.append(" = ");
					uptSql.append(" ? ");
					if(i < uptfields.size()-1){
						uptSql.append(" , ");
					}
					i++;
				}
			}
			uptSql.append(" where 1=1 ");
			List<Object[]> params = new ArrayList<Object[]>();
			if(fields==null||fields.size()<=0){
				fieldNms=JpaEntityUtils.getIdColumnsByEntity(saveClass);
				Iterator<String> it = fieldNms.keySet().iterator();
				while (it.hasNext()) {
					String fieldNm = it.next();
					uptSql.append(" and ");
					uptSql.append(fieldNms.get(fieldNm));
					uptSql.append(" = ");
					uptSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[uptfields.size()+fieldNms.keySet().size()];
					for (String uptfield : uptfields) {
						objs[index] = getValueByFieldExpr(uptfield,
								saveObjTmp);
						index++;
					}
					for (String fieldNmTmp : fieldNms.keySet()) {
						objs[index] = getValueByFieldExpr(fieldNmTmp,
								saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			else{
				for(String field: fields){
					uptSql.append(" and ");
					uptSql.append(fieldNms.get(field));
					uptSql.append(" = ");
					uptSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[uptfields.size()+fields.size()];
					for (String uptfield : uptfields) {
						objs[index] = getValueByFieldExpr(uptfield,
								saveObjTmp);
						index++;
					}
					for (String field : fields) {
						objs[index] = getValueByFieldExpr(field,saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			batchUpdate(conn, uptSql.toString(), params, 1000);
		}
	}
	
	public <T> void uptEntityJdbcBatch(Connection conn, Collection<T> saveObjs) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			
			StringBuilder uptSql = new StringBuilder("update ").append(
					tableNm).append(" set ");
			int i = 0;
			for(String field: fieldNms.keySet()){
				uptSql.append(fieldNms.get(field));
				uptSql.append(" = ");
				uptSql.append(" ? ");
				if(i < fieldNms.size()-1){
					uptSql.append(" , ");
				}
				i++;
			}
			uptSql.append(" where 1=1 ");
			
			List<Object[]> params = new ArrayList<Object[]>();
			Map<String, String> fieldIdNms=JpaEntityUtils.getIdColumnsByEntity(saveClass);
			Iterator<String> it = fieldIdNms.keySet().iterator();
			while (it.hasNext()) {
				String fieldIdNm = it.next();
				uptSql.append(" and ");
				uptSql.append(fieldIdNms.get(fieldIdNm));
				uptSql.append(" = ");
				uptSql.append(" ? ");
			}
			for (Object saveObjTmp : saveObjs) {
				Object[] objs = new Object[fieldNms.keySet().size()+fieldIdNms.keySet().size()];
				int index = 0;
				for (String fieldNmTmp : fieldNms.keySet()) {
					objs[index] = getValueByFieldExpr(fieldNmTmp,
							saveObjTmp);
					index++;
				}
				for (String fieldNmTmp : fieldIdNms.keySet()) {
					objs[index] = getValueByFieldExpr(fieldNmTmp,
							saveObjTmp);
					index++;
				}
				params.add(objs);
			}
			
			batchUpdate(conn, uptSql.toString(), params, 1000);
		}
	}
	
	public <T> void deleteEntityJdbcBatch(Connection conn, Collection<T> saveObjs,List<String> fields) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder deleteSql = new StringBuilder("delete from ").append(
					tableNm).append(" where 1=1");
			List<Object[]> params = new ArrayList<Object[]>();
			if(fields==null||fields.size()<=0){
				fieldNms=JpaEntityUtils.getIdColumnsByEntity(saveClass);
				Iterator<String> it = fieldNms.keySet().iterator();
				while (it.hasNext()) {
					String fieldNm = it.next();
					deleteSql.append(" and ");
					deleteSql.append(fieldNms.get(fieldNm));
					deleteSql.append(" = ");
					deleteSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[fieldNms.keySet().size()];
					for (String fieldNmTmp : fieldNms.keySet()) {
						objs[index] = getValueByFieldExpr(fieldNmTmp,
								saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			else{
				for(String field: fields){
					deleteSql.append(" and ");
					deleteSql.append(fieldNms.get(field));
					deleteSql.append(" = ");
					deleteSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[fields.size()];
					for (String field : fields) {
						objs[index] = getValueByFieldExpr(field,saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			batchUpdate(conn, deleteSql.toString(), params, 1000);
		}
	}
	
	public <T> void saveEntityJdbcBatch(Connection conn, Collection<T> saveObjs) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder uptSql = new StringBuilder("insert into ").append(
					tableNm).append("(");
			Iterator<String> it = fieldNms.keySet().iterator();
			StringBuilder valuesTmp = new StringBuilder("");
			boolean isFirst = true;
			while (it.hasNext()) {
				String fieldNm = it.next();
				if (!isFirst) {
					uptSql.append(" , ");
					valuesTmp.append(" , ");
				}
				/*if(fieldNms.get(fieldNm).equals("PRECISION"))
					uptSql.append("`"+fieldNms.get(fieldNm)+"`");
				else*/
					uptSql.append(fieldNms.get(fieldNm));
				valuesTmp.append(" ? ");
				isFirst = false;
			}
			uptSql.append(") values (").append(valuesTmp).append(")");
			List<Object[]> params = new ArrayList<Object[]>();
			for (Object saveObjTmp : saveObjs) {
				int index = 0;
				Object[] objs = new Object[fieldNms.keySet().size()];
				for (String fieldNmTmp : fieldNms.keySet()) {
					objs[index] = getValueByFieldExpr(fieldNmTmp,
							saveObjTmp);
					index++;
				}
				params.add(objs);
			}
			batchUpdate(conn, uptSql.toString(), params, 1000);
		}
	}
	
	
	
	
	
	private Object getValueByFieldExpr(String fieldExpr, Object obj) {
		Object val = null;
		if (!StringUtils.isEmpty(fieldExpr)) {
			String[] fieldDetails = StringUtils.split(fieldExpr, '.');
			val = obj;
			for (int i = 0; i < fieldDetails.length; i++) {
				if (fieldDetails[i] == null
						|| "".equals(fieldDetails[i].trim())) {
					continue;
				}
				try {
					val = getField(val, fieldDetails[i].trim());
				} catch (Exception e) {
					e.printStackTrace();
					val = null;
				}
			}
		}
		return val;
	}
	
	private Object getField(Object obj, String field) throws Exception {
		String firstLetter = field.substring(0, 1).toUpperCase();
		String getMethodName = "get" + firstLetter + field.substring(1);
		Method method = obj.getClass().getMethod(getMethodName);
		return method.invoke(obj);
	}
	
	/**
	 * 
	 * @param conn
	 * @param sql
	 * @param batchArgs
	 * @param cycleSize
	 */
	private void batchUpdate(Connection conn, String sql, List<Object[]> batchArgs, int cycleSize){
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            int count = 0;
            logger.debug("------------------------------sql执行开始--------------------------------------\r\n");
            for (Object[] obj : batchArgs) {
                count++;
                String info="";
                for (int i = 0; i < obj.length; i++) {
                    info+=obj[i]+",";
                    ps.setObject(i + 1, obj[i]);
                }
                logger.trace("执行第"+count+"条信息---"+sql+"("+info+")");
                ps.addBatch();
                if (count % cycleSize == 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            }
            if (batchArgs.size() % cycleSize != 0) {
                ps.executeBatch();
                conn.commit();
            }
            logger.debug("---------------------------sql执行完成共插入"+count+"信息--------------------------------------\r\n");
        }catch(SQLException se){
            try {
                conn.rollback();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            se.printStackTrace();
            if(se.getNextException() != null){              
                se.getNextException().printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                	conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
