package com.thoughtworks.rslist.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class User {

    public User(String name, String gender, Integer age, String email, String phone) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.phone = phone;
    }

    @NotEmpty
    private String name;
    private String gender;
    private Integer age;
    private String email;
    private String phone;
}
