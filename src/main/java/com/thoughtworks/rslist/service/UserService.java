package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.dto.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;

    public List<User> getUsers(){
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(item -> User.builder()
                .userName(item.getUserName())
                .age(item.getAge())
                .email(item.getEmail())
                .gender(item.getGender())
                .phone(item.getPhone())
                .voteNum(item.getVoteNum()).build()).collect(Collectors.toList());
    }

    public List<UserEntity> register(User user){
        UserEntity userEntity = UserEntity.builder()
                .userName(user.getUserName())
                .age(user.getAge())
                .email(user.getEmail())
                .gender(user.getGender())
                .phone(user.getPhone())
                .build();
        userRepository.save(userEntity);
        return userRepository.findAll();
    }

    public Optional<UserEntity> getUserById(Integer id){
        return userRepository.findById(id);
    }

    public void deleteUserById(Integer id){
        userRepository.deleteById(id);
    }
}
