package com.fastcode.example.addons.reporting.restcontrollers;

import com.fastcode.example.addons.reporting.application.dashboard.IDashboardAppService;
import com.fastcode.example.addons.reporting.application.dashboard.dto.*;
import com.fastcode.example.addons.reporting.application.dashboarduser.IDashboarduserAppService;
import com.fastcode.example.addons.reporting.application.dashboarduser.dto.FindDashboarduserByIdOutput;
import com.fastcode.example.addons.reporting.application.dashboardversionreport.IDashboardversionreportAppService;
import com.fastcode.example.addons.reporting.application.dashboardversionreport.dto.FindDashboardversionreportByIdOutput;
import com.fastcode.example.addons.reporting.application.report.IReportAppService;
import com.fastcode.example.addons.reporting.application.report.dto.FindReportByIdOutput;
import com.fastcode.example.addons.reporting.application.report.dto.ShareReportInput;
import com.fastcode.example.addons.reporting.application.report.dto.ShareReportInputByRole;
import com.fastcode.example.addons.reporting.application.report.dto.ShareReportInputByUser;
import com.fastcode.example.addons.reporting.domain.dashboarduser.DashboarduserId;
import com.fastcode.example.application.core.authorization.role.dto.FindRoleByIdOutput;
import com.fastcode.example.application.core.authorization.users.dto.FindUsersByIdOutput;
import com.fastcode.example.application.extended.authorization.role.IRoleAppServiceExtended;
import com.fastcode.example.application.extended.authorization.users.IUsersAppServiceExtended;
import com.fastcode.example.commons.domain.EmptyJsonResponse;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.OffsetBasedPageRequest;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchUtils;
import com.fastcode.example.domain.core.authorization.users.Users;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SuppressWarnings({ "unchecked", "rawtypes" })
@RequestMapping("/reporting/dashboard")
public class DashboardController {

    @Autowired
    @Qualifier("dashboardAppService")
    private IDashboardAppService _dashboardAppService;

    @Autowired
    private IDashboarduserAppService _dashboarduserAppService;

    @Autowired
    private IRoleAppServiceExtended _roleAppService;

    @Autowired
    @Qualifier("dashboardversionreportAppService")
    private IDashboardversionreportAppService _reportdashboardAppService;

    @Autowired
    @Qualifier("reportAppService")
    private IReportAppService _reportAppService;

    @Autowired
    @Qualifier("usersAppServiceExtended")
    private IUsersAppServiceExtended _usersAppService;

    @Autowired
    private LoggingHelper logHelper;

    @Autowired
    private Environment env;

    public DashboardController(
        IDashboardAppService dashboardAppService,
        IDashboardversionreportAppService reportdashboardAppService,
        IUsersAppServiceExtended usersAppService,
        LoggingHelper helper
    ) {
        super();
        this._dashboardAppService = dashboardAppService;
        this._reportdashboardAppService = reportdashboardAppService;
        this._usersAppService = usersAppService;
        this.logHelper = helper;
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_CREATE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreateDashboardOutput> create(@RequestBody @Valid CreateDashboardInput dashboard) {
        Users users = _usersAppService.getUsers();

        dashboard.setOwnerId(users.getId());
        dashboard.setIsPublished(true);
        dashboard.setIsShareable(true);
        CreateDashboardOutput output = _dashboardAppService.create(dashboard);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    // ------------ Delete dashboard ------------
    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_DELETE')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void delete(@PathVariable String id) {
        Users users = _usersAppService.getUsers();
        FindDashboardByIdOutput output = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a dashboard with a id=%s", id))
            );

        FindDashboarduserByIdOutput dashboarduser = _dashboarduserAppService.findById(
            new DashboarduserId(Long.valueOf(id), users.getId())
        );

        if (output.getOwnerId() != users.getId() && dashboarduser == null) {
            logHelper.getLogger().error("You do not have access to update a dashboard with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to update a dashboard with a id=%s", id)
            );
        }

