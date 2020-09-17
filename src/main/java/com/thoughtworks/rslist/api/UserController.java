package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.User;
import com.thoughtworks.rslist.exceptions.CommentError;
import com.thoughtworks.rslist.exceptions.InvalidUserException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    public static List<User> userList = initUserList();

    public static List<User> initUserList() {
        List<User> temptUserList = new ArrayList<>();
        temptUserList.add(new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888"));
        return temptUserList;
    }

    @PostMapping("/user/register")
    public ResponseEntity<Object> register(@Valid @RequestBody User user, BindingResult bindingResult) throws InvalidUserException {
        if (bindingResult.getAllErrors().size() != 0){
            throw new InvalidUserException("invalid user");
        }
            userList.add(user);
        return ResponseEntity.created(null).header("index", String.valueOf(userList.size())).build();
    }

    @GetMapping("/user/users")
    public ResponseEntity<List<User>> getUserList() {
        return ResponseEntity.ok(userList);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<CommentError> handleInvalidUserException(Exception ex){
        CommentError commentError = new CommentError();
        commentError.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(commentError);
    }
}
