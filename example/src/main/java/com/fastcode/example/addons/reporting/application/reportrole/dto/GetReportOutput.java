package com.fastcode.example.addons.reporting.application.reportrole.dto;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class GetReportOutput {

    private String ctype;
    private String description;
    private Long id;
    private JSONObject query;
    private String reportType;
    private String title;
    private String reportWidth;
    private Long reportroleRoleId;
    private Long reportroleReportId;
}
