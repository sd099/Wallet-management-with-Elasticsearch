package org.paytm.milestone2._Milestone2.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.paytm.milestone2._Milestone2.DTO.Request.AddMoneyRequestBody;
import org.paytm.milestone2._Milestone2.DTO.Request.WalletCreationRequestBody;
import org.paytm.milestone2._Milestone2.DTO.Response.MessageResponse;
import org.paytm.milestone2._Milestone2.models.User;
import org.paytm.milestone2._Milestone2.models.Wallet;
import org.paytm.milestone2._Milestone2.repository.UserRepository;
import org.paytm.milestone2._Milestone2.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletRepository walletRepository;

    private static Logger logger = LogManager.getLogger(UserService.class);

    //Create new wallet for user method
    public ResponseEntity<?> createWallet(WalletCreationRequestBody walletCreationRequestBody,String userNameFromToken){

        User user = userRepository.findByMobileNumber(walletCreationRequestBody.getMobileNumber());

        if(user==null){
            logger.error("User not found with the mobile number = "+walletCreationRequestBody.getMobileNumber());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User Not Found with this Mobile Number"));
        }

        if(user.getUserName().compareTo(userNameFromToken)!=0){
            logger.error("Unauthorized user error");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Unauthorized User"));
        }

        if(walletRepository.findByMobileNumber(walletCreationRequestBody.getMobileNumber())!=null){
            logger.debug("User already has a wallet with the mobile number = "+walletCreationRequestBody.getMobileNumber());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User already has a wallet"));
        }
        Wallet newWallet = new Wallet();
        newWallet.setCurrentBalance(0.0F);
        newWallet.setMobileNumber(walletCreationRequestBody.getMobileNumber());
        walletRepository.save(newWallet);

        logger.debug("Wallet creation successfull");
        logger.info("Wallet creation details. Mobile number = "+newWallet.getMobileNumber());

        return ResponseEntity.ok(new MessageResponse("Wallet Created Successfully!!"));

    }

    //add money into user wallet method
    public ResponseEntity<?> addMoneyIntoWallet(AddMoneyRequestBody addMoneyRequestBody,String userNameFromToken){

        User user = userRepository.findByMobileNumber(addMoneyRequestBody.getMobileNumber());

        if(user==null){
            logger.error("User not found with the mobile number = "+addMoneyRequestBody.getMobileNumber());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User Not Found with this Mobile Number"));
        }

        if(user.getUserName().compareTo(userNameFromToken)!=0){
            logger.error("Unauthorized user error");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Unauthorized User"));
        }

        Wallet wallet = walletRepository.findByMobileNumber(addMoneyRequestBody.getMobileNumber());

        if(wallet==null){
            logger.debug("Wallet not found with the mobile number = "+addMoneyRequestBody.getMobileNumber());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Wallet not available. First Create your wallet"));
        }

        if(addMoneyRequestBody.getMoney()<=0){
            logger.debug("Negative value entered for money. Positive value needed here.");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Enter a positive value for money"));
        }

        wallet.setCurrentBalance(wallet.getCurrentBalance()+addMoneyRequestBody.getMoney());
        walletRepository.save(wallet);

        logger.debug("Money added successfully");
        logger.info("Details. username = "+user.getUserName()+" mobile number = "+user.getMobileNumber()+" new balance = "+wallet.getCurrentBalance());

        return ResponseEntity.ok(wallet);
    }
}
