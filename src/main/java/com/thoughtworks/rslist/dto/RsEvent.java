package com.thoughtworks.rslist.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
public class RsEvent {
    public interface CommonView {};
    public interface IgnoreUser extends CommonView {};

    @NotEmpty
    @JsonView(CommonView.class)
    private String eventName;

    @NotEmpty
    @JsonView(CommonView.class)
    private String keyWord;

    @Valid
    @JsonView(IgnoreUser.class)
    private User user;

    public RsEvent(String eventName, String keyWord, User user) {
        this.eventName = eventName;
        this.keyWord = keyWord;
        this.user = user;
    }
}
