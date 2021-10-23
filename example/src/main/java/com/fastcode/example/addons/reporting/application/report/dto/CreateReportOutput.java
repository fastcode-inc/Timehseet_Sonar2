package com.fastcode.example.addons.reporting.application.report.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.json.simple.JSONObject;

@Getter
@Setter
public class CreateReportOutput {

    private Long id;
    private Boolean isPublished;
    private String ctype;
    private String description;
    private JSONObject query;

    @Length(max = 255, message = "reportType must be less than 255 characters")
    private String reportType;

    private String title;
    private Boolean isRefreshed;
    private String reportWidth;
    private Long ownerId;
    private String ownerDescriptiveField;
    private Boolean isAssignedByDashboard;
}
