package com.fastcode.example.addons.reporting.application.report;

import com.fastcode.example.addons.reporting.application.report.dto.*;
import com.fastcode.example.addons.reporting.application.reportrole.IReportroleAppService;
import com.fastcode.example.addons.reporting.application.reportrole.dto.CreateReportroleInput;
import com.fastcode.example.addons.reporting.application.reportuser.IReportuserAppService;
import com.fastcode.example.addons.reporting.application.reportuser.dto.CreateReportuserInput;
import com.fastcode.example.addons.reporting.application.reportversion.IReportversionAppService;
import com.fastcode.example.addons.reporting.application.reportversion.IReportversionMapper;
import com.fastcode.example.addons.reporting.application.reportversion.dto.CreateReportversionInput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.CreateReportversionOutput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.UpdateReportversionInput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.UpdateReportversionOutput;
import com.fastcode.example.addons.reporting.domain.dashboarduser.Dashboarduser;
import com.fastcode.example.addons.reporting.domain.dashboarduser.DashboarduserId;
import com.fastcode.example.addons.reporting.domain.dashboarduser.IDashboarduserRepository;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.Dashboardversionreport;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.DashboardversionreportId;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.IDashboardversionreportRepository;
import com.fastcode.example.addons.reporting.domain.report.IReportRepository;
import com.fastcode.example.addons.reporting.domain.report.QReport;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportrole.IReportroleRepository;
import com.fastcode.example.addons.reporting.domain.reportrole.Reportrole;
import com.fastcode.example.addons.reporting.domain.reportrole.ReportroleId;
import com.fastcode.example.addons.reporting.domain.reportuser.IReportuserRepository;
import com.fastcode.example.addons.reporting.domain.reportuser.Reportuser;
import com.fastcode.example.addons.reporting.domain.reportuser.ReportuserId;
import com.fastcode.example.addons.reporting.domain.reportversion.IReportversionRepository;
import com.fastcode.example.addons.reporting.domain.reportversion.Reportversion;
import com.fastcode.example.addons.reporting.domain.reportversion.ReportversionId;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.*;
import com.fastcode.example.domain.core.authorization.role.Role;
import com.fastcode.example.domain.core.authorization.users.Users;
import com.fastcode.example.domain.core.authorization.usersrole.Usersrole;
import com.fastcode.example.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.fastcode.example.domain.extended.authorization.usersrole.IUsersroleRepositoryExtended;
import com.querydsl.core.BooleanBuilder;
import java.time.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service("reportAppService")
public class ReportAppService implements IReportAppService {

    public static final String RUNNING = "running";
    public static final String PUBLISHED = "published";
    @Autowired
    @Qualifier("reportRepository")
    protected IReportRepository _reportRepository;

    @Autowired
    @Qualifier("reportuserRepository")
    protected IReportuserRepository _reportuserRepository;

    @Autowired
    @Qualifier("dashboarduserRepository")
    protected IDashboarduserRepository _dashboarduserRepository;

    @Autowired
    @Qualifier("usersroleRepositoryExtended")
    protected IUsersroleRepositoryExtended _usersroleRepository;

    @Autowired
    @Qualifier("reportuserAppService")
    protected IReportuserAppService _reportuserAppservice;

    @Autowired
    @Qualifier("reportroleAppService")
    protected IReportroleAppService _reportroleAppservice;

    @Autowired
    @Qualifier("reportroleRepository")
    protected IReportroleRepository _reportroleRepository;

    @Autowired
    @Qualifier("reportversionRepository")
    protected IReportversionRepository _reportversionRepository;

    @Autowired
    @Qualifier("usersRepositoryExtended")
    protected IUsersRepositoryExtended _usersRepository;

    @Autowired
    @Qualifier("dashboardversionreportRepository")
    protected IDashboardversionreportRepository _reportDashboardRepository;

    @Autowired
    @Qualifier("reportversionAppService")
    protected IReportversionAppService _reportversionAppservice;

    @Autowired
    @Qualifier("IReportMapperImpl")
    protected IReportMapper mapper;

    @Autowired
    @Qualifier("IReportversionMapperImpl")
    protected IReportversionMapper reportversionMapper;

