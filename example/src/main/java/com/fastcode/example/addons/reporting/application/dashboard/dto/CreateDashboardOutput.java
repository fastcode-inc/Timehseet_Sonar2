package com.fastcode.example.addons.reporting.application.dashboard.dto;

import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDashboardOutput {

    private String description;
    private Long id;
    private String title;
    private Boolean isPublished;
    private Boolean isRefreshed;
    private Long ownerId;
    private String ownerDescriptiveField;
    private Boolean isShareable;
}
