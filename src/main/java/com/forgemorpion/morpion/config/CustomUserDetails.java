package com.forgemorpion.morpion.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.forgemorpion.morpion.bean.User;
import com.forgemorpion.morpion.bussiness.user.UserBusiness;

@Service
public class CustomUserDetails implements UserDetailsService {
	
	private UserBusiness userBusiness;

	public CustomUserDetails(@Lazy UserBusiness userBusiness) {
		super();
		this.userBusiness = userBusiness;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userBusiness.getByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("Utilisateur non trouver avec le username: " + username);
		}
		
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
		
	}

}
