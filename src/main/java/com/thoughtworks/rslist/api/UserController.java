package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.exceptions.CommentError;
import com.thoughtworks.rslist.exceptions.InvalidUserException;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    public static List<User> userList = initUserList();

    public final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static List<User> initUserList() {
        List<User> temptUserList = new ArrayList<>();
        temptUserList.add(new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888"));
        return temptUserList;
    }

    @PostMapping("/user/register")
    public ResponseEntity<Object> register(@Valid @RequestBody User user, BindingResult bindingResult) throws InvalidUserException {
        if (bindingResult.getAllErrors().size() != 0) {
            throw new InvalidUserException("invalid user");
        }
        UserEntity userEntity = UserEntity.builder()
                .userName(user.getUserName())
                .age(user.getAge())
                .email(user.getEmail())
                .gender(user.getGender())
                .phone(user.getPhone())
                .build();
        userRepository.save(userEntity);
        userList.add(user);
        return ResponseEntity.created(null).header("index", String.valueOf(userList.size())).build();
    }

    @GetMapping("/user/users")
    public ResponseEntity<List<User>> getUserList() {
        return ResponseEntity.ok(userList);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Integer id){
        Optional<UserEntity> userOptional = userRepository.findById(id);
        return ResponseEntity.ok(userOptional.get());
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Integer id){
        userRepository.deleteById(id);
        return ResponseEntity.status(200).build();
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<CommentError> handleInvalidUserException(Exception ex) {
        CommentError commentError = new CommentError();
        commentError.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(commentError);
    }
}
