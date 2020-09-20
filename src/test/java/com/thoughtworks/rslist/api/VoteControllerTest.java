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


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                .eventName("eventName1")
                .keyWord("keyWord1")
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
        assertEquals(rsEventEntity.getId(), votes.get(index).getRsEventId());
        assertEquals(userEntity.getId(), votes.get(index).getUserId());
    }

    @Test
    void should_return_bad_request_when_remain_vote_less_than_votes() throws Exception {
        Vote vote = new Vote(rsEventEntity.getId(), rsEventEntity.getId(), null, 20);
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
        Vote vote = new Vote(10, rsEventEntity.getId(), null, 5);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(vote);
        mockMvc.perform(post("/rs/vote/{rsEventId}", 10)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_get_votes_in_page_by_user_id_and_rs_event_id() throws Exception {
        setVotesData();
        mockMvc.perform(get("/votes")
                .param("userId", String.valueOf(userEntity.getId()))
                .param("rsEventId", String.valueOf(rsEventEntity.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].userId", is(userEntity.getId())))
                .andExpect(jsonPath("$[0].rsEventId", is(rsEventEntity.getId())))
                .andExpect(jsonPath("$[0].voteNum", is(1)));
        mockMvc.perform(get("/votes")
                .param("userId", String.valueOf(userEntity.getId()))
                .param("rsEventId", String.valueOf(rsEventEntity.getId()))
                .param("pageIndex", "2"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].voteNum", is(2)));
    }

    @Test
    void should_return_bad_request_when_start_time_more_than_end_time() throws Exception {
        setVotesData();
        LocalDateTime startTime = LocalDateTime.of(2020, 9, 19, 8, 20);
        LocalDateTime endTime = LocalDateTime.of(2020, 9, 19, 8, 15);
        mockMvc.perform(get("/votes/time")
                .param("startTime", String.valueOf(startTime))
                .param("endTime", String.valueOf(endTime)))
                .andExpect(status().isBadRequest());
    }

    public void setVotesData() {
        voteEntity = VoteEntity.builder()
                .voteTime(LocalDateTime.of(2020, 9, 19, 8, 10))
                .userId(userEntity.getId())
                .rsEventId(rsEventEntity.getId())
                .voteNum(1)
                .build();
        voteRepository.save(voteEntity);
        voteEntity = VoteEntity.builder()
                .voteTime(LocalDateTime.of(2020, 9, 19, 8, 12))
                .userId(userEntity.getId())
                .rsEventId(rsEventEntity.getId())
                .voteNum(1)
                .build();
        voteRepository.save(voteEntity);
        voteEntity = VoteEntity.builder()
                .voteTime(LocalDateTime.of(2020, 9, 19, 8, 15))
                .userId(userEntity.getId())
                .rsEventId(rsEventEntity.getId())
                .voteNum(1)
                .build();
        voteRepository.save(voteEntity);
        voteEntity = VoteEntity.builder()
                .voteTime(LocalDateTime.of(2020, 9, 19, 8, 16))
                .userId(userEntity.getId())
                .rsEventId(rsEventEntity.getId())
                .voteNum(1)
                .build();
        voteRepository.save(voteEntity);
        voteEntity = VoteEntity.builder()
                .voteTime(LocalDateTime.of(2020, 9, 19, 8, 17))
                .userId(userEntity.getId())
                .rsEventId(rsEventEntity.getId())
                .voteNum(1)
                .build();
        voteRepository.save(voteEntity);
        voteEntity = VoteEntity.builder()
                .voteTime(LocalDateTime.of(2020, 9, 19, 8, 19))
                .userId(userEntity.getId())
                .rsEventId(rsEventEntity.getId())
                .voteNum(2)
                .build();
        voteRepository.save(voteEntity);
        voteEntity = VoteEntity.builder()
                .voteTime(LocalDateTime.of(2020, 9, 19, 8, 21))
                .userId(userEntity.getId())
                .rsEventId(rsEventEntity.getId())
                .voteNum(1)
                .build();
        voteRepository.save(voteEntity);
        voteEntity = VoteEntity.builder()
                .voteTime(LocalDateTime.of(2020, 9, 19, 8, 23))
                .userId(userEntity.getId())
                .rsEventId(rsEventEntity.getId())
                .voteNum(1)
                .build();
        voteRepository.save(voteEntity);
    }
}