package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.dto.Vote;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoteService {
    private final VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public List<Vote> getVotes(int userId, int rsEventId, Pageable pageable) {
        List<VoteEntity> votes = voteRepository.findAllByUserIdAndRsEventId(userId, rsEventId, pageable);
        return votes.stream().map(vote -> Vote.builder()
                .userId(vote.getUserId())
                .rsEventId(vote.getRsEventId())
                .voteNum(vote.getVoteNum())
                .voteTime(vote.getVoteTime())
                .build()).collect(Collectors.toList());
    }

    public void save(Vote vote, int rsEventId, int userId) {
        VoteEntity voteEntity =
                VoteEntity.builder()
                        .voteTime(vote.getVoteTime())
                        .voteNum(vote.getVoteNum())
                        .rsEventId(rsEventId)
                        .userId(userId)
                        .build();
        voteRepository.save(voteEntity);
    }

    public List<Vote> getVotesBetween(LocalDateTime startTime, LocalDateTime endTime) {
        List<VoteEntity> votes = voteRepository.findAllByVoteTimeBetween(startTime, endTime);
        return votes.stream().map(vote -> Vote.builder()
                .userId(vote.getUserId())
                .rsEventId(vote.getRsEventId())
                .voteNum(vote.getVoteNum())
                .voteTime(vote.getVoteTime())
                .build()).collect(Collectors.toList());
    }
}
