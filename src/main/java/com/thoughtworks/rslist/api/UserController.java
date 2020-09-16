package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.User;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity register(@Valid @RequestBody User user) {
        userList.add(user);
        return ResponseEntity.created(null).header("index",String.valueOf(userList.size())).build();
    }

    @GetMapping("/user/list")
    public ResponseEntity<List<User>> getUserList(){
        return ResponseEntity.ok(userList);
    }
}
