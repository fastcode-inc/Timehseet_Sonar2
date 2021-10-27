package com.fastcode.example.addons.reporting.application.dashboardrole;

import com.fastcode.example.addons.reporting.application.dashboardrole.dto.*;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboard.IDashboardRepository;
import com.fastcode.example.addons.reporting.domain.dashboardrole.Dashboardrole;
import com.fastcode.example.addons.reporting.domain.dashboardrole.DashboardroleId;
import com.fastcode.example.addons.reporting.domain.dashboardrole.IDashboardroleRepository;
import com.fastcode.example.addons.reporting.domain.dashboardrole.QDashboardrole;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.*;
import com.fastcode.example.domain.core.authorization.role.IRoleRepository;
import com.fastcode.example.domain.core.authorization.role.Role;
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
public class DashboardroleAppService implements IDashboardroleAppService {

    @Autowired
    @Qualifier("dashboardroleRepository")
    private IDashboardroleRepository _dashboardroleRepository;

    @Autowired
    @Qualifier("roleRepository")
    private IRoleRepository _roleRepository;

    @Autowired
    @Qualifier("dashboardRepository")
    private IDashboardRepository _dashboardRepository;

    @Autowired
    @Qualifier("IDashboardroleMapperImpl")
    private IDashboardroleMapper mapper;

    @Autowired
    private LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateDashboardroleOutput create(CreateDashboardroleInput input) {
        Dashboardrole dashboardrole = mapper.createDashboardroleInputToDashboardrole(input);
        if (input.getRoleId() != null) {
            Role foundRole = _roleRepository.findById(input.getRoleId()).orElse(null);
            if (foundRole != null) {
                dashboardrole.setRole(foundRole);
            }
        }
        if (input.getDashboardId() != null) {
            Dashboard foundDashboard = _dashboardRepository.findById(input.getDashboardId()).orElse(null);
            if (foundDashboard != null) {
                dashboardrole.setDashboard(foundDashboard);
            }
        }

        Dashboardrole createdDashboardrole = _dashboardroleRepository.save(dashboardrole);
        return mapper.dashboardroleToCreateDashboardroleOutput(createdDashboardrole);
    }

    public Boolean addDashboardsToRole(Role role, List<Dashboard> dashboardsList) {
        Boolean status = true;
        Dashboardrole dashboardrole = new Dashboardrole();
        dashboardrole.setRoleId(role.getId());
        dashboardrole.setRole(role);

        for (Dashboard dashboard : dashboardsList) {
            dashboardrole.setDashboard(dashboard);
            dashboardrole.setDashboardId(dashboard.getId());

            Dashboardrole createdDashboardrole = _dashboardroleRepository.save(dashboardrole);

            if (createdDashboardrole == null) {
                status = false;
            }
        }

        return status;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateDashboardroleOutput update(DashboardroleId dashboardroleId, UpdateDashboardroleInput input) {
        Dashboardrole dashboardrole = mapper.updateDashboardroleInputToDashboardrole(input);
        if (input.getRoleId() != null) {
            Role foundRole = _roleRepository.findById(input.getRoleId()).orElse(null);
            if (foundRole != null) {
                dashboardrole.setRole(foundRole);
            }
        }
        if (input.getDashboardId() != null) {
            Dashboard foundDashboard = _dashboardRepository.findById(input.getDashboardId()).orElse(null);
            if (foundDashboard != null) {
                dashboardrole.setDashboard(foundDashboard);
            }
        }

        Dashboardrole updatedDashboardrole = _dashboardroleRepository.save(dashboardrole);
        return mapper.dashboardroleToUpdateDashboardroleOutput(updatedDashboardrole);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(DashboardroleId dashboardroleId) {
        Dashboardrole existing = _dashboardroleRepository.findById(dashboardroleId).orElse(null);
        _dashboardroleRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindDashboardroleByIdOutput findById(DashboardroleId dashboardroleId) {
        Dashboardrole foundDashboardrole = _dashboardroleRepository.findById(dashboardroleId).orElse(null);
        if (foundDashboardrole == null) return null;

        FindDashboardroleByIdOutput output = mapper.dashboardroleToFindDashboardroleByIdOutput(foundDashboardrole);
        return output;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetDashboardOutput getDashboard(DashboardroleId dashboardroleId) {
        Dashboardrole foundDashboardrole = _dashboardroleRepository.findById(dashboardroleId).orElse(null);

        if (foundDashboardrole == null) {
            logHelper.getLogger().error("There does not exist a dashboardrole wth a id=%s", dashboardroleId);
            return null;
        }

        Dashboard re = foundDashboardrole.getDashboard();
        return mapper.dashboardToGetDashboardOutput(re, foundDashboardrole);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindDashboardroleByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Dashboardrole> foundDashboardrole = _dashboardroleRepository.findAll(search(search), pageable);
        List<Dashboardrole> dashboardroleList = foundDashboardrole.getContent();
        Iterator<Dashboardrole> dashboardroleIterator = dashboardroleList.iterator();
        List<FindDashboardroleByIdOutput> output = new ArrayList<>();

        while (dashboardroleIterator.hasNext()) {
            output.add(mapper.dashboardroleToFindDashboardroleByIdOutput(dashboardroleIterator.next()));
        }

        return output;
    }

    public BooleanBuilder search(SearchCriteria search) throws Exception {
        QDashboardrole dashboardrole = QDashboardrole.dashboardrole;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(dashboardrole, map, search.getJoinColumns());
        }
        return null;
    }

    public void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("role") ||
                    list.get(i).replace("%20", "").trim().equals("roleId") ||
                    list.get(i).replace("%20", "").trim().equals("dashboard") ||
                    list.get(i).replace("%20", "").trim().equals("dashboardId")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    public BooleanBuilder searchKeyValuePair(
        QDashboardrole dashboardrole,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {}
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("roleId")) {
                builder.and(dashboardrole.role.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("dashboardId")) {
                builder.and(dashboardrole.dashboard.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public DashboardroleId parseDashboardroleKey(String keysString) {
        String[] keyEntries = keysString.split(",");
        DashboardroleId dashboardroleId = new DashboardroleId();

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

        dashboardroleId.setRoleId(Long.valueOf(keyMap.get("roleId")));
        dashboardroleId.setDashboardId(Long.valueOf(keyMap.get("dashboardId")));
        return dashboardroleId;
    }
}
