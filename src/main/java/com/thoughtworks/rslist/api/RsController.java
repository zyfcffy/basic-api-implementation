package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.RsEvent;
import com.thoughtworks.rslist.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping("/rs/list")
    public List<RsEvent> getAllRsEvent(@RequestParam(required = false) Integer start,
                                       @RequestParam(required = false) Integer end) {
        if (start == null || end == null) {
            return rsList;
        }
        return rsList.subList(start - 1, end);
    }

    @GetMapping("/rs/{index}")
    public RsEvent getOneRsEvent(@PathVariable int index) {
        return rsList.get(index - 1);
    }

    @PostMapping("/rs/event")
    public void addRsEvent(@Valid @RequestBody RsEvent reEvent) {
        if (!userList.contains(reEvent.getUser())) {
            userList.add(reEvent.getUser());
        }
        rsList.add(reEvent);
    }

    @PutMapping("/rs/event/{index}")
    public List<RsEvent> editOneRsEvent(@PathVariable Integer index,
                                        @RequestBody RsEvent reEvent) {
        RsEvent editRsEvent = rsList.get(index - 1);
        if (reEvent.getEventName() != null) {
            editRsEvent.setEventName(reEvent.getEventName());
        }
        if (reEvent.getKeyWord() != null) {
            editRsEvent.setKeyWord(reEvent.getKeyWord());
        }
        return rsList;
    }

    @DeleteMapping("/rs/event/{index}")
    private List<RsEvent> deleteEvent(@PathVariable Integer index) {
        rsList.remove(index - 1);
        return rsList;
    }
}
