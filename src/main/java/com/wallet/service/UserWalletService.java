package com.wallet.service;

import org.springframework.stereotype.Service;

import com.wallet.entity.UserWallet;

public interface UserWalletService {

	UserWallet save(UserWallet userWallet);
	
}