        _dashboardAppService.delete(Long.valueOf(id), users.getId());
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_DELETE')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}/report/{reportId}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void deleteReportFromDashboard(@PathVariable String id, @PathVariable String reportId) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a dashboard with a id=%s", id))
            );
        FindDashboarduserByIdOutput dashboarduser = _dashboarduserAppService.findById(
            new DashboarduserId(Long.valueOf(id), users.getId())
        );

        if (currentDashboard.getOwnerId() != users.getId() && dashboarduser == null) {
            logHelper.getLogger().error("You do not have access to update a dashboard with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to update a dashboard with a id=%s", id)
            );
        }

        _dashboardAppService.deleteReportFromDashboard(Long.valueOf(id), Long.valueOf(reportId), users.getId());
    }

    // ------------ Update dashboard ------------
    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UpdateDashboardOutput> update(
        @PathVariable String id,
        @RequestBody @Valid UpdateDashboardInput dashboard
    ) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(String.format("Unable to update. Dashboard with id=%s not found.", id))
            );

        FindDashboarduserByIdOutput dashboarduser = _dashboarduserAppService.findById(
            new DashboarduserId(Long.valueOf(id), users.getId())
        );

        if (currentDashboard.getOwnerId() != users.getId() && dashboarduser == null) {
            logHelper.getLogger().error("You do not have access to update a dashboard with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to update a dashboard with a id=%s", id)
            );
        }

        dashboard.setUserId(users.getId());
        //dashboard.setVersiono(currentDashboard.getVersiono());

        UpdateDashboardOutput output = _dashboardAppService.update(Long.valueOf(id), dashboard);
        output.setReportDetails(_dashboardAppService.setReportsList(Long.valueOf(id), users.getId(), "running"));
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> findById(@PathVariable String id) {
        Users users = _usersAppService.getUsers();
        FindDashboardByIdOutput output = _dashboardAppService.findByDashboardIdAndUsersId(
            Long.valueOf(id),
            users.getId(),
            "running"
        );

        Optional
            .ofNullable(output)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Dashboard with id=%s not found.", id)));
        output.setReportDetails(_dashboardAppService.setReportsList(Long.valueOf(id), users.getId(), "running"));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/{id}/getPublishedVersion",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> getPublishedVersion(@PathVariable String id) {
        Users users = _usersAppService.getUsers();
        FindDashboardByIdOutput output = _dashboardAppService.findByDashboardIdAndUsersId(
            Long.valueOf(id),
            users.getId(),
            "published"
        );

        Optional
            .ofNullable(output)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Dashboard with id=%s not found.", id)));
        output.setReportDetails(_dashboardAppService.setReportsList(Long.valueOf(id), users.getId(), "published"));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_CREATE')")
    @RequestMapping(
        value = "/addNewReportToNewDashboard",
        method = RequestMethod.POST,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> addNewReportsToNewDashboard(
        @RequestBody AddNewReportToNewDashboardInput input
    ) {
        Users users = _usersAppService.getUsers();

        input.setOwnerId(users.getId());
        input.setIsPublished(true);

        FindDashboardByIdOutput output = _dashboardAppService.addNewReportsToNewDashboard(input);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/addNewReportToExistingDashboard",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> addNewReportsToExistingDashboard(
        @RequestBody AddNewReportToExistingDashboardInput input
    ) {
        FindDashboardByIdOutput dashboard = _dashboardAppService.findById(input.getId());
        Optional
            .ofNullable(dashboard)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format("There does not exist a dashboard with a id=%s", input.getId())
                    )
            );

        Users users = _usersAppService.getUsers();
        FindDashboarduserByIdOutput dashboarduser = _dashboarduserAppService.findById(
            new DashboarduserId(Long.valueOf(input.getId()), users.getId())
        );

        if (dashboard.getOwnerId() != users.getId() && (dashboarduser == null || !dashboarduser.getEditable())) {
            logHelper.getLogger().error("You do not have access to add report to dashboard with id=%s", input.getId());
            throw new EntityNotFoundException(
                String.format("You do not have access to add report to dashboard dashboard with id=%s", input.getId())
            );
        }

        input.setOwnerId(users.getId());
        FindDashboardByIdOutput output = _dashboardAppService.addNewReportsToExistingDashboard(input);

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_CREATE')")
    @RequestMapping(
        value = "/addExistingReportToNewDashboard",
        method = RequestMethod.POST,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> addExistingReportsToNewDashboard(
        @RequestBody AddExistingReportToNewDashboardInput input
    ) {
        for (ExistingReportInput reportInput : input.getReportDetails()) {
            FindReportByIdOutput report = _reportAppService.findById(reportInput.getId());
            Optional
                .ofNullable(report)
                .orElseThrow(
                    () ->
                        new EntityNotFoundException(
                            String.format("There does not exist a report with a id=%s", reportInput.getId())
                        )
                );
        }

        Users users = _usersAppService.getUsers();

        input.setOwnerId(users.getId());
        input.setIsPublished(true);
        FindDashboardByIdOutput output = _dashboardAppService.addExistingReportsToNewDashboard(input);

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/addExistingReportToExistingDashboard",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> addExistingReportsToExistingDashboard(
        @RequestBody AddExistingReportToExistingDashboardInput input
    ) {
        FindDashboardByIdOutput dashboard = _dashboardAppService.findById(input.getId());
        Optional
            .ofNullable(dashboard)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format("There does not exist a dashboard with a id=%s", input.getId())
                    )
            );

        Users users = _usersAppService.getUsers();
        FindDashboarduserByIdOutput dashboarduser = _dashboarduserAppService.findById(
            new DashboarduserId(Long.valueOf(input.getId()), users.getId())
        );

        if (dashboard.getOwnerId() != users.getId() && (dashboarduser == null || !dashboarduser.getEditable())) {
            logHelper.getLogger().error("You do not have access to add report to dashboard with id=%s", input.getId());
            throw new EntityNotFoundException(
                String.format("You do not have access to add report to dashboard dashboard with id=%s", input.getId())
            );
        }

        dashboard.setReportDetails(_dashboardAppService.setReportsList(input.getId(), users.getId(), "running"));

        for (FindReportByIdOutput reportInput : dashboard.getReportDetails()) {
            if (
                input
                    .getReportDetails()
                    .stream()
                    .filter(o -> o.getId().equals(reportInput.getId()))
                    .findFirst()
                    .isPresent()
            ) {
                logHelper.getLogger().error("Report already exist in dashboard with a id=%s", input.getId());
                throw new EntityExistsException(
                    String.format("Report already exist in dashboard with a id=%s", input.getId())
                );
            }
        }

        for (ExistingReportInput reportInput : input.getReportDetails()) {
            FindReportByIdOutput report = _reportAppService.findById(reportInput.getId());
            if (report == null) {
                logHelper.getLogger().error("There does not exist a report with a id=%s", reportInput.getId());
                throw new EntityNotFoundException(
                    String.format("There does not exist a report with a id=%s", reportInput.getId())
                );
            }
        }

        input.setOwnerId(users.getId());
        FindDashboardByIdOutput output = _dashboardAppService.addExistingReportsToExistingDashboard(input);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/{id}/reportdashboard",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity getReportdashboard(
        @PathVariable String id,
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

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);

        Map<String, String> joinColDetails = _dashboardAppService.parseReportdashboardJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid join column")));
        searchCriteria.setJoinColumns(joinColDetails);

        List<FindDashboardversionreportByIdOutput> output = _reportdashboardAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/{id}/user",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<GetUsersOutput> getUsers(
        @PathVariable String id,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) {
        Users users = _usersAppService.getUsers();
        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a dashboard with a id=%s", id))
            );

        if (currentDashboard.getOwnerId() == null || users.getId() != currentDashboard.getOwnerId()) {
            logHelper.getLogger().error("Unable to get users for dashboard with id '{}'.", id);
            throw new EntityNotFoundException(String.format("Unable to get users for dashboard with id '{}'.", id));
        }

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

        List<GetUsersOutput> output = _dashboardAppService.getUserssAssociatedWithDashboard(
            Long.valueOf(id),
            search,
            pageable
        );
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/{id}/availableUser",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<GetUsersOutput>> getAvailableUsers(
        @PathVariable String id,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) {
        Users users = _usersAppService.getUsers();
        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a dashboard with a id=%s", id))
            );

        if (currentDashboard.getOwnerId() == null || users.getId() != currentDashboard.getOwnerId()) {
            logHelper.getLogger().error("Unable to get Userss for dashboard with id=%s.", id);
            throw new EntityNotFoundException(String.format("Unable to users for dashboard with id=%s.", id));
        }

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

        List<GetUsersOutput> output = _dashboardAppService.getAvailableUserss(Long.valueOf(id), search, pageable);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/{id}/role",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<GetRoleOutput>> getRole(
        @PathVariable String id,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a dashboard with a id=%s", id))
            );

        if (currentDashboard.getOwnerId() == null || users.getId() != currentDashboard.getOwnerId()) {
            logHelper.getLogger().error("Unable to get users for dashboard with id=%s", id);
            throw new EntityNotFoundException(String.format("Unable to users for dashboard with id=%s.", id));
        }

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

        List<GetRoleOutput> output = _dashboardAppService.getRolesAssociatedWithDashboard(
            Long.valueOf(id),
            search,
            pageable
        );
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/{id}/availableRole",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<GetRoleOutput>> getAvailableRole(
        @PathVariable String id,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a dashboard with a id=%s", id))
            );

        if (currentDashboard.getOwnerId() == null || users.getId() != currentDashboard.getOwnerId()) {
            logHelper.getLogger().error("Unable to get users for dashboard with id=%s.", id);
            throw new EntityNotFoundException(String.format("Unable to get users for dashboard with id=%s.", id));
        }

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);

        List<GetRoleOutput> output = _dashboardAppService.getAvailableRoles(Long.valueOf(id), search, pageable);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(method = RequestMethod.GET, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<List<DashboardDetailsOutput>> getDashboard(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    )
        throws Exception {
        Users users = _usersAppService.getUsers();

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        List<DashboardDetailsOutput> output = _dashboardAppService.getDashboards(users.getId(), search, pageable);

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/available/{reportId}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<DashboardDetailsOutput>> getAvailableDashboard(
        @PathVariable String reportId,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    )
        throws Exception {
        Users users = _usersAppService.getUsers();

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        List<DashboardDetailsOutput> output = _dashboardAppService.getAvailableDashboards(
            users.getId(),
            Long.valueOf(reportId),
            search,
            pageable
        );

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_READ')")
    @RequestMapping(
        value = "/shared",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<DashboardDetailsOutput>> getSharedDashboard(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    )
        throws Exception {
        Users users = _usersAppService.getUsers();

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        List<DashboardDetailsOutput> output = _dashboardAppService.getSharedDashboards(users.getId(), search, pageable);

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/editAccess",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<DashboardDetailsOutput> editDashboardAccess(
        @PathVariable String id,
        @RequestBody @Valid ShareReportInput input
    ) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a dashboard with a id=%s", id))
            );

        if (currentDashboard.getOwnerId() == null || users.getId() != currentDashboard.getOwnerId()) {
            logHelper.getLogger().error("Unable to change access of dashboard with id=%s.", id);
            throw new EntityNotFoundException(String.format("Unable to change access of dashboard with id=%s.", id));
        }

        List<ShareReportInputByRole> rolesList;
        if (input.getRolesList() != null) {
            rolesList = input.getRolesList();
        } else {
            rolesList = new ArrayList<>();
        }
        List<ShareReportInputByUser> usersList;

        if (input.getUsersList() != null) {
            usersList = input.getUsersList();
        } else {
            usersList = new ArrayList<>();
        }

        for (ShareReportInputByRole roleInput : rolesList) {
            FindRoleByIdOutput foundRole = _roleAppService.findById(roleInput.getId());
            Optional
                .ofNullable(foundRole)
                .orElseThrow(
                    () -> new EntityNotFoundException(String.format("Role not found with id=%s", roleInput.getId()))
                );
        }

        for (ShareReportInputByUser userInput : usersList) {
            FindUsersByIdOutput foundUsers = _usersAppService.findById(userInput.getId());
            Optional
                .ofNullable(foundUsers)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Users not found.")));
        }

        DashboardDetailsOutput output = _dashboardAppService.editDashboardAccess(
            Long.valueOf(id),
            usersList,
            rolesList
        );
        Optional
            .ofNullable(output)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(String.format("Unable to found user published version with id=%s", id))
            );

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/share",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<DashboardDetailsOutput> shareDashboard(
        @PathVariable String id,
        @RequestBody @Valid ShareReportInput input
    ) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a dashboard with a id=%s", id))
            );

        if (currentDashboard.getOwnerId() == null || users.getId() != currentDashboard.getOwnerId()) {
            logHelper.getLogger().error("Unable to share dashboard with id=%s.", id);
            throw new EntityNotFoundException(String.format("Unable to share dashboard with id=%s.", id));
        }

        List<ShareReportInputByRole> rolesList;
        if (input.getRolesList() != null) {
            rolesList = input.getRolesList();
        } else {
            rolesList = new ArrayList<>();
        }
        List<ShareReportInputByUser> usersList;

        if (input.getUsersList() != null) {
            usersList = input.getUsersList();
        } else {
            usersList = new ArrayList<>();
        }

        for (ShareReportInputByRole roleInput : rolesList) {
            FindRoleByIdOutput foundRole = _roleAppService.findById(roleInput.getId());
            Optional
                .ofNullable(foundRole)
                .orElseThrow(
                    () -> new EntityNotFoundException(String.format("Role not found with id=%s", roleInput.getId()))
                );
        }

        for (ShareReportInputByUser userInput : usersList) {
            FindUsersByIdOutput foundUsers = _usersAppService.findById(userInput.getId());
            Optional
                .ofNullable(foundUsers)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Users not found.")));
        }

        DashboardDetailsOutput output = _dashboardAppService.shareDashboard(Long.valueOf(id), usersList, rolesList);
        Optional
            .ofNullable(output)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(String.format("Unable to found user published version with id=%s", id))
            );

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/updateRecipientSharingStatus",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<DashboardDetailsOutput> updateRecipientSharingStatus(
        @PathVariable String id,
        @RequestBody Boolean status
    ) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(String.format("Unable to update. Dashboard with id=%s not found.", id))
            );

        DashboardDetailsOutput output = _dashboardAppService.updateRecipientSharingStatus(
            users.getId(),
            Long.valueOf(id),
            status
        );
        Optional
            .ofNullable(output)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Dashboard user does't exists")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/publish",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> publishDashboard(@PathVariable String id) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(String.format("Unable to publish. Dashboard with id=%s not found.", id))
            );

        if (!currentDashboard.getIsShareable()) {
            logHelper.getLogger().error("Unable to publish. Dashboard have shared reports.");
            return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.NOT_FOUND);
        }

        if (!users.getId().equals(currentDashboard.getOwnerId())) {
            logHelper.getLogger().error("You do not have access to publish a dashboard with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to publish a dashboard with a id=%s", id)
            );
        }

        if (currentDashboard.getIsPublished()) {
            logHelper.getLogger().error("Dashboard is already published with a id=%s", id);
            throw new EntityExistsException(String.format("Dashboard is already published with a id=%s", id));
        }

        FindDashboardByIdOutput output = _dashboardAppService.publishDashboard(users.getId(), Long.valueOf(id));
        output.setReportDetails(_dashboardAppService.setReportsList(output.getId(), users.getId(), "running"));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/refresh",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> refreshDashboard(@PathVariable String id) {
        Users users = _usersAppService.getUsers();

        FindDashboardByIdOutput dashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(dashboard)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Dashboard not found with id=%s", id)));
        FindDashboarduserByIdOutput dashboarduser = _dashboarduserAppService.findById(
            new DashboarduserId(Long.valueOf(id), users.getId())
        );

        if (dashboarduser == null && !users.getId().equals(dashboard.getOwnerId())) {
            logHelper.getLogger().error("You do not have access to refresh dashboard with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to refresh dashboard with a id=%s", id)
            );
        }

        FindDashboardByIdOutput output = _dashboardAppService.refreshDashboard(users.getId(), Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("No updates available. Dashboard can not be refreshed"))
            );

        output.setReportDetails(_dashboardAppService.setReportsList(output.getId(), users.getId(), "running"));
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/changeOwner/{userId}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<DashboardDetailsOutput> changeOwner(@PathVariable String id, @PathVariable String userId) {
        Users users = _usersAppService.getUsers();

        FindUsersByIdOutput output = _usersAppService.findById(Long.valueOf(userId));

        FindDashboardByIdOutput currentDashboard = _dashboardAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentDashboard)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(String.format("Unable to update. Dashboard with id=%s not found.", id))
            );

        if (output == null) {
            logHelper.getLogger().error("User does not exist with id=%s", id);
            throw new EntityNotFoundException(String.format("User does not exist with id=%s", id));
        }

        if (!users.getId().equals(currentDashboard.getOwnerId())) {
            logHelper.getLogger().error("You do not have access to update owner of a dashboard with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to update owner of a dashboard with a id=%s", id)
            );
        }

        if (currentDashboard.getOwnerId() == output.getId()) {
            logHelper.getLogger().error("Dashboard is already owned by user with id= %s", id);
            throw new EntityExistsException(String.format("Dashboard is already owned by userwith a id=%s", id));
        }

        return new ResponseEntity(
            _dashboardAppService.changeOwner(currentDashboard.getOwnerId(), Long.valueOf(id), output.getId()),
            HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyAuthority('DASHBOARDENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/reset",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindDashboardByIdOutput> resetDashboard(@PathVariable String id) {
        Users users = _usersAppService.getUsers();

        FindDashboarduserByIdOutput dashboarduser = _dashboarduserAppService.findById(
            new DashboarduserId(Long.valueOf(id), users.getId())
        );
        Optional
            .ofNullable(dashboarduser)
            .orElseThrow(() -> new EntityNotFoundException(String.format("No Dashboard is shared with id=%s", id)));

        FindDashboardByIdOutput output = _dashboardAppService.resetDashboard(users.getId(), Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Dashboard can not be resetted")));

        output.setReportDetails(_dashboardAppService.setReportsList(output.getId(), users.getId(), "running"));
        return new ResponseEntity(output, HttpStatus.OK);
    }
}
