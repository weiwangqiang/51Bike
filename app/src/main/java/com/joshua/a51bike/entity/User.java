package com.joshua.a51bike.entity;

public class User {
    private  int userid;
	private  String username;
	private String realName = null;
	private  int usermoney;
	private  int userstate;  // 0 可租 1不可租 2 正在租
	private  String userpass;
	private  String userpic;
	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	private String school = "";

	public void setRealName(String name){
		this.realName = name;
	}
	public String getRealName(){
		return realName;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getUsermoney() {
		return usermoney;
	}
	public void setUsermoney(int usermoney) {
		this.usermoney = usermoney;
	}
	public int getUserstate() {
		return userstate;
	}
	public void setUserstate(int userstate) {
		this.userstate = userstate;
	}
	public String getUserpass() {
		return userpass;
	}
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	public String getUserpic() {
		return userpic;
	}
	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}
	public String toString(){
		return "username : "+ username +
				"\n userpass : "+userpass +
				" \n userid : "+userid;
	}
}
