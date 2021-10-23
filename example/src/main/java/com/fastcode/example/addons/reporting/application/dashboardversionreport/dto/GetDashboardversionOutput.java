package com.fastcode.example.addons.reporting.application.dashboardversionreport.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDashboardversionOutput {

    private String description;
    private Long id;
    private String title;
    private Long reportdashboardDashboardId;
    private Long reportdashboardReportId;
}
