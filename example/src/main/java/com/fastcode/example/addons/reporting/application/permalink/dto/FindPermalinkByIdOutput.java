package com.fastcode.example.addons.reporting.application.permalink.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindPermalinkByIdOutput {

    private String authentication;
    private Boolean description;
    private UUID id;
    private Long refreshRate;
    private String rendering;
    private String resource;
    private Long resourceId;
    private Boolean toolbar;
    private String password;
    private Long userId;
    private Long versiono;
}
