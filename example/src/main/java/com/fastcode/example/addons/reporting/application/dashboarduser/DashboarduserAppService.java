package com.fastcode.example.addons.reporting.application.dashboarduser;

import com.fastcode.example.addons.reporting.application.dashboarduser.dto.*;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboard.IDashboardRepository;
import com.fastcode.example.addons.reporting.domain.dashboarduser.Dashboarduser;
import com.fastcode.example.addons.reporting.domain.dashboarduser.DashboarduserId;
import com.fastcode.example.addons.reporting.domain.dashboarduser.IDashboarduserRepository;
import com.fastcode.example.addons.reporting.domain.dashboarduser.QDashboarduser;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.*;
import com.fastcode.example.domain.core.authorization.users.Users;
import com.fastcode.example.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.querydsl.core.BooleanBuilder;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class DashboarduserAppService implements IDashboarduserAppService {

    static final int case1 = 1;
    static final int case2 = 2;
    static final int case3 = 3;

    @Autowired
    @Qualifier("dashboarduserRepository")
    private IDashboarduserRepository _dashboarduserRepository;

    @Autowired
    @Qualifier("usersRepositoryExtended")
    private IUsersRepositoryExtended _usersRepository;

    @Autowired
    @Qualifier("dashboardRepository")
    private IDashboardRepository _dashboardRepository;

    @Autowired
    @Qualifier("IDashboarduserMapperImpl")
    private IDashboarduserMapper mapper;

    @Autowired
    private LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateDashboarduserOutput create(CreateDashboarduserInput input) {
        Dashboarduser dashboarduser = mapper.createDashboarduserInputToDashboarduser(input);
        if (input.getUserId() != null) {
            Users foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);

            if (foundUsers != null) {
                dashboarduser.setUsers(foundUsers);
            }
        }
        if (input.getDashboardId() != null) {
            Dashboard foundDashboard = _dashboardRepository.findById(input.getDashboardId()).orElse(null);
            if (foundDashboard != null) {
                dashboarduser.setDashboard(foundDashboard);
            }
        }
        Dashboarduser createdDashboarduser = _dashboarduserRepository.save(dashboarduser);

        return mapper.dashboarduserToCreateDashboarduserOutput(createdDashboarduser);
    }

    public Boolean addDashboardsToUsers(Users users, List<Dashboard> dashboardsList) {
        Boolean status = true;
        Dashboarduser dashboarduser = new Dashboarduser();

        dashboarduser.setUserId(users.getId());
        dashboarduser.setUsers(users);

        for (Dashboard dashboard : dashboardsList) {
            dashboarduser.setDashboard(dashboard);
            dashboarduser.setDashboardId(dashboard.getId());

            Dashboarduser createdDashboarduser = _dashboarduserRepository.save(dashboarduser);

            if (createdDashboarduser == null) {
                status = false;
            }
        }

        return status;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateDashboarduserOutput update(DashboarduserId dashboarduserId, UpdateDashboarduserInput input) {
        Dashboarduser dashboarduser = mapper.updateDashboarduserInputToDashboarduser(input);

        if (input.getUserId() != null) {
            Users foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);

            if (foundUsers != null) {
                dashboarduser.setUsers(foundUsers);
            }
        }

        if (input.getDashboardId() != null) {
            Dashboard foundDashboard = _dashboardRepository.findById(input.getDashboardId()).orElse(null);
            if (foundDashboard != null) {
                dashboarduser.setDashboard(foundDashboard);
            }
        }

        Dashboarduser updatedDashboarduser = _dashboarduserRepository.save(dashboarduser);

        return mapper.dashboarduserToUpdateDashboarduserOutput(updatedDashboarduser);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(DashboarduserId dashboarduserId) {
        Dashboarduser existing = _dashboarduserRepository.findById(dashboarduserId).orElse(null);
        _dashboarduserRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindDashboarduserByIdOutput findById(DashboarduserId dashboarduserId) {
        Dashboarduser foundDashboarduser = _dashboarduserRepository.findById(dashboarduserId).orElse(null);
        if (foundDashboarduser == null) return null;

        FindDashboarduserByIdOutput output = mapper.dashboarduserToFindDashboarduserByIdOutput(foundDashboarduser);
        return output;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetDashboardOutput getDashboard(DashboarduserId dashboarduserId) {
        Dashboarduser foundDashboarduser = _dashboarduserRepository.findById(dashboarduserId).orElse(null);

        if (foundDashboarduser == null) {
            logHelper.getLogger().error("There does not exist a dashboarduser wth a id=%s", dashboarduserId);
            return null;
        }

        Dashboard re = foundDashboarduser.getDashboard();
        return mapper.dashboardToGetDashboardOutput(re, foundDashboarduser);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindDashboarduserByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Dashboarduser> foundDashboarduser = _dashboarduserRepository.findAll(search(search), pageable);
        List<Dashboarduser> dashboarduserList = foundDashboarduser.getContent();
        Iterator<Dashboarduser> dashboarduserIterator = dashboarduserList.iterator();
        List<FindDashboarduserByIdOutput> output = new ArrayList<>();

        while (dashboarduserIterator.hasNext()) {
            output.add(mapper.dashboarduserToFindDashboarduserByIdOutput(dashboarduserIterator.next()));
        }
        return output;
    }

    public BooleanBuilder search(SearchCriteria search) throws Exception {
        QDashboarduser dashboarduser = QDashboarduser.dashboarduser;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(dashboarduser, map, search.getJoinColumns());
        }
        return null;
    }

    public void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("users") ||
                    list.get(i).replace("%20", "").trim().equals("userId") ||
                    list.get(i).replace("%20", "").trim().equals("dashboard") ||
                    list.get(i).replace("%20", "").trim().equals("dashboardId")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    public BooleanBuilder searchKeyValuePair(
        QDashboarduser dashboarduser,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {}

        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("userId")) {
                builder.and(dashboarduser.users.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("dashboardId")) {
                builder.and(dashboarduser.dashboard.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public DashboarduserId parseDashboarduserKey(String keysString) {
        String[] keyEntries = keysString.split(",");
        DashboarduserId dashboarduserId = new DashboarduserId();

        Map<String, String> keyMap = new HashMap<String, String>();
        if (keyEntries.length > 1) {
            for (String keyEntry : keyEntries) {
                String[] keyEntryArr = keyEntry.split(":");
                if (keyEntryArr.length > 1) {
                    keyMap.put(keyEntryArr[0], keyEntryArr[1]);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }

        dashboarduserId.setUserId(Long.valueOf(keyMap.get("userId")));
        dashboarduserId.setDashboardId(Long.valueOf(keyMap.get("dashboardId")));
        return dashboarduserId;
    }
}
