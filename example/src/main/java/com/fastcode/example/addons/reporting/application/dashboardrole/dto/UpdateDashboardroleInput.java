package com.fastcode.example.addons.reporting.application.dashboardrole.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDashboardroleInput {

    @NotNull(message = "roleId Should not be null")
    private Long roleId;

    @NotNull(message = "dashboardId Should not be null")
    private Long dashboardId;

    private Long versiono;
}
