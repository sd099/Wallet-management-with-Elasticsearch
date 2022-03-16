package org.paytm.milestone2._Milestone2.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paytm.milestone2._Milestone2.DTO.Request.AddMoneyRequestBody;
import org.paytm.milestone2._Milestone2.DTO.Request.WalletCreationRequestBody;
import org.paytm.milestone2._Milestone2.models.User;
import org.paytm.milestone2._Milestone2.models.Wallet;
import org.paytm.milestone2._Milestone2.repository.UserRepository;
import org.paytm.milestone2._Milestone2.repository.WalletRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class WalletServiceTest {

    @MockBean
    private WalletRepository walletRepository;
    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private WalletService walletService;

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("Create wallet test")
    void createWallet() throws Exception {

        String userNameFromToken = "Sd099";

        String createWalletObject = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/createWalletRequestObject1.json")));
        WalletCreationRequestBody walletCreationRequestBody1 =objectMapper.readValue(createWalletObject,WalletCreationRequestBody.class);

        String userObject = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/userObject1.json")));
        User user = objectMapper.readValue(userObject,User.class);

        Wallet wallet = new Wallet(1,0.0F,walletCreationRequestBody1.getMobileNumber());

        Mockito.when(userRepository.findByMobileNumber(walletCreationRequestBody1.getMobileNumber())).thenReturn(user);
        Mockito.when(walletRepository.save(wallet)).thenReturn(wallet);

        ResponseEntity<?> res = walletService.createWallet(walletCreationRequestBody1,userNameFromToken);
        assertEquals(200,res.getStatusCodeValue());
    }

    @Test
    @DisplayName("Add money into wallet test")
    void addMoneyIntoWallet() throws Exception {
        String userNameFromToken = "Sd099";

        String userObject = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/userObject1.json")));
        User user = objectMapper.readValue(userObject,User.class);

        String addMoneyObject = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/addMoneyObject1.json")));
        AddMoneyRequestBody addMoneyRequestBody1 =objectMapper.readValue(addMoneyObject,AddMoneyRequestBody.class);

        Wallet wallet = new Wallet(1,200.0F,addMoneyRequestBody1.getMobileNumber());

        Mockito.when(userRepository.findByMobileNumber(addMoneyRequestBody1.getMobileNumber())).thenReturn(user);
        Mockito.when(walletRepository.findByMobileNumber(addMoneyRequestBody1.getMobileNumber())).thenReturn(wallet);

        Mockito.when(walletRepository.save(wallet)).thenReturn(wallet);

        ResponseEntity<?> res = walletService.addMoneyIntoWallet(addMoneyRequestBody1,userNameFromToken);
        assertEquals(wallet,res.getBody());


    }
}