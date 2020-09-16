package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.dto.RsEvent;
import com.thoughtworks.rslist.dto.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RsControllerTest {
    @Autowired
    MockMvc mockMvc;

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
                .andExpect(jsonPath("$[2].keyWord", is("无分类")));

    }

    @Test
    void should_get_one_event() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(jsonPath("$.keyWord", is("无分类")));
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
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        MvcResult mvcResult = mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
        String index = mvcResult.getResponse().getHeader("index");
        assertEquals(index,"4");
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].eventName", is("猪肉涨价了")))
                .andExpect(jsonPath("$[3].keyWord", is("经济")));
    }

    @Test
    void should_edit_one_rs_event() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无分类")));
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(put("/rs/event/1")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName", is("猪肉涨价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")));
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
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void event_name_should_not_empty() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void event_key_word_should_not_empty() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "", user);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void user_exist_when_add_rs_event() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济", user);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void user_not_exist_when_add_rs_event() throws Exception {
        User user = new User("Mary", "female", 20, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济", user);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/user/list"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].userName", is("Mary")))
                .andExpect(jsonPath("$[1].gender", is("female")));
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].eventName", is("猪肉涨价了")))
                .andExpect(jsonPath("$[3].keyWord", is("经济")))
                .andExpect(jsonPath("$[3].user.userName", is("Mary")))
                .andExpect(jsonPath("$[3].user.gender", is("female")));
    }
}