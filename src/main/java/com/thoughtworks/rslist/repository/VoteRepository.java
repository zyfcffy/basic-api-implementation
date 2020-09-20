package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.entity.VoteEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface VoteRepository extends CrudRepository<VoteEntity, Integer> {
    List<VoteEntity> findAll();

    List<VoteEntity> findAllByUserIdAndRsEventId(int userId, int rsEventId);
}
