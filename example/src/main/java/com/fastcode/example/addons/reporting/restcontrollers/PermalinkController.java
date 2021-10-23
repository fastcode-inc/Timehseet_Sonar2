package com.fastcode.example.addons.reporting.restcontrollers;

import com.fastcode.example.addons.reporting.application.dashboard.IDashboardAppService;
import com.fastcode.example.addons.reporting.application.dashboard.dto.FindDashboardByIdOutput;
import com.fastcode.example.addons.reporting.application.dashboarduser.IDashboarduserAppService;
import com.fastcode.example.addons.reporting.application.dashboarduser.dto.FindDashboarduserByIdOutput;
import com.fastcode.example.addons.reporting.application.permalink.IPermalinkAppService;
import com.fastcode.example.addons.reporting.application.permalink.dto.*;
import com.fastcode.example.addons.reporting.application.report.IReportAppService;
import com.fastcode.example.addons.reporting.application.report.dto.FindReportByIdOutput;
import com.fastcode.example.addons.reporting.application.reportuser.IReportuserAppService;
import com.fastcode.example.addons.reporting.application.reportuser.dto.FindReportuserByIdOutput;
import com.fastcode.example.addons.reporting.domain.dashboarduser.DashboarduserId;
import com.fastcode.example.addons.reporting.domain.reportuser.ReportuserId;
import com.fastcode.example.application.extended.authorization.users.IUsersAppServiceExtended;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.OffsetBasedPageRequest;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchUtils;
import com.fastcode.example.domain.core.authorization.users.Users;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reporting/permalink")
public class PermalinkController {

    @Autowired
    private IPermalinkAppService _permalinkAppService;

    @Autowired
    private IReportuserAppService _reportuserAppService;

    @Autowired
    private IReportAppService _reportAppService;

    @Autowired
    private IDashboarduserAppService _dashboarduserAppService;

    @Autowired
    private IDashboardAppService _dashboardAppService;

    @Autowired
    @Qualifier("usersAppServiceExtended")
    private IUsersAppServiceExtended _usersAppService;

    @Autowired
    private LoggingHelper logHelper;

    @Autowired
    private Environment env;

    public PermalinkController(
        IPermalinkAppService permalinkAppService,
        IReportuserAppService reportuserAppService,
        IReportAppService reportAppService,
        LoggingHelper helper
    ) {
        super();
        this._permalinkAppService = permalinkAppService;
        this.logHelper = helper;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreatePermalinkOutput> create(@RequestBody @Valid CreatePermalinkInput permalink) {
        Users users = _usersAppService.getUsers();

        if (permalink.getResource() == "report") {
            FindReportByIdOutput report = _reportAppService.findById(Long.valueOf(permalink.getResourceId()));
            Optional
                .ofNullable(report)
                .orElseThrow(
                    () ->
                        new EntityNotFoundException(
                            String.format("There does not exist a report with a id=%s", permalink.getResourceId())
                        )
                );

            FindReportuserByIdOutput reportuser = _reportuserAppService.findById(
                new ReportuserId(Long.valueOf(permalink.getResourceId()), users.getId())
            );

            if (report.getOwnerId() != users.getId() && reportuser == null) {
                logHelper
                    .getLogger()
                    .error("You do not have access to the report with a id=%s", permalink.getResourceId());
                throw new EntityNotFoundException(
                    String.format("You do not have access to the report with a id=%s", permalink.getResourceId())
                );
            }
        }

        if (permalink.getResource() == "dashboard") {
            FindDashboardByIdOutput dashboard = _dashboardAppService.findById(Long.valueOf(permalink.getResourceId()));
            Optional
                .ofNullable(dashboard)
                .orElseThrow(
                    () ->
                        new EntityNotFoundException(
                            String.format("There does not exist a dashboard with a id=%s", permalink.getResourceId())
                        )
                );

            FindDashboarduserByIdOutput dashboarduser = _dashboarduserAppService.findById(
                new DashboarduserId(Long.valueOf(permalink.getResourceId()), users.getId())
            );

            if (dashboard.getOwnerId() != users.getId() && dashboarduser == null) {
                logHelper
                    .getLogger()
                    .error("You do not have access to the dashboard with a id=%s", permalink.getResourceId());
                throw new EntityNotFoundException(
                    String.format("You do not have access to the dashboard with a id=%s", permalink.getResourceId())
                );
            }
        }
        CreatePermalinkOutput output = _permalinkAppService.create(permalink);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    // ------------ Delete permalink ------------
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void delete(@PathVariable UUID id) {
        FindPermalinkByIdOutput output = _permalinkAppService.findById(id);
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a permalink with a id=%s", id))
            );

        _permalinkAppService.delete(id);
    }

    // ------------ Update permalink ------------
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UpdatePermalinkOutput> update(
        @PathVariable UUID id,
        @RequestBody @Valid UpdatePermalinkInput permalink
    ) {
        FindPermalinkByIdOutput currentPermalink = _permalinkAppService.findById(id);
        Optional
            .ofNullable(currentPermalink)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Unable to update. Permalink with id=%snot found.", id))
            );

        permalink.setVersiono(currentPermalink.getVersiono());
        return new ResponseEntity(_permalinkAppService.update(id, permalink), HttpStatus.OK);
    }

    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindPermalinkByIdOutput> findById(@PathVariable UUID id) {
        FindPermalinkByIdOutput output = _permalinkAppService.findById(id);
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a permalink with a id=%s", id))
            );

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity find(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    )
        throws Exception {
        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable Pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

        return ResponseEntity.ok(_permalinkAppService.find(searchCriteria, Pageable));
    }
}
