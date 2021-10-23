package com.fastcode.example.addons.reporting.restcontrollers;

import com.fastcode.example.addons.reporting.application.dashboardversionreport.IDashboardversionreportAppService;
import com.fastcode.example.addons.reporting.application.dashboardversionreport.dto.FindDashboardversionreportByIdOutput;
import com.fastcode.example.addons.reporting.application.report.IReportAppService;
import com.fastcode.example.addons.reporting.application.report.dto.*;
import com.fastcode.example.addons.reporting.application.reportuser.IReportuserAppService;
import com.fastcode.example.addons.reporting.application.reportuser.dto.FindReportuserByIdOutput;
import com.fastcode.example.addons.reporting.domain.reportuser.ReportuserId;
import com.fastcode.example.application.core.authorization.role.dto.FindRoleByIdOutput;
import com.fastcode.example.application.core.authorization.users.dto.FindUsersByIdOutput;
import com.fastcode.example.application.extended.authorization.role.IRoleAppServiceExtended;
import com.fastcode.example.application.extended.authorization.users.IUsersAppServiceExtended;
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
@RequestMapping("/reporting/report")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ReportController {

    @Autowired
    @Qualifier("reportAppService")
    private IReportAppService _reportAppService;

    @Autowired
    private IReportuserAppService _reportuserAppService;

    @Autowired
    private IRoleAppServiceExtended _roleAppService;

    @Autowired
    @Qualifier("dashboardversionreportAppService")
    private IDashboardversionreportAppService _reportdashboardAppService;

    @Autowired
    @Qualifier("usersAppServiceExtended")
    private IUsersAppServiceExtended _usersAppService;

    @Autowired
    private LoggingHelper logHelper;

    @Autowired
    private Environment env;

    public ReportController(
        IReportAppService reportAppService,
        IDashboardversionreportAppService reportdashboardAppService,
        IUsersAppServiceExtended usersAppService,
        LoggingHelper helper
    ) {
        super();
        this._reportAppService = reportAppService;
        this._reportdashboardAppService = reportdashboardAppService;
        this._usersAppService = usersAppService;
        this.logHelper = helper;
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_CREATE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreateReportOutput> create(@RequestBody @Valid CreateReportInput report) {
        Users users = _usersAppService.getUsers();

        report.setOwnerId(users.getId());
        report.setIsPublished(true);
        report.setIsAssignedByDashboard(false);
        CreateReportOutput output = _reportAppService.create(report);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    // ------------ Delete report ------------
    @PreAuthorize("hasAnyAuthority('REPORTENTITY_DELETE')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public void delete(@PathVariable String id) {
        Users users = _usersAppService.getUsers();
        FindReportByIdOutput output = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("There does not exist a report with a id=%s", id))
            );

        FindReportuserByIdOutput reportuser = _reportuserAppService.findById(
            new ReportuserId(Long.valueOf(id), users.getId())
        );

        if (output.getOwnerId() != users.getId() && reportuser == null) {
            logHelper.getLogger().error("You do not have access to update a report with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to update a report with a id=%s", id)
            );
        }

        _reportAppService.delete(Long.valueOf(id), users.getId());
    }

    // ------------ Update report ------------
    @PreAuthorize("hasAnyAuthority('REPORTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<UpdateReportOutput> update(
        @PathVariable String id,
        @RequestBody @Valid UpdateReportInput report
    ) {
        Users users = _usersAppService.getUsers();
        FindReportByIdOutput currentReport = _reportAppService.findById(report.getId());
        Optional
            .ofNullable(currentReport)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Unable to update. Report with id=%s not found.", id))
            );

        FindReportuserByIdOutput reportuser = _reportuserAppService.findById(
            new ReportuserId(Long.valueOf(id), users.getId())
        );

        if (currentReport.getOwnerId() != users.getId() && reportuser == null) {
            logHelper.getLogger().error("You do not have access to update a report with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to update a report with a id=%s", id)
            );
        }

        report.setUserId(users.getId());
        report.setVersiono(currentReport.getVersiono());

        return new ResponseEntity(_reportAppService.update(Long.valueOf(id), report), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindReportByIdOutput> findById(@PathVariable String id) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput output = _reportAppService.findByReportIdAndUsersId(
            Long.valueOf(id),
            users.getId(),
            "running"
        );
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
    @RequestMapping(
        value = "/{id}/getPublishedVersion",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<FindReportByIdOutput> getPublishedVersion(@PathVariable String id) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput output = _reportAppService.findByReportIdAndUsersId(
            Long.valueOf(id),
            users.getId(),
            "published"
        );
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
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
        Map<String, String> joinColDetails = _reportAppService.parseReportdashboardJoinColumn(id);
        Optional
            .ofNullable(joinColDetails)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid Join Column")));

        searchCriteria.setJoinColumns(joinColDetails);

        List<FindDashboardversionreportByIdOutput> output = _reportdashboardAppService.find(searchCriteria, pageable);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
    @RequestMapping(
        value = "/{id}/user",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<GetUsersOutput>> getUsers(
        @PathVariable String id,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report with id=%s not found.", id)));

        if ((currentReport.getOwnerId() == null || users.getId() != currentReport.getOwnerId())) {
            logHelper.getLogger().error("Unable to get users for report with id '{}'.", id);
            throw new EntityNotFoundException(String.format("Unable to get users for report with id '{}'.", id));
        }

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        List<GetUsersOutput> output = _reportAppService.getUserssAssociatedWithReport(
            Long.valueOf(id),
            search,
            pageable
        );
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
    @RequestMapping(
        value = "/{id}/availableUser",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<GetUsersOutput>> getAvailableUser(
        @PathVariable String id,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    ) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report with id=%s not found.", id)));

        if ((currentReport.getOwnerId() == null || users.getId() != currentReport.getOwnerId())) {
            logHelper.getLogger().error("Unable to get users for report with id {}.", id);
            throw new EntityNotFoundException(String.format("Unable to users for report with id {}.", id));
        }

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        List<GetUsersOutput> output = _reportAppService.getAvailableUserss(Long.valueOf(id), search, pageable);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
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

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report with id=%s not found.", id)));

        if ((currentReport.getOwnerId() == null || users.getId() != currentReport.getOwnerId())) {
            logHelper.getLogger().error("Unable to get users for report with id {}.", id);
            throw new EntityNotFoundException(String.format("Unable to users for report with id {}.", id));
        }

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        List<GetRoleOutput> output = _reportAppService.getRolesAssociatedWithReport(Long.valueOf(id), search, pageable);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
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

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report with id=%s not found.", id)));

        if ((currentReport.getOwnerId() == null || users.getId() != currentReport.getOwnerId())) {
            logHelper.getLogger().error("Unable to get users for report with id {}.", id);
            throw new EntityNotFoundException(String.format("Unable to get users for report with id {}.", id));
        }

        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }

        Pageable pageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        List<GetRoleOutput> output = _reportAppService.getAvailableRoles(Long.valueOf(id), search, pageable);
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
    @RequestMapping(method = RequestMethod.GET, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<List<ReportDetailsOutput>> getReport(
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
        List<ReportDetailsOutput> output = _reportAppService.getReports(users.getId(), search, pageable);

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_READ')")
    @RequestMapping(
        value = "/shared",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<ReportDetailsOutput>> getSharedReport(
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
        List<ReportDetailsOutput> output = _reportAppService.getSharedReports(users.getId(), search, pageable);

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/editAccess",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<ReportDetailsOutput> editReportAccess(
        @PathVariable String id,
        @RequestBody @Valid ShareReportInput input
    ) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report with id=%s not found.", id)));

        if ((currentReport.getOwnerId() == null || users.getId() != currentReport.getOwnerId())) {
            logHelper.getLogger().error("Unable to share report with id '{}'.", id);
            throw new EntityNotFoundException(String.format("Unable to share report with id {}.", id));
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

        ReportDetailsOutput output = _reportAppService.editReportAccess(Long.valueOf(id), usersList, rolesList);
        Optional
            .ofNullable(output)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(String.format("Unable to found user published version with id=%s", id))
            );

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/share",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<ReportDetailsOutput> shareReport(
        @PathVariable String id,
        @RequestBody @Valid ShareReportInput input
    ) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report with id=%s not found.", id)));

        if ((currentReport.getOwnerId() == null || users.getId() != currentReport.getOwnerId())) {
            logHelper.getLogger().error("Unable to share report with id '{}'.", id);
            throw new EntityNotFoundException(String.format("Unable to share report with id {}.", id));
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
                .orElseThrow(() -> new EntityNotFoundException(String.format("Users not found")));
        }

        ReportDetailsOutput output = _reportAppService.shareReport(Long.valueOf(id), false, usersList, rolesList);
        Optional
            .ofNullable(output)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(String.format("Unable to found user published version with id=%s", id))
            );

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/updateRecipientSharingStatus",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<ReportDetailsOutput> updateRecipientSharingStatus(
        @PathVariable String id,
        @RequestBody Boolean status
    ) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Unable to update. Report with id=%s not found.", id))
            );

        ReportDetailsOutput output = _reportAppService.updateRecipientSharingStatus(
            users.getId(),
            Long.valueOf(id),
            status
        );
        Optional
            .ofNullable(output)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report user does't exists")));

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/publish",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<ReportDetailsOutput> publishReport(@PathVariable String id) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Unable to update. Report with id=%s not found.", id))
            );

        if (!users.getId().equals(currentReport.getOwnerId())) {
            logHelper.getLogger().error("You do not have access to publish a report with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to publish a report with a id=%s", id)
            );
        }

        if (currentReport.getIsPublished()) {
            logHelper.getLogger().error("Report is already published with a id=%s", id);
            throw new EntityExistsException(String.format("Report is already published with a id=%s", id));
        }

        ReportDetailsOutput output = _reportAppService.publishReport(users.getId(), Long.valueOf(id));
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/refresh",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<ReportDetailsOutput> refreshReport(@PathVariable String id) {
        Users users = _usersAppService.getUsers();

        FindReportByIdOutput report = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(report)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report not found with id=%s", id)));

        FindReportuserByIdOutput reportuser = _reportuserAppService.findById(
            new ReportuserId(Long.valueOf(id), users.getId())
        );

        if (reportuser == null && !users.getId().equals(report.getOwnerId())) {
            logHelper.getLogger().error("You do not have access to refresh report with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to refresh report with a id=%s", id)
            );
        }

        ReportDetailsOutput output = _reportAppService.refreshReport(users.getId(), Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("No updates available. Report can not be refreshed"))
            );

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/changeOwner/{userId}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<ReportDetailsOutput> changeOwner(@PathVariable String id, @PathVariable String userId) {
        Users users = _usersAppService.getUsers();

        FindUsersByIdOutput output = _usersAppService.findById(Long.valueOf(userId));

        FindReportByIdOutput currentReport = _reportAppService.findById(Long.valueOf(id));
        Optional
            .ofNullable(currentReport)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Unable to update. Report with id=%s not found.", id))
            );

        if (output == null) {
            logHelper.getLogger().error("User does not exist with id=%s", id);
            throw new EntityNotFoundException(String.format("User does not exist with id=%s", id));
        }

        if (!users.getId().equals(currentReport.getOwnerId())) {
            logHelper.getLogger().error("You do not have access to update owner of a report with a id=%s", id);
            throw new EntityNotFoundException(
                String.format("You do not have access to update owner of a report with a id=%s", id)
            );
        }

        if (currentReport.getOwnerId() == output.getId()) {
            logHelper.getLogger().error("Report is already owned by user with id= %s", id);
            throw new EntityExistsException(String.format("Report is already owned by userwith a id=%s", id));
        }

        return new ResponseEntity(
            _reportAppService.changeOwner(currentReport.getOwnerId(), Long.valueOf(id), output.getId()),
            HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyAuthority('REPORTENTITY_UPDATE')")
    @RequestMapping(
        value = "/{id}/reset",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<ReportDetailsOutput> resetReport(@PathVariable String id) {
        Users users = _usersAppService.getUsers();

        FindReportuserByIdOutput reportuser = _reportuserAppService.findById(
            new ReportuserId(Long.valueOf(id), users.getId())
        );
        Optional
            .ofNullable(reportuser)
            .orElseThrow(() -> new EntityNotFoundException(String.format("No Report is shared with id=%s", id)));

        ReportDetailsOutput output = _reportAppService.resetReport(users.getId(), Long.valueOf(id));
        Optional
            .ofNullable(output)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Report can not be resetted")));

        return new ResponseEntity(output, HttpStatus.OK);
    }
}
