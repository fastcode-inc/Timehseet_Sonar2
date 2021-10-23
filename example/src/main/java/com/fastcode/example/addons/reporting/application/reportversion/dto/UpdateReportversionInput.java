package com.fastcode.example.addons.reporting.application.reportversion.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class UpdateReportversionInput {

    private String ctype;
    private String description;
    private JSONObject query;
    private String reportType;
    private String title;
    private String reportVersion;
    private Long userId;
    private String userDescriptiveField;
    private String reportWidth;
    private Long reportId;
    private Boolean isRefreshed;
    private Boolean isAssignedByDashboard;
    private Long versiono;
}
