package com.fastcode.example.addons.reporting.application.reportuser.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReportuserOutput {

    private Long userId;
    private Long reportId;
    private Boolean editable;
    private Boolean isResetted;
    private Boolean isRefreshed;
    private Boolean ownerSharingStatus;
    private Boolean recipientSharingStatus;
    private Boolean isAssignedByRole;
    private String userDescriptiveField;
}
