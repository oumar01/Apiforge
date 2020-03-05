package com.forgemorpion.morpion.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forgemorpion.morpion.bean.User;


@Repository
public interface UserDao extends JpaRepository<User, Long> {
	
	/*
	 * Recherche d'un utilisateur
	 */
	User findByUsername(String username);
	
}
