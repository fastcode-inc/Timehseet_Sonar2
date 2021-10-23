package com.fastcode.example.addons.reporting.application.dashboardrole.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindDashboardroleByIdOutput {

    private Long roleId;
    private Long dashboardId;
    private String roleDescriptiveField;
    private Long versiono;
}
