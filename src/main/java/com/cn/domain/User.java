package com.cn.domain;

import java.io.Serializable;

import com.cn.annotation.Column;
import com.cn.annotation.Entity;
import com.cn.annotation.Id;
import com.cn.annotation.Table;
@Entity
@Table(name = "user")
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID", unique = true, nullable = false)
	private String id;
    @Column(name = "NAME", nullable = false)
	private String name;
    @Column(name = "BRITHDAY", nullable = false)
	private String brithday;
    @Column(name = "SEX", nullable = true)
	private int sex;
    @Column(name = "ADDRESS", nullable = true)
	private String address;

	public User() {
	}

	public User(String id, String name, String brithday, int sex, String address) {
		this.id = id;
		this.name = name;
		this.brithday = brithday;
		this.sex = sex;
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrithday() {
		return brithday;
	}

	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "User{" + "address='" + address + '\'' + ", id=" + id + ", name='" + name + '\'' + ", birthday="
				+ brithday + ", sex=" + sex + '}';
	}
}
