package com.fastcode.example.addons.reporting.application.dashboarduser.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDashboardOutput {

    private String description;
    private Long id;
    private String title;
    private Long dashboarduserUserId;
}
