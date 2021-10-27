package com.fastcode.example.addons.reporting.application.dashboard;

import com.fastcode.example.addons.reporting.application.dashboard.dto.*;
import com.fastcode.example.addons.reporting.application.dashboardrole.IDashboardroleAppService;
import com.fastcode.example.addons.reporting.application.dashboardrole.dto.CreateDashboardroleInput;
import com.fastcode.example.addons.reporting.application.dashboarduser.IDashboarduserAppService;
import com.fastcode.example.addons.reporting.application.dashboarduser.dto.CreateDashboarduserInput;
import com.fastcode.example.addons.reporting.application.dashboardversion.IDashboardversionAppService;
import com.fastcode.example.addons.reporting.application.dashboardversion.IDashboardversionMapper;
import com.fastcode.example.addons.reporting.application.dashboardversion.dto.CreateDashboardversionInput;
import com.fastcode.example.addons.reporting.application.dashboardversion.dto.CreateDashboardversionOutput;
import com.fastcode.example.addons.reporting.application.dashboardversion.dto.UpdateDashboardversionInput;
import com.fastcode.example.addons.reporting.application.dashboardversion.dto.UpdateDashboardversionOutput;
import com.fastcode.example.addons.reporting.application.dashboardversionreport.IDashboardversionreportAppService;
import com.fastcode.example.addons.reporting.application.report.IReportAppService;
import com.fastcode.example.addons.reporting.application.report.IReportMapper;
import com.fastcode.example.addons.reporting.application.report.dto.CreateReportInput;
import com.fastcode.example.addons.reporting.application.report.dto.CreateReportOutput;
import com.fastcode.example.addons.reporting.application.report.dto.FindReportByIdOutput;
import com.fastcode.example.addons.reporting.application.report.dto.ShareReportInputByRole;
import com.fastcode.example.addons.reporting.application.report.dto.ShareReportInputByUser;
import com.fastcode.example.addons.reporting.application.report.dto.UpdateReportInput;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboard.IDashboardRepository;
import com.fastcode.example.addons.reporting.domain.dashboard.QDashboard;
import com.fastcode.example.addons.reporting.domain.dashboardrole.Dashboardrole;
import com.fastcode.example.addons.reporting.domain.dashboardrole.DashboardroleId;
import com.fastcode.example.addons.reporting.domain.dashboardrole.IDashboardroleRepository;
import com.fastcode.example.addons.reporting.domain.dashboarduser.Dashboarduser;
import com.fastcode.example.addons.reporting.domain.dashboarduser.DashboarduserId;
import com.fastcode.example.addons.reporting.domain.dashboarduser.IDashboarduserRepository;
import com.fastcode.example.addons.reporting.domain.dashboardversion.Dashboardversion;
import com.fastcode.example.addons.reporting.domain.dashboardversion.DashboardversionId;
import com.fastcode.example.addons.reporting.domain.dashboardversion.IDashboardversionRepository;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.Dashboardversionreport;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.DashboardversionreportId;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.IDashboardversionreportRepository;
import com.fastcode.example.addons.reporting.domain.report.IReportRepository;
import com.fastcode.example.addons.reporting.domain.report.Report;
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

import java.net.MalformedURLException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("dashboardAppService")
public class DashboardAppService implements IDashboardAppService {

    public static final String RUNNING = "running";
    public static final String MEDIUMCHART = "mediumchart";
    public static final String PUBLISHED = "published";
    @Autowired
    @Qualifier("dashboardRepository")
    protected IDashboardRepository _dashboardRepository;

    @Autowired
    @Qualifier("dashboardversionreportAppService")
    protected IDashboardversionreportAppService _reportDashboardAppService;

    @Autowired
    @Qualifier("reportAppService")
    protected IReportAppService _reportAppService;

    @Autowired
    @Qualifier("dashboardversionAppService")
    protected IDashboardversionAppService _dashboardversionAppservice;

    @Autowired
    @Qualifier("IDashboardversionMapperImpl")
    protected IDashboardversionMapper dashboardversionMapper;

    @Autowired
    @Qualifier("dashboardroleAppService")
    protected IDashboardroleAppService _dashboardroleAppservice;

    @Autowired
    @Qualifier("dashboarduserAppService")
    protected IDashboarduserAppService _dashboarduserAppservice;

    @Autowired
    @Qualifier("dashboarduserRepository")
    protected IDashboarduserRepository _dashboarduserRepository;

    @Autowired
    @Qualifier("reportuserRepository")
    protected IReportuserRepository _reportuserRepository;

    @Autowired
    @Qualifier("dashboardroleRepository")
    protected IDashboardroleRepository _dashboardroleRepository;

    @Autowired
    @Qualifier("usersroleRepositoryExtended")
    protected IUsersroleRepositoryExtended _usersroleRepository;

    @Autowired
    @Qualifier("dashboardversionRepository")
    protected IDashboardversionRepository _dashboardversionRepository;

    @Autowired
    @Qualifier("reportversionRepository")
    protected IReportversionRepository _reportversionRepository;

    @Autowired
    @Qualifier("dashboardversionreportRepository")
    protected IDashboardversionreportRepository _reportDashboardRepository;

    @Autowired
    @Qualifier("usersRepositoryExtended")
    protected IUsersRepositoryExtended _usersRepository;

    @Autowired
    @Qualifier("IDashboardMapperImpl")
    protected IDashboardMapper mapper;

    @Autowired
    @Qualifier("IReportMapperImpl")
    protected IReportMapper reportMapper;

    @Autowired
    @Qualifier("reportRepository")
    protected IReportRepository _reportRepository;

