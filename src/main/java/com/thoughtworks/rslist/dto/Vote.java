package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    private int rsEventId;
    private int userId;
    private LocalDateTime voteTime;
    private int voteNum;
}
