package com.forgemorpion.morpion.bussiness.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.forgemorpion.morpion.bean.User;
import com.forgemorpion.morpion.config.CustomUserDetails;
import com.forgemorpion.morpion.dao.user.UserDao;
import com.forgemorpion.morpion.dto.AuthResponse;
import com.forgemorpion.morpion.dto.RegisterRequest;

@Service
public class UserBusinessImpl implements UserBusiness {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private com.forgemorpion.morpion.config.jwt.JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Autowired
	private CustomUserDetails userDetailsService;
	
	public UserBusinessImpl() {
		super();
	}

	@Override
	public User save(User user) {
		User userSaved = userDao.save(user);
		return userSaved;
	}

	@Override
	public User getByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public AuthResponse authentication(String username, String password) throws Exception {
		
		User user = getByUsername(username);
		
		if (user == null) {
			throw new Exception("USERNAME_OR_PASSWORD_IS_INCORRECT");
		}
		
		authenticate(username, password);
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		final String token = jwtTokenUtil.generateToken(userDetails);
		
		AuthResponse authResponse = new AuthResponse();
		authResponse.setUsername(username);
		authResponse.setAccessToken(token);
		
		return authResponse;
	}

	@Override
	public User registration(User user) {
		String passwordEncrypt = bcryptEncoder.encode(user.getPassword());
		user.setPassword(passwordEncrypt);
		return save(user);
	}
	
	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@Override
	public User update(User userToUpdate, RegisterRequest registerRequest) {
		userToUpdate.setName(registerRequest.getName());
		userToUpdate.setUsername(registerRequest.getUsername());
		userToUpdate.setEmail(registerRequest.getEmail());
		userToUpdate.setPassword(registerRequest.getPassword());
		
		return save(userToUpdate);
	}

	
	

}
