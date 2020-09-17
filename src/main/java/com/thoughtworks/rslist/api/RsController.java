package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.thoughtworks.rslist.dto.RsEvent;
import com.thoughtworks.rslist.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.rslist.api.UserController.userList;

@RestController
public class RsController {
    private List<RsEvent> rsList = initRsList();

    private List<RsEvent> initRsList() {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        List<RsEvent> tempRsList = new ArrayList<>();
        tempRsList.add(new RsEvent("第一条事件", "无分类", user));
        tempRsList.add(new RsEvent("第二条事件", "无分类", user));
        tempRsList.add(new RsEvent("第三条事件", "无分类", user));
        return tempRsList;
    }

    @JsonView(RsEvent.CommonView.class)
    @GetMapping("/rs/list")
    public ResponseEntity<List<RsEvent>> getAllRsEvent(@RequestParam(required = false) Integer start,
                                                       @RequestParam(required = false) Integer end) {
        if (start == null || end == null) {
            return ResponseEntity.ok(rsList);
        }
        return ResponseEntity.ok(rsList.subList(start - 1, end));
    }

    @JsonView(RsEvent.CommonView.class)
    @GetMapping("/rs/{index}")
    public ResponseEntity<RsEvent> getOneRsEvent(@PathVariable int index) {
        return ResponseEntity.ok(rsList.get(index - 1));
    }

    @PostMapping("/rs/event")
    public ResponseEntity<Object> addRsEvent(@Valid @RequestBody RsEvent rsEvent) {
        if (!userList.contains(rsEvent.getUser())) {
            userList.add(rsEvent.getUser());
        }
        rsList.add(rsEvent);
        return ResponseEntity.created(null).header("index",String.valueOf(rsList.size())).build();
    }

    @PutMapping("/rs/event/{index}")
    public ResponseEntity<List<RsEvent>> editOneRsEvent(@PathVariable Integer index,
                                        @RequestBody RsEvent reEvent) {
        RsEvent editRsEvent = rsList.get(index - 1);
        if (reEvent.getEventName() != null) {
            editRsEvent.setEventName(reEvent.getEventName());
        }
        if (reEvent.getKeyWord() != null) {
            editRsEvent.setKeyWord(reEvent.getKeyWord());
        }
        return ResponseEntity.ok(rsList);
    }

    @DeleteMapping("/rs/event/{index}")
    private ResponseEntity<List<RsEvent>> deleteEvent(@PathVariable Integer index) {
        rsList.remove(index - 1);
        return ResponseEntity.ok(rsList);
    }
}
