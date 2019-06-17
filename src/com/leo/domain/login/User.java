package com.leo.domain.login;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
	private static final long serialVersionUID = 1169958693886162392l;
    private int userId;
    private String loginName;
    private String password;
    private int type;
    private long registTime;
	private String personalMail;
}
