package com.fastcode.example.addons.reporting.application.report.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class ReportDetailsOutput {

    private Long id;
    private Boolean isPublished;
    private String ctype;
    private String description;
    private JSONObject query;
    private String reportType;
    private String title;
    private String reportVersion;
    private String reportWidth;
    private Long userId;
    private Boolean editable;
    private Boolean isResetted;
    private Boolean isRefreshed;
    private Boolean ownerSharingStatus;
    private Boolean recipientSharingStatus;
    private Boolean isAssignedByRole;
    private Boolean sharedWithMe;
    private Boolean sharedWithOthers;
    private Long ownerId;
    private Boolean isAssignedByDashboard;
    private String ownerDescriptiveField;
}
