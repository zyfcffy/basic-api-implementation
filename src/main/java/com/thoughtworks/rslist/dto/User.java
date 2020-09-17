package com.thoughtworks.rslist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("user_gender")
    private String gender;
    @NotNull
    @Max(100)
    @Min(18)
    @JsonProperty("user_age")
    private Integer age;
    @Email
    @JsonProperty("user_email")
    private String email;
    @Pattern(regexp = "^1\\d{10}$")
    @JsonProperty("user_phone")
    private String phone;
}
