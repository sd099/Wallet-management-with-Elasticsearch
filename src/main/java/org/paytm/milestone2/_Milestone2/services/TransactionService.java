package org.paytm.milestone2._Milestone2.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.paytm.milestone2._Milestone2.DTO.Request.TransactionP2pRequestBody;
import org.paytm.milestone2._Milestone2.DTO.Response.MessageResponse;
import org.paytm.milestone2._Milestone2.models.Transaction;
import org.paytm.milestone2._Milestone2.models.User;
import org.paytm.milestone2._Milestone2.models.Wallet;
import org.paytm.milestone2._Milestone2.repository.TransactionRepository;
import org.paytm.milestone2._Milestone2.repository.UserRepository;
import org.paytm.milestone2._Milestone2.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    TransactionRepository transactionRepository;

    Logger logger = LogManager.getLogger(TransactionService.class);

    //Create new transaction between two user(P2P) method
    public ResponseEntity<?> transactionP2P(TransactionP2pRequestBody transactionP2pRequestBody,String userNameFromToken){
        User payerName = userRepository.findByMobileNumber(transactionP2pRequestBody.getPayerMobileNumber());

        if(payerName==null){
            logger.error("Payer not found with the mobile number: "+transactionP2pRequestBody.getPayerMobileNumber());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Payer User Not Found with this Mobile Number"));
        }

        if(payerName.getUserName().compareTo(userNameFromToken)!=0){
            logger.error("Unauthorized user error");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Unauthorized User"));
        }

        Wallet payerWallet = walletRepository.findByMobileNumber(transactionP2pRequestBody.getPayerMobileNumber());

        if(payerWallet==null){
            logger.debug("Wallet not found with the payer mobile number = "+transactionP2pRequestBody.getPayerMobileNumber());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Payer Wallet Not Found"));
        }

        if(payerWallet.getCurrentBalance() < transactionP2pRequestBody.getAmount()){
            logger.debug("Insufficient balance in the payer wallet");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Insufficient balance"));
        }

        Wallet payeeWallet = walletRepository.findByMobileNumber(transactionP2pRequestBody.getPayeeMobileNumber());

        if(payeeWallet==null){
            logger.debug("Wallet not found with the payee mobile number = "+transactionP2pRequestBody.getPayeeMobileNumber());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Payee Wallet Not Found"));
        }

        if(payerWallet==payeeWallet){
            logger.debug("Payer and payee wallet same. Different wallet needed");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Payer wallet and Payee wallet same. Try different wallet"));
        }

        payerWallet.setCurrentBalance(payerWallet.getCurrentBalance()-transactionP2pRequestBody.getAmount());
        payeeWallet.setCurrentBalance(payeeWallet.getCurrentBalance()+transactionP2pRequestBody.getAmount());

        walletRepository.save(payerWallet);
        walletRepository.save(payeeWallet);

        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(transactionP2pRequestBody.getAmount());
        newTransaction.setPayerMobileNumber(transactionP2pRequestBody.getPayerMobileNumber());
        newTransaction.setPayeeMobileNumber(transactionP2pRequestBody.getPayeeMobileNumber());
        newTransaction.setStatus("Success");
        newTransaction.setTimestamp(new Timestamp(System.currentTimeMillis()));

        transactionRepository.save(newTransaction);

        logger.debug("Transaction successfull");
        logger.info("Details. Payer mobile number = "+transactionP2pRequestBody.getPayerMobileNumber()+" payee mobile number = "+transactionP2pRequestBody.getPayeeMobileNumber()+" amount = "+transactionP2pRequestBody.getAmount());

        return ResponseEntity.ok(new MessageResponse("Transaction Successfull"));

    }

    //Get transaction by transaction id method
    public ResponseEntity<?> viewTransactionById(int txnId){
        Transaction transaction = transactionRepository.findById(txnId).orElse(null);

        if(transaction==null){
            logger.error("Transaction not found with txnId = "+txnId);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Transaction not exist"));
        }

        logger.debug("Transaction found with txnId = "+txnId);
        return ResponseEntity.ok(transaction);
    }

    //Get all transaction by user id
    public ResponseEntity<?> viewTransactionByUserId(int userId,int pageNo,String userNameFromToken){

        User user = userRepository.findById(userId).orElse(null);

        if(user==null){
            logger.error("User not found with userId = "+userId);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No user found"));
        }

        if(user.getUserName().compareTo(userNameFromToken)!=0){
            logger.error("Unauthorized user error");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Unauthorized User"));
        }

        Pageable pageable =  PageRequest.of(pageNo,2);

        List<Transaction> transactionsWithPagination = transactionRepository.findByPayerMobileNumberOrPayeeMobileNumber(user.getMobileNumber(),user.getMobileNumber(),pageable);


        if(transactionsWithPagination==null){
            logger.debug("Transaction not found with userId = "+userId);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No transaction found"));
        }

        logger.debug("Transaction found with userId = "+userId);
        return ResponseEntity.ok(transactionsWithPagination);
    }
}
