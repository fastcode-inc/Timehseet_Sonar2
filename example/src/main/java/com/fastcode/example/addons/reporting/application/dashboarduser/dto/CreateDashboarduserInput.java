package com.fastcode.example.addons.reporting.application.dashboarduser.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDashboarduserInput {

    @NotNull(message = "userId Should not be null")
    private Long userId;

    @NotNull(message = "dashboardId Should not be null")
    private Long dashboardId;

    private Boolean editable;
    private Boolean isResetted;
    private Boolean isRefreshed;
    private Boolean ownerSharingStatus;
    private Boolean recipientSharingStatus;
    private Boolean isAssignedByRole;
}
