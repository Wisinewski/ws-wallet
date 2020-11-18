package com.wallet.security;

import com.wallet.entity.User;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JwtUserFactory {
	
	public static JwtUser create(User user) {
		return new JwtUser(user.getId(), user.getEmail(), user.getPassword());
	}
	
	/*
	 * public static JwtUser create(User user) { return new JwtUser(user.getId(),
	 * user.getEmail(), user.getPassword(),
	 * createGrantedAuthorities(user.getRole())); }
	 * 
	 * private static List<GrantedAuthority> createGrantedAuthorities(RoleEnum role)
	 * { List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	 * authorities.add(new SimpleGrantedAuthority(role.toString())); return
	 * authorities; }
	 */
}
