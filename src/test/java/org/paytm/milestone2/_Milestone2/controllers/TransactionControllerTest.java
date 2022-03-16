package org.paytm.milestone2._Milestone2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.paytm.milestone2._Milestone2.DTO.Response.MessageResponse;
import org.paytm.milestone2._Milestone2.DTO.Response.SignInResponseBody;
import org.paytm.milestone2._Milestone2.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
class TransactionControllerTest {

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
    @DisplayName("Create transaction test")
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (1000,\"Sd099\",\"Son\",\"Doe\",\"sondoe@gmail.com\",\"9876543210\",\"Time square, NY, USA\",\"$2a$10$hph6xo16D8ED1ZOHcMo7wuvrCH5ebxiVyTtPFGsHaZmIgAHWa55C2\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (2000,\"Jd099\",\"John\",\"Doe\",\"johndoe@gmail.com\",\"0123456789\",\"Time square, NY, USA\",\"$2a$10$3IT1NBW1r60UtCPOXGlwweqmHJDwy3bsr4cuO4XkNnUguTtYgA8va\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (3000,\"Ss099\",\"Steve\",\"Smith\",\"stevesmith@gmail.com\",\"5432109876\",\"Time square, NY, USA\",\"$2a$10$7uLjRL1XR99ovnrin52/puncFBPdMKv79FQ8CZVi8UfGqcxv6Yfge\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into wallet (wallet_id,mobile_number,current_balance) values (1000,\"9876543210\",5000.0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into wallet (wallet_id,mobile_number,current_balance) values (2000,\"0123456789\",0.0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM user", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "DELETE FROM wallet", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "DELETE FROM transaction", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

    //1 Son doe
    //2 John doe
    //3 Steve Smith
    //1 son doe wallet money
    //2 john doe wallet
    public class transactionP2P{
        @Test
        @DisplayName("Create transaction test success")
        void transactionP2PSuccess() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String transactionCreateRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionP2pRequestObject1.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(transactionCreateRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Transaction Successfull",response.getMessage());
        }
        @Test
        @DisplayName("Create transaction test payer not found failure")
        void transactionP2PPayerNotFound() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String transactionCreateRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionP2pRequestObject2.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(transactionCreateRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Payer User Not Found with this Mobile Number",response.getMessage());
        }
        @Test
        @DisplayName("Create transaction test user unauthorized failure")
        void transactionP2PUserUnauthorized() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject2.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String transactionCreateRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionP2pRequestObject1.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(transactionCreateRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Unauthorized User",response.getMessage());
        }
        @Test
        @DisplayName("Create transaction test payer wallet not found failure")
        void transactionP2PPayerWalletNotFound() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject3.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String transactionCreateRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionP2pRequestObject3.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(transactionCreateRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Payer Wallet Not Found",response.getMessage());
        }
        @Test
        @DisplayName("Create transaction test insufficient balance failure")
        void transactionP2PInsufficientBalance() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String transactionCreateRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionP2pRequestObject4.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(transactionCreateRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Insufficient balance",response.getMessage());
        }
        @Test
        @DisplayName("Create transaction test payee wallet not found failure")
        void transactionP2PPayeeWalletNotFound() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String transactionCreateRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionP2pRequestObject5.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(transactionCreateRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Payee Wallet Not Found",response.getMessage());
        }
        @Test
        @DisplayName("Create transaction test payer and payee same failure")
        void transactionP2PPayerAndPayeeSame() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            String transactionCreateRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/transactionObjects/transactionP2pRequestObject6.json")));

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .content(transactionCreateRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Payer wallet and Payee wallet same. Try different wallet",response.getMessage());
        }

    }

    @Nested
    @DisplayName("View transaction by transaction Id")
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (3000,\"Ss099\",\"Steve\",\"Smith\",\"stevesmith@gmail.com\",\"5432109876\",\"Time square, NY, USA\",\"$2a$10$7uLjRL1XR99ovnrin52/puncFBPdMKv79FQ8CZVi8UfGqcxv6Yfge\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into transaction (txn_id,amount,payee_mobile_number,payer_mobile_number,status,timestamp) values (3000,200.0,\"0123456789\",\"5432109876\",\"SUCCESS\",\"1998-01-02 00:00:00.000\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM user", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "DELETE FROM transaction", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public class viewTransactionById{
        @Test
        @DisplayName("View transaction by transaction Id success")
        void viewTransactionByIdSuccess() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject3.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            int txnId = 3000;

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.get("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .param("txnId",String.valueOf(txnId))
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            Transaction response = objectMapper.readValue(resultAsString,Transaction.class);

            assertEquals(txnId,response.getTxnId());
            assertNotNull(result);
        }

        @Test
        @DisplayName("View transaction by transaction Id not found failure")
        void viewTransactionByIdNotFound() throws Exception {

            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject3.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            int txnId = 2000;

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.get("/transaction")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .param("txnId",String.valueOf(txnId))
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Transaction not exist",response.getMessage());
        }
    }

    @Nested
    @DisplayName("View transaction by user id")
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (3000,\"Ss099\",\"Steve\",\"Smith\",\"stevesmith@gmail.com\",\"5432109876\",\"Time square, NY, USA\",\"$2a$10$7uLjRL1XR99ovnrin52/puncFBPdMKv79FQ8CZVi8UfGqcxv6Yfge\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (1000,\"Sd099\",\"Son\",\"Doe\",\"sondoe@gmail.com\",\"9876543210\",\"Time square, NY, USA\",\"$2a$10$hph6xo16D8ED1ZOHcMo7wuvrCH5ebxiVyTtPFGsHaZmIgAHWa55C2\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "insert into transaction (txn_id,amount,payee_mobile_number,payer_mobile_number,status,timestamp) values (3000,200.0,\"0123456789\",\"5432109876\",\"SUCCESS\",\"1998-01-02 00:00:00.000\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM user", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "DELETE FROM transaction", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public class viewTransactionByUserId{
        @Test
        @DisplayName("View transaction by user id success")
        void viewTransactionByUserIdSuccess() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject3.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            int userId = 3000;
            int pageNo = 0;

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.get("/transaction/"+userId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .param("pageNo",String.valueOf(pageNo))
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();


            assertEquals(200,result.getResponse().getStatus());
            assertNotNull(result);
        }

        @Test
        @DisplayName("View transaction by user id user not exist failure")
        void viewTransactionByUserIdUserNotExist() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject3.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            int userId = 4000;
            int pageNo = 0;

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.get("/transaction/"+userId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .param("pageNo",String.valueOf(pageNo))
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: No user found",response.getMessage());
        }

        @Test
        @DisplayName("View transaction by user id user unauthorized failure")
        void viewTransactionByUserIdUserUnauthorized() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));
            String jwtForUser = generateTokenUsingLogin(signInRequestBody);
            int userId = 3000;
            int pageNo = 0;

            MvcResult result= mockMvc.perform(MockMvcRequestBuilders.get("/transaction/"+userId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtForUser)
                            .param("pageNo",String.valueOf(pageNo))
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Unauthorized User",response.getMessage());
        }
    }
}