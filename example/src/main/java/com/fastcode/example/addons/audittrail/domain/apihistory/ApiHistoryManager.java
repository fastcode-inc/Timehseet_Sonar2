package com.fastcode.example.addons.audittrail.domain.apihistory;

import com.fastcode.example.addons.audittrail.domain.irepository.IApiHistoryRepository;
import com.fastcode.example.addons.audittrail.domain.model.ApiHistoryEntity;
import com.querydsl.core.types.Predicate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ApiHistoryManager implements IApiHistoryManager {

    @Autowired
    IApiHistoryRepository _apiHistoryRepository;

    public ApiHistoryEntity create(ApiHistoryEntity apiHistory) {
        return _apiHistoryRepository.save(apiHistory);
    }

    public void delete(ApiHistoryEntity apiHistory) {
        _apiHistoryRepository.delete(apiHistory);
    }

    public ApiHistoryEntity update(ApiHistoryEntity apiHistory) {
        return _apiHistoryRepository.save(apiHistory);
    }

    public ApiHistoryEntity findById(Integer apiHistoryId) {
        Optional<ApiHistoryEntity> dbApiHistory = _apiHistoryRepository.findById(apiHistoryId);
        if (dbApiHistory.isPresent()) {
            ApiHistoryEntity existingApiHistory = dbApiHistory.get();
            return existingApiHistory;
        } else {
            return null;
        }
    }

    public Page<ApiHistoryEntity> findAll(Predicate predicate, Pageable pageable) {
        return _apiHistoryRepository.findAll(predicate, pageable);
    }
}
