package com.fastcode.example.addons.audittrail.application.apihistory;

import com.fastcode.example.addons.audittrail.domain.apihistory.IApiHistoryManager;
import com.fastcode.example.addons.audittrail.domain.model.ApiHistoryEntity;
import com.fastcode.example.addons.audittrail.domain.model.QApiHistoryEntity;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchFields;
import com.querydsl.core.BooleanBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ApiHistoryAppService implements IApiHistoryAppService {

    static final int case1 = 1;
    static final int case2 = 2;
    static final int case3 = 3;

    @Autowired
    private IApiHistoryManager _apiHistoryManager;

    @Transactional(propagation = Propagation.REQUIRED)
    public ApiHistoryEntity create(ApiHistoryEntity input) {
        ApiHistoryEntity createdApiHistory = _apiHistoryManager.create(input);
        return createdApiHistory;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ApiHistoryEntity update(Integer apiHistoryId, ApiHistoryEntity input) {
        ApiHistoryEntity updatedApiHistory = _apiHistoryManager.update(input);

        return updatedApiHistory;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApiHistoryEntity findById(Integer apiHistoryId) {
        ApiHistoryEntity foundApiHistory = _apiHistoryManager.findById(apiHistoryId);
        return foundApiHistory;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<ApiHistoryEntity> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<ApiHistoryEntity> foundApiHistory = _apiHistoryManager.findAll(search(search), pageable);
        List<ApiHistoryEntity> apiHistoryList = foundApiHistory.getContent();

        return apiHistoryList;
    }

    public BooleanBuilder search(SearchCriteria search) throws Exception {
        QApiHistoryEntity apiHistory = QApiHistoryEntity.apiHistoryEntity;
        // apiHistory.requestTime.desc();
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(apiHistory, map, search.getJoinColumns());
        }
        return null;
    }

    public void checkProperties(List<String> list) throws Exception {
        //		for (int i = 0; i < list.size(); i++) {
        //			if (!(list.get(i).replace("%20", "").trim().equals("id") || list.get(i).replace("%20", "").trim().equals("name") || list.get(i).replace("%20", "").trim().equals("pets"))) {
        //				throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
        //			}
        //		}
    }

    public BooleanBuilder searchKeyValuePair(
        QApiHistoryEntity apiHistory,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("userName")) {
                builder.and(apiHistory.userName.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
            }
            if (details.getKey().replace("%20", "").trim().equals("path")) {
                builder.and(apiHistory.path.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
            }
            if (details.getKey().replace("%20", "").trim().equals("method")) {
                builder.and(apiHistory.method.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
            }
            if (details.getKey().replace("%20", "").trim().equals("requestTime")) {
                builder.and(
                    apiHistory.requestTime.after(getDateFromStartDateString(details.getValue().getSearchValue()))
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("responseTime")) {
                builder.and(
                    apiHistory.responseTime.before(getDateFromEndDateString(details.getValue().getSearchValue()))
                );
            }
        }
        return builder;
    }

    private LocalDateTime getDateFromStartDateString(String dateString) {
        String[] dateArray = dateString.split("-");
        dateString =
            dateArray[0] +
            "-" +
            (dateArray[1].length() == 1 ? "0" + dateArray[1] : dateArray[1]) +
            "-" +
            (dateArray[2].length() == 1 ? "0" + dateArray[2] : dateArray[2]);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(dateString, dateTimeFormatter);
        LocalDateTime ldt = LocalDateTime.of(ld, LocalTime.of(0, 0));
        return ldt;
    }

    private LocalDateTime getDateFromEndDateString(String dateString) {
        String[] dateArray = dateString.split("-");
        dateString =
            dateArray[0] +
            "-" +
            (dateArray[1].length() == 1 ? "0" + dateArray[1] : dateArray[1]) +
            "-" +
            (dateArray[2].length() == 1 ? "0" + dateArray[2] : dateArray[2]);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(dateString, dateTimeFormatter);
        LocalDateTime ldt = LocalDateTime.of(ld, LocalTime.of(23, 59));
        return ldt;
    }

    public Map<String, String> parsePetsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("typeId", keysString);
        return joinColumnMap;
    }
}
