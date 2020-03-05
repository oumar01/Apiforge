package com.forgemorpion.morpion.utils;

import com.forgemorpion.morpion.bean.User;
import com.forgemorpion.morpion.dto.RegisterRequest;
public class AppUtils {


	private AppUtils() {
		super();
	}

	public static User mapRegisterRequestToUser(RegisterRequest registerRequest) {
		User user = new User();
		
		user.setName(registerRequest.getName());
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(registerRequest.getPassword());
		
		return user;
		
	}
	
}
