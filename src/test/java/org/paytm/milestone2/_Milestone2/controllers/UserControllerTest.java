package org.paytm.milestone2._Milestone2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.paytm.milestone2._Milestone2.DTO.Response.MessageResponse;
import org.paytm.milestone2._Milestone2.DTO.Response.SignInResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();


    @Nested
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (3000,\"Ss099\",\"Steve\",\"Smith\",\"stevesmith@gmail.com\",\"5432109876\",\"Time square, NY, USA\",\"$2a$10$7uLjRL1XR99ovnrin52/puncFBPdMKv79FQ8CZVi8UfGqcxv6Yfge\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM user", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Signup users test")
    public class signUpUser{
        @Test
        @DisplayName("Signup users success")
        void signUpUserSuccess() throws Exception {

            String user = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/userObject2.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                            .content(user)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Signup Complete",response.getMessage());
        }

        @Test
        @DisplayName("Signup users with same username failure")
        void signUpUserSameUserName() throws Exception {

            String user = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/userObject3.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                            .content(user)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Username already Exist.Try different Username",response.getMessage());
        }

        @Test
        @DisplayName("Signup users with same email failure")
        void signUpUserSameEmail() throws Exception {

            String user = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/userObject4.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                            .content(user)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Email already Exist.Try different Email",response.getMessage());
        }

        @Test
        @DisplayName("Signup users with same mobile number failure")
        void signUpUserSameMobileNumber() throws Exception {

            String user = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/userObject5.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                            .content(user)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Error: Mobile Number already Exist.Try different Mobile Number",response.getMessage());
        }
    }

    @Nested
    @Sql(statements = "insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (1000,\"Sd099\",\"Son\",\"Doe\",\"sondoe@gmail.com\",\"9876543210\",\"Time square, NY, USA\",\"$2a$10$hph6xo16D8ED1ZOHcMo7wuvrCH5ebxiVyTtPFGsHaZmIgAHWa55C2\")\n", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM user", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Sign in user test")
    public class signInUser{
        @Test
        @DisplayName("SignIn Users success")
        void signInUserSuccess() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject1.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                            .content(signInRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            SignInResponseBody response = objectMapper.readValue(resultAsString,SignInResponseBody.class);

            assertNotNull(result);
            assertNotNull(response.getJwt());
        }

        @Test
        @DisplayName("SignIn Users failure")
        void signInUserFailure() throws Exception {
            String signInRequestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/jsonObject/userObjects/signInRequestObject4.json")));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                            .content(signInRequestBody)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

            String resultAsString = result.getResponse().getContentAsString();
            MessageResponse response = objectMapper.readValue(resultAsString,MessageResponse.class);

            assertNotNull(result);
            assertEquals("Invalid credentials!",response.getMessage());
        }
    }
}

