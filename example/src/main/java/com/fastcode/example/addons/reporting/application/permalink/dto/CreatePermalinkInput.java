package com.fastcode.example.addons.reporting.application.permalink.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class CreatePermalinkInput {

    @Length(max = 30, message = "authentication must be less than 30 characters")
    private String authentication;

    private Boolean description;

    private Long refreshRate;

    @NotNull(message = "rendering Should not be null")
    @Length(max = 30, message = "rendering must be less than 30 characters")
    private String rendering;

    @NotNull(message = "resource Should not be null")
    @Length(max = 30, message = "resource must be less than 30 characters")
    private String resource;

    private Long resourceId;

    private Boolean toolbar;
}
