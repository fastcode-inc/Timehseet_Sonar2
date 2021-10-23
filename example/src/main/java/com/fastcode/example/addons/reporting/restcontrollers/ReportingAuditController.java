package com.fastcode.example.addons.reporting.restcontrollers;

import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.Dashboardversionreport;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.commons.search.SearchUtils;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/audit")
public class ReportingAuditController {

    private final Javers javers;

    @Autowired
    private Environment env;

    @Autowired
    public ReportingAuditController(Javers javers) {
        this.javers = javers;
    }

    @RequestMapping(value = "/report", consumes = { "application/json" }, produces = { "application/json" })
    public String getReportChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Report.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(value = "/dashboard", consumes = { "application/json" }, produces = { "application/json" })
    public String getDashboardChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Dashboard.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(value = "/reportdashboard", consumes = { "application/json" }, produces = { "application/json" })
    public String getReportdashboardChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(
            QueryBuilder.byClass(Dashboardversionreport.class),
            limit,
            offset,
            search
        );
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(value = "/reportingChanges", consumes = { "application/json" }, produces = { "application/json" })
    public String getAllChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        JqlQuery jqlQuery = addPaginationAndFilters(
            QueryBuilder.anyDomainObject().withNewObjectChanges(),
            limit,
            offset,
            search
        )
            .build();
        List<Change> changes = javers.findChanges(jqlQuery);
        return javers.getJsonConverter().toJson(changes);
    }

    public QueryBuilder addPaginationAndFilters(QueryBuilder query, String limit, String offset, String search) {
        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        query = query.limit(Integer.parseInt(limit)).skip(Integer.parseInt(offset));
        Map<String, Object> map = parseSearchString(search);
        if (map.containsKey("author") && map.get("author") != null) {
            query =
                query
                    .byAuthor(map.get("author").toString())
                    .from((LocalDateTime) map.get("from"))
                    .to((LocalDateTime) map.get("to"));
        } else query = query.from((LocalDateTime) map.get("from")).to((LocalDateTime) map.get("to"));

        return query;
    }

    public Map<String, Object> parseSearchString(String searchString) {
        Map<String, Object> searchMap = new HashMap<>();
        if (searchString != null && searchString.length() > 0) {
            String[] fields = searchString.split(";");

            for (String field : fields) {
                String fieldName = field.substring(0, field.indexOf('=')).toLowerCase();
                String searchValue = field.substring(field.indexOf('=') + 1);

                searchMap.put(fieldName, searchValue);
            }
        }
        if (searchMap.containsKey("from")) {
            LocalDateTime from = SearchUtils
                .stringToDate(searchMap.get("from").toString())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            searchMap.put("from", from);
        } else {
            searchMap.put("from", LocalDateTime.of(1970, Month.JANUARY, 1, 10, 10, 30));
        }
        if (searchMap.containsKey("to")) {
            LocalDateTime to = SearchUtils
                .stringToDate(searchMap.get("to").toString())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            searchMap.put("to", to);
        } else {
            searchMap.put("to", LocalDateTime.now());
        }

        return searchMap;
    }
}
