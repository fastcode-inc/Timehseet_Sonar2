package com.fastcode.example.addons.reporting.application.dashboardversionreport.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDashboardversionreportInput {

    @NotNull(message = "dashboardId Should not be null")
    private Long dashboardId;

    private Long userId;
    private String dashboardVersion;
    private String reportWidth;
    private Long orderId;

    @NotNull(message = "reportId Should not be null")
    private Long reportId;
}
