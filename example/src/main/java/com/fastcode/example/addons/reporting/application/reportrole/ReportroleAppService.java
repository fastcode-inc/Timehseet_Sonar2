package com.fastcode.example.addons.reporting.application.reportrole;

import com.fastcode.example.addons.reporting.application.reportrole.dto.*;
import com.fastcode.example.addons.reporting.domain.report.IReportRepository;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportrole.IReportroleRepository;
import com.fastcode.example.addons.reporting.domain.reportrole.QReportrole;
import com.fastcode.example.addons.reporting.domain.reportrole.Reportrole;
import com.fastcode.example.addons.reporting.domain.reportrole.ReportroleId;
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
public class ReportroleAppService implements IReportroleAppService {

    static final int case1 = 1;
    static final int case2 = 2;
    static final int case3 = 3;

    @Autowired
    @Qualifier("reportroleRepository")
    private IReportroleRepository _reportroleRepository;

    @Autowired
    @Qualifier("roleRepository")
    private IRoleRepository _roleRepository;

    @Autowired
    @Qualifier("reportRepository")
    private IReportRepository _reportRepository;

    @Autowired
    @Qualifier("IReportroleMapperImpl")
    private IReportroleMapper mapper;

    @Autowired
    private LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateReportroleOutput create(CreateReportroleInput input) {
        Reportrole reportrole = mapper.createReportroleInputToReportrole(input);
        if (input.getRoleId() != null) {
            Role foundRole = _roleRepository.findById(input.getRoleId()).orElse(null);
            if (foundRole != null) {
                reportrole.setRole(foundRole);
            }
        }

        if (input.getReportId() != null) {
            Report foundReport = _reportRepository.findById(input.getReportId()).orElse(null);
            if (foundReport != null) {
                reportrole.setReport(foundReport);
            }
        }

        Reportrole createdReportrole = _reportroleRepository.save(reportrole);
        return mapper.reportroleToCreateReportroleOutput(createdReportrole);
    }

    public Boolean addReportsToRole(Role role, List<Report> reportsList) {
        Boolean status = true;
        Reportrole reportrole = new Reportrole();
        reportrole.setRoleId(role.getId());
        reportrole.setRole(role);

        for (Report report : reportsList) {
            reportrole.setReport(report);
            reportrole.setReportId(report.getId());

            Reportrole createdReportrole = _reportroleRepository.save(reportrole);

            if (createdReportrole == null) {
                status = false;
            }
        }

        return status;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateReportroleOutput update(ReportroleId reportroleId, UpdateReportroleInput input) {
        Reportrole reportrole = mapper.updateReportroleInputToReportrole(input);
        if (input.getRoleId() != null) {
            Role foundRole = _roleRepository.findById(input.getRoleId()).orElse(null);
            if (foundRole != null) {
                reportrole.setRole(foundRole);
            }
        }

        if (input.getReportId() != null) {
            Report foundReport = _reportRepository.findById(input.getReportId()).orElse(null);
            if (foundReport != null) {
                reportrole.setReport(foundReport);
            }
        }

        Reportrole updatedReportrole = _reportroleRepository.save(reportrole);
        return mapper.reportroleToUpdateReportroleOutput(updatedReportrole);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(ReportroleId reportroleId) {
        Reportrole existing = _reportroleRepository.findById(reportroleId).orElse(null);
        _reportroleRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindReportroleByIdOutput findById(ReportroleId reportroleId) {
        Reportrole foundReportrole = _reportroleRepository.findById(reportroleId).orElse(null);
        if (foundReportrole == null) return null;

        FindReportroleByIdOutput output = mapper.reportroleToFindReportroleByIdOutput(foundReportrole);
        return output;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetReportOutput getReport(ReportroleId reportroleId) {
        Reportrole foundReportrole = _reportroleRepository.findById(reportroleId).orElse(null);

        if (foundReportrole == null) {
            logHelper.getLogger().error("There does not exist a reportrole wth a id=%s", reportroleId);
            return null;
        }

        Report re = foundReportrole.getReport();
        return mapper.reportToGetReportOutput(re, foundReportrole);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindReportroleByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Reportrole> foundReportrole = _reportroleRepository.findAll(search(search), pageable);
        List<Reportrole> reportroleList = foundReportrole.getContent();
        Iterator<Reportrole> reportroleIterator = reportroleList.iterator();
        List<FindReportroleByIdOutput> output = new ArrayList<>();

        while (reportroleIterator.hasNext()) {
            output.add(mapper.reportroleToFindReportroleByIdOutput(reportroleIterator.next()));
        }
        return output;
    }

    public BooleanBuilder search(SearchCriteria search) throws Exception {
        QReportrole reportrole = QReportrole.reportrole;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(reportrole, map, search.getJoinColumns());
        }
        return null;
    }

    public void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("role") ||
                    list.get(i).replace("%20", "").trim().equals("roleId") ||
                    list.get(i).replace("%20", "").trim().equals("report") ||
                    list.get(i).replace("%20", "").trim().equals("reportId")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    public BooleanBuilder searchKeyValuePair(
        QReportrole reportrole,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("editable")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) builder.and(
                    reportrole.editable.eq(Boolean.parseBoolean(details.getValue().getSearchValue()))
                ); else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) builder.and(reportrole.editable.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("roleId")) {
                builder.and(reportrole.role.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("reportId")) {
                builder.and(reportrole.report.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public ReportroleId parseReportroleKey(String keysString) {
        String[] keyEntries = keysString.split(",");
        ReportroleId reportroleId = new ReportroleId();

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

        reportroleId.setRoleId(Long.valueOf(keyMap.get("roleId")));
        reportroleId.setReportId(Long.valueOf(keyMap.get("reportId")));
        return reportroleId;
    }
}
