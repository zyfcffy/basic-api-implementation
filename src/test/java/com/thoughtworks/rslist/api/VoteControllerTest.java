package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.dto.Vote;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class VoteControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    VoteRepository voteRepository;

    UserEntity userEntity;
    RsEventEntity rsEventEntity;
    VoteEntity voteEntity;

    private void setData() {
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
    void clear() {
        voteRepository.deleteAll();
        rsEventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void should_vote_successful_when_remain_votes_more_than_vote_num() throws Exception {
        int voteNum = 5;
        int index = 0;
        Vote vote = new Vote(rsEventEntity.getId(), userEntity.getId(), null, voteNum);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(vote);
        mockMvc.perform(post("/rs/vote/{rsEventId}", rsEventEntity.getId())
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<VoteEntity> votes = voteRepository.findAll();
        assertEquals(voteNum, votes.get(index).getVoteNum());
        assertEquals(rsEventEntity.getId(), votes.get(index).getRsEventEntity().getId());
        assertEquals(userEntity.getId(), votes.get(index).getUserEntity().getId());
    }

    @Test
    void should_return_bad_request_when_remain_vote_less_than_votes() throws Exception {
        Vote vote = new Vote(rsEventEntity.getId(), userEntity.getId(), null, 20);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(vote);
        mockMvc.perform(post("/rs/vote/{rsEventId}", rsEventEntity.getId())
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_vote_user_id_is_not_exist() throws Exception {
        Vote vote = new Vote(rsEventEntity.getId(), 10, null, 5);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(vote);
        mockMvc.perform(post("/rs/vote/{rsEventId}", rsEventEntity.getId())
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_vote_rs_event_id_not_exist() throws Exception {
        Vote vote = new Vote(10, userEntity.getId(), null, 5);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(vote);
        mockMvc.perform(post("/rs/vote/{rsEventId}", 10)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}