package com.wallet.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.dto.UserDTO;
import com.wallet.entity.User;
import com.wallet.response.Response;
import com.wallet.service.UserService;
import com.wallet.util.Bcrypt;

@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping
	public ResponseEntity<Response<UserDTO>> create(@Valid @RequestBody UserDTO userDto, BindingResult result) {
		Response<UserDTO> response = new Response<UserDTO>();
		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		User user = userService.save(this.convertDtoToEntity(userDto));
		
		response.setData(this.convertEntityToDto(user));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	private User convertDtoToEntity(UserDTO userDto) {
		User user = new User();
		user.setId(userDto.getId());
		user.setEmail(userDto.getEmail());
		user.setName(userDto.getName());
		user.setPassword(Bcrypt.getHash(userDto.getPassword()));
		
		return user;
	}
	
	private UserDTO convertEntityToDto(User user) {
		UserDTO userDto = new UserDTO();
		userDto.setId(user.getId());
		userDto.setEmail(user.getEmail());
		userDto.setName(user.getName());
		
		return userDto;
	}
	
}
