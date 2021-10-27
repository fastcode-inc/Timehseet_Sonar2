package com.fastcode.example.addons.entityaudit.restcontrollers;

import com.fastcode.example.addons.audittrail.application.apihistory.ApiHistoryAppService;
import com.fastcode.example.addons.audittrail.domain.model.ApiHistoryEntity;
import com.fastcode.example.commons.search.OffsetBasedPageRequest;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchFields;
import com.fastcode.example.commons.search.SearchUtils;
import com.fastcode.example.domain.core.authorization.permission.Permission;
import com.fastcode.example.domain.core.authorization.role.Role;
import com.fastcode.example.domain.core.authorization.rolepermission.Rolepermission;
import com.fastcode.example.domain.core.authorization.userspermission.Userspermission;
import com.fastcode.example.domain.core.authorization.usersrole.Usersrole;
import com.fastcode.example.domain.core.customer.Customer;
import com.fastcode.example.domain.core.project.Project;
import com.fastcode.example.domain.core.task.Task;
import com.fastcode.example.domain.core.timeofftype.Timeofftype;
import com.fastcode.example.domain.core.timesheet.Timesheet;
import com.fastcode.example.domain.core.timesheetdetails.Timesheetdetails;
import com.fastcode.example.domain.core.timesheetstatus.Timesheetstatus;
import com.fastcode.example.domain.core.usertask.Usertask;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAnyAuthority('ENTITYHISTORY')")
@RestController
@RequestMapping(value = "/audit")
public class AuditController {

    public static final String AUTHOR = "author";
    @Autowired
    private Environment env;

    @Autowired
    private ApiHistoryAppService _apiHistoryService;

    private final Javers javers;

    @Autowired
    public AuditController(Javers javers) {
        this.javers = javers;
    }

    @RequestMapping(
        value = "/role",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getRoleChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Role.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/permission",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getPermissionChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Permission.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/rolepermission",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getRolepermissionChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(
            QueryBuilder.byClass(Rolepermission.class),
            limit,
            offset,
            search
        );
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/userspermission",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getUserspermissionChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(
            QueryBuilder.byClass(Userspermission.class),
            limit,
            offset,
            search
        );
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/usersrole",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getUsersroleChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Usersrole.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/task",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getTaskChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Task.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/timeofftype",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getTimeofftypeChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Timeofftype.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/customer",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getCustomerChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Customer.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/timesheet",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getTimesheetChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Timesheet.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/project",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getProjectChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Project.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/usertask",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getUsertaskChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(QueryBuilder.byClass(Usertask.class), limit, offset, search);
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/timesheetstatus",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getTimesheetstatusChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(
            QueryBuilder.byClass(Timesheetstatus.class),
            limit,
            offset,
            search
        );
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/timesheetdetails",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public String getTimesheetdetailsChanges(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit
    ) {
        QueryBuilder jqlQuery = addPaginationAndFilters(
            QueryBuilder.byClass(Timesheetdetails.class),
            limit,
            offset,
            search
        );
        List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());
        return javers.getJsonConverter().toJson(changes);
    }

    @RequestMapping(
        value = "/changes",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
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

    private QueryBuilder addPaginationAndFilters(QueryBuilder query, String limit, String offset, String search) {
        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        query = query.limit(Integer.parseInt(limit)).skip(Integer.parseInt(offset));
        Map<String, Object> map = parseSearchString(search);
        if (map.containsKey(AUTHOR) && map.get(AUTHOR) != null) {
            query =
                query
                    .byAuthor(map.get(AUTHOR).toString())
                    .from((LocalDateTime) map.get("from"))
                    .to((LocalDateTime) map.get("to"));
        } else query = query.from((LocalDateTime) map.get("from")).to((LocalDateTime) map.get("to"));

        return query;
    }

    private Map<String, Object> parseSearchString(String searchString) {
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

    @RequestMapping("/apis")
    public List<ApiHistoryEntity> getAllApiHistories(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    )
         {
        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }
        if (Sort.unsorted().equals(sort)) {
            sort = Sort.by("id").descending();
        }
        Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        SearchCriteria searchCriteria = generateSearchCriteriaObjectForAPiHistory(search);

        return _apiHistoryService.find(searchCriteria, Pageable);
    }

    private SearchCriteria generateSearchCriteriaObjectForAPiHistory(String searchString) {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setType(3);

        List<SearchFields> searchFields = new ArrayList<SearchFields>();

        if (searchString != null && searchString.length() > 0) {
            String[] fields = searchString.split(";");

            for (String field : fields) {
                SearchFields searchField = new SearchFields();

                String fieldName = field.substring(0, field.indexOf('='));
                String searchValue = field.substring(field.indexOf('=') + 1);

                searchField.setFieldName(fieldName);
                searchField.setSearchValue(searchValue);
                searchFields.add(searchField);
            }
        }

        searchCriteria.setFields(searchFields);
        return searchCriteria;
    }
}