    @Autowired
    protected LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateDashboardOutput create(CreateDashboardInput input) {
        Dashboard dashboard = mapper.createDashboardInputToDashboard(input);
        if (input.getOwnerId() != null) {
            Users foundUsers = _usersRepository.findById(input.getOwnerId()).orElse(null);

            if (foundUsers != null) {
                foundUsers.addDashboards(dashboard);
            }
        }

        Dashboard createdDashboard = _dashboardRepository.save(dashboard);
        CreateDashboardversionInput dashboardversion = mapper.creatDashboardInputToCreateDashboardversionInput(input);
        dashboardversion.setDashboardId(createdDashboard.getId());

        CreateDashboardversionOutput dashboardversionOutput = _dashboardversionAppservice.create(dashboardversion);

        return mapper.dashboardAndCreateDashboardversionOutputToCreateDashboardOutput(
            createdDashboard,
            dashboardversionOutput
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateDashboardOutput update(Long dashboardId, UpdateDashboardInput input) {
        DashboardversionId dashboardversionId = new DashboardversionId(input.getUserId(), dashboardId, RUNNING);

        Dashboardversion rv = _dashboardversionRepository.findById(dashboardversionId).orElse(null);
        UpdateDashboardversionInput dashboardversion = mapper.updateDashboardInputToUpdateDashboardversionInput(input);
        dashboardversion.setDashboardId(rv.getDashboard().getId());
        dashboardversion.setDashboardVersion(rv.getDashboardVersion());
        dashboardversion.setVersiono(rv.getVersiono());
        dashboardversion.setIsRefreshed(false);

        UpdateDashboardversionOutput dashboardversionOutput = _dashboardversionAppservice.update(
            dashboardversionId,
            dashboardversion
        );

        Dashboarduser dashboarduser = _dashboarduserRepository
            .findById(new DashboarduserId(dashboardId, input.getUserId()))
            .orElse(null);
        if (dashboarduser != null) {
            dashboarduser.setIsResetted(false);
            _dashboarduserRepository.save(dashboarduser);
        }
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        if (foundDashboard.getUsers() != null && foundDashboard.getUsers().getId().equals(input.getUserId())) {
            foundDashboard.setIsPublished(false);
            foundDashboard = _dashboardRepository.save(foundDashboard);
        }

        Long count = 0L;
        for (UpdateReportInput reportInput : input.getReportDetails()) {
            Dashboardversionreport dashboardreport = _reportDashboardRepository
                .findById(new DashboardversionreportId(dashboardId, input.getUserId(), RUNNING, reportInput.getId()))
                .orElse(null);
            if (reportInput.getReportWidth() != null) {
                dashboardreport.setReportWidth(reportInput.getReportWidth());
            } else {
                dashboardreport.setReportWidth(MEDIUMCHART);
            }
            dashboardreport.setOrderId(count);
            count++;

            _reportDashboardRepository.save(dashboardreport);
        }

        return mapper.dashboardAndUpdateDashboardversionOutputToUpdateDashboardOutput(
            foundDashboard,
            dashboardversionOutput
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long dashboardId, Long userId) {
        Dashboard existing = _dashboardRepository.findById(dashboardId).orElse(null);
        existing.removeDashboardversion(
            _dashboardversionRepository.findById(new DashboardversionId(userId, dashboardId, RUNNING)).orElse(null)
        );
        existing.removeDashboardversion(
            _dashboardversionRepository.findById(new DashboardversionId(userId, dashboardId, PUBLISHED)).get()
        );

        _dashboardversionAppservice.delete(new DashboardversionId(userId, dashboardId, RUNNING));
        _dashboardversionAppservice.delete(new DashboardversionId(userId, dashboardId, PUBLISHED));

        List<Dashboarduser> reportUserList = _dashboarduserRepository.findByDashboardId(existing.getId());
        for (Dashboarduser reportuser : reportUserList) {
            reportuser.setOwnerSharingStatus(false);
            _dashboarduserRepository.save(reportuser);
        }

        existing.setUsers(null);
        existing.setIsPublished(true);
        _dashboardRepository.save(existing);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteReportFromDashboard(Long dashboardId, Long reportId, Long userId) {
        Dashboardversionreport existing = _reportDashboardRepository
            .findById(new DashboardversionreportId(dashboardId, userId, RUNNING, reportId))
            .get();

        _dashboardversionRepository
            .findById(new DashboardversionId(userId, dashboardId, RUNNING))
            .get()
            .removeDashboardversionreport(existing);
        _reportRepository.findByReportIdAndUsersId(reportId, userId).removeDashboardversionreport(existing);

        _reportDashboardAppService.delete(new DashboardversionreportId(dashboardId, userId, RUNNING, reportId));

        List<Dashboardversionreport> dashboardReportList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
            dashboardId,
                RUNNING,
            userId
        );

        Boolean sharable = true;
        for (Dashboardversionreport dashboardreport : dashboardReportList) {
            Reportuser reportuser = _reportuserRepository
                .findById(new ReportuserId(dashboardreport.getReportId(), userId))
                .orElse(null);
            if (reportuser != null) sharable = false;
        }

        if (sharable) {
            Dashboard dashboard = _dashboardRepository.findByDashboardIdAndUsersId(dashboardId, userId);
            dashboard.setIsShareable(sharable);
            dashboard.setIsPublished(false);
            _dashboardRepository.save(dashboard);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindDashboardByIdOutput findById(Long dashboardId) {
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        if (foundDashboard == null) return null;

        FindDashboardByIdOutput output = mapper.dashboardToFindDashboardByIdOutput(foundDashboard);
        return output;
    }

    public List<FindReportByIdOutput> setReportsList(Long dashboardId, Long userId, String version) {
        List<Dashboardversionreport> reportDashboardList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
            dashboardId,
            version,
            userId
        );

        List<FindReportByIdOutput> reportDetails = new ArrayList<>();
        for (Dashboardversionreport rd : reportDashboardList) {
            Reportversion reportversion = _reportversionRepository
                .findById(new ReportversionId(rd.getUserId(), rd.getReportId(), version))
                .orElse(null);
            Reportuser reportuser = _reportuserRepository
                .findById(new ReportuserId(rd.getReportId(), rd.getUserId()))
                .orElse(null);
            FindReportByIdOutput output = reportMapper.reportEntitiesToFindReportByIdOutput(
                rd.getReport(),
                reportversion,
                reportuser
            );
            if (reportuser != null) {
                output.setSharedWithMe(true);
            }

            List<Reportuser> reportuserList = _reportuserRepository.findByReportId(rd.getReportId());
            if (reportuserList != null && !reportuserList.isEmpty()) {
                output.setSharedWithOthers(true);
            }
            output.setOrderId(rd.getOrderId());
            output.setReportWidth(rd.getReportWidth());
            reportDetails.add(output);
        }

        List<FindReportByIdOutput> sortedReports = reportDetails
            .stream()
            .sorted(Comparator.comparing(FindReportByIdOutput::getOrderId))
            .collect(Collectors.toList());

        return sortedReports;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindDashboardByIdOutput findByDashboardIdAndUsersId(Long dashboardId, Long userId, String version) {
        Dashboard foundDashboard = _dashboardRepository.findByDashboardIdAndUsersId(dashboardId, userId);
        Dashboarduser dashboarduser = _dashboarduserRepository
            .findById(new DashboarduserId(dashboardId, userId))
            .orElse(null);
        if (foundDashboard == null && dashboarduser == null) return null;

        Dashboardversion dashboardVersion, publishedversion;
        publishedversion =
            _dashboardversionRepository.findById(new DashboardversionId(userId, dashboardId, PUBLISHED)).orElse(null);
        if (StringUtils.isNotBlank(version) && version.equalsIgnoreCase(PUBLISHED)) {
            dashboardVersion = publishedversion;
        } else {
            dashboardVersion =
                _dashboardversionRepository
                    .findById(new DashboardversionId(userId, dashboardId, RUNNING))
                    .orElse(null);
        }
        FindDashboardByIdOutput output = mapper.dashboardEntitiesToFindDashboardByIdOutput(
            foundDashboard,
            dashboardVersion,
            dashboarduser
        );

        if (dashboarduser != null) {
            output.setSharedWithMe(true);
        }

        List<Dashboarduser> dashboarduserList = _dashboarduserRepository.findByDashboardId(dashboardId);
        if (dashboarduserList != null && !dashboarduserList.isEmpty()) {
            output.setSharedWithOthers(true);
        }

        if (publishedversion == null) {
            output.setIsResetable(false);
        } else {
            output.setIsResetable(true);
        }
        return output;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetUsersOutput getUsers(Long dashboardId) {
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        if (foundDashboard == null) {
            logHelper.getLogger().error("There does not exist a dashboard wth a id=%s", dashboardId);
            return null;
        }

        Users re = foundDashboard.getUsers();
        return mapper.usersToGetUsersOutput(re, foundDashboard);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GetUsersOutput> getUserssAssociatedWithDashboard(Long dashboardId, String search, Pageable pageable) {
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        Page<Users> foundUsers = _dashboarduserRepository.getDashboardusersList(dashboardId, search, pageable);
        List<Users> usersList = foundUsers.getContent();
        Iterator<Users> usersIterator = usersList.iterator();
        List<GetUsersOutput> userssList = new ArrayList<>();

        while (usersIterator.hasNext()) {
            Users users = usersIterator.next();
            Dashboarduser dashboarduser = _dashboarduserRepository
                .findById(new DashboarduserId(dashboardId, users.getId()))
                .orElse(null);
            GetUsersOutput output = mapper.usersToGetUsersOutput(users, foundDashboard);

            if (dashboarduser != null) {
                output.setEditable(dashboarduser.getEditable());
            }

            userssList.add(output);
        }

        return userssList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GetUsersOutput> getAvailableUserss(Long dashboardId, String search, Pageable pageable) {
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        Page<Users> foundUsers = _dashboarduserRepository.getAvailableDashboardusersList(dashboardId, search, pageable);
        List<Users> usersList = foundUsers.getContent();
        Iterator<Users> usersIterator = usersList.iterator();
        List<GetUsersOutput> userssList = new ArrayList<>();

        while (usersIterator.hasNext()) {
            Users users = usersIterator.next();
            GetUsersOutput output = mapper.usersToGetUsersOutput(users, foundDashboard);
            userssList.add(output);
        }

        return userssList;
    }

    //Role
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GetRoleOutput> getRolesAssociatedWithDashboard(Long dashboardId, String search, Pageable pageable) {
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        Page<Role> foundRole = _dashboardroleRepository.getDashboardrolesList(dashboardId, search, pageable);
        List<Role> roleList = foundRole.getContent();
        Iterator<Role> roleIterator = roleList.iterator();
        List<GetRoleOutput> rolesList = new ArrayList<>();

        while (roleIterator.hasNext()) {
            Role role = roleIterator.next();
            Dashboardrole dashboardrole = _dashboardroleRepository
                .findById(new DashboardroleId(dashboardId, role.getId()))
                .orElse(null);
            GetRoleOutput output = mapper.roleToGetRoleOutput(role, foundDashboard);
            if (dashboardrole != null) {
                output.setEditable(dashboardrole.getEditable());
            }
            rolesList.add(output);
        }

        return rolesList;
    }

    //User
    // ReST API Call - GET /dashboard/1/user
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GetRoleOutput> getAvailableRoles(Long dashboardId, String search, Pageable pageable) {
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        Page<Role> foundRole = _dashboardroleRepository.getAvailableDashboardrolesList(dashboardId, search, pageable);
        List<Role> roleList = foundRole.getContent();
        Iterator<Role> roleIterator = roleList.iterator();
        List<GetRoleOutput> rolesList = new ArrayList<>();

        while (roleIterator.hasNext()) {
            Role role = roleIterator.next();
            GetRoleOutput output = mapper.roleToGetRoleOutput(role, foundDashboard);
            rolesList.add(output);
        }

        return rolesList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DashboardDetailsOutput shareDashboard(
        Long dashboardId,
        List<ShareReportInputByUser> usersList,
        List<ShareReportInputByRole> rolesList
    ) {
        Dashboard dashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        Dashboardversion ownerPublishedVersion = _dashboardversionRepository
            .findById(new DashboardversionId(dashboard.getUsers().getId(), dashboard.getId(), PUBLISHED))
            .orElse(null);
        if (ownerPublishedVersion == null) {
            return null;
        }

        List<Dashboardversionreport> dashboardReportsList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
            dashboardId,
                PUBLISHED,
            dashboard.getUsers().getId()
        );
        List<ShareReportInputByUser> reportUsers = new ArrayList<>();
        List<Long> usersWithSharedReportsByRole = new ArrayList<>();
        for (ShareReportInputByRole roleInput : rolesList) {
            Dashboardrole dashboardrole = _dashboardroleRepository
                .findById(new DashboardroleId(dashboardId, roleInput.getId()))
                .orElse(null);
            if (dashboardrole == null) {
                CreateDashboardroleInput dashboardRoleInput = new CreateDashboardroleInput();
                dashboardRoleInput.setRoleId(roleInput.getId());
                dashboardRoleInput.setDashboardId(dashboardId);
                dashboardRoleInput.setEditable(roleInput.getEditable());
                dashboardRoleInput.setOwnerSharingStatus(true);
                _dashboardroleAppservice.create(dashboardRoleInput);
            } else if (dashboardrole != null && !dashboardrole.getOwnerSharingStatus()) {
                dashboardrole.setOwnerSharingStatus(true);
                dashboardrole.setEditable(roleInput.getEditable());
                _dashboardroleRepository.save(dashboardrole);
            }

            List<Usersrole> userroleList = _usersroleRepository.findByRoleId(roleInput.getId());
            for (Usersrole userrole : userroleList) {
                usersWithSharedReportsByRole.add(userrole.getUsersId());
                Dashboarduser dashboarduser = _dashboarduserRepository
                    .findById(new DashboarduserId(dashboardId, userrole.getUsersId()))
                    .orElse(null);

                if (dashboarduser != null) {
                    if (!dashboard.getUsers().getId().equals(dashboarduser.getUsers().getId())) {
                        shareDashboardWithUsers(dashboarduser, ownerPublishedVersion, roleInput.getEditable());
                        dashboarduser.setEditable(roleInput.getEditable());
                        dashboarduser.setIsAssignedByRole(true);
                        _dashboarduserRepository.save(dashboarduser);
                    }
                } else {
                    createDashboarduserAndDashboardVersion(
                        ownerPublishedVersion,
                        userrole.getUsersId(),
                        roleInput.getEditable(),
                        true
                    );
                }

                ShareReportInputByUser reportInput = new ShareReportInputByUser();
                reportInput.setId(userrole.getUsersId());
                reportInput.setEditable(roleInput.getEditable());
                reportUsers.add(reportInput);
            }
        }

        for (ShareReportInputByUser userInput : usersList) {
            if (!usersWithSharedReportsByRole.contains(userInput.getId())) {
                Dashboarduser dashboarduser = _dashboarduserRepository
                    .findById(new DashboarduserId(dashboardId, userInput.getId()))
                    .orElse(null);

                if (dashboarduser != null) {
                    if (!dashboard.getUsers().getId().equals(dashboarduser.getUsers().getId())) {
                        shareDashboardWithUsers(dashboarduser, ownerPublishedVersion, userInput.getEditable());
                        dashboarduser.setEditable(userInput.getEditable());
                        dashboarduser.setIsAssignedByRole(false);
                        _dashboarduserRepository.save(dashboarduser);
                    }
                } else {
                    createDashboarduserAndDashboardVersion(
                        ownerPublishedVersion,
                        userInput.getId(),
                        userInput.getEditable(),
                        false
                    );
                }

                ShareReportInputByUser reportInput = new ShareReportInputByUser();
                reportInput.setId(userInput.getId());
                reportInput.setEditable(userInput.getEditable());
                reportUsers.add(reportInput);
            }
        }

        for (Dashboardversionreport dvr : dashboardReportsList) {
            _reportAppService.shareReport(dvr.getReportId(), true, usersList, rolesList);

            for (ShareReportInputByUser reportuser : reportUsers) {
                Dashboardversionreport running = _reportDashboardRepository
                    .findById(
                        new DashboardversionreportId(
                            dvr.getDashboardId(),
                            reportuser.getId(),
                                RUNNING,
                            dvr.getReportId()
                        )
                    )
                    .orElse(null);

                if (running == null) {
                    running =
                        dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                            dvr,
                            reportuser.getId(),
                                RUNNING
                        );
                    _reportDashboardRepository.save(running);
                }
                if (reportuser.getEditable()) {
                    Dashboardversionreport published = _reportDashboardRepository
                        .findById(
                            new DashboardversionreportId(
                                dvr.getDashboardId(),
                                reportuser.getId(),
                                    PUBLISHED,
                                dvr.getReportId()
                            )
                        )
                        .orElse(null);

                    if (published == null) {
                        published =
                            dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                                running,
                                reportuser.getId(),
                                    PUBLISHED
                            );
                        _reportDashboardRepository.save(published);
                    }
                }
            }
        }

        DashboardDetailsOutput dashboardDetails = mapper.dashboardEntitiesToDashboardDetailsOutput(
            dashboard,
            ownerPublishedVersion,
            null
        );
        dashboardDetails.setSharedWithOthers(true);
        return dashboardDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DashboardDetailsOutput editDashboardAccess(
        Long dashboardId,
        List<ShareReportInputByUser> usersList,
        List<ShareReportInputByRole> rolesList
    ) {
        Dashboard dashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        Dashboardversion ownerPublishedVersion = _dashboardversionRepository
            .findById(new DashboardversionId(dashboard.getUsers().getId(), dashboard.getId(), PUBLISHED))
            .orElse(null);
        if (ownerPublishedVersion == null) {
            return null;
        }

        List<Dashboardversionreport> dashboardReportsList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
            dashboardId,
                PUBLISHED,
            dashboard.getUsers().getId()
        );
        List<ShareReportInputByUser> reportUsers = new ArrayList<>();
        List<Long> usersWithSharedReportsByRole = new ArrayList<>();
        for (ShareReportInputByRole roleInput : rolesList) {
            Dashboardrole dashboardRole = _dashboardroleRepository
                .findById(new DashboardroleId(dashboardId, roleInput.getId()))
                .orElse(null);
            if (roleInput.getEditable() != null) {
                dashboardRole.setEditable(roleInput.getEditable());
                dashboardRole.setOwnerSharingStatus(true);
                _dashboardroleRepository.save(dashboardRole);
            } else {
                dashboardRole.setOwnerSharingStatus(false);
                _dashboardroleRepository.save(dashboardRole);
            }

            List<Usersrole> userroleList = _usersroleRepository.findByRoleId(roleInput.getId());
            for (Usersrole userrole : userroleList) {
                Dashboarduser dashboarduser = _dashboarduserRepository
                    .findById(new DashboarduserId(dashboardId, userrole.getUsersId()))
                    .orElse(null);

                if (!dashboard.getUsers().getId().equals(userrole.getUsersId())) {
                    usersWithSharedReportsByRole.add(userrole.getUsersId());
                    if (dashboarduser.getIsAssignedByRole()) {
                        if (dashboarduser != null && roleInput.getEditable() != null) {
                            shareDashboardWithUsers(dashboarduser, ownerPublishedVersion, roleInput.getEditable());
                            dashboarduser.setEditable(roleInput.getEditable());
                            dashboarduser.setIsAssignedByRole(true);
                            _dashboarduserRepository.save(dashboarduser);
                        } else if (roleInput.getEditable() == null && dashboarduser != null) {
                            dashboarduser.setOwnerSharingStatus(false);
                            _dashboarduserRepository.save(dashboarduser);
                        }

                        ShareReportInputByUser reportInput = new ShareReportInputByUser();
                        reportInput.setId(userrole.getUsersId());
                        reportInput.setEditable(roleInput.getEditable());
                        reportUsers.add(reportInput);
                    }
                }
            }
        }

        for (ShareReportInputByUser userInput : usersList) {
            if (!usersWithSharedReportsByRole.contains(userInput.getId())) {
                Dashboarduser dashboarduser = _dashboarduserRepository
                    .findById(new DashboarduserId(dashboardId, userInput.getId()))
                    .orElse(null);

                if (dashboarduser != null && !dashboarduser.getIsAssignedByRole()) {
                    if (dashboarduser != null && userInput.getEditable() != null) {
                        shareDashboardWithUsers(dashboarduser, ownerPublishedVersion, userInput.getEditable());
                        dashboarduser.setEditable(userInput.getEditable());
                        dashboarduser.setIsAssignedByRole(false);
                        _dashboarduserRepository.save(dashboarduser);
                    } else if (userInput.getEditable() == null && dashboarduser != null) {
                        dashboarduser.setOwnerSharingStatus(false);
                        _dashboarduserRepository.save(dashboarduser);
                    }

                    ShareReportInputByUser reportInput = new ShareReportInputByUser();
                    reportInput.setId(userInput.getId());
                    reportInput.setEditable(userInput.getEditable());
                    reportUsers.add(reportInput);
                }
            }
        }

        for (Dashboardversionreport dvr : dashboardReportsList) {
            _reportAppService.editReportAccess(dvr.getReportId(), usersList, rolesList);

            for (ShareReportInputByUser reportuser : reportUsers) {
                if (reportuser.getEditable() != null) {
                    Dashboardversionreport running = _reportDashboardRepository
                        .findById(
                            new DashboardversionreportId(
                                dvr.getDashboardId(),
                                reportuser.getId(),
                                    RUNNING,
                                dvr.getReportId()
                            )
                        )
                        .orElse(null);
                    if (running == null) {
                        running =
                            dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                                dvr,
                                reportuser.getId(),
                                    PUBLISHED
                            );
                        _reportDashboardRepository.save(running);
                    }

                    if (reportuser.getEditable()) {
                        Dashboardversionreport published = _reportDashboardRepository
                            .findById(
                                new DashboardversionreportId(
                                    dvr.getDashboardId(),
                                    reportuser.getId(),
                                        PUBLISHED,
                                    dvr.getReportId()
                                )
                            )
                            .orElse(null);

                        if (published == null) {
                            published =
                                dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                                    running,
                                    reportuser.getId(),
                                        PUBLISHED
                                );
                        }

                        _reportDashboardRepository.save(published);
                    }
                }
            }
        }

        DashboardDetailsOutput dashboardDetails = mapper.dashboardEntitiesToDashboardDetailsOutput(
            dashboard,
            ownerPublishedVersion,
            null
        );
        return dashboardDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createDashboarduserAndDashboardVersion(
        Dashboardversion ownerReportversion,
        Long userId,
        Boolean editable,
        Boolean isAssigByRole
    ) {
        CreateDashboarduserInput createDashboarduserInput = new CreateDashboarduserInput();
        createDashboarduserInput.setDashboardId(ownerReportversion.getDashboardId());
        createDashboarduserInput.setUserId(userId);
        createDashboarduserInput.setEditable(editable);
        createDashboarduserInput.setIsAssignedByRole(isAssigByRole);
        createDashboarduserInput.setIsResetted(true);
        createDashboarduserInput.setIsRefreshed(true);
        createDashboarduserInput.setOwnerSharingStatus(true);
        createDashboarduserInput.setRecipientSharingStatus(true);
        _dashboarduserAppservice.create(createDashboarduserInput);

        Users users = _usersRepository.findById(userId).orElse(null);

        if (editable) {
            Dashboardversion publishedDashboardversion = dashboardversionMapper.dashboardversionToDashboardversion(
                ownerReportversion,
                users.getId(),
                    PUBLISHED
            );
            publishedDashboardversion.setUsers(users);
            _dashboardversionRepository.save(publishedDashboardversion);
            Dashboardversion runningDashboardversion = dashboardversionMapper.dashboardversionToDashboardversion(
                ownerReportversion,
                users.getId(),
                    RUNNING
            );
            runningDashboardversion.setUsers(users);
            _dashboardversionRepository.save(runningDashboardversion);
        } else {
            Dashboardversion runningdashboardversion = dashboardversionMapper.dashboardversionToDashboardversion(
                ownerReportversion,
                users.getId(),
                    RUNNING
            );

            runningdashboardversion.setUsers(users);
            _dashboardversionRepository.save(runningdashboardversion);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void shareDashboardWithUsers(
        Dashboarduser dashboarduser,
        Dashboardversion ownerPublishedVersion,
        Boolean editable
    ) {
        Users users = _usersRepository.findById(dashboarduser.getUserId()).orElse(null);
        Dashboardversion dashboardPublishedVersion = _dashboardversionRepository
            .findById(new DashboardversionId(users.getId(), dashboarduser.getDashboardId(), PUBLISHED))
            .orElse(null);
        Dashboardversion dashboardRunningVersion = _dashboardversionRepository
            .findById(new DashboardversionId(users.getId(), dashboarduser.getDashboardId(), RUNNING))
            .orElse(null);

        if (dashboarduser.getEditable() && !editable) {
            if (dashboarduser.getOwnerSharingStatus()) {
                if (dashboarduser.getIsResetted()) {
                    if (dashboardPublishedVersion != null) {
                        _dashboardversionRepository.delete(dashboardPublishedVersion);
                    }

                    Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                        ownerPublishedVersion,
                        users.getId(),
                            RUNNING
                    );
                    publishedVersion.setUsers(users);
                    _dashboardversionRepository.save(publishedVersion);
                } else if (!dashboarduser.getIsResetted()) {
                    Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                        ownerPublishedVersion,
                        users.getId(),
                            PUBLISHED
                    );

                    publishedVersion.setUsers(users);
                    _dashboardversionRepository.save(publishedVersion);
                }
            } else {
                if (dashboarduser.getIsResetted() && (dashboardPublishedVersion != null)) {
                        _dashboardversionRepository.delete(dashboardPublishedVersion);
                }
            }
        } else if (!dashboarduser.getEditable() && !editable) {
            if (dashboarduser.getOwnerSharingStatus()) {
                if (dashboardPublishedVersion != null && !dashboarduser.getIsResetted()) {
                    Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                        ownerPublishedVersion,
                        users.getId(),
                            PUBLISHED
                    );
                    publishedVersion.setUsers(users);
                    _dashboardversionRepository.save(publishedVersion);
                } else if (dashboarduser.getIsResetted()) {
                    if (dashboardPublishedVersion != null) {
                        _dashboardversionRepository.delete(dashboardPublishedVersion);
                    }
                    Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                        ownerPublishedVersion,
                        users.getId(),
                            RUNNING
                    );

                    publishedVersion.setUsers(users);
                    _dashboardversionRepository.save(publishedVersion);
                }
            }
        } else if (dashboarduser.getEditable() && editable) {
            if (dashboarduser.getOwnerSharingStatus()) {
                Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                    ownerPublishedVersion,
                    users.getId(),
                        PUBLISHED
                );
                publishedVersion.setUsers(users);
                _dashboardversionRepository.save(publishedVersion);
            }
        } else if (!dashboarduser.getEditable() && editable) {
            if (dashboarduser.getOwnerSharingStatus()) {
                if (dashboardPublishedVersion != null && !dashboarduser.getIsResetted()) {
                    Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                        ownerPublishedVersion,
                        users.getId(),
                            PUBLISHED
                    );
                    publishedVersion.setUsers(users);
                    _dashboardversionRepository.save(publishedVersion);
                } else if (dashboarduser.getIsResetted()) {
                    if (dashboardPublishedVersion != null) {
                        _dashboardversionRepository.delete(dashboardPublishedVersion);
                    }

                    Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                        ownerPublishedVersion,
                        users.getId(),
                            PUBLISHED
                    );
                    publishedVersion.setUsers(users);
                    _dashboardversionRepository.save(publishedVersion);
                }
            } else {
                Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                    dashboardRunningVersion,
                    users.getId(),
                        PUBLISHED
                );
                publishedVersion.setUsers(users);
                _dashboardversionRepository.save(publishedVersion);
            }
        }

