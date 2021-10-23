package com.fastcode.example.addons.reporting.application.dashboard.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardDetailsOutput {

    private Long userId;
    private Long id;
    private String description;
    private String dashboardVersion;
    private Boolean isPublished;
    private String title;
    private Boolean isRefreshed;
    private Boolean editable;
    private Boolean isResetted;
    //	private Boolean isRefreshed;
    private Boolean ownerSharingStatus;
    private Boolean recipientSharingStatus;
    private Boolean isAssignedByRole;
    private Boolean sharedWithMe;
    private Boolean sharedWithOthers;
    private Boolean isShareable;
    private Long ownerId;
    private String ownerDescriptiveField;
}
