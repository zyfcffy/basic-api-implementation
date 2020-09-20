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
import com.thoughtworks.rslist.service.RsEventService;
import com.thoughtworks.rslist.service.UserService;
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

@RestController
public class RsController {
    @Autowired
    RsEventService rsEventService;

    @Autowired
    UserService  userService;

    @JsonView(RsEvent.CommonView.class)
    @GetMapping("/rs/list")
    public ResponseEntity<List<RsEvent>> getAllRsEvent(@RequestParam(required = false) Integer start,
                                                       @RequestParam(required = false) Integer end) {
        List<RsEvent> rsEvents = rsEventService.getAllRsEvent();
        if (start == null || end == null) {
            return ResponseEntity.ok(rsEvents);
        }
        if (start < 0 || start > rsEvents.size() || end < start || end > rsEvents.size()) {
            throw new IndexOutOfBoundsException("invalid request param");
        }
        return ResponseEntity.ok(rsEvents.subList(start - 1, end));
    }

    @GetMapping("rs/{id}")
    public ResponseEntity<RsEvent> getOneRsEventById(@PathVariable Integer id) throws RequestNotValidException {
        Optional<RsEventEntity> result = rsEventService.getOneRsEventById(id);
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

    @GetMapping("/rs/byIndex/{index}")
    public ResponseEntity<RsEvent> getRsEvent(@PathVariable int index) throws RequestNotValidException {
        List<RsEvent> rsEvents = rsEventService.getAllRsEvent();
        if (index < 1 || index > rsEvents.size()) {
            throw new RequestNotValidException("invalid index");
        }
        return ResponseEntity.ok(rsEvents.get(index - 1));
    }

    @PostMapping("/rs/event")
    public ResponseEntity<Object> addRsEvent(@Valid @RequestBody RsEvent rsEvent,
                                             BindingResult bindingResult) throws InvalidRsEventException {
        if (bindingResult.getAllErrors().size() != 0) {
            throw new InvalidRsEventException("invalid param");
        }
        Optional<UserEntity> user = userService.getUserById(rsEvent.getUserId());
        if(!user.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        rsEventService.saveRsEvent(rsEvent);
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/rs/event/{rsEventId}")
    public ResponseEntity<List<RsEvent>> editOneRsEvent(@PathVariable Integer rsEventId,
                                                        @RequestBody RsEvent rsEvent) {
        RsEventEntity rsEventEntity = rsEventService.getOneRsEventById(rsEventId).get();
        if (!rsEventEntity.getUserEntity().getId().equals(rsEvent.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        rsEventService.updateRsEvent(rsEventEntity,rsEvent,rsEventId);
        return ResponseEntity.created(null).build();
    }

    @DeleteMapping("/rs/event/{deleteId}")
    private ResponseEntity<Object> deleteEvent(@PathVariable Integer deleteId) {
        rsEventService.deleteById(deleteId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(InvalidRsEventException.class)
    public ResponseEntity<CommentError> handleInvalidRsEventException(Exception ex) {
        CommentError commentError = new CommentError();
        commentError.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(commentError);
    }
}
