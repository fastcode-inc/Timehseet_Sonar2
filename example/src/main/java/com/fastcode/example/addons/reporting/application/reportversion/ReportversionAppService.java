package com.fastcode.example.addons.reporting.application.reportversion;

import com.fastcode.example.addons.reporting.application.reportversion.dto.CreateReportversionInput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.CreateReportversionOutput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.FindReportversionByIdOutput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.GetUsersOutput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.UpdateReportversionInput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.UpdateReportversionOutput;
import com.fastcode.example.addons.reporting.domain.report.IReportRepository;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportversion.IReportversionRepository;
import com.fastcode.example.addons.reporting.domain.reportversion.QReportversion;
import com.fastcode.example.addons.reporting.domain.reportversion.Reportversion;
import com.fastcode.example.addons.reporting.domain.reportversion.ReportversionId;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchFields;
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

@Service("reportversionAppService")
public class ReportversionAppService implements IReportversionAppService {

    public static final String CONTAINS = "contains";
    public static final String EQUALS_TO = "equals";
    public static final String NOT_EQUAL = "notEqual";
    @Autowired
    @Qualifier("reportversionRepository")
    protected IReportversionRepository _reportversionRepository;

    @Autowired
    @Qualifier("usersRepositoryExtended")
    protected IUsersRepositoryExtended _usersRepository;

    @Autowired
    @Qualifier("reportRepository")
    protected IReportRepository _reportRepository;

    @Autowired
    @Qualifier("IReportversionMapperImpl")
    protected IReportversionMapper mapper;

    @Autowired
    protected LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateReportversionOutput create(CreateReportversionInput input) {
        Reportversion reportversion = mapper.createReportversionInputToReportversion(input);
        if (input.getUserId() != null) {
            Users foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);
            foundUsers.addReportversions(reportversion);
        }

        if (input.getReportId() != null) {
            Report foundReport = _reportRepository.findById(input.getReportId()).orElse(null);
            foundReport.addReportversion(reportversion);
        }

        reportversion.setReportVersion("running");
        reportversion.setIsRefreshed(true);
        Reportversion createdRunningReportversion = _reportversionRepository.save(reportversion);

        reportversion = mapper.createReportversionInputToReportversion(input);
        reportversion.setReportVersion("published");

        _reportversionRepository.save(reportversion);

        return mapper.reportversionToCreateReportversionOutput(createdRunningReportversion);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateReportversionOutput update(ReportversionId reportversionId, UpdateReportversionInput input) {
        Reportversion reportversion = mapper.updateReportversionInputToReportversion(input);

        if (input.getUserId() != null) {
            Users foundUsers = _usersRepository.findById(input.getUserId()).orElse(null);
            foundUsers.addReportversions(reportversion);
        }

        if (input.getReportId() != null) {
            Report foundReport = _reportRepository.findById(input.getReportId()).orElse(null);
            foundReport.addReportversion(reportversion);
        }

        reportversion.setReportVersion(reportversionId.getReportVersion());
        Reportversion updatedReportversion = _reportversionRepository.save(reportversion);

        return mapper.reportversionToUpdateReportversionOutput(updatedReportversion);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(ReportversionId reportversionId) {
        Reportversion existing = _reportversionRepository.findById(reportversionId).orElse(null);

        Report report = null;
        Optional<Report> rep = _reportRepository.findById(reportversionId.getReportId());
        if(rep.isPresent())
            report = rep.get();
        else
            throw new EntityNotFoundException("Entity not found");

        report.removeReportversion(existing);

        Users users = null;
        Optional<Users> u = _usersRepository.findById(reportversionId.getUserId());
        if(u.isPresent())
            users = u.get();
        else
            throw new EntityNotFoundException("Entity not found");

        users.removeReportversions(existing);

        _reportversionRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindReportversionByIdOutput findById(ReportversionId reportversionId) {
        Reportversion foundReportversion = _reportversionRepository.findById(reportversionId).orElse(null);
        if (foundReportversion == null) return null;

        FindReportversionByIdOutput output = mapper.reportversionToFindReportversionByIdOutput(foundReportversion);
        return output;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetUsersOutput getUsers(ReportversionId reportversionId) {
        Reportversion foundReportversion = _reportversionRepository.findById(reportversionId).orElse(null);
        if (foundReportversion == null) {
            logHelper.getLogger().error("There does not exist a reportversion wth a id=%s", reportversionId);
            return null;
        }
        Users re = foundReportversion.getUsers();
        return mapper.usersToGetUsersOutput(re, foundReportversion);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindReportversionByIdOutput> findByUsersId(Long userId) {
        List<Reportversion> reportList = _reportversionRepository.findByUsersId(userId);
        if (reportList == null) return null;

        Iterator<Reportversion> reportIterator = reportList.iterator();
        List<FindReportversionByIdOutput> output = new ArrayList<>();

        while (reportIterator.hasNext()) {
            output.add(mapper.reportversionToFindReportversionByIdOutput(reportIterator.next()));
        }

        return output;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindReportversionByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Reportversion> foundReportversion = _reportversionRepository.findAll(search(search), pageable);
        List<Reportversion> reportList = foundReportversion.getContent();
        Iterator<Reportversion> reportIterator = reportList.iterator();
        List<FindReportversionByIdOutput> output = new ArrayList<>();

        while (reportIterator.hasNext()) {
            output.add(mapper.reportversionToFindReportversionByIdOutput(reportIterator.next()));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QReportversion reportversion = QReportversion.reportversion;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(reportversion, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("users") ||
                    list.get(i).replace("%20", "").trim().equals("userId") ||
                    list.get(i).replace("%20", "").trim().equals("ctype") ||
                    list.get(i).replace("%20", "").trim().equals("description") ||
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("query") ||
                    list.get(i).replace("%20", "").trim().equals("reportType") ||
                    list.get(i).replace("%20", "").trim().equals("reportdashboard") ||
                    list.get(i).replace("%20", "").trim().equals("title")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QReportversion reportversion,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("ctype")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    reportversion.ctype.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    reportversion.ctype.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    reportversion.ctype.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("description")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    reportversion.description.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    reportversion.description.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    reportversion.description.ne(details.getValue().getSearchValue())
                );
            }

            if (details.getKey().replace("%20", "").trim().equals("reportType")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    reportversion.reportType.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    reportversion.reportType.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    reportversion.reportType.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("title")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    reportversion.title.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    reportversion.title.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    reportversion.title.ne(details.getValue().getSearchValue())
                );
            }
        }

        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("userId")) {
                builder.and(reportversion.users.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public Map<String, String> parsedashboardversionreportJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("reportId", keysString);
        return joinColumnMap;
    }
}
