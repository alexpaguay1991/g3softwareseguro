package com.espe.pageimage.security;



import com.espe.pageimage.repository.UserRepository;
import com.espe.pageimage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	//carga un user por el nombre o email
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Encuentra al usuario en la base de datos por su nombre de usuario
		com.espe.pageimage.model.User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ese username: " + username));
		// Mapea el rol del usuario (String) a un objeto GrantedAuthority
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+user.getRole());
		// Crea y retorna el User de Spring Security, pasando los roles como autoridades
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.singletonList(authority));
	}

	private Collection<? extends GrantedAuthority> mapearRoles(Set<String> roles) {
		return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}




}
