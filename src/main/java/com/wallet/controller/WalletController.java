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

import com.wallet.dto.WalletDTO;
import com.wallet.entity.Wallet;
import com.wallet.response.Response;
import com.wallet.service.WalletService;

@RestController
@RequestMapping("wallet")
public class WalletController {
	
	@Autowired
	private WalletService walletService;
	
	@PostMapping
	public ResponseEntity<Response<WalletDTO>> create(@Valid @RequestBody WalletDTO walletDto, BindingResult result) {
		Response<WalletDTO> response = new Response<WalletDTO>();
		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));
			
			return ResponseEntity.badRequest().body(response);
		}
		
		Wallet wallet = walletService.save(this.convertDtoToEntity(walletDto));
		
		response.setData(this.convertEntityToDto(wallet));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	private Wallet convertDtoToEntity(WalletDTO walletDto) {
		Wallet wallet = new Wallet();
		wallet.setId(walletDto.getId());
		wallet.setName(walletDto.getName());
		wallet.setValue(walletDto.getValue());
		
		return wallet;
	}
	
	private WalletDTO convertEntityToDto(Wallet wallet) {
		WalletDTO walletDto = new WalletDTO();
		walletDto.setId(wallet.getId());
		walletDto.setName(wallet.getName());
		walletDto.setValue(wallet.getValue());
		
		return walletDto;
	}

}
