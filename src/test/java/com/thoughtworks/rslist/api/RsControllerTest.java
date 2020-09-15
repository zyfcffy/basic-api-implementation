package com.thoughtworks.rslist.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void should_get_rs_list() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")));

    }

    @Test
    void should_get_one_event() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("第一条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")));
        mockMvc.perform(get("/rs/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("第二条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")));
        mockMvc.perform(get("/rs/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("第三条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")));
    }



}