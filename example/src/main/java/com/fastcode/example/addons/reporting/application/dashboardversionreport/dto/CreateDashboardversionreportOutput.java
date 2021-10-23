package com.fastcode.example.addons.reporting.application.dashboardversionreport.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDashboardversionreportOutput {

    private Long dashboardId;
    private Long reportId;
    private Long userId;
    private String dasboardVersion;
    private String reportWidth;
    private Long orderId;
}
