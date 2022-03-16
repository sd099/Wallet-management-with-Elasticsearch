package org.paytm.milestone2._Milestone2.controllers;

import org.paytm.milestone2._Milestone2.DTO.Request.AddMoneyRequestBody;
import org.paytm.milestone2._Milestone2.DTO.Request.WalletCreationRequestBody;
import org.paytm.milestone2._Milestone2.Kafka.Producer;
import org.paytm.milestone2._Milestone2.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletController {

    @Autowired
    WalletService walletService;

    @Autowired
    Producer producer;

    //Method to get username from jwt token
    public String getUserNameFromToken(){
        String UserName = SecurityContextHolder.getContext().getAuthentication().getName();
        return UserName;
    }

    // @desc    Wallet Creation
    // @route   POST /wallet
    // @access  Private(JWT access)
    @RequestMapping(value = "/wallet",method = RequestMethod.POST)
    public ResponseEntity<?> createWallet(@RequestBody WalletCreationRequestBody walletCreationRequestBody){
        String userNameFromToken = getUserNameFromToken();
        ResponseEntity<?> response = walletService.createWallet(walletCreationRequestBody,userNameFromToken);

        //If wallet creation is successfull push to walletTopic in kafka
        if(response.getStatusCodeValue()==200){
            String msg = "Wallet created successfully for mobileNumber " + walletCreationRequestBody.getMobileNumber();
            producer.publishToWalletTopic(msg);
        }
        return response;
    }

    // @desc    Add money into wallet
    // @route   PUT /wallet/addmoney
    // @access  Private(JWT access)
    @RequestMapping(value = "wallet/addmoney",method = RequestMethod.PUT)
    public ResponseEntity<?> addMoneyIntoWallet(@RequestBody AddMoneyRequestBody addMoneyRequestBody){
        String userNameFromToken = getUserNameFromToken();
        return walletService.addMoneyIntoWallet(addMoneyRequestBody,userNameFromToken);
    }
}
