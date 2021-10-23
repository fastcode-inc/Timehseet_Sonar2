package com.fastcode.example.addons.reporting.application.dashboardversionreport;

import com.fastcode.example.addons.reporting.application.dashboard.dto.CreateDashboardOutput;
import com.fastcode.example.addons.reporting.application.dashboardversionreport.dto.*;
import com.fastcode.example.addons.reporting.application.report.dto.CreateReportOutput;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.DashboardversionreportId;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.commons.search.SearchCriteria;
import java.time.*;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface IDashboardversionreportAppService {
    CreateDashboardversionreportOutput create(CreateDashboardversionreportInput reportdashboard);

    void delete(DashboardversionreportId reportdashboardId);

    Boolean addReportsToDashboard(Dashboard dashboard, List<Report> reportsList);

    UpdateDashboardversionreportOutput update(
        DashboardversionreportId reportdashboardId,
        UpdateDashboardversionreportInput input
    );

    FindDashboardversionreportByIdOutput findById(DashboardversionreportId reportdashboardId);

    Boolean addReportsToDashboardRunningversion(CreateDashboardOutput dashboard, List<CreateReportOutput> reportsList);

    Boolean addReportsToDashboardPublishedversion(
        CreateDashboardOutput dashboard,
        List<CreateReportOutput> reportsList
    );

    List<FindDashboardversionreportByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    DashboardversionreportId parseReportdashboardKey(String keysString);

    //Dashboard
    GetDashboardversionOutput getDashboard(DashboardversionreportId reportdashboardId);

    //Report
    GetReportOutput getReport(DashboardversionreportId reportdashboardId);
}