    @Autowired
    protected LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateReportOutput create(CreateReportInput input) {
        Report report = mapper.createReportInputToReport(input);
        if (input.getOwnerId() != null) {
            Users foundUsers = _usersRepository.findById(input.getOwnerId()).orElse(null);

            if (foundUsers != null) {
                foundUsers.addReports(report);
            }
        }

        Report createdReport = _reportRepository.save(report);
        CreateReportversionInput reportversion = mapper.createReportInputToCreateReportversionInput(input);
        reportversion.setReportId(createdReport.getId());

        CreateReportversionOutput reportversionOutput = _reportversionAppservice.create(reportversion);
        return mapper.reportAndCreateReportversionOutputToCreateReportOutput(createdReport, reportversionOutput);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateReportOutput update(Long reportId, UpdateReportInput input) {
        ReportversionId reportversionId = new ReportversionId(input.getUserId(), reportId, RUNNING);

        Reportversion rv = _reportversionRepository.findById(reportversionId).orElse(null);
        UpdateReportversionInput reportversion = mapper.updateReportInputToUpdateReportversionInput(input);
        reportversion.setReportId(rv.getReport().getId());
        reportversion.setReportVersion(rv.getReportVersion());
        reportversion.setVersiono(rv.getVersiono());
        reportversion.setIsRefreshed(false);

        UpdateReportversionOutput reportversionOutput = _reportversionAppservice.update(reportversionId, reportversion);

        Reportuser reportuser = _reportuserRepository
            .findById(new ReportuserId(reportId, input.getUserId()))
            .orElse(null);
        if (reportuser != null) {
            reportuser.setIsResetted(false);
            _reportuserRepository.save(reportuser);
        }

        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        if (foundReport.getUsers() != null && (foundReport.getUsers().getId().equals(input.getUserId()))) {
            foundReport.setIsPublished(false);
            foundReport = _reportRepository.save(foundReport);
        }

        return mapper.reportAndUpdateReportversionOutputToUpdateReportOutput(foundReport, reportversionOutput);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long reportId, Long userId) {
        Report existing = _reportRepository.findById(reportId).orElse(null);
        _reportversionAppservice.delete(new ReportversionId(userId, reportId, RUNNING));
        _reportversionAppservice.delete(new ReportversionId(userId, reportId, PUBLISHED));

        List<Reportuser> reportUserList = _reportuserRepository.findByReportId(existing.getId());
        for (Reportuser reportuser : reportUserList) {
            reportuser.setOwnerSharingStatus(false);
            _reportuserRepository.save(reportuser);
        }

        existing.setUsers(null);
        existing.setIsPublished(true);
        _reportRepository.save(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindReportByIdOutput findById(Long reportId) {
        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        if (foundReport == null) return null;

        Reportversion reportversion = _reportversionRepository
            .findById(new ReportversionId(foundReport.getUsers().getId(), foundReport.getId(), RUNNING))
            .orElse(null);

        FindReportByIdOutput output = mapper.reportToFindReportByIdOutput(foundReport, reportversion);
        return output;
    }

    //User
    // ReST API Call - GET /report/1/user
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetUsersOutput getUsers(Long reportId) {
        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        if (foundReport == null) {
            logHelper.getLogger().error("There does not exist a report with a id=%s", reportId);
            return null;
        }

        Users re = foundReport.getUsers();
        return mapper.usersToGetUsersOutput(re, foundReport);
    }

    // ReST API Call - GET /report/1/user
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GetUsersOutput> getUserssAssociatedWithReport(Long reportId, String search, Pageable pageable) {
        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        Page<Users> foundUsers = _reportuserRepository.getReportusersList(reportId, search, pageable);
        List<Users> usersList = foundUsers.getContent();
        Iterator<Users> usersIterator = usersList.iterator();
        List<GetUsersOutput> userssList = new ArrayList<>();

        while (usersIterator.hasNext()) {
            Users users = usersIterator.next();
            GetUsersOutput output = mapper.usersToGetUsersOutput(users, foundReport);
            Reportuser reportuser = _reportuserRepository
                .findById(new ReportuserId(reportId, users.getId()))
                .orElse(null);
            if (reportuser != null) {
                output.setEditable(reportuser.getEditable());
            }
            userssList.add(output);
        }

        return userssList;
    }

    // ReST API Call - GET /report/1/user
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GetUsersOutput> getAvailableUserss(Long reportId, String search, Pageable pageable) {
        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        Page<Users> foundUsers = _reportuserRepository.getAvailableReportusersList(reportId, search, pageable);
        List<Users> usersList = foundUsers.getContent();
        Iterator<Users> usersIterator = usersList.iterator();
        List<GetUsersOutput> userssList = new ArrayList<>();

        while (usersIterator.hasNext()) {
            Users users = usersIterator.next();
            GetUsersOutput output = mapper.usersToGetUsersOutput(users, foundReport);
            Reportuser reportuser = _reportuserRepository
                .findById(new ReportuserId(reportId, users.getId()))
                .orElse(null);
            if (reportuser != null) {
                output.setEditable(reportuser.getEditable());
            }
            userssList.add(output);
        }

        return userssList;
    }

    //Role
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GetRoleOutput> getRolesAssociatedWithReport(Long reportId, String search, Pageable pageable) {
        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        Page<Role> foundRole = _reportroleRepository.getReportrolesList(reportId, search, pageable);
        List<Role> roleList = foundRole.getContent();
        Iterator<Role> roleIterator = roleList.iterator();
        List<GetRoleOutput> rolesList = new ArrayList<>();

        while (roleIterator.hasNext()) {
            Role role = roleIterator.next();
            Reportrole reportrole = _reportroleRepository
                .findById(new ReportroleId(reportId, role.getId()))
                .orElse(null);
            GetRoleOutput output = mapper.roleToGetRoleOutput(role, foundReport);
            if (reportrole != null) {
                output.setEditable(reportrole.getEditable());
            }
            rolesList.add(output);
        }

        return rolesList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GetRoleOutput> getAvailableRoles(Long reportId, String search, Pageable pageable) {
        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        Page<Role> foundRole = _reportroleRepository.getAvailableReportrolesList(reportId, search, pageable);
        List<Role> roleList = foundRole.getContent();
        Iterator<Role> roleIterator = roleList.iterator();
        List<GetRoleOutput> rolesList = new ArrayList<>();

        while (roleIterator.hasNext()) {
            Role role = roleIterator.next();
            GetRoleOutput output = mapper.roleToGetRoleOutput(role, foundReport);
            rolesList.add(output);
        }

        return rolesList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindReportByIdOutput findByReportIdAndUsersId(Long reportId, Long userId, String version) {
        Report foundReport = _reportRepository.findByReportIdAndUsersId(reportId, userId);
        Reportuser reportuser = _reportuserRepository.findById(new ReportuserId(reportId, userId)).orElse(null);
        if (foundReport == null && reportuser == null) {
            return null;
        }

        Reportversion reportVersion, publishedversion;
        publishedversion =
            _reportversionRepository.findById(new ReportversionId(userId, reportId, PUBLISHED)).orElse(null);

        if (StringUtils.isNotBlank(version) && version.equalsIgnoreCase(PUBLISHED)) {
            reportVersion = publishedversion;
        } else {
            reportVersion =
                _reportversionRepository.findById(new ReportversionId(userId, reportId, RUNNING)).orElse(null);
        }
        FindReportByIdOutput output = mapper.reportEntitiesToFindReportByIdOutput(
            foundReport,
            reportVersion,
            reportuser
        );

        if (reportuser != null) {
            output.setSharedWithMe(true);
        }

        List<Reportuser> reportuserList = _reportuserRepository.findByReportId(reportId);
        if (reportuserList != null && !reportuserList.isEmpty()) {
            output.setSharedWithOthers(true);
        }
        if (publishedversion == null) {
            output.setIsResetable(false);
        } else {
            output.setIsResetable(true);
        }
        return output;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ReportDetailsOutput updateRecipientSharingStatus(Long userId, Long reportId, Boolean status) {
        Reportuser foundReportuser = _reportuserRepository.findById(new ReportuserId(reportId, userId)).orElse(null);
        if (foundReportuser == null) return null;
        foundReportuser.setRecipientSharingStatus(status);
        foundReportuser = _reportuserRepository.save(foundReportuser);

        Reportversion foundReportversion = _reportversionRepository
            .findById(new ReportversionId(userId, reportId, RUNNING))
            .orElse(null);
        Report foundReport = _reportRepository.findById(reportId).orElse(null);

        return mapper.reportEntitiesToReportDetailsOutput(foundReport, foundReportversion, foundReportuser);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ReportDetailsOutput publishReport(Long userId, Long reportId) {
        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        List<Reportuser> reportuserList = _reportuserRepository.findByReportId(reportId);
        for (Reportuser reportuser : reportuserList) {
            reportuser.setIsRefreshed(false);
            _reportuserRepository.save(reportuser);
        }

        foundReport.setIsPublished(true);
        foundReport = _reportRepository.save(foundReport);
        Reportversion foundReportversion = _reportversionRepository
            .findById(new ReportversionId(userId, reportId, RUNNING))
            .orElse(null);
        Reportversion foundPublishedversion = _reportversionRepository
            .findById(new ReportversionId(userId, reportId, PUBLISHED))
            .orElse(null);
        Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
            foundReportversion,
            userId,
                PUBLISHED
        );

        if (foundPublishedversion != null) {
            publishedVersion.setVersiono(foundPublishedversion.getVersiono());
        } else publishedVersion.setVersiono(0L);

        _reportversionRepository.save(publishedVersion);

        return mapper.reportEntitiesToReportDetailsOutput(foundReport, foundReportversion, null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ReportDetailsOutput refreshReport(Long userId, Long reportId) {
        Reportuser foundReportuser = _reportuserRepository.findById(new ReportuserId(reportId, userId)).orElse(null);
        Report foundReport = _reportRepository.findById(reportId).orElse(null);

        if (foundReportuser != null && foundReportuser.getOwnerSharingStatus()) {
            Reportversion ownerPublishedversion = _reportversionRepository
                .findById(new ReportversionId(foundReport.getUsers().getId(), reportId, PUBLISHED))
                .orElse(null);
            Users foundUsers = _usersRepository.findById(userId).orElse(null);

            Reportversion publishedversion = _reportversionRepository
                .findById(new ReportversionId(userId, reportId, PUBLISHED))
                .orElse(null);
            Reportversion updatedVersion;
            if (publishedversion == null) {
                updatedVersion =
                    reportversionMapper.reportversionToReportversion(ownerPublishedversion, userId, PUBLISHED);
            } else {
                updatedVersion = reportversionMapper.reportversionToReportversion(publishedversion, userId, RUNNING);
            }

            updatedVersion.setUsers(foundUsers);
            _reportversionRepository.save(updatedVersion);
            foundReportuser.setIsRefreshed(true);
            foundReportuser.setIsResetted(false);
            foundReportuser = _reportuserRepository.save(foundReportuser);

            Reportversion runningversion = _reportversionRepository
                .findById(new ReportversionId(userId, reportId, RUNNING))
                .orElse(null);
            ReportDetailsOutput output = mapper.reportEntitiesToReportDetailsOutput(
                foundReport,
                runningversion,
                foundReportuser
            );
            output.setSharedWithMe(true);

            return output;
        } else if (foundReport != null && foundReport.getUsers() != null && foundReport.getUsers().getId().equals(userId)) {
            Reportversion ownerPublishedversion = _reportversionRepository
                .findById(new ReportversionId(userId, reportId, PUBLISHED))
                .orElse(null);
            Reportversion ownerRunningversion = _reportversionRepository
                .findById(new ReportversionId(userId, reportId, RUNNING))
                .orElse(null);

            Users foundUsers = _usersRepository.findById(userId).orElse(null);
            Reportversion updatedVersion = reportversionMapper.reportversionToReportversion(
                ownerPublishedversion,
                userId,
                    RUNNING
            );
            updatedVersion.setUsers(foundUsers);
            updatedVersion.setVersiono(ownerRunningversion.getVersiono());
            updatedVersion.setIsRefreshed(true);
            _reportversionRepository.save(updatedVersion);

            Reportversion runningversion = _reportversionRepository
                .findById(new ReportversionId(userId, reportId, RUNNING))
                .orElse(null);
            ReportDetailsOutput output = mapper.reportEntitiesToReportDetailsOutput(
                foundReport,
                runningversion,
                foundReportuser
            );
            output.setSharedWithMe(false);
            return output;
        }

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ReportDetailsOutput changeOwner(Long ownerId, Long reportId, Long newOwnerId) {
        Report foundReport = _reportRepository.findById(reportId).orElse(null);
        Users foundUsers = _usersRepository.findById(newOwnerId).orElse(null);

        Reportversion reportRunningversion;
        Reportuser ru = _reportuserRepository.findById(new ReportuserId(reportId, newOwnerId)).orElse(null);
        if (ru != null) {
            reportRunningversion =
                _reportversionRepository.findById(new ReportversionId(newOwnerId, reportId, RUNNING)).orElse(null);
            Reportversion reportPublishedversion = _reportversionRepository
                .findById(new ReportversionId(newOwnerId, reportId, PUBLISHED))
                .orElse(null);
            if (reportPublishedversion == null) {
                reportPublishedversion =
                    reportversionMapper.reportversionToReportversion(reportRunningversion, newOwnerId, PUBLISHED);
                reportPublishedversion.setUsers(foundUsers);
                _reportversionRepository.save(reportPublishedversion);
            }

            _reportuserRepository.delete(ru);
        } else {
            Reportversion foundOwnerReportRunningversion = _reportversionRepository
                .findById(new ReportversionId(ownerId, reportId, RUNNING))
                .orElse(null);
            Reportversion foundOwnerReportPublishedversion = _reportversionRepository
                .findById(new ReportversionId(ownerId, reportId, PUBLISHED))
                .orElse(null);
            reportRunningversion =
                reportversionMapper.reportversionToReportversion(
                    foundOwnerReportRunningversion,
                    foundUsers.getId(),
                        RUNNING
                );
            reportRunningversion.setUsers(foundUsers);
            _reportversionRepository.save(reportRunningversion);

            Reportversion reportPublishedversion = reportversionMapper.reportversionToReportversion(
                foundOwnerReportPublishedversion,
                foundUsers.getId(),
                    PUBLISHED
            );

            reportPublishedversion.setUsers(foundUsers);
            _reportversionRepository.save(reportPublishedversion);
        }

        _reportversionAppservice.delete(new ReportversionId(ownerId, reportId, RUNNING));
        _reportversionAppservice.delete(new ReportversionId(ownerId, reportId, PUBLISHED));

        List<Dashboardversionreport> dvrRunningversionsList = _reportDashboardRepository.findByReportIdAndUsersIdAndVersion(
            reportId,
            ownerId,
                RUNNING
        );

        for (Dashboardversionreport dvr : dvrRunningversionsList) {
            List<Dashboardversionreport> sharedDashboardReportList = _reportDashboardRepository.findByIdIfUsersIdNotSame(
                dvr.getDashboardId(),
                reportId,
                ownerId,
                    RUNNING
            );
            for (Dashboardversionreport shared : sharedDashboardReportList) {
                Dashboarduser du = _dashboarduserRepository
                    .findById(new DashboarduserId(shared.getDashboardId(), shared.getUserId()))
                    .orElse(null);
                if (du != null) {
                    du.setIsRefreshed(false);
                    _dashboarduserRepository.save(du);
                }
            }
            Dashboardversionreport ownerPublishedDashboardReport = _reportDashboardRepository
                .findById(new DashboardversionreportId(dvr.getDashboardId(), ownerId, PUBLISHED, reportId))
                .orElse(null);

            _reportDashboardRepository.delete(dvr);
            if (ownerPublishedDashboardReport != null) _reportDashboardRepository.delete(ownerPublishedDashboardReport);
        }

        foundReport.setUsers(foundUsers);
        _reportRepository.save(foundReport);

        return mapper.reportEntitiesToReportDetailsOutput(foundReport, reportRunningversion, null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ReportDetailsOutput resetReport(Long userId, Long reportId) {
        Reportuser foundReportuser = _reportuserRepository.findById(new ReportuserId(reportId, userId)).orElse(null);
        Assert.notNull(foundReportuser,"Reportuser cannot be null");
        Report foundReport = _reportRepository.findById(reportId).orElse(null);

        Reportversion publishedversion = _reportversionRepository
            .findById(new ReportversionId(userId, reportId, PUBLISHED))
            .orElse(null);
        if (publishedversion != null) {
            Reportversion runningversion = reportversionMapper.reportversionToReportversion(
                publishedversion,
                userId,
                    RUNNING
            );

            runningversion = _reportversionRepository.save(runningversion);
            if (!foundReportuser.getEditable()) {
                _reportversionRepository.delete(publishedversion);
            }

            foundReportuser.setIsResetted(true);
            foundReportuser = _reportuserRepository.save(foundReportuser);

            ReportDetailsOutput output = mapper.reportEntitiesToReportDetailsOutput(
                foundReport,
                runningversion,
                foundReportuser
            );
            output.setSharedWithMe(true);

            return output;
        }

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ReportDetailsOutput editReportAccess(
        Long reportId,
        List<ShareReportInputByUser> usersList,
        List<ShareReportInputByRole> rolesList
    ) {
        Report report = _reportRepository.findById(reportId).orElse(null);
        Reportversion ownerPublishedVersion = _reportversionRepository
            .findById(new ReportversionId(report.getUsers().getId(), report.getId(), PUBLISHED))
            .orElse(null);
        if (ownerPublishedVersion == null) {
            return null;
        }

        List<Long> usersWithSharedReportsByRole = new ArrayList<>();
        for (ShareReportInputByRole roleInput : rolesList) {
            Reportrole reportRole = _reportroleRepository
                .findById(new ReportroleId(reportId, roleInput.getId()))
                .orElse(null);
            if (reportRole != null && roleInput.getEditable() != null) {
                reportRole.setEditable(roleInput.getEditable());
                reportRole.setOwnerSharingStatus(true);
                _reportroleRepository.save(reportRole);
            } else if (reportRole != null && roleInput.getEditable() == null) {
                reportRole.setOwnerSharingStatus(false);
                _reportroleRepository.save(reportRole);
            }

            List<Usersrole> userroleList = _usersroleRepository.findByRoleId(roleInput.getId());
            for (Usersrole userrole : userroleList) {
                if (!userrole.getUsersId().equals(report.getUsers().getId())) {
                    usersWithSharedReportsByRole.add(userrole.getUsersId());
                    Reportuser reportuser = _reportuserRepository
                        .findById(new ReportuserId(reportId, userrole.getUsersId()))
                        .orElse(null);

                    if (reportuser != null && reportuser.getIsAssignedByRole()) {
                        if (reportuser != null && roleInput.getEditable() != null) {
                            shareReportWithUser(reportuser, ownerPublishedVersion, roleInput.getEditable());
                            reportuser.setEditable(roleInput.getEditable());
                            reportuser.setIsAssignedByRole(true);
                            _reportuserRepository.save(reportuser);
                        } else if (roleInput.getEditable() == null && reportuser != null) {
                            reportuser.setOwnerSharingStatus(false);
                            _reportuserRepository.save(reportuser);
                        }
                    }
                }
            }
        }

        for (ShareReportInputByUser userInput : usersList) {
            if (
                !userInput.getId().equals(report.getUsers().getId()) &&
                !usersWithSharedReportsByRole.contains(userInput.getId())
            ) {
                Reportuser reportuser = _reportuserRepository
                    .findById(new ReportuserId(reportId, userInput.getId()))
                    .orElse(null);
                if (reportuser != null && !reportuser.getIsAssignedByRole()) {
                    if (reportuser != null && userInput.getEditable() != null) {
                        shareReportWithUser(reportuser, ownerPublishedVersion, userInput.getEditable());
                        reportuser.setEditable(userInput.getEditable());
                        reportuser.setIsAssignedByRole(false);
                        _reportuserRepository.save(reportuser);
                    } else if (userInput.getEditable() == null && reportuser != null) {
                        reportuser.setOwnerSharingStatus(false);
                        _reportuserRepository.save(reportuser);
                    }
                }
            }
        }

        ReportDetailsOutput reportDetails = mapper.reportEntitiesToReportDetailsOutput(
            report,
            ownerPublishedVersion,
            null
        );
        return reportDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ReportDetailsOutput shareReport(
        Long reportId,
        Boolean isAssignedByDashboard,
        List<ShareReportInputByUser> usersList,
        List<ShareReportInputByRole> rolesList
    ) {
        Report report = _reportRepository.findById(reportId).orElse(null);
        Reportversion ownerPublishedVersion = _reportversionRepository
            .findById(new ReportversionId(report.getUsers().getId(), report.getId(), PUBLISHED))
            .orElse(null);

        if (ownerPublishedVersion == null) {
            return null;
        }

        List<Long> usersWithSharedReportsByRole = new ArrayList<>();

        for (ShareReportInputByRole roleInput : rolesList) {
            Reportrole reportrole = _reportroleRepository
                .findById(new ReportroleId(reportId, roleInput.getId()))
                .orElse(null);
            if (reportrole == null) {
                CreateReportroleInput reportRoleInput = new CreateReportroleInput();
                reportRoleInput.setRoleId(roleInput.getId());
                reportRoleInput.setReportId(reportId);
                reportRoleInput.setEditable(roleInput.getEditable());
                reportRoleInput.setOwnerSharingStatus(true);
                _reportroleAppservice.create(reportRoleInput);
            } else if (reportrole != null && !reportrole.getOwnerSharingStatus()) {
                reportrole.setOwnerSharingStatus(true);
                reportrole.setEditable(roleInput.getEditable());
                _reportroleRepository.save(reportrole);
            }

            List<Usersrole> userroleList = _usersroleRepository.findByRoleId(roleInput.getId());
            for (Usersrole userrole : userroleList) {
                usersWithSharedReportsByRole.add(userrole.getUsersId());
                Reportuser reportuser = _reportuserRepository
                    .findById(new ReportuserId(reportId, userrole.getUsersId()))
                    .orElse(null);

                if (reportuser != null) {
                    if (!report.getUsers().getId().equals(reportuser.getUsers().getId())) {
                        shareReportWithUser(reportuser, ownerPublishedVersion, roleInput.getEditable());
                        reportuser.setEditable(roleInput.getEditable());
                        reportuser.setIsAssignedByRole(true);
                        _reportuserRepository.save(reportuser);
                    }
                } else {
                    createReportuserAndReportVersion(
                        ownerPublishedVersion,
                        userrole.getUsersId(),
                        roleInput.getEditable(),
                        true,
                        isAssignedByDashboard
                    );
                }
            }
        }

        for (ShareReportInputByUser userInput : usersList) {
            if (!usersWithSharedReportsByRole.contains(userInput.getId())) {
                Reportuser reportuser = _reportuserRepository
                    .findById(new ReportuserId(reportId, userInput.getId()))
                    .orElse(null);

                if (reportuser != null) {
                    if (!report.getUsers().getId().equals(userInput.getId())) {
                        shareReportWithUser(reportuser, ownerPublishedVersion, userInput.getEditable());
                        reportuser.setEditable(userInput.getEditable());
                        reportuser.setIsAssignedByRole(false);
                        _reportuserRepository.save(reportuser);
                    }
                } else {
                    createReportuserAndReportVersion(
                        ownerPublishedVersion,
                        userInput.getId(),
                        userInput.getEditable(),
                        false,
                        isAssignedByDashboard
                    );
                }
            }
        }

        ReportDetailsOutput reportDetails = mapper.reportEntitiesToReportDetailsOutput(
            report,
            ownerPublishedVersion,
            null
        );
        reportDetails.setSharedWithOthers(true);
        return reportDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createReportuserAndReportVersion(
        Reportversion ownerReportversion,
        Long userId,
        Boolean editable,
        Boolean isAssigByRole,
        Boolean isAssignedByDashboard
    ) {
        CreateReportuserInput createReportuserInput = new CreateReportuserInput();
        createReportuserInput.setReportId(ownerReportversion.getReportId());
        createReportuserInput.setUserId(userId);
        createReportuserInput.setEditable(editable);
        createReportuserInput.setIsAssignedByRole(isAssigByRole);
        createReportuserInput.setIsResetted(true);
        createReportuserInput.setIsRefreshed(true);
        createReportuserInput.setOwnerSharingStatus(true);
        createReportuserInput.setRecipientSharingStatus(true);
        _reportuserAppservice.create(createReportuserInput);

        Users users = _usersRepository.findById(userId).orElse(null);

        if (editable) {
            Reportversion publishedreportversion = reportversionMapper.reportversionToReportversion(
                ownerReportversion,
                users.getId(),
                    PUBLISHED
            );
            publishedreportversion.setUsers(users);
            publishedreportversion.setIsAssignedByDashboard(isAssignedByDashboard);
            _reportversionRepository.save(publishedreportversion);
            Reportversion runningreportversion = reportversionMapper.reportversionToReportversion(
                ownerReportversion,
                users.getId(),
                    RUNNING
            );
            runningreportversion.setUsers(users);
            runningreportversion.setIsAssignedByDashboard(isAssignedByDashboard);
            _reportversionRepository.save(runningreportversion);
        } else {
            Reportversion runningreportversion = reportversionMapper.reportversionToReportversion(
                ownerReportversion,
                users.getId(),
                    RUNNING
            );
            runningreportversion.setUsers(users);
            runningreportversion.setIsAssignedByDashboard(isAssignedByDashboard);
            _reportversionRepository.save(runningreportversion);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void shareReportWithUser(Reportuser reportuser, Reportversion ownerPublishedVersion, Boolean editable) {
        Users users = _usersRepository.findById(reportuser.getUserId()).orElse(null);
        Reportversion reportPublishedVersion = _reportversionRepository
            .findById(new ReportversionId(users.getId(), reportuser.getReportId(), PUBLISHED))
            .orElse(null);
        Reportversion reportRunningVersion = _reportversionRepository
            .findById(new ReportversionId(users.getId(), reportuser.getReportId(), RUNNING))
            .orElse(null);

        if (reportuser.getEditable() && !editable) {
            if (reportuser.getOwnerSharingStatus()) {
                if (reportuser.getIsResetted()) {
                    if (reportPublishedVersion != null) {
                        _reportversionRepository.delete(reportPublishedVersion);
                    }

                    Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
                        ownerPublishedVersion,
                        users.getId(),
                            RUNNING
                    );
                    publishedVersion.setUsers(users);
                    _reportversionRepository.save(publishedVersion);
                } else if (!reportuser.getIsResetted()) {
                    Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
                        ownerPublishedVersion,
                        users.getId(),
                            PUBLISHED
                    );
                    publishedVersion.setUsers(users);
                    _reportversionRepository.save(publishedVersion);
                }
            } else {
                if (reportuser.getIsResetted() && reportPublishedVersion != null) {
                    _reportversionRepository.delete(reportPublishedVersion);
                }
            }
        } else if (!reportuser.getEditable() && !editable) {
            if (reportuser.getOwnerSharingStatus()) {
                if (reportPublishedVersion != null && !reportuser.getIsResetted()) {
                    Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
                        ownerPublishedVersion,
                        users.getId(),
                            PUBLISHED
                    );
                    publishedVersion.setUsers(users);
                    _reportversionRepository.save(publishedVersion);
                } else if (reportuser.getIsResetted()) {
                    if (reportPublishedVersion != null) {
                        _reportversionRepository.delete(reportPublishedVersion);
                    }
                    Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
                        ownerPublishedVersion,
                        users.getId(),
                            RUNNING
                    );

                    publishedVersion.setUsers(users);
                    _reportversionRepository.save(publishedVersion);
                }
            }
        } else if (reportuser.getEditable() && editable) {
            if (reportuser.getOwnerSharingStatus()) {
                Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
                    ownerPublishedVersion,
                    users.getId(),
                        PUBLISHED
                );

                publishedVersion.setUsers(users);
                _reportversionRepository.save(publishedVersion);
            }
        } else if (!reportuser.getEditable() && editable) {
            if (reportuser.getOwnerSharingStatus()) {
                if (reportPublishedVersion != null && !reportuser.getIsResetted()) {
                    Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
                        ownerPublishedVersion,
                        users.getId(),
                            PUBLISHED
                    );

                    publishedVersion.setUsers(users);
                    _reportversionRepository.save(publishedVersion);
                } else if (reportuser.getIsResetted()) {
                    if (reportPublishedVersion != null) {
                        _reportversionRepository.delete(reportPublishedVersion);
                    }

                    Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
                        ownerPublishedVersion,
                        users.getId(),
                            PUBLISHED
                    );
                    publishedVersion.setUsers(users);
                    _reportversionRepository.save(publishedVersion);
                }
            } else {
                Reportversion publishedVersion = reportversionMapper.reportversionToReportversion(
                    reportRunningVersion,
                    users.getId(),
                        PUBLISHED
                );
                publishedVersion.setUsers(users);
                _reportversionRepository.save(publishedVersion);
            }
        }

        if (!reportuser.getOwnerSharingStatus()) {
            reportuser.setOwnerSharingStatus(true);
            reportuser.setIsRefreshed(false);
            _reportuserRepository.save(reportuser);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<ReportDetailsOutput> getSharedReports(Long userId, String search, Pageable pageable) throws Exception {
        Page<ReportDetailsOutput> foundReport = _reportRepository.getSharedReportsByUsersId(userId, search, pageable);
        List<ReportDetailsOutput> reportList = foundReport.getContent();

        return reportList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<ReportDetailsOutput> getReports(Long userId, String search, Pageable pageable) throws Exception {
        Page<ReportDetailsOutput> foundReport = _reportRepository.getAllReportsByUsersId(userId, search, pageable);
        List<ReportDetailsOutput> reportList = foundReport.getContent();

        return reportList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindReportByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Report> foundReport = _reportRepository.findAll(search(search), pageable);
        List<Report> reportList = foundReport.getContent();
        Iterator<Report> reportIterator = reportList.iterator();
        List<FindReportByIdOutput> output = new ArrayList<>();

        while (reportIterator.hasNext()) {
            Report report = reportIterator.next();
            Reportversion reportVersion = _reportversionRepository
                .findById(new ReportversionId(report.getUsers().getId(), report.getId(), RUNNING))
                .orElse(null);
            Reportuser reportuser = _reportuserRepository
                .findById(new ReportuserId(report.getId(), reportVersion.getUserId()))
                .orElse(null);
            FindReportByIdOutput reportOutput = mapper.reportEntitiesToFindReportByIdOutput(
                report,
                reportVersion,
                reportuser
            );
            Reportversion publishedversion = _reportversionRepository
                .findById(new ReportversionId(report.getUsers().getId(), report.getId(), PUBLISHED))
                .orElse(null);
            if (publishedversion == null) {
                reportOutput.setIsResetable(false);
            } else reportOutput.setIsResetable(true);
            output.add(reportOutput);
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QReport report = QReport.report;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(report, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("userId") ||
                    list.get(i).replace("%20", "").trim().equals("ctype") ||
                    list.get(i).replace("%20", "").trim().equals("description") ||
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("query") ||
                    list.get(i).replace("%20", "").trim().equals("reportType") ||
                    list.get(i).replace("%20", "").trim().equals("reportdashboard") ||
                    list.get(i).replace("%20", "").trim().equals("title") ||
                    list.get(i).replace("%20", "").trim().equals("user")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QReport report,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("isPublished")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) builder.and(
                    report.isPublished.eq(Boolean.parseBoolean(details.getValue().getSearchValue()))
                ); else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) builder.and(report.isPublished.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
            }
        }
        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("ownerId")) {
                builder.and(report.users.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public Map<String, String> parseReportdashboardJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("reportId", keysString);
        return joinColumnMap;
    }
}
