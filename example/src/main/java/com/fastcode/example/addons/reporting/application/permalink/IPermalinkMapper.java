package com.fastcode.example.addons.reporting.application.permalink;

import com.fastcode.example.addons.reporting.application.permalink.dto.*;
import com.fastcode.example.addons.reporting.domain.permalink.Permalink;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPermalinkMapper {
    Permalink createPermalinkInputToPermalink(CreatePermalinkInput permalinkDto);

    CreatePermalinkOutput permalinkToCreatePermalinkOutput(Permalink entity);

    Permalink updatePermalinkInputToPermalink(UpdatePermalinkInput permalinkDto);

    UpdatePermalinkOutput permalinkToUpdatePermalinkOutput(Permalink entity);

    FindPermalinkByIdOutput permalinkToFindPermalinkByIdOutput(Permalink entity);
}
