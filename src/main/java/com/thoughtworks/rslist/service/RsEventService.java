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
    private final RsEventRepository rsEventRepository;

    public RsEventService(RsEventRepository rsEventRepository) {
        this.rsEventRepository = rsEventRepository;
    }

    public List<RsEvent> getAllRsEvent() {
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

    public Optional<RsEventEntity> getRsEventById(Integer rsEventId) {
        return rsEventRepository.findById(rsEventId);
    }

    public void saveRsEvent(RsEvent rsEvent) {
        RsEventEntity rsEventEntity = RsEventEntity.builder()
                .eventName(rsEvent.getEventName())
                .keyWord(rsEvent.getKeyWord())
                .userEntity(UserEntity.builder()
                        .id(rsEvent.getUserId())
                        .build())
                .build();
        rsEventRepository.save(rsEventEntity);
    }

    public void updateRsEvent(RsEventEntity rsEventEntity) {
        rsEventRepository.save(rsEventEntity);
    }

    public void deleteById(int id) {
        rsEventRepository.deleteById(id);
    }
}
