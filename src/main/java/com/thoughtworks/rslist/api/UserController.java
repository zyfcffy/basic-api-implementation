package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    public static List<User> userList = initUserList();

    public static List<User> initUserList() {
        List<User> temptUserList = new ArrayList<>();
        temptUserList.add(new User("xiaowang","female",19,"a@thoughtworks.com","18888888888"));
        return temptUserList;
    }

    @PostMapping("/user/register")
    public void register(@Valid @RequestBody User user) {
        userList.add(user);
    }

    @GetMapping("/user/list")
    public List<User> getUserList(){
        return userList;
    }
}
