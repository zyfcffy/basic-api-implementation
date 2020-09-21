package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.Vote;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import com.thoughtworks.rslist.service.RsEventService;
import com.thoughtworks.rslist.service.UserService;
import com.thoughtworks.rslist.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class VoteController {
    @Autowired
    VoteService voteService;
    @Autowired
    UserService userService;
    @Autowired
    RsEventService rsEventService;
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
        Optional<RsEventEntity> rsEventEntity = rsEventService.getRsEventById(rsEventId);
        Optional<UserEntity> userEntity = userService.getUserById(vote.getUserId());
        if (!rsEventEntity.isPresent()
                || !userEntity.isPresent()
                || vote.getVoteNum() > userEntity.get().getVoteNum()) {
            return ResponseEntity.badRequest().build();
        }
        voteService.save(vote, rsEventId, userEntity.get().getId());
        UserEntity user = userEntity.get();
        user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
        userService.updateUser(user);
        RsEventEntity rsEvent = rsEventEntity.get();
        rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
        rsEventService.updateRsEvent(rsEvent);
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/votes")
    public ResponseEntity<List<Vote>> getVotes(@RequestParam int userId,
                                               @RequestParam int rsEventId,
                                               @RequestParam(defaultValue = "1") int pageIndex) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        List<Vote> votes = voteService.getVotes(userId, rsEventId, pageable);
        return ResponseEntity.ok(votes);
    }

    @GetMapping("/votes/time")
    public ResponseEntity<List<Vote>> getVotesBetween(@RequestParam String startTime,
                                                      @RequestParam String endTime) {
        DateTimeFormatter datsFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, datsFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime, datsFormatter);
        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().build();
        }
        List<Vote> votes = voteService.getVotesBetween(start, end);
        return ResponseEntity.ok(votes);
    }
}
