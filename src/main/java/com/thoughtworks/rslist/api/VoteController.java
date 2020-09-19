package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.Vote;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Object> vote(@PathVariable Integer rsEventId , @RequestBody Vote vote){
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
                        .rsEventEntity(rsEventEntity.get())
                        .userEntity(userEntity.get())
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
}
