package com.fastcode.example.addons.reporting.application.dashboardversion;

import com.fastcode.example.addons.reporting.application.dashboardversion.dto.*;
import com.fastcode.example.addons.reporting.domain.dashboardversion.Dashboardversion;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.Dashboardversionreport;
import com.fastcode.example.domain.core.authorization.users.Users;
import java.time.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IDashboardversionMapper {
    Dashboardversion createDashboardversionInputToDashboardversion(CreateDashboardversionInput dashboardversionDto);

    @Mappings(
        {
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "users.username", target = "userDescriptiveField"),
        }
    )
    CreateDashboardversionOutput dashboardversionToCreateDashboardversionOutput(Dashboardversion entity);

    Dashboardversion updateDashboardversionInputToDashboardversion(UpdateDashboardversionInput dashboardversionDto);

    @Mappings(
        {
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "users.username", target = "userDescriptiveField"),
        }
    )
    UpdateDashboardversionOutput dashboardversionToUpdateDashboardversionOutput(Dashboardversion entity);

    @Mappings(
        {
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "users.username", target = "userDescriptiveField"),
        }
    )
    FindDashboardversionByIdOutput dashboardversionToFindDashboardversionByIdOutput(Dashboardversion entity);

    @Mappings(
        { @Mapping(source = "userId", target = "userId"), @Mapping(source = "dversion", target = "dashboardVersion") }
    )
    Dashboardversion dashboardversionToDashboardversion(Dashboardversion entity, Long userId, String dversion);

    @Mappings(
        { @Mapping(source = "userId", target = "userId"), @Mapping(source = "dversion", target = "dashboardVersion") }
    )
    Dashboardversionreport dashboardversionreportToDashboardversionreport(
        Dashboardversionreport dashboardreport,
        Long userId,
        String dversion
    );

    @Mappings(
        {
            @Mapping(source = "users.id", target = "id"),
            @Mapping(source = "dashboardversion.dashboardVersion", target = "dashboardVersion"),
        }
    )
    GetUsersOutput usersToGetUsersOutput(Users users, Dashboardversion dashboardversion);
}
