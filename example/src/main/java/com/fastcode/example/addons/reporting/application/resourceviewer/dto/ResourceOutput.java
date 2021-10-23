package com.fastcode.example.addons.reporting.application.resourceviewer.dto;

import com.fastcode.example.addons.reporting.application.permalink.dto.FindPermalinkByIdOutput;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceOutput {

    FindPermalinkByIdOutput resourceInfo;
    Object data;
}