        if (!dashboarduser.getOwnerSharingStatus()) {
            dashboarduser.setOwnerSharingStatus(true);
            dashboarduser.setIsRefreshed(false);
            _dashboarduserRepository.save(dashboarduser);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<DashboardDetailsOutput> getDashboards(Long userId, String search, Pageable pageable) throws Exception {
        Page<DashboardDetailsOutput> foundDashboard = _dashboardRepository.getAllDashboardsByUsersId(
            userId,
            search,
            pageable
        );
        List<DashboardDetailsOutput> dashboardList = foundDashboard.getContent();

        return dashboardList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<DashboardDetailsOutput> getAvailableDashboards(
        Long userId,
        Long reportId,
        String search,
        Pageable pageable
    )
        throws Exception {
        Page<DashboardDetailsOutput> foundDashboard = _dashboardRepository.getAvailableDashboardsByUsersId(
            userId,
            reportId,
            search,
            pageable
        );
        List<DashboardDetailsOutput> dashboardList = foundDashboard.getContent();

        return dashboardList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<DashboardDetailsOutput> getSharedDashboards(Long userId, String search, Pageable pageable)
        throws Exception {
        Page<DashboardDetailsOutput> foundDashboard = _dashboardRepository.getSharedDashboardsByUsersId(
            userId,
            search,
            pageable
        );
        List<DashboardDetailsOutput> dashboardList = foundDashboard.getContent();

        return dashboardList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DashboardDetailsOutput updateRecipientSharingStatus(Long userId, Long dashboardId, Boolean status) {
        Dashboarduser foundDashboarduser = _dashboarduserRepository
            .findById(new DashboarduserId(dashboardId, userId))
            .orElse(null);
        if (foundDashboarduser == null) return null;
        foundDashboarduser.setRecipientSharingStatus(status);
        foundDashboarduser = _dashboarduserRepository.save(foundDashboarduser);

        Dashboardversion foundDashboardversion = _dashboardversionRepository
            .findById(new DashboardversionId(userId, dashboardId, RUNNING))
            .orElse(null);
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);

        return mapper.dashboardEntitiesToDashboardDetailsOutput(
            foundDashboard,
            foundDashboardversion,
            foundDashboarduser
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public FindDashboardByIdOutput publishDashboard(Long userId, Long dashboardId) {
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        List<Dashboarduser> dashboarduserList = _dashboarduserRepository.findByDashboardId(dashboardId);
        for (Dashboarduser dashboarduser : dashboarduserList) {
            dashboarduser.setIsRefreshed(false);
            _dashboarduserRepository.save(dashboarduser);
        }

        foundDashboard.setIsPublished(true);
        foundDashboard = _dashboardRepository.save(foundDashboard);

        Dashboardversion foundDashboardversion = _dashboardversionRepository
            .findById(new DashboardversionId(userId, dashboardId, RUNNING))
            .orElse(null);
        Dashboardversion foundPublishedversion = _dashboardversionRepository
            .findById(new DashboardversionId(userId, dashboardId, PUBLISHED))
            .orElse(null);
        Dashboardversion publishedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
            foundDashboardversion,
            userId,
                PUBLISHED
        );

        if (foundPublishedversion != null) {
            publishedVersion.setVersiono(foundPublishedversion.getVersiono());
        } else publishedVersion.setVersiono(0L);

        _dashboardversionRepository.save(publishedVersion);
        //check if report is added in running version
        List<Dashboardversionreport> dashboardreportList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
            dashboardId,
                RUNNING,
            userId
        );

        for (Dashboardversionreport dashboardreport : dashboardreportList) {
            Dashboardversionreport publishedDashboardreport = _reportDashboardRepository
                .findById(new DashboardversionreportId(dashboardId, userId, PUBLISHED, dashboardreport.getReportId()))
                .orElse(null);
            if (publishedDashboardreport != null) {
                publishedDashboardreport.setOrderId(dashboardreport.getOrderId());
                publishedDashboardreport.setReportWidth(dashboardreport.getReportWidth());
                _reportDashboardRepository.save(publishedDashboardreport);
            } else {
                publishedDashboardreport =
                    dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                        dashboardreport,
                        userId,
                            PUBLISHED
                    );
                _reportDashboardRepository.save(publishedDashboardreport);
            }

            _reportAppService.publishReport(dashboardreport.getUserId(), dashboardreport.getReportId());
        }

        //check if report is removed from running version
        List<Dashboardversionreport> dashboardPublishedreportList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
            dashboardId,
                PUBLISHED,
            userId
        );

        for (Dashboardversionreport dashboardeport : dashboardPublishedreportList) {
            Dashboardversionreport runningDashboardreport = _reportDashboardRepository
                .findById(new DashboardversionreportId(dashboardId, userId, RUNNING, dashboardeport.getReportId()))
                .orElse(null);
            if (runningDashboardreport == null) {
                runningDashboardreport =
                    dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                        dashboardeport,
                        userId,
                            PUBLISHED
                    );
                runningDashboardreport.getReport().removeDashboardversionreport(runningDashboardreport);
                runningDashboardreport.getDashboardversionEntity().removeDashboardversionreport(runningDashboardreport);

                _reportDashboardRepository.delete(runningDashboardreport);
                Reportversion reportversion = _reportversionRepository
                    .findById(new ReportversionId(dashboardeport.getUserId(), dashboardeport.getReportId(), RUNNING))
                    .orElse(null);

                if (reportversion.getIsAssignedByDashboard()) {
                    _reportAppService.delete(dashboardeport.getReportId(), dashboardeport.getUserId());
                }
            }
        }

        return mapper.dashboardEntitiesToFindDashboardByIdOutput(foundDashboard, foundDashboardversion, null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public FindDashboardByIdOutput refreshDashboard(Long userId, Long dashboardId) {
        Dashboarduser foundDashboarduser = _dashboarduserRepository
            .findById(new DashboarduserId(dashboardId, userId))
            .orElse(null);
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);

        if (foundDashboarduser != null && foundDashboarduser.getOwnerSharingStatus()) {
            Dashboardversion ownerPublishedversion = _dashboardversionRepository
                .findById(new DashboardversionId(foundDashboard.getUsers().getId(), dashboardId, PUBLISHED))
                .orElse(null);
            Users foundUsers = _usersRepository.findById(userId).orElse(null);
            Dashboardversion publishedversion = _dashboardversionRepository
                .findById(new DashboardversionId(userId, dashboardId, PUBLISHED))
                .orElse(null);
            Dashboardversion updatedVersion;
            if (publishedversion == null) {
                updatedVersion =
                    dashboardversionMapper.dashboardversionToDashboardversion(
                        ownerPublishedversion,
                        userId,
                            PUBLISHED
                    );
            } else {
                updatedVersion =
                    dashboardversionMapper.dashboardversionToDashboardversion(publishedversion, userId, RUNNING);
            }

            updatedVersion.setUsers(foundUsers);
            _dashboardversionRepository.save(updatedVersion);
            foundDashboarduser.setIsRefreshed(true);
            foundDashboarduser.setIsResetted(false);
            foundDashboarduser = _dashboarduserRepository.save(foundDashboarduser);

            List<Dashboardversionreport> dvrList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
                dashboardId,
                    PUBLISHED,
                foundDashboard.getUsers().getId()
            );
            for (Dashboardversionreport dvr : dvrList) {
                Dashboardversionreport updateDashboardreport = dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                    dvr,
                    userId,
                        PUBLISHED
                );
                _reportDashboardRepository.save(updateDashboardreport);
                _reportAppService.refreshReport(userId, dvr.getReportId());
            }

            Dashboardversion runningversion = _dashboardversionRepository
                .findById(new DashboardversionId(userId, dashboardId, RUNNING))
                .orElse(null);

            FindDashboardByIdOutput output = mapper.dashboardEntitiesToFindDashboardByIdOutput(
                foundDashboard,
                runningversion,
                foundDashboarduser
            );
            output.setSharedWithMe(true);
            return output;
        } else if (
            foundDashboard != null && foundDashboard.getUsers() != null && foundDashboard.getUsers().getId().equals(userId)
        ) {
            Dashboardversion ownerPublishedversion = _dashboardversionRepository
                .findById(new DashboardversionId(userId, dashboardId, PUBLISHED))
                .orElse(null);
            Dashboardversion ownerRunningversion = _dashboardversionRepository
                .findById(new DashboardversionId(userId, dashboardId, RUNNING))
                .orElse(null);

            Users foundUsers = _usersRepository.findById(userId).orElse(null);

            Dashboardversion updatedVersion = dashboardversionMapper.dashboardversionToDashboardversion(
                ownerPublishedversion,
                userId,
                    RUNNING
            );
            updatedVersion.setUsers(foundUsers);
            updatedVersion.setVersiono(ownerRunningversion.getVersiono());
            updatedVersion.setIsRefreshed(true);
            _dashboardversionRepository.save(updatedVersion);

            List<Dashboardversionreport> dvrList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
                dashboardId,
                    PUBLISHED,
                userId
            );
            for (Dashboardversionreport dvr : dvrList) {
                Dashboardversionreport updateDashboardreport = dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                    dvr,
                    userId,
                        RUNNING
                );
                _reportDashboardRepository.save(updateDashboardreport);
                _reportAppService.refreshReport(userId, dvr.getReportId());
            }

            Dashboardversion runningversion = _dashboardversionRepository
                .findById(new DashboardversionId(userId, dashboardId, RUNNING))
                .orElse(null);

            FindDashboardByIdOutput output = mapper.dashboardEntitiesToFindDashboardByIdOutput(
                foundDashboard,
                runningversion,
                null
            );
            output.setSharedWithMe(false);
            return output;
        }

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DashboardDetailsOutput changeOwner(Long ownerId, Long dashboardId, Long newOwnerId) {
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);
        Users foundUsers = _usersRepository.findById(newOwnerId).orElse(null);

        Dashboardversion dashboardRunningversion;
        Dashboarduser dashboarduser = _dashboarduserRepository
            .findById(new DashboarduserId(dashboardId, newOwnerId))
            .orElse(null);

        if (dashboarduser != null) {
            dashboardRunningversion =
                _dashboardversionRepository
                    .findById(new DashboardversionId(newOwnerId, dashboardId, RUNNING))
                    .orElse(null);
            Dashboardversion dashboardPublishedversion = _dashboardversionRepository
                .findById(new DashboardversionId(newOwnerId, dashboardId, PUBLISHED))
                .orElse(null);

            if (dashboardPublishedversion == null) {
                dashboardPublishedversion =
                    dashboardversionMapper.dashboardversionToDashboardversion(
                        dashboardRunningversion,
                        newOwnerId,
                            PUBLISHED
                    );
                dashboardPublishedversion.setUsers(foundUsers);
                _dashboardversionRepository.save(dashboardPublishedversion);
            }

            _dashboarduserRepository.delete(dashboarduser);
        } else {
            Dashboardversion foundOwnerDashboardRunningversion = _dashboardversionRepository
                .findById(new DashboardversionId(ownerId, dashboardId, RUNNING))
                .orElse(null);
            Dashboardversion foundOwnerDashboardPublishedversion = _dashboardversionRepository
                .findById(new DashboardversionId(ownerId, dashboardId, PUBLISHED))
                .orElse(null);
            dashboardRunningversion =
                dashboardversionMapper.dashboardversionToDashboardversion(
                    foundOwnerDashboardRunningversion,
                    foundUsers.getId(),
                        RUNNING
                );
            dashboardRunningversion.setUsers(foundUsers);
            _dashboardversionRepository.save(dashboardRunningversion);

            Dashboardversion dashboardPublishedversion = dashboardversionMapper.dashboardversionToDashboardversion(
                foundOwnerDashboardPublishedversion,
                foundUsers.getId(),
                    PUBLISHED
            );

            dashboardPublishedversion.setUsers(foundUsers);
            _dashboardversionRepository.save(dashboardPublishedversion);
        }

        List<Dashboardversionreport> dvrRunningList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
            dashboardId,
                RUNNING,
            ownerId
        );
        List<Dashboardversionreport> dvrPublishedList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
            dashboardId,
                PUBLISHED,
            ownerId
        );

        for (Dashboardversionreport dvr : dvrRunningList) {
            _reportAppService.changeOwner(ownerId, dvr.getReportId(), newOwnerId);
            Reportuser ru = _reportuserRepository.findById(new ReportuserId(dvr.getReportId(), ownerId)).orElse(null);
            if (ru == null) {
                Dashboardversionreport updatedRunning = dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                    dvr,
                    newOwnerId,
                        RUNNING
                );
                _reportDashboardRepository.save(updatedRunning);
            }
            _reportDashboardRepository.delete(dvr);

            Dashboardversionreport ownerPublished = _reportDashboardRepository
                .findById(
                    new DashboardversionreportId(dvr.getDashboardId(), dvr.getUserId(), PUBLISHED, dvr.getReportId())
                )
                .orElse(null);
            if (ownerPublished != null) _reportDashboardRepository.delete(ownerPublished);
        }

        for (Dashboardversionreport dvr : dvrPublishedList) {
            if (
                !dvrRunningList.stream().filter(o -> o.getReportId().equals(dvr.getReportId())).findFirst().isPresent()
            ) {
                _reportAppService.changeOwner(ownerId, dvr.getReportId(), newOwnerId);
                Reportuser ru = _reportuserRepository
                    .findById(new ReportuserId(dvr.getReportId(), ownerId))
                    .orElse(null);
                if (ru == null) {
                    Dashboardversionreport updatedPublished = dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                        dvr,
                        newOwnerId,
                            PUBLISHED
                    );
                    _reportDashboardRepository.save(updatedPublished);
                }
                _reportDashboardRepository.delete(dvr);
            }
            Reportuser ru = _reportuserRepository.findById(new ReportuserId(dvr.getReportId(), ownerId)).orElse(null);
            if (ru == null) {
                Dashboardversionreport updatedPublished = dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                    dvr,
                    newOwnerId,
                        PUBLISHED
                );
                _reportDashboardRepository.save(updatedPublished);
            }
        }

        _dashboardversionAppservice.delete(new DashboardversionId(ownerId, dashboardId, RUNNING));
        _dashboardversionAppservice.delete(new DashboardversionId(ownerId, dashboardId, PUBLISHED));

        foundDashboard.setUsers(foundUsers);
        _dashboardRepository.save(foundDashboard);

        return mapper.dashboardEntitiesToDashboardDetailsOutput(foundDashboard, dashboardRunningversion, null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public FindDashboardByIdOutput resetDashboard(Long userId, Long dashboardId) {
        Dashboarduser foundDashboarduser = _dashboarduserRepository
            .findById(new DashboarduserId(dashboardId, userId))
            .orElse(null);
        Dashboard foundDashboard = _dashboardRepository.findById(dashboardId).orElse(null);

        Dashboardversion publishedversion = _dashboardversionRepository
            .findById(new DashboardversionId(userId, dashboardId, PUBLISHED))
            .orElse(null);
        if (publishedversion != null) {
            Dashboardversion runningversion = dashboardversionMapper.dashboardversionToDashboardversion(
                publishedversion,
                userId,
                    RUNNING
            );
            runningversion = _dashboardversionRepository.save(runningversion);

            foundDashboarduser.setIsResetted(true);
            foundDashboarduser = _dashboarduserRepository.save(foundDashboarduser);

            List<Dashboardversionreport> dvrList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
                dashboardId,
                    PUBLISHED,
                userId
            );
            for (Dashboardversionreport dvr : dvrList) {
                Dashboardversionreport updateDashboardreport = dashboardversionMapper.dashboardversionreportToDashboardversionreport(
                    dvr,
                    userId,
                        RUNNING
                );
                _reportDashboardRepository.save(updateDashboardreport);
                _reportAppService.resetReport(userId, dvr.getReportId());
            }

            List<Dashboardversionreport> dvrRunningList = _reportDashboardRepository.findByDashboardIdAndVersionAndUsersId(
                dashboardId,
                    RUNNING,
                userId
            );
            for (Dashboardversionreport dvr : dvrRunningList) {
                if (!dvrList.stream().filter(o -> o.getReportId().equals(dvr.getReportId())).findFirst().isPresent()) {
                    _reportDashboardRepository.delete(dvr);
                }
            }

            if (foundDashboarduser != null && !foundDashboarduser.getEditable()) {
                _dashboardversionRepository.delete(publishedversion);
            }

            FindDashboardByIdOutput output = mapper.dashboardEntitiesToFindDashboardByIdOutput(
                foundDashboard,
                runningversion,
                foundDashboarduser
            );
            output.setSharedWithMe(true);
            return output;
        }

        return null;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindDashboardByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Dashboard> foundDashboard = _dashboardRepository.findAll(search(search), pageable);
        List<Dashboard> dashboardList = foundDashboard.getContent();
        Iterator<Dashboard> dashboardIterator = dashboardList.iterator();
        List<FindDashboardByIdOutput> output = new ArrayList<>();

        while (dashboardIterator.hasNext()) {
            Dashboard dashboard = dashboardIterator.next();
            Dashboardversion dashboardVersion = _dashboardversionRepository
                .findById(new DashboardversionId(dashboard.getUsers().getId(), dashboard.getId(), RUNNING))
                .orElse(null);
            Dashboarduser dashboarduser = _dashboarduserRepository
                .findById(new DashboarduserId(dashboard.getId(), dashboardVersion.getUserId()))
                .orElse(null);
            FindDashboardByIdOutput dashboardOutput = mapper.dashboardEntitiesToFindDashboardByIdOutput(
                dashboard,
                dashboardVersion,
                dashboarduser
            );
            Dashboardversion publishedversion = _dashboardversionRepository
                .findById(new DashboardversionId(dashboard.getUsers().getId(), dashboard.getId(), PUBLISHED))
                .orElse(null);
            if (publishedversion == null) {
                dashboardOutput.setIsResetable(false);
            } else dashboardOutput.setIsResetable(true);
            output.add(dashboardOutput);
        }
        return output;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public FindDashboardByIdOutput addNewReportsToNewDashboard(AddNewReportToNewDashboardInput input) {
        CreateDashboardInput dashboardInput = mapper.addNewReportToNewDashboardInputTocreatDashboardInput(input);
        dashboardInput.setIsShareable(true);
        CreateDashboardOutput createdDashboard = create(dashboardInput);

        List<FindReportByIdOutput> reportsOutput = new ArrayList<>();
        List<CreateReportOutput> reportList = new ArrayList<>();

        for (CreateReportInput report : input.getReportDetails()) {
            report.setIsPublished(true);
            report.setOwnerId(createdDashboard.getOwnerId());
            report.setIsAssignedByDashboard(false);
            CreateReportOutput createdReport = _reportAppService.create(report);
            if (report.getReportWidth() != null) {
                createdReport.setReportWidth(report.getReportWidth());
            } else createdReport.setReportWidth(MEDIUMCHART);
            reportList.add(createdReport);
            FindReportByIdOutput output = reportMapper.createReportOutputToFindReportByIdOutput(createdReport, null);
            output.setReportWidth(report.getReportWidth());
            reportsOutput.add(output);
        }

        _reportDashboardAppService.addReportsToDashboardRunningversion(createdDashboard, reportList);
        _reportDashboardAppService.addReportsToDashboardPublishedversion(createdDashboard, reportList);

        FindDashboardByIdOutput dashboardOuputDto = mapper.dashboardOutputToFindDashboardByIdOutput(createdDashboard);
        dashboardOuputDto.setReportDetails(reportsOutput);
        dashboardOuputDto.setIsResetable(false);
        return dashboardOuputDto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public FindDashboardByIdOutput addNewReportsToExistingDashboard(AddNewReportToExistingDashboardInput input) {
        Dashboardversion dashboardversion = _dashboardversionRepository
            .findById(new DashboardversionId(input.getOwnerId(), input.getId(), RUNNING))
            .orElse(null);
        Dashboard dashboard = _dashboardRepository.findById(input.getId()).orElse(null);

        CreateDashboardOutput createdDashboard = mapper.dashboardAndDashboardversionToCreateDashboardOutput(
            dashboard,
            dashboardversion
        );

        List<FindReportByIdOutput> reportsOutput = new ArrayList<>();
        List<CreateReportOutput> reportList = new ArrayList<>();

        for (CreateReportInput report : input.getReportDetails()) {
            report.setIsPublished(true);
            report.setOwnerId(createdDashboard.getOwnerId());
            report.setIsAssignedByDashboard(false);
            CreateReportOutput createdReport = _reportAppService.create(report);
            if (report.getReportWidth() != null) {
                createdReport.setReportWidth(report.getReportWidth());
            } else createdReport.setReportWidth(MEDIUMCHART);

            reportList.add(createdReport);
            FindReportByIdOutput output = reportMapper.createReportOutputToFindReportByIdOutput(createdReport, null);
            output.setReportWidth(report.getReportWidth());
            reportsOutput.add(output);
        }

        _reportDashboardAppService.addReportsToDashboardRunningversion(createdDashboard, reportList);

        FindDashboardByIdOutput dashboardOuputDto = mapper.dashboardOutputToFindDashboardByIdOutput(createdDashboard);
        dashboardOuputDto.setReportDetails(reportsOutput);
        dashboardOuputDto.setIsResetable(true);

        Dashboarduser dashboarduser = _dashboarduserRepository
            .findById(new DashboarduserId(input.getId(), input.getOwnerId()))
            .orElse(null);

        if (dashboarduser != null) {
            dashboarduser.setIsResetted(false);
            _dashboarduserRepository.save(dashboarduser);
        }

        if ((dashboard.getUsers() != null && dashboard.getUsers().getId().equals(input.getOwnerId()))) {
            dashboard.setIsPublished(false);
            dashboard.setIsShareable(true);
            _dashboardRepository.save(dashboard);
        }

        return dashboardOuputDto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public FindDashboardByIdOutput addExistingReportsToNewDashboard(AddExistingReportToNewDashboardInput input) {
        List<FindReportByIdOutput> reportsOutput = new ArrayList<>();
        List<CreateReportOutput> reportList = new ArrayList<>();

        Boolean sharable = true;
        for (ExistingReportInput report : input.getReportDetails()) {
            Report reportEntity = _reportRepository.findById(report.getId()).orElse(null);
            Reportversion reportversion = _reportversionRepository
                .findById(new ReportversionId(input.getOwnerId(), report.getId(), PUBLISHED))
                .orElse(null);
            if (reportversion == null) {
                reportversion =
                    _reportversionRepository
                        .findById(new ReportversionId(input.getOwnerId(), report.getId(), RUNNING))
                        .orElse(null);
            }

            Reportuser reportuser = _reportuserRepository
                .findById(new ReportuserId(report.getId(), input.getOwnerId()))
                .orElse(null);

            if (reportuser != null) {
                sharable = false;
            }

            CreateReportOutput reportOutput = reportMapper.reportAndReportversionToCreateReportOutput(
                reportEntity,
                reportversion
            );
            if (report.getReportWidth() != null) {
                reportOutput.setReportWidth(report.getReportWidth());
            } else reportOutput.setReportWidth(MEDIUMCHART);
            reportList.add(reportOutput);

            FindReportByIdOutput output = reportMapper.createReportOutputToFindReportByIdOutput(
                reportOutput,
                reportuser
            );
            output.setReportWidth(reportOutput.getReportWidth());
            reportsOutput.add(output);
        }

        CreateDashboardInput dashboardInput = mapper.addExistingReportToNewDashboardInputTocreatDashboardInput(input);
        dashboardInput.setIsShareable(sharable);
        CreateDashboardOutput createdDashboard = create(dashboardInput);

        _reportDashboardAppService.addReportsToDashboardRunningversion(createdDashboard, reportList);
        _reportDashboardAppService.addReportsToDashboardPublishedversion(createdDashboard, reportList);

        FindDashboardByIdOutput dashboardOuputDto = mapper.dashboardOutputToFindDashboardByIdOutput(createdDashboard);
        dashboardOuputDto.setReportDetails(reportsOutput);
        dashboardOuputDto.setIsResetable(false);

        return dashboardOuputDto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public FindDashboardByIdOutput addExistingReportsToExistingDashboard(
        AddExistingReportToExistingDashboardInput input
    ) {
        Dashboard dashboard = _dashboardRepository.findById(input.getId()).orElse(null);
        Dashboardversion dashboardversion = _dashboardversionRepository
            .findById(new DashboardversionId(input.getOwnerId(), dashboard.getId(), RUNNING))
            .orElse(null);

        CreateDashboardOutput createdDashboard = mapper.dashboardAndDashboardversionToCreateDashboardOutput(
            dashboard,
            dashboardversion
        );
        Dashboarduser dashboarduser = _dashboarduserRepository
            .findById(new DashboarduserId(input.getId(), input.getOwnerId()))
            .orElse(null);
        Boolean sharable = true;

        List<FindReportByIdOutput> reportsOutput = new ArrayList<>();
        List<CreateReportOutput> reportList = new ArrayList<>();
        for (ExistingReportInput report : input.getReportDetails()) {
            Report reportEntity = _reportRepository.findById(report.getId()).orElse(null);
            Reportversion reportversion = _reportversionRepository
                .findById(new ReportversionId(input.getOwnerId(), report.getId(), PUBLISHED))
                .orElse(null);
            if (reportversion == null) {
                reportversion =
                    _reportversionRepository
                        .findById(new ReportversionId(input.getOwnerId(), report.getId(), RUNNING))
                        .orElse(null);
            }

            Reportuser reportuser = _reportuserRepository
                .findById(new ReportuserId(report.getId(), input.getOwnerId()))
                .orElse(null);

            if (reportuser != null) {
                sharable = false;
            }

            CreateReportOutput reportOutput = reportMapper.reportAndReportversionToCreateReportOutput(
                reportEntity,
                reportversion
            );
            if (report.getReportWidth() != null) {
                reportOutput.setReportWidth(report.getReportWidth());
            } else reportOutput.setReportWidth(MEDIUMCHART);
            reportList.add(reportOutput);

            FindReportByIdOutput output = reportMapper.createReportOutputToFindReportByIdOutput(
                reportOutput,
                reportuser
            );
            output.setReportWidth(reportOutput.getReportWidth());
            reportsOutput.add(output);
        }

        _reportDashboardAppService.addReportsToDashboardRunningversion(createdDashboard, reportList);

        FindDashboardByIdOutput dashboardOuputDto = mapper.dashboardOutputToFindDashboardByIdOutput(createdDashboard);
        dashboardOuputDto.setReportDetails(reportsOutput);
        dashboardOuputDto.setIsResetable(true);

        if (dashboarduser != null) {
            dashboarduser.setIsResetted(false);
            _dashboarduserRepository.save(dashboarduser);
        }

        if ((dashboard.getUsers() != null && dashboard.getUsers().getId().equals(input.getOwnerId()))) {
            dashboard.setIsPublished(false);
            dashboard.setIsShareable(sharable);
            _dashboardRepository.save(dashboard);

            List<Dashboarduser> dashboardusersList = _dashboarduserRepository.findByDashboardId(dashboard.getId());
            for (Dashboarduser du : dashboardusersList) {
                du.setOwnerSharingStatus(sharable);
                _dashboarduserRepository.save(du);
            }
        }

        return dashboardOuputDto;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QDashboard dashboard = QDashboard.dashboard;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(dashboard, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws MalformedURLException {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("userId") ||
                    list.get(i).replace("%20", "").trim().equals("description") ||
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("reportdashboard") ||
                    list.get(i).replace("%20", "").trim().equals("title") ||
                    list.get(i).replace("%20", "").trim().equals("user")
                )
            ) {
                throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QDashboard dashboard,
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
                    dashboard.isPublished.eq(Boolean.parseBoolean(details.getValue().getSearchValue()))
                ); else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) builder.and(dashboard.isPublished.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
            }
        }

        for (Map.Entry<String, String> joinCol : joinColumns.entrySet()) {
            if (joinCol != null && joinCol.getKey().equals("ownerId")) {
                builder.and(dashboard.users.id.eq(Long.parseLong(joinCol.getValue())));
            }
        }
        return builder;
    }

    public Map<String, String> parseReportdashboardJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("dashboardId", keysString);
        return joinColumnMap;
    }
}
