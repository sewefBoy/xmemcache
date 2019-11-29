package com.cn.memcached;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import com.cn.util.YuFormatUtil;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientCallable;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class MemcachedJava {
	private YuFormatUtil bsyuUtil = new YuFormatUtil();
	
	public void memcacheNameSpace_Set(){
		long startTime = System.currentTimeMillis();
		XMemcachedClient memcachedClient = bsyuUtil.getMemcachedClient();
		try {
			String ns = "namespace" ;
			memcachedClient.withNamespace(ns,
				new MemcachedClientCallable<Void>() {
					public Void call(MemcachedClient client)
							throws MemcachedException, InterruptedException,
							TimeoutException {
		                            //a,b,c都在namespace下
									client.set("a",10,1);
		                            client.set("b",10,2);
		                            client.set("c",10,3);
		                            return null;
					}
				});
			//获取命名空间内的a对应的值
			Integer[] aValue = memcachedClient.withNamespace(ns,
				new MemcachedClientCallable<Integer[]>() {
					public Integer[] call(MemcachedClient client)throws MemcachedException, InterruptedException,TimeoutException {
						Integer[] value = new Integer[3];	
						value[0] = client.get("a");
						value[1] = client.get("b");
						value[2] = client.get("c");
	                    return value;
					}
			});
			//使得命名空间失效
			memcachedClient.invalidateNamespace(ns);
			System.out.println("名称空间aValue 对应的值为："+ Arrays.toString(aValue));
			long endTime = System.currentTimeMillis();
			System.out.println("程序耗时 " + (endTime - startTime));
		} catch (MemcachedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
}
