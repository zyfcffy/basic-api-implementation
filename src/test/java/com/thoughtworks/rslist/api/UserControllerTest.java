package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.dto.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void user_is_ok() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("index", "2"));
    }

    @Test
    void user_name_should_not_empty() throws Exception {
        User user = new User("", "female", 20, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_name_length_should_not_more_than_8() throws Exception {
        User user = new User("123456789", "female", 20, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_age_should_not_empty() throws Exception {
        User user = new User("xiaowang", "female", null, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_age_should_under_100() throws Exception {
        User user = new User("xiaowang", "female", 101, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_age_should_above_18() throws Exception {
        User user = new User("xiaowang", "female", 1, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_email_should_be_valid() throws Exception {
        User user = new User("xiaowang", "female", 20, "1", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_phone_number_should_not_empty() throws Exception {
        User user = new User("xiaowang", "female", 20, "a@thoughtworks.com", "");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_phone_number_should_be_valid() throws Exception {
        User user = new User("xiaowang", "female", 20, "a@thoughtworks.com", "155");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_formatting_users() throws Exception {
        mockMvc.perform(get("/user/users"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user_name", is("xiaowang")))
                .andExpect(jsonPath("$[0].user_gender", is("female")))
                .andExpect(jsonPath("$[0].user_age", is(19)))
                .andExpect(jsonPath("$[0].user_email", is("a@thoughtworks.com")))
                .andExpect(jsonPath("$[0].user_phone", is("18888888888")));
    }
}