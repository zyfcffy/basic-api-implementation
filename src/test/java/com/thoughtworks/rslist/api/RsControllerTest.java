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
    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private final RsEventRepository rsEventRepository;

    public RsControllerTest(MockMvc mockMvc, UserRepository userRepository, RsEventRepository rsEventRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.rsEventRepository = rsEventRepository;
    }

    UserEntity userEntity;
    RsEventEntity rsEventEntity01;
    RsEventEntity rsEventEntity02;

    private void setData() {
        userEntity = UserEntity.builder()
                .userName("user1")
                .age(20)
                .gender("male")
                .email("a@123.com")
                .phone("17725563479")
                .voteNum(10)
                .build();
        userRepository.save(userEntity);
        rsEventEntity01 = RsEventEntity.builder()
                .eventName("eventName1")
                .keyWord("keyWord1")
                .userEntity(userEntity)
                .voteNum(0)
                .build();
        rsEventRepository.save(rsEventEntity01);
        rsEventEntity02 = RsEventEntity.builder()
                .eventName("eventName2")
                .keyWord("keyWord2")
                .userEntity(userEntity)
                .voteNum(0)
                .build();
        rsEventRepository.save(rsEventEntity02);
    }

    @BeforeEach
    void setUp() {
        setData();
    }

    @AfterEach
    void clear() {
        rsEventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void should_get_rs_list() throws Exception {
        mockMvc.perform(get("/rs/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("eventName1")))
                .andExpect(jsonPath("$[0].keyWord", is("keyWord1")))
                .andExpect(jsonPath("$[0].userId", is(userEntity.getId())))
                .andExpect(jsonPath("$[1].eventName", is("eventName2")))
                .andExpect(jsonPath("$[1].keyWord", is("keyWord2")))
                .andExpect(jsonPath("$[1].userId", is(userEntity.getId())));
    }

    @Test
    void should_get_rs_event_by_range() throws Exception {
        mockMvc.perform(get("/rs/events?start=1&end=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventName", is("eventName1")))
                .andExpect(jsonPath("$[0].keyWord", is("keyWord1")))
                .andExpect(jsonPath("$[1].eventName", is("eventName2")))
                .andExpect(jsonPath("$[1].keyWord", is("keyWord2")));
    }

    @Test
    void should_get_one_event_by_id() throws Exception {
        mockMvc.perform(get("/rs/event/{rsEventId}", rsEventEntity01.getId()))
                .andExpect(jsonPath("$.eventName", is("eventName1")))
                .andExpect(jsonPath("$.keyWord", is("keyWord1")))
                .andExpect(jsonPath("$.userId", is(userEntity.getId())))
                .andExpect(jsonPath("$.user.user_name", is("user1")));
    }

    @Test
    void should_add_rs_event() throws Exception {
        String json = "{\"eventName\":\"猪肉涨价了\",\"keyWord\":\"经济\",\"userId\":" + userEntity.getId() + "}";
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        int index = 2;
        int rsEventSize = 3;
        RsEventEntity rsEvent = rsEventRepository.findAll().get(index);
        assertEquals(rsEventSize, rsEventRepository.findAll().size());
        assertEquals("猪肉涨价了", rsEvent.getEventName());
        assertEquals("经济", rsEvent.getKeyWord());
        assertEquals(userEntity.getId(), rsEvent.getUserEntity().getId());
    }

    @Test
    void should_add_rs_event_when_user_not_exit() throws Exception {
        String json = "{\"eventName\":\"猪肉涨价了\",\"keyWord\":\"经济\",\"userId\":100}";
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        List<RsEventEntity> rsEvents = rsEventRepository.findAll();
        int oldRsEventSize = 2;
        assertEquals(oldRsEventSize, rsEvents.size());
    }

    @Test
    void event_name_should_not_empty() throws Exception {
        String json = "{\"eventName\":\"\",\"keyWord\":\"经济\",\"userId\":" + userEntity.getId() + "}";
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void event_key_word_should_not_empty() throws Exception {
        String json = "{\"eventName\":\"猪肉涨价了\",\"keyWord\":\"\",\"userId\":" + userEntity.getId() + "}";
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_edit_one_rs_event_when_userId_equals_reEvent_userId() throws Exception {
        String json = "{\"eventName\":\"猪肉涨价了\",\"keyWord\":\"经济\",\"userId\":" + userEntity.getId() + "}";
        mockMvc.perform(put("/rs/event/{rsEventId}", rsEventEntity01.getId())
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<RsEventEntity> rsEvents = rsEventRepository.findAll();
        assertEquals("猪肉涨价了", rsEvents.get(0).getEventName());
        assertEquals(userEntity.getId(), rsEvents.get(0).getUserEntity().getId());
    }

    @Test
    void should_delete_one_rs_event() throws Exception {
        mockMvc.perform(delete("/rs/event/{deleteId}", rsEventEntity01.getId()))
                .andExpect(status().isNoContent());
        int rsEventSize = 1;
        assertEquals(rsEventSize, rsEventRepository.findAll().size());
    }

    @Test
    void should_return_400_and_error_message_when_IndexOutOfBoundsException() throws Exception {
        mockMvc.perform(get("/rs/events?start=-1&end=5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid request param")));
    }

    @Test
    void should_return_400_and_error_message_when_InvalidIndexException() throws Exception {
        mockMvc.perform(get("/rs/byIndex/40"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid index")));
    }

    @Test
    void should_return_400_and_error_message_when_InvalidRsEventException() throws Exception {
        String json = "{\"eventName\":\"猪肉涨价了\",\"keyWord\":\"\",\"userId\":" + userEntity.getId() + "}";
        mockMvc.perform(post("/rs/event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid param")));
    }
}