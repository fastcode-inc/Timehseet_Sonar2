package com.fastcode.example.addons.reporting.application.dashboard;

import com.fastcode.example.addons.reporting.application.dashboard.dto.*;
import com.fastcode.example.addons.reporting.application.report.dto.FindReportByIdOutput;
import com.fastcode.example.addons.reporting.application.report.dto.ShareReportInputByRole;
import com.fastcode.example.addons.reporting.application.report.dto.ShareReportInputByUser;
import com.fastcode.example.addons.reporting.domain.dashboarduser.Dashboarduser;
import com.fastcode.example.addons.reporting.domain.dashboardversion.Dashboardversion;
import com.fastcode.example.commons.search.SearchCriteria;
import java.time.*;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface IDashboardAppService {
    CreateDashboardOutput create(CreateDashboardInput dashboard);

    void delete(Long id, Long userId);

    void deleteReportFromDashboard(Long dashboardId, Long reportId, Long userId);

    UpdateDashboardOutput update(Long id, UpdateDashboardInput input);

    FindDashboardByIdOutput findById(Long id);

    List<FindDashboardByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    List<FindReportByIdOutput> setReportsList(Long dashboardId, Long userId, String version);

    FindDashboardByIdOutput findByDashboardIdAndUsersId(Long dashboardId, Long userId, String version);

    List<GetUsersOutput> getUserssAssociatedWithDashboard(Long dashboardId, String search, Pageable pageable);

    List<GetUsersOutput> getAvailableUserss(Long dashboardId, String search, Pageable pageable);

    List<GetRoleOutput> getRolesAssociatedWithDashboard(Long dashboardId, String search, Pageable pageable);

    List<GetRoleOutput> getAvailableRoles(Long dashboardId, String search, Pageable pageable);

    GetUsersOutput getUsers(Long dashboardid);

    DashboardDetailsOutput shareDashboard(
        Long dashboardId,
        List<ShareReportInputByUser> usersList,
        List<ShareReportInputByRole> rolesList
    );

    DashboardDetailsOutput editDashboardAccess(
        Long dashboardId,
        List<ShareReportInputByUser> usersList,
        List<ShareReportInputByRole> rolesList
    );

    void createDashboarduserAndDashboardVersion(
        Dashboardversion ownerReportversion,
        Long userId,
        Boolean editable,
        Boolean isAssigByRole
    );

    void shareDashboardWithUsers(Dashboarduser dashboarduser, Dashboardversion ownerPublishedVersion, Boolean editable);

    List<DashboardDetailsOutput> getSharedDashboards(Long userId, String search, Pageable pageable) throws Exception;

    DashboardDetailsOutput updateRecipientSharingStatus(Long userId, Long dashboardId, Boolean status);

    FindDashboardByIdOutput resetDashboard(Long userId, Long dashboardId);

    DashboardDetailsOutput changeOwner(Long ownerId, Long dashboardId, Long newOwnerId);

    List<DashboardDetailsOutput> getDashboards(Long userId, String search, Pageable pageable) throws Exception;

    List<DashboardDetailsOutput> getAvailableDashboards(Long userId, Long reportId, String search, Pageable pageable)
        throws Exception;

    FindDashboardByIdOutput publishDashboard(Long userId, Long dashboardId);

    FindDashboardByIdOutput refreshDashboard(Long userId, Long dashboardId);

    FindDashboardByIdOutput addNewReportsToNewDashboard(AddNewReportToNewDashboardInput input);

    FindDashboardByIdOutput addNewReportsToExistingDashboard(AddNewReportToExistingDashboardInput input);

    FindDashboardByIdOutput addExistingReportsToNewDashboard(AddExistingReportToNewDashboardInput input);

    FindDashboardByIdOutput addExistingReportsToExistingDashboard(AddExistingReportToExistingDashboardInput input);

    Map<String, String> parseReportdashboardJoinColumn(String keysString);
}
