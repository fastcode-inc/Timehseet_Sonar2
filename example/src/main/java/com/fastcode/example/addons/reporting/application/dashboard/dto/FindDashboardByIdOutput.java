package com.fastcode.example.addons.reporting.application.dashboard.dto;

import com.fastcode.example.addons.reporting.application.report.dto.FindReportByIdOutput;
import java.time.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindDashboardByIdOutput {

    private String description;
    private String title;
    private Long userId;
    private Long id;
    private Boolean isPublished;
    private Boolean isRefreshed;
    private Long ownerId;
    private String ownerDescriptiveField;
    private List<FindReportByIdOutput> reportDetails;
    private Long versiono;
    private Boolean editable;
    private Boolean isResetted;
    private Boolean ownerSharingStatus;
    private Boolean recipientSharingStatus;
    private Boolean isAssignedByRole;
    private Boolean isResetable;
    private Boolean sharedWithMe;
    private Boolean sharedWithOthers;
    private Boolean isShareable;
}
