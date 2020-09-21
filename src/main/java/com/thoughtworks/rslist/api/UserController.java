package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.exceptions.CommentError;
import com.thoughtworks.rslist.exceptions.InvalidUserException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<Object> register(@Valid @RequestBody User user, BindingResult bindingResult) throws InvalidUserException {
        if (bindingResult.getAllErrors().size() != 0) {
            throw new InvalidUserException("invalid user");
        }
        List<UserEntity> users = userService.register(user);
        return ResponseEntity.created(null).header("index", String.valueOf(users.size())).build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Integer id) {
        Optional<UserEntity> userOptional = userService.getUserById(id);
        return ResponseEntity.ok(userOptional.get());
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Integer id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<CommentError> handleInvalidUserException(Exception ex) {
        CommentError commentError = new CommentError();
        commentError.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(commentError);
    }
}
