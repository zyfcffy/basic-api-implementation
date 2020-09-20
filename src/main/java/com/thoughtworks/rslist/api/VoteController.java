package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.Vote;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class VoteController {
    private final UserRepository userRepository;
    private final RsEventRepository rsEventRepository;
    private final VoteRepository voteRepository;


    public VoteController(UserRepository userRepository, RsEventRepository rsEventRepository, VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.rsEventRepository = rsEventRepository;
        this.voteRepository = voteRepository;
    }

    @PostMapping("rs/vote/{rsEventId}")
    public ResponseEntity<Object> vote(@PathVariable Integer rsEventId, @RequestBody Vote vote) {
        Optional<RsEventEntity> rsEventEntity = rsEventRepository.findById(rsEventId);
        Optional<UserEntity> userEntity = userRepository.findById(vote.getUserId());
        if (!rsEventEntity.isPresent()
                || !userEntity.isPresent()
                || vote.getVoteNum() > userEntity.get().getVoteNum()) {
            return ResponseEntity.badRequest().build();
        }
        VoteEntity voteEntity =
                VoteEntity.builder()
                        .voteTime(vote.getVoteTime())
                        .voteNum(vote.getVoteNum())
                        .rsEventId(rsEventId)
                        .userId(userEntity.get().getId())
                        .build();
        voteRepository.save(voteEntity);
        UserEntity user = userEntity.get();
        user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
        userRepository.save(user);
        RsEventEntity rsEvent = rsEventEntity.get();
        rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
        rsEventRepository.save(rsEvent);
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/votes")
    public ResponseEntity<List<Vote>> getVotes(@RequestParam int userId,
                                               @RequestParam int rsEventId,
                                               @RequestParam(defaultValue = "1") int pageIndex) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        List<VoteEntity> votes = voteRepository.findAllByUserIdAndRsEventId(userId, rsEventId, pageable);
        return ResponseEntity.ok(votes.stream().map(vote -> Vote.builder()
                .userId(vote.getUserId())
                .rsEventId(vote.getRsEventId())
                .voteNum(vote.getVoteNum())
                .voteTime(vote.getVoteTime())
                .build()).collect(Collectors.toList()));
    }

    @GetMapping("/votes/time")
    public ResponseEntity<List<Vote>> getVotesBetween(@RequestParam LocalDateTime startTime,
                                                      @RequestParam LocalDateTime endTime){
        if(startTime.isAfter(endTime)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
