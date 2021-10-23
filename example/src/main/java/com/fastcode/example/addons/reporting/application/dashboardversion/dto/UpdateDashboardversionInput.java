package com.fastcode.example.addons.reporting.application.dashboardversion.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDashboardversionInput {

    private String description;

    @NotNull(message = "id Should not be null")
    private Long id;

    private String title;
    private Boolean isRefreshed;
    private Long userId;
    private Long dashboardId;
    private String dashboardVersion;
    private Long versiono;
}
