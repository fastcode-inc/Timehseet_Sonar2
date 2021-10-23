package com.fastcode.example.addons.reporting.application.reportuser;

import com.fastcode.example.addons.reporting.application.reportuser.dto.*;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportuser.Reportuser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IReportuserMapper {
    Reportuser createReportuserInputToReportuser(CreateReportuserInput reportuserDto);

    @Mappings(
        {
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "users.username", target = "userDescriptiveField"),
            @Mapping(source = "reportId", target = "reportId"),
            //	@Mapping(source = "report.title", target = "reportDescriptiveField"),
        }
    )
    CreateReportuserOutput reportuserToCreateReportuserOutput(Reportuser entity);

    Reportuser updateReportuserInputToReportuser(UpdateReportuserInput reportuserDto);

    @Mappings(
        {
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "users.username", target = "userDescriptiveField"),
            @Mapping(source = "reportId", target = "reportId"),
            //	@Mapping(source = "report.title", target = "reportDescriptiveField"),
        }
    )
    UpdateReportuserOutput reportuserToUpdateReportuserOutput(Reportuser entity);

    @Mappings(
        {
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "users.username", target = "userDescriptiveField"),
            @Mapping(source = "reportId", target = "reportId"),
            //	@Mapping(source = "report.title", target = "reportDescriptiveField"),
        }
    )
    FindReportuserByIdOutput reportuserToFindReportuserByIdOutput(Reportuser entity);

    @Mappings(
        {
            @Mapping(source = "reportuser.userId", target = "reportuserUserId"),
            //	@Mapping(source = "reportuser.reportId", target = "reportuserReportId"),
        }
    )
    GetReportOutput reportToGetReportOutput(Report report, Reportuser reportuser);
}
