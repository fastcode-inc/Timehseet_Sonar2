package com.fastcode.example.addons.reporting.application.dashboard.dto;

import com.fastcode.example.addons.reporting.application.report.dto.UpdateReportInput;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDashboardInput {

    private String description;

    @NotNull(message = "id Should not be null")
    private Long id;

    @NotNull
    private String title;

    private Long ownerId;
    private Long userId;
    private Boolean isPublished;
    List<UpdateReportInput> reportDetails = new ArrayList<>();
    private Boolean isShareable;
    private Long versiono;
}
