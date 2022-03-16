package org.paytm.milestone2._Milestone2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.paytm.milestone2._Milestone2.DTO.Response.MessageResponse;
import org.paytm.milestone2._Milestone2.DTO.Response.SignInResponseBody;
import org.paytm.milestone2._Milestone2.models.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class WalletControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    String generateTokenUsingLogin(String signInRequestBody) throws Exception{

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                        .content(signInRequestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertNotNull(result);

        String body = result.getResponse().getContentAsString();
        SignInResponseBody signInResponseBody1 = objectMapper.readValue(body,SignInResponseBody.class);
        return signInResponseBody1.getJwt();

    }

    @Nested
    @DisplayName("Create wallet tests")
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (2000,\"Jd099\",\"John\",\"Doe\",\"johndoe@gmail.com\",\"0123456789\",\"Time square, NY, USA\",\"$2a$10$3IT1NBW1r60UtCPOXGlwweqmHJDwy3bsr4cuO4XkNnUguTtYgA8va\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (1000,\"Sd099\",\"Son\",\"Doe\",\"sondoe@gmail.com\",\"9876543210\",\"Time square, NY, USA\",\"$2a$10$hph6xo16D8ED1ZOHcMo7wuvrCH5ebxiVyTtPFGsHaZmIgAHWa55C2\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into wallet (wallet_id,mobile_number,current_balance) values (1000,\"9876543210\",0.0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM user", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "DELETE FROM wallet", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public class createWallet{

        @Test
        @DisplayName("Create wallet success")
        void createWalletSuccess() throws Exception{

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject2.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String createWalletRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/createWalletRequestObject2.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(createWalletRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String resultAsString= result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Wallet Created Successfully!!",response.getMessage());

        }

        @Test
        @DisplayName("Create wallet user not exist failure")
        void createWalletUserNotExist() throws Exception{

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject2.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String createWalletRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/createWalletRequestObject3.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(createWalletRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString= result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: User Not Found with this Mobile Number",response.getMessage());

        }

        @Test
        @DisplayName("Create wallet user unauthorized failure")
        void createWalletUserUnauthorized() throws Exception{

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String createWalletRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/createWalletRequestObject2.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(createWalletRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString= result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Unauthorized User",response.getMessage());

        }

        @Test
        @DisplayName("Create wallet user wallet already exist failure")
        void createWalletUserWalletAlreadyExists() throws Exception{

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String createWalletRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/createWalletRequestObject1.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(createWalletRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString= result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: User already has a wallet",response.getMessage());

        }

    }

    @Nested
    @DisplayName("Add money into wallet tests")
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (2000,\"Jd099\",\"John\",\"Doe\",\"johndoe@gmail.com\",\"0123456789\",\"Time square, NY, USA\",\"$2a$10$3IT1NBW1r60UtCPOXGlwweqmHJDwy3bsr4cuO4XkNnUguTtYgA8va\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (1000,\"Sd099\",\"Son\",\"Doe\",\"sondoe@gmail.com\",\"9876543210\",\"Time square, NY, USA\",\"$2a$10$hph6xo16D8ED1ZOHcMo7wuvrCH5ebxiVyTtPFGsHaZmIgAHWa55C2\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into wallet (wallet_id,mobile_number,current_balance) values (1000,\"9876543210\",0.0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM user", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "DELETE FROM wallet", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public class addMoneyIntoWallet{
        @Test
        @DisplayName("Add money into wallet tests success")
        void addMoneyIntoWalletSuccess() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String addMoneyRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/addMoneyObject1.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.put("/wallet/addmoney")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(addMoneyRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();


            String resultAsString= result.getResponse().getContentAsString();
            Wallet response = objectMapper.readValue(resultAsString,Wallet.class);

            assertTrue(response.getWalletId()>0);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Add money into wallet user not exist failure")
        void addMoneyIntoWalletUserNotExist() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String addMoneyRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/addMoneyObject3.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.put("/wallet/addmoney")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(addMoneyRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();


            String resultAsString= result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: User Not Found with this Mobile Number",response.getMessage());
        }

        @Test
        @DisplayName("Add money into wallet user unauthorized failure")
        void addMoneyIntoWalletUserUnauthorized() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject2.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String addMoneyRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/addMoneyObject1.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.put("/wallet/addmoney")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(addMoneyRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString= result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Unauthorized User",response.getMessage());
        }

        @Test
        @DisplayName("Add money into wallet. Wallet not found failure")
        void addMoneyIntoWalletWalletNotFound() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject2.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String addMoneyRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/addMoneyObject2.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.put("/wallet/addmoney")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(addMoneyRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString= result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Wallet not available. First Create your wallet",response.getMessage());
        }

        @Test
        @DisplayName("Add money into wallet money value negative failure")
        void addMoneyIntoWalletNegativeMoney() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String addMoneyRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/walletObjects/addMoneyObject4.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.put("/wallet/addmoney")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(addMoneyRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString= result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Enter a positive value for money",response.getMessage());
        }

    }
}