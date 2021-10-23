package com.fastcode.example.addons.reporting.application.report.dto;

import java.time.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.json.simple.JSONObject;

@Getter
@Setter
public class UpdateReportInput {

    @NotNull
    private Long id;

    private Long ownerId;
    private Long userId;
    private Boolean isPublished;
    private String ctype;
    private String description;
    private JSONObject query;

    @Length(max = 255, message = "reportType must be less than 255 characters")
    private String reportType;

    @NotNull
    private String title;

    private String reportWidth;
    private Boolean isAssignedByDashboard;
    private Long versiono;
}
