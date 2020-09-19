package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.thoughtworks.rslist.dto.RsEvent;
import com.thoughtworks.rslist.dto.User;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.exceptions.CommentError;
import com.thoughtworks.rslist.exceptions.InvalidIndexException;
import com.thoughtworks.rslist.exceptions.InvalidRsEventException;
import com.thoughtworks.rslist.exceptions.RequestNotValidException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.thoughtworks.rslist.api.UserController.userList;

@RestController
public class RsController {
    private final UserRepository userRepository;
    private final RsEventRepository rsEventRepository;

    public RsController(UserRepository userRepository, RsEventRepository rsEventRepository) {
        this.rsEventRepository = rsEventRepository;
        this.userRepository = userRepository;
    }


    private List<RsEvent> rsList = new ArrayList<>();
    ;

    @JsonView(RsEvent.CommonView.class)
    @GetMapping("/rs/list")
    public ResponseEntity<List<RsEvent>> getAllRsEvent(@RequestParam(required = false) Integer start,
                                                       @RequestParam(required = false) Integer end) {
        List<RsEventEntity> rsEvents = rsEventRepository.findAll();
        if (start == null || end == null) {
            return ResponseEntity.ok(
                    rsEvents.stream()
                            .map(item ->
                                    RsEvent.builder()
                                            .eventName(item.getEventName())
                                            .keyWord(item.getKeyWord())
                                            .voteNum(item.getVoteNum())
                                            .userId(item.getUserEntity().getId())
                                            .build())
                            .collect(Collectors.toList()));
        }
        if (start < 0 || start > rsEvents.size() || end < start || end > rsEvents.size()) {
            throw new IndexOutOfBoundsException("invalid request param");
        }
        return ResponseEntity.ok(
                rsEvents.stream()
                        .map(item ->
                                RsEvent.builder()
                                        .eventName(item.getEventName())
                                        .keyWord(item.getKeyWord())
                                        .voteNum(item.getVoteNum())
                                        .userId(item.getUserEntity().getId())
                                        .build())
                        .collect(Collectors.toList()).subList(start - 1, end));
    }

    @GetMapping("rs/{id}")
    public ResponseEntity<RsEvent> getOneRsEventById(@PathVariable Integer id) throws RequestNotValidException {
        Optional<RsEventEntity> result = rsEventRepository.findById(id);
        if (!result.isPresent()) {
            throw new RequestNotValidException("invalid id");
        }
        RsEventEntity rsEvent = result.get();
        UserEntity userEntity = rsEvent.getUserEntity();
        return ResponseEntity.ok(RsEvent.builder()
                .eventName(rsEvent.getEventName())
                .keyWord(rsEvent.getKeyWord())
                .user(new User(
                        userEntity.getUserName(),
                        userEntity.getGender(),
                        userEntity.getAge(),
                        userEntity.getEmail(),
                        userEntity.getPhone()))
                .voteNum(rsEvent.getVoteNum())
                .userId(rsEvent.getUserEntity().getId())
                .build());
    }

    @PostMapping("/rs/event")
    public ResponseEntity<Object> addRsEvent(@Valid @RequestBody RsEvent rsEvent) {
        RsEventEntity rsEventEntity = RsEventEntity.builder()
                .eventName(rsEvent.getEventName())
                .keyWord(rsEvent.getKeyWord())
                .userEntity(UserEntity.builder()
                        .id(rsEvent.getUserId())
                        .build())
                .build();
        rsEventRepository.save(rsEventEntity);
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/rs/event/{rsEventId}")
    public ResponseEntity<List<RsEvent>> editOneRsEvent(@PathVariable Integer rsEventId,
                                                        @RequestBody RsEvent rsEvent) {
        RsEventEntity rsEventEntity = rsEventRepository.findById(rsEventId).get();
        if (!rsEventEntity.getUserEntity().getId().equals(rsEvent.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        if (rsEvent.getEventName() != null) {
            rsEventEntity.setId(rsEventId);
            rsEventEntity.setEventName(rsEvent.getEventName());
            rsEventRepository.save(rsEventEntity);
        }
        if (rsEvent.getKeyWord() != null) {
            rsEventEntity.setId(rsEventId);
            rsEventEntity.setKeyWord(rsEvent.getKeyWord());
            rsEventRepository.save(rsEventEntity);
        }
        return ResponseEntity.created(null).build();
    }

    @DeleteMapping("/rs/event/{index}")
    private ResponseEntity<List<RsEvent>> deleteEvent(@PathVariable Integer index) {
        rsList.remove(index - 1);
        return ResponseEntity.ok(rsList);
    }

    @ExceptionHandler(InvalidRsEventException.class)
    public ResponseEntity<CommentError> handleInvalidRsEventException(Exception ex) {
        CommentError commentError = new CommentError();
        commentError.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(commentError);
    }
}
