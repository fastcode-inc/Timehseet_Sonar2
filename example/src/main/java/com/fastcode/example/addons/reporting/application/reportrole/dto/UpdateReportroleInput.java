package com.fastcode.example.addons.reporting.application.reportrole.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReportroleInput {

    @NotNull(message = "roleId Should not be null")
    private Long roleId;

    @NotNull(message = "reportId Should not be null")
    private Long reportId;
}
