package com.thoughtworks.rslist.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
public class User {

    public User(String userName, String gender, Integer age, String email, String phone) {
        this.userName = userName;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.phone = phone;
    }

    @NotEmpty
    @Size(max = 8)
    private String userName;
    private String gender;
    @NotNull
    @Max(100)
    @Min(18)
    private Integer age;
    private String email;
    private String phone;
}
