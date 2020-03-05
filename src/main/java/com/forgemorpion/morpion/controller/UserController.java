package com.forgemorpion.morpion.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.forgemorpion.morpion.bean.User;
import com.forgemorpion.morpion.bussiness.user.UserBusiness;
import com.forgemorpion.morpion.dao.user.UserDao;
import com.forgemorpion.morpion.dto.AuthRequest;
import com.forgemorpion.morpion.dto.RegisterRequest;
import com.forgemorpion.morpion.utils.AppUtils;


@RestController
@RequestMapping(UserController.PATH)
public class UserController {
	
	private UserBusiness userBusiness;
	
	static final String PATH = "users";

	public UserController(UserBusiness userBusiness) {
		super();
		this.userBusiness = userBusiness;
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> authentication(@RequestBody AuthRequest authRequest) throws Exception {
		return ResponseEntity.ok(userBusiness.authentication(authRequest.getUsername(), authRequest.getPassword()));
	}
	
	@PostMapping("/register")
	@ResponseStatus(code = HttpStatus.CREATED)
	public User authentication(@RequestBody RegisterRequest registerRequest) throws Exception {
		User user = AppUtils.mapRegisterRequestToUser(registerRequest);
		System.out.println("controller auth " + user.toString());
		return userBusiness.registration(user);
	}
	
	@GetMapping("/me")
	public ResponseEntity<User> getUserAuthenticated(Authentication auth)
			throws Exception {
		String usernameAuthenticated = auth.getName();
		User user =  Optional.ofNullable(userBusiness.getByUsername(usernameAuthenticated))
				.orElseThrow(() -> new Exception("User not found for this username :: " + usernameAuthenticated));
		
		return ResponseEntity.ok().body(user);
	}
	
	@PutMapping("/update")
	public ResponseEntity<User> getUserUpdate(Authentication auth, @RequestBody RegisterRequest registerRequest)
			throws Exception {
		String usernameAuthenticated = auth.getName();
		User user =  Optional.ofNullable(userBusiness.getByUsername(usernameAuthenticated))
				.orElseThrow(() -> new Exception("User not found for this username : " + usernameAuthenticated));
		
		return ResponseEntity.ok(userBusiness.update(user, registerRequest));
	}

	@GetMapping("/username/{userid}")
	public ResponseEntity<User> getByUsername(@PathVariable(value = "userid") String username)
			throws Exception {
		
		User user =  Optional.ofNullable(userBusiness.getByUsername(username))
				.orElseThrow(() -> new Exception("User not found for this username :: " + username));
		
		return ResponseEntity.ok().body(user);
	}

}
