package com.thoughtworks.rslist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vote")
public class VoteEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private LocalDateTime voteTime;
    private int voteNum;
    private int rsEventId;
    private int userId;
}
