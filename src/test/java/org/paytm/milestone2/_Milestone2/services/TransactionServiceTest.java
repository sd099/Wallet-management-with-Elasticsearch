package org.paytm.milestone2._Milestone2.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paytm.milestone2._Milestone2.DTO.Request.TransactionP2pRequestBody;
import org.paytm.milestone2._Milestone2.models.Transaction;
import org.paytm.milestone2._Milestone2.models.User;
import org.paytm.milestone2._Milestone2.models.Wallet;
import org.paytm.milestone2._Milestone2.repository.TransactionRepository;
import org.paytm.milestone2._Milestone2.repository.UserRepository;
import org.paytm.milestone2._Milestone2.repository.WalletRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class TransactionServiceTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    WalletRepository walletRepository;
    @MockBean
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionService transactionService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Create transaction P2P")
    void transactionP2P() throws Exception {
        String userNameFromToken = "Sd099";

        String payerNameObject = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/userObject1.json")));
        User payerName = objectMapper.readValue(payerNameObject,User.class);

        String transactionP2pRequestObject = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionP2pRequestObject1.json")));
        TransactionP2pRequestBody transactionP2pRequestBody = objectMapper.readValue(transactionP2pRequestObject,TransactionP2pRequestBody.class);

        Wallet payerWallet = new Wallet(1,5000,"9876543210");
        Wallet payeeWallet = new Wallet(2,200,"0123456789");

        Mockito.when(userRepository.findByMobileNumber(transactionP2pRequestBody.getPayerMobileNumber())).thenReturn(payerName);
        Mockito.when(walletRepository.findByMobileNumber(transactionP2pRequestBody.getPayerMobileNumber())).thenReturn(payerWallet);
        Mockito.when(walletRepository.findByMobileNumber(transactionP2pRequestBody.getPayeeMobileNumber())).thenReturn(payeeWallet);
        Mockito.when(walletRepository.save(payerWallet)).thenReturn(payerWallet);
        Mockito.when(walletRepository.save(payeeWallet)).thenReturn(payeeWallet);

        Transaction newTransaction = new Transaction();

        newTransaction.setAmount(transactionP2pRequestBody.getAmount());
        newTransaction.setPayerMobileNumber(transactionP2pRequestBody.getPayerMobileNumber());
        newTransaction.setPayeeMobileNumber(transactionP2pRequestBody.getPayeeMobileNumber());
        newTransaction.setStatus("Success");
        newTransaction.setTimestamp(new Timestamp(System.currentTimeMillis()));

        Mockito.when(transactionRepository.save(newTransaction)).thenReturn(newTransaction);

        ResponseEntity<?> response = transactionService.transactionP2P(transactionP2pRequestBody,userNameFromToken);

        assertEquals(200,response.getStatusCodeValue());
    }

    @Test
    @DisplayName("View transaction using transaction id")
    void viewTransactionById() throws Exception {
        String transactionObj = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionResObject1.json")));
        Transaction transaction = objectMapper.readValue(transactionObj,Transaction.class);

        Mockito.when(transactionRepository.findById(transaction.getTxnId())).thenReturn(Optional.of(transaction));

        ResponseEntity<?> response = transactionService.viewTransactionById(transaction.getTxnId());

        assertEquals(200,response.getStatusCodeValue());
        assertEquals(transaction,response.getBody());
    }

    @Test
    @DisplayName("View all transaction using User Id")
    void viewTransactionByUserId() throws Exception {

        String userNameFromToken = "Sd099";
        int pageNo = 0;

        String transactionObj = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionResObject1.json")));
        Transaction transaction = objectMapper.readValue(transactionObj,Transaction.class);

        List<Transaction> transactionsList = new ArrayList<>();
        transactionsList.add(transaction);

        String userObject = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/userObject1.json")));
        User user = objectMapper.readValue(userObject,User.class);

        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        Pageable pageable =  PageRequest.of(pageNo,2);
        Mockito.when( transactionRepository.findByPayerMobileNumberOrPayeeMobileNumber(user.getMobileNumber(),user.getMobileNumber(),pageable)).thenReturn(transactionsList);

        ResponseEntity<?> response = transactionService.viewTransactionByUserId(user.getUserId(),pageNo,userNameFromToken);

        assertEquals(200,response.getStatusCodeValue());
    }
}