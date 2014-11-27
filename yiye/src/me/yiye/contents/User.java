package me.yiye.contents;

public class User {
	public long id;
	public String email;
	public String username;
	public String password;
	public String avatar;
	
	@Override
	public String toString() {
		return "[id:" + id + " email:" + email + " username:" + username + " password:" + password + " avatar:" + avatar + "]";
	}
}
