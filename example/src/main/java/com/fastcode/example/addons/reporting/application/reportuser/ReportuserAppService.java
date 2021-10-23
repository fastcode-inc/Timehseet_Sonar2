package com.fastcode.example.addons.reporting.application.reportuser;

import com.fastcode.example.addons.reporting.application.reportuser.dto.*;
import com.fastcode.example.addons.reporting.domain.report.IReportRepository;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportuser.IReportuserRepository;
import com.fastcode.example.addons.reporting.domain.reportuser.QReportuser;
import com.fastcode.example.addons.reporting.domain.reportuser.Reportuser;
import com.fastcode.example.addons.reporting.domain.reportuser.ReportuserId;
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

@Service
public class ReportuserAppService implements IReportuserAppService {

    static final int case1 = 1;
    static final int case2 = 2;
    static final int case3 = 3;

    @Autowired
    @Qualifier("reportuserRepository")
    private IReportuserRepository _reportuserRepository;

    @Autowired
    @Qualifier("usersRepositoryExtended")
    private IUsersRepositoryExtended _usersRepository;

    @Autowired
    @Qualifier("reportRepository")
    private IReportRepository _reportRepository;

    @Autowired
    @Qualifier("IReportuserMapperImpl")
    private IReportuserMapper mapper;

    @Autowired
    private LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateReportuserOutput create(CreateReportuserInput input) {
        Reportuser reportuser = mapper.createReportuserInputToReportuser(input);
        if (input.getUserId() != null) {
            Users foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);

            if (foundUsers != null) {
                reportuser.setUsers(foundUsers);
            }
        }
        if (input.getReportId() != null) {
            Report foundReport = _reportRepository.findById(input.getReportId()).orElse(null);
            if (foundReport != null) {
                reportuser.setReport(foundReport);
            }
        }
        Reportuser createdReportuser = _reportuserRepository.save(reportuser);

        return mapper.reportuserToCreateReportuserOutput(createdReportuser);
    }

    public Boolean addReportsToUsers(Users users, List<Report> reportsList) {
        Boolean status = true;
        Reportuser reportuser = new Reportuser();
        reportuser.setUserId(users.getId());
        reportuser.setUsers(users);

        for (Report report : reportsList) {
            reportuser.setReport(report);
            reportuser.setReportId(report.getId());

            Reportuser createdReportuser = _reportuserRepository.save(reportuser);

            if (createdReportuser == null) {
                status = false;
            }
        }

        return status;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateReportuserOutput update(ReportuserId reportuserId, UpdateReportuserInput input) {
        Reportuser reportuser = mapper.updateReportuserInputToReportuser(input);
        if (input.getUserId() != null) {
            Users foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);

            if (foundUsers != null) {
                reportuser.setUsers(foundUsers);
            }
        }
        if (input.getReportId() != null) {
            Report foundReport = _reportRepository.findById(input.getReportId()).orElse(null);
            if (foundReport != null) {
                reportuser.setReport(foundReport);
            }
        }

        Reportuser updatedReportuser = _reportuserRepository.save(reportuser);

        return mapper.reportuserToUpdateReportuserOutput(updatedReportuser);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(ReportuserId reportuserId) {
        Reportuser existing = _reportuserRepository.findById(reportuserId).orElse(null);
        _reportuserRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindReportuserByIdOutput findById(ReportuserId reportuserId) {
        Reportuser foundReportuser = _reportuserRepository.findById(reportuserId).orElse(null);
        if (foundReportuser == null) return null;

        FindReportuserByIdOutput output = mapper.reportuserToFindReportuserByIdOutput(foundReportuser);
        return output;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetReportOutput getReport(ReportuserId reportuserId) {
        Reportuser foundReportuser = _reportuserRepository.findById(reportuserId).orElse(null);

        if (foundReportuser == null) {
            logHelper.getLogger().error("There does not exist a reportuser wth a id=%s", reportuserId);
            return null;
        }

        Report re = foundReportuser.getReport();
        return mapper.reportToGetReportOutput(re, foundReportuser);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindReportuserByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Reportuser> foundReportuser = _reportuserRepository.findAll(search(search), pageable);
        List<Reportuser> reportuserList = foundReportuser.getContent();
        Iterator<Reportuser> reportuserIterator = reportuserList.iterator();
        List<FindReportuserByIdOutput> output = new ArrayList<>();

        while (reportuserIterator.hasNext()) {
            output.add(mapper.reportuserToFindReportuserByIdOutput(reportuserIterator.next()));
        }
        return output;
    }

    public BooleanBuilder search(SearchCriteria search) throws Exception {
        QReportuser reportuser = QReportuser.reportuser;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(reportuser, map, search.getJoinColumns());
        }
        return null;
    }

    public void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("users") ||
                    list.get(i).replace("%20", "").trim().equals("userId") ||
                    list.get(i).replace("%20", "").trim().equals("report") ||
                    list.get(i).replace("%20", "").trim().equals("reportId")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    public BooleanBuilder searchKeyValuePair(
        QReportuser reportuser,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {}
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("userId")) {
                builder.and(reportuser.users.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("reportId")) {
                builder.and(reportuser.report.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public ReportuserId parseReportuserKey(String keysString) {
        String[] keyEntries = keysString.split(",");
        ReportuserId reportuserId = new ReportuserId();

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

        reportuserId.setUserId(Long.valueOf(keyMap.get("userId")));
        reportuserId.setReportId(Long.valueOf(keyMap.get("reportId")));
        return reportuserId;
    }
}
