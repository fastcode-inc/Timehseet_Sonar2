package com.fastcode.example.addons.reporting.application.dashboardrole.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDashboardroleInput {

    @NotNull(message = "roleId Should not be null")
    private Long roleId;

    @NotNull(message = "dashboardId Should not be null")
    private Long dashboardId;

    @NotNull(message = "editable Should not be null")
    private Boolean editable;

    @NotNull(message = "ownerSharingStatus Should not be null")
    private Boolean ownerSharingStatus;
}
