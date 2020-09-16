package com.thoughtworks.rslist.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.Empty;

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
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "^1\\\\d{10}$")
    private String phone;
}
