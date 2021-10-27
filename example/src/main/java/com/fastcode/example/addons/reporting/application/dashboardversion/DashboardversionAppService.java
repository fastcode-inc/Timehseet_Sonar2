package com.fastcode.example.addons.reporting.application.dashboardversion;

import com.fastcode.example.addons.reporting.application.dashboardversion.dto.*;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboard.IDashboardRepository;
import com.fastcode.example.addons.reporting.domain.dashboardversion.Dashboardversion;
import com.fastcode.example.addons.reporting.domain.dashboardversion.DashboardversionId;
import com.fastcode.example.addons.reporting.domain.dashboardversion.IDashboardversionRepository;
import com.fastcode.example.addons.reporting.domain.dashboardversion.QDashboardversion;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.*;
import com.fastcode.example.domain.core.authorization.users.Users;
import com.fastcode.example.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.querydsl.core.BooleanBuilder;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service("dashboardversionAppService")
public class DashboardversionAppService implements IDashboardversionAppService {

    @Autowired
    @Qualifier("dashboardversionRepository")
    protected IDashboardversionRepository _dashboardversionRepository;

    @Autowired
    @Qualifier("usersRepositoryExtended")
    protected IUsersRepositoryExtended _usersRepository;

    @Autowired
    @Qualifier("dashboardRepository")
    protected IDashboardRepository _dashboardRepository;

    @Autowired
    @Qualifier("IDashboardversionMapperImpl")
    protected IDashboardversionMapper mapper;

    @Autowired
    protected LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateDashboardversionOutput create(CreateDashboardversionInput input) {
        Dashboardversion dashboardversion = mapper.createDashboardversionInputToDashboardversion(input);
        if (input.getUserId() != null) {
            Users foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);

            if (foundUsers != null) {
                foundUsers.addDashboardversions(dashboardversion);
            }
        }

        if (input.getDashboardId() != null) {
            Dashboard foundDashboard = _dashboardRepository.findById(input.getDashboardId()).orElse(null);
            if (foundDashboard != null) {
                foundDashboard.addDashboardversion(dashboardversion);
            }
        }
        dashboardversion.setDashboardVersion("running");
        dashboardversion.setIsRefreshed(true);
        Dashboardversion createdRunningDashboardversion = _dashboardversionRepository.save(dashboardversion);
        dashboardversion = mapper.createDashboardversionInputToDashboardversion(input);
        dashboardversion.setDashboardVersion("published");
        _dashboardversionRepository.save(dashboardversion);

        return mapper.dashboardversionToCreateDashboardversionOutput(createdRunningDashboardversion);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateDashboardversionOutput update(
        DashboardversionId dashboardversionId,
        UpdateDashboardversionInput input
    ) {
        Dashboardversion dashboardversion = mapper.updateDashboardversionInputToDashboardversion(input);

        if (input.getUserId() != null) {
            Users foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);

            if (foundUsers != null) {
                foundUsers.addDashboardversions(dashboardversion);
            }
        }

        if (input.getDashboardId() != null) {
            Dashboard foundDashboard = _dashboardRepository.findById(input.getDashboardId()).orElse(null);
            foundDashboard.addDashboardversion(dashboardversion);
        }

        dashboardversion.setDashboardVersion(dashboardversionId.getDashboardVersion());
        Dashboardversion updatedDashboardversion = _dashboardversionRepository.save(dashboardversion);

        return mapper.dashboardversionToUpdateDashboardversionOutput(updatedDashboardversion);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(DashboardversionId dashboardversionId) {
        Dashboardversion existing = _dashboardversionRepository.findById(dashboardversionId).orElse(null);

        Dashboard dashboard = null;
        Optional<Dashboard> d = _dashboardRepository.findById(dashboardversionId.getDashboardId());
        if(d.isPresent())
            dashboard = d.get();
        else
            throw new EntityNotFoundException("Entity not found");

        dashboard.removeDashboardversion(existing);

        Users users = null;
        Optional<Users> u = _usersRepository.findById(dashboardversionId.getUserId());
        if(u.isPresent())
            users = u.get();
        else
            throw new EntityNotFoundException("Entity not found");

        users.removeDashboardversions(existing);

        _dashboardversionRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindDashboardversionByIdOutput findById(DashboardversionId dashboardversionId) {
        Dashboardversion foundDashboardversion = _dashboardversionRepository.findById(dashboardversionId).orElse(null);
        if (foundDashboardversion == null) return null;

        FindDashboardversionByIdOutput output = mapper.dashboardversionToFindDashboardversionByIdOutput(
            foundDashboardversion
        );
        return output;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetUsersOutput getUsers(DashboardversionId dashboardversionId) {
        Dashboardversion foundDashboardversion = _dashboardversionRepository.findById(dashboardversionId).orElse(null);
        if (foundDashboardversion == null) {
            logHelper.getLogger().error("There does not exist a dashboardversion wth a id=%s", dashboardversionId);
            return null;
        }

        Users re = foundDashboardversion.getUsers();
        return mapper.usersToGetUsersOutput(re, foundDashboardversion);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindDashboardversionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Dashboardversion> foundDashboardversion = _dashboardversionRepository.findAll(search(search), pageable);
        List<Dashboardversion> dashboardversionList = foundDashboardversion.getContent();
        Iterator<Dashboardversion> dashboardversionIterator = dashboardversionList.iterator();
        List<FindDashboardversionByIdOutput> output = new ArrayList<>();

        while (dashboardversionIterator.hasNext()) {
            output.add(mapper.dashboardversionToFindDashboardversionByIdOutput(dashboardversionIterator.next()));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QDashboardversion dashboardversion = QDashboardversion.dashboardversion;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(dashboardversion, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("users") ||
                    list.get(i).replace("%20", "").trim().equals("userId") ||
                    list.get(i).replace("%20", "").trim().equals("description") ||
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("reportdashboard") ||
                    list.get(i).replace("%20", "").trim().equals("title")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QDashboardversion dashboardversion,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("description")) {
                if (details.getValue().getOperator().equals("contains")) builder.and(
                    dashboardversion.description.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals("equals")) builder.and(
                    dashboardversion.description.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals("notEqual")) builder.and(
                    dashboardversion.description.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("title")) {
                if (details.getValue().getOperator().equals("contains")) builder.and(
                    dashboardversion.title.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals("equals")) builder.and(
                    dashboardversion.title.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals("notEqual")) builder.and(
                    dashboardversion.title.ne(details.getValue().getSearchValue())
                );
            }
        }

        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("userId")) {
                builder.and(dashboardversion.users.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }

        return builder;
    }

    public Map<String, String> parseReportdashboardJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("dashboardversionId", keysString);
        return joinColumnMap;
    }
}
