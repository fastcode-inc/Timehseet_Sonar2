package com.fastcode.example.addons.reporting.application.dashboardversion.dto;

import com.fastcode.example.addons.reporting.application.report.dto.FindReportByIdOutput;
import java.time.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindDashboardversionByIdOutput {

    private String description;
    private Long id;
    private String title;
    private Boolean isRefreshed;
    private Long userId;
    private String userDescriptiveField;
    private List<FindReportByIdOutput> reportDetails;
    private Long versiono;
}
