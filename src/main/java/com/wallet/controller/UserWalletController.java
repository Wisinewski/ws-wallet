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

import com.wallet.dto.UserWalletDTO;
import com.wallet.entity.User;
import com.wallet.entity.UserWallet;
import com.wallet.entity.Wallet;
import com.wallet.response.Response;
import com.wallet.service.UserWalletService;

@RestController
@RequestMapping("user-wallet")
public class UserWalletController {

	@Autowired
	private UserWalletService userWalletService;
	
	@PostMapping
	public ResponseEntity<Response<UserWalletDTO>> create(@Valid @RequestBody UserWalletDTO userWalletDto, BindingResult result) {
		Response<UserWalletDTO> response = new Response<UserWalletDTO>();
		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));
			
			return ResponseEntity.badRequest().body(response);
		}
		
		UserWallet userWallet = userWalletService.save(this.convertDtoToEntity(userWalletDto));
		
		response.setData(this.convertEntityToDto(userWallet));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	public UserWallet convertDtoToEntity(UserWalletDTO userWalletDto) {
		UserWallet userWallet = new UserWallet();
		User user = new User();
		user.setId(userWalletDto.getUsers());
		Wallet wallet = new Wallet();
		wallet.setId(userWalletDto.getWallet());
		
		userWallet.setId(userWalletDto.getId());
		userWallet.setUsers(user);
		userWallet.setWallet(wallet);
		
		return userWallet;
	}
	
	public UserWalletDTO convertEntityToDto(UserWallet userWallet) {
		UserWalletDTO userWalletDto = new UserWalletDTO();
		userWalletDto.setId(userWallet.getId());
		userWalletDto.setUsers(userWallet.getUsers().getId());
		userWalletDto.setWallet(userWallet.getWallet().getId());
		
		return userWalletDto;
	}
	
}
