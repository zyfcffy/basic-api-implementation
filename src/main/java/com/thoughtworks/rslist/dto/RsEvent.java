package com.thoughtworks.rslist.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsEvent {
    public interface CommonView {};
    public interface IgnoreUser extends CommonView {};

    @NotEmpty
    @JsonView(CommonView.class)
    private String eventName;

    @NotEmpty
    @JsonView(CommonView.class)
    private String keyWord;

    @NotNull
    private int userId;

    @JsonIgnore
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    public void setUserId(int userId) {
        this.userId = userId;
    }

//    @Valid
//    private User user;
}
