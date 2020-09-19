package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.dto.RsEvent;
import com.thoughtworks.rslist.dto.User;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;

    UserEntity userEntity;
    RsEventEntity rsEventEntity;

    private void setData(){
        userEntity = UserEntity.builder()
                .userName("user0")
                .age(20)
                .gender("male")
                .email("a@123.com")
                .phone("17725563479")
                .voteNum(10)
                .build();
        userRepository.save(userEntity);
        rsEventEntity = RsEventEntity.builder()
                .eventName("eventName01")
                .keyWord("keyWord")
                .userEntity(userEntity)
                .build();
        rsEventRepository.save(rsEventEntity);
    }

    @BeforeEach
    void setUp() {
        setData();
    }

    @AfterEach
    void clear(){
        rsEventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void should_get_rs_list() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无分类")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无分类")))
                .andExpect(jsonPath("$[2].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord", is("无分类")))
                .andExpect(jsonPath("$[0]", not(hasKey("user"))));
    }

    @Test
    void should_get_one_event() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(jsonPath("$.keyWord", is("无分类")))
                .andExpect(jsonPath("$", not(hasKey("user"))));
    }

    @Test
    void should_get_one_event_by_id() throws Exception {
        mockMvc.perform(get("/rs/{id}",rsEventEntity.getId()))
                .andExpect(jsonPath("$.eventName", is("eventName01")))
                .andExpect(jsonPath("$.user.user_name",is("user01")));
    }

    @Test
    void should_get_rs_event_by_range() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无分类")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无分类")))
                .andExpect(jsonPath("$[2].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord", is("无分类")));
    }

    @Test
    void should_add_rs_event() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",0, user,0);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("index", "4"));
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].eventName", is("猪肉涨价了")))
                .andExpect(jsonPath("$[3].keyWord", is("经济")));
    }

    @Test
    void should_edit_one_rs_event_when_userId_equals_reEvent_userId() throws Exception {
        mockMvc.perform(get("/rs/{id}",rsEventEntity.getId()))
                .andExpect(status().isOk());
        String json = "{\"eventName\":\"猪肉涨价了\",\"keyWord\":\"经济\",\"userId\":"+userEntity.getId()+"}";
        mockMvc.perform(put("/rs/event/{rsEventId}",rsEventEntity.getId())
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<RsEventEntity> rsEvents = rsEventRepository.findAll();
        assertEquals("猪肉涨价了",rsEvents.get(0).getEventName());
        assertEquals(userEntity.getId(),rsEvents.get(0).getUserEntity().getId());
    }

    @Test
    void should_delete_one_rs_event() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无分类")));
        mockMvc.perform(delete("/rs/event/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无分类")));
    }

    @Test
    void add_event_user_should_be_valid() throws Exception {
        User user = new User("", "female", 1, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",0,user ,0);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void event_name_should_not_empty() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",0, user,0);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void event_key_word_should_not_empty() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",0, user,0);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_exist_when_add_rs_event() throws Exception {
        String json = "{\"eventName\":\"猪肉涨价了\",\"keyWord\":\"经济\",\"userId\":"+userEntity.getId()+"}";
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<RsEventEntity> rsEvents = rsEventRepository.findAll();
        assertEquals(2,rsEvents.size());
        assertEquals("猪肉涨价了",rsEvents.get(0).getEventName());
        assertEquals(userEntity.getId(),rsEvents.get(0).getUserEntity().getId());
    }

    @Test
    void user_not_exist_when_add_rs_event() throws Exception {
        User user = new User("Mary", "female", 20, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",0, user,0);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/user/users"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].user_name", is("Mary")))
                .andExpect(jsonPath("$[1].user_gender", is("female")));
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].eventName", is("猪肉涨价了")))
                .andExpect(jsonPath("$[3].keyWord", is("经济")));
    }

    @Test
    void should_return_400_and_error_message_when_IndexOutOfBoundsException() throws Exception {
        mockMvc.perform(get("/rs/list?start=-1&end=5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
    }

    @Test
    void should_return_400_and_error_message_when_InvalidIndexException() throws Exception {
        mockMvc.perform(get("/rs/40"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid index")));
    }

    @Test
    void should_return_400_and_error_message_when_InvalidRsEventException() throws Exception {
        User user = new User("", "female", 1, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",0, user,0);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid param")));
    }

    @Test
    void should_add_rs_event_when_user_not_exit() throws Exception {
        String json = "{\"eventName\":\"猪肉涨价了\",\"keyWord\":\"经济\",\"userId\":1}";
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        List<RsEventEntity> rsEvents = rsEventRepository.findAll();
        assertEquals(0,rsEvents.size());
    }
}