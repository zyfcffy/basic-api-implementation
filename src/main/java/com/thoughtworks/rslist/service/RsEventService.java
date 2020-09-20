package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.dto.RsEvent;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RsEventService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;

    public List<RsEvent> getAllRsEvent(){
        return rsEventRepository.findAll().stream()
                .map(item ->
                        RsEvent.builder()
                                .eventName(item.getEventName())
                                .keyWord(item.getKeyWord())
                                .voteNum(item.getVoteNum())
                                .userId(item.getUserEntity().getId())
                                .build())
                .collect(Collectors.toList());
    }

    public Optional<RsEventEntity> getOneRsEventById(Integer id){
        return rsEventRepository.findById(id);
    }

    public void saveRsEvent(RsEvent rsEvent){
        RsEventEntity rsEventEntity = RsEventEntity.builder()
                .eventName(rsEvent.getEventName())
                .keyWord(rsEvent.getKeyWord())
                .userEntity(UserEntity.builder()
                        .id(rsEvent.getUserId())
                        .build())
                .build();
        rsEventRepository.save(rsEventEntity);
    }

    public void updateRsEvent(RsEventEntity rsEventEntity,RsEvent rsEvent,Integer rsEventId){
        if (rsEvent.getEventName() != null) {
            rsEventEntity.setId(rsEventId);
            rsEventEntity.setEventName(rsEvent.getEventName());
            rsEventRepository.save(rsEventEntity);
        }
        if (rsEvent.getKeyWord() != null) {
            rsEventEntity.setId(rsEventId);
            rsEventEntity.setKeyWord(rsEvent.getKeyWord());
            rsEventRepository.save(rsEventEntity);
        }
    }

    public void deleteById(int id){
        rsEventRepository.deleteById(id);
    }
}
