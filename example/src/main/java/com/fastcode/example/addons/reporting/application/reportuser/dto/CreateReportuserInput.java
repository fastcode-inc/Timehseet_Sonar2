package com.fastcode.example.addons.reporting.application.reportuser.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReportuserInput {

    @NotNull(message = "userId Should not be null")
    private Long userId;

    @NotNull(message = "reportId Should not be null")
    private Long reportId;

    private Boolean editable;
    private Boolean isResetted;
    private Boolean isRefreshed;
    private Boolean ownerSharingStatus;
    private Boolean recipientSharingStatus;
    private Boolean isAssignedByRole;
}
