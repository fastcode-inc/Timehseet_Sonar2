package com.fastcode.example.addons.reporting.application.report;

import com.fastcode.example.addons.reporting.application.report.dto.*;
import com.fastcode.example.addons.reporting.domain.reportversion.Reportversion;
import com.fastcode.example.commons.search.SearchCriteria;
import java.time.*;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface IReportAppService {
    CreateReportOutput create(CreateReportInput report);

    void delete(Long id, Long userId);

    UpdateReportOutput update(Long id, UpdateReportInput input);

    FindReportByIdOutput findById(Long id);

    List<FindReportByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    List<GetUsersOutput> getUserssAssociatedWithReport(Long reportId, String search, Pageable pageable);

    List<GetUsersOutput> getAvailableUserss(Long reportId, String search, Pageable pageable);

    List<GetRoleOutput> getRolesAssociatedWithReport(Long reportId, String search, Pageable pageable);

    List<GetRoleOutput> getAvailableRoles(Long reportId, String search, Pageable pageable);

    ReportDetailsOutput updateRecipientSharingStatus(Long userId, Long reportId, Boolean status);

    ReportDetailsOutput resetReport(Long userId, Long reportId);

    ReportDetailsOutput editReportAccess(
        Long reportId,
        List<ShareReportInputByUser> usersList,
        List<ShareReportInputByRole> rolesList
    );

    ReportDetailsOutput shareReport(
        Long reportId,
        Boolean isAssignedByDashboard,
        List<ShareReportInputByUser> usersList,
        List<ShareReportInputByRole> rolesList
    );

    void createReportuserAndReportVersion(
        Reportversion ownerReportversion,
        Long userId,
        Boolean editable,
        Boolean isAssigByRole,
        Boolean isAssignedByDashboard
    );

    List<ReportDetailsOutput> getSharedReports(Long userId, String search, Pageable pageable) throws Exception;

    //Users
    GetUsersOutput getUsers(Long reportid);

    ReportDetailsOutput changeOwner(Long ownerId, Long reportId, Long newOwnerId);

    List<ReportDetailsOutput> getReports(Long userId, String search, Pageable pageable) throws Exception;

    FindReportByIdOutput findByReportIdAndUsersId(Long reportId, Long userId, String version);

    ReportDetailsOutput publishReport(Long userId, Long reportId);

    ReportDetailsOutput refreshReport(Long userId, Long reportId);

    Map<String, String> parseReportdashboardJoinColumn(String keysString);
}
