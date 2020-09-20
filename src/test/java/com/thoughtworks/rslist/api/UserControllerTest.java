package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.dto.RsEvent;
import com.thoughtworks.rslist.dto.User;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RsEventRepository rsEventRepository;

    @BeforeEach
    void setUp() {
        rsEventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void should_register_user() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<UserEntity> users = userRepository.findAll();
        int index = users.size() - 1;
        assertEquals("xiaowang", users.get(index).getUserName());
        assertEquals("female", users.get(index).getGender());
        assertEquals(19, users.get(index).getAge());
        assertEquals("a@thoughtworks.com", users.get(index).getEmail());
        assertEquals("18888888888", users.get(index).getPhone());
    }

    @Test
    void user_name_should_not_empty() throws Exception {
        User user = new User("", "female", 20, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid user")));
    }

    @Test
    void user_name_length_should_not_more_than_8() throws Exception {
        User user = new User("123456789", "female", 20, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid user")));
    }

    @Test
    void user_age_should_not_empty() throws Exception {
        User user = new User("xiaowang", "female", null, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid user")));
    }

    @Test
    void user_age_should_under_100() throws Exception {
        User user = new User("xiaowang", "female", 101, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid user")));
    }

    @Test
    void user_age_should_above_18() throws Exception {
        User user = new User("xiaowang", "female", 1, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid user")));
    }

    @Test
    void user_email_should_be_valid() throws Exception {
        User user = new User("xiaowang", "female", 20, "1", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid user")));
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
        UserEntity userEntity = UserEntity.builder()
                .userName("user01")
                .age(19)
                .email("12@a.com")
                .gender("male")
                .phone("15527765431")
                .build();
        userRepository.save(userEntity);
        mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user_name", is("user01")))
                .andExpect(jsonPath("$[0].user_gender", is("male")))
                .andExpect(jsonPath("$[0].user_age", is(19)))
                .andExpect(jsonPath("$[0].user_email", is("12@a.com")))
                .andExpect(jsonPath("$[0].user_phone", is("15527765431")));
    }

    @Test
    void should_return_400_and_error_message_when_InvalidUserException() throws Exception {
        User user = new User("", "female", 20, "a@twu.com", "15525557689");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/register")
                .content(userJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid user")));
    }

    @Test
    void should_get_user_by_id() throws Exception {
        UserEntity userEntity = UserEntity.builder()
                .userName("user01")
                .age(19)
                .email("12@a.com")
                .gender("male")
                .phone("15527765431")
                .build();
        userRepository.save(userEntity);
        mockMvc.perform(get("/user/{id}", userEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userEntity.getId())))
                .andExpect(jsonPath("$.userName", is("user01")))
                .andExpect(jsonPath("$.age", is(19)))
                .andExpect(jsonPath("$.gender", is("male")))
                .andExpect(jsonPath("$.phone", is("15527765431")));
    }

    @Test
    void should_delete_user_by_id() throws Exception {
        UserEntity userEntity = UserEntity.builder()
                .userName("user01")
                .age(19)
                .email("12@a.com")
                .gender("male")
                .phone("15527765431")
                .voteNum(10)
                .build();
        userRepository.save(userEntity);
        mockMvc.perform(delete("/user/{id}", userEntity.getId()))
                .andExpect(status().isNoContent());
        List<UserEntity> userEntities = userRepository.findAll();
        assertEquals(0, userEntities.size());
    }

    @Test
    void should_delete_user() throws Exception {
        UserEntity userEntity = UserEntity.builder()
                .userName("user01")
                .age(19)
                .email("12@a.com")
                .gender("male")
                .phone("15527765431")
                .voteNum(10)
                .build();
        userRepository.save(userEntity);
        RsEventEntity rsEventEntity = RsEventEntity.builder()
                .eventName("eventName01")
                .keyWord("keyWord")
                .userEntity(userEntity)
                .build();
        rsEventRepository.save(rsEventEntity);
        mockMvc.perform(delete("/user/{id}", userEntity.getId()))
                .andExpect(status().isNoContent());
        List<UserEntity> userEntities = userRepository.findAll();
        List<RsEventEntity> rsEventEntities = rsEventRepository.findAll();
        assertEquals(0, userEntities.size());
        assertEquals(0, rsEventEntities.size());
    }
}