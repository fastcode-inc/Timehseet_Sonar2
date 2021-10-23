package com.fastcode.example.application.extended.authorization.users;

import com.fastcode.example.addons.reporting.domain.dashboarduser.IDashboarduserRepository;
import com.fastcode.example.addons.reporting.domain.dashboardversion.*;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.*;
import com.fastcode.example.addons.reporting.domain.reportuser.IReportuserRepository;
import com.fastcode.example.addons.reporting.domain.reportversion.*;
import com.fastcode.example.application.core.authorization.users.UsersAppService;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.domain.core.authorization.userspreference.IUserspreferenceRepository;
import com.fastcode.example.domain.extended.authorization.users.IUsersRepositoryExtended;
import org.springframework.stereotype.Service;

@Service("usersAppServiceExtended")
public class UsersAppServiceExtended extends UsersAppService implements IUsersAppServiceExtended {

    public UsersAppServiceExtended(
        IDashboarduserRepository dashboarduserRepository,
        IReportuserRepository reportuserRepository,
        IDashboardversionRepository dashboardversionRepository,
        IReportversionRepository reportversionRepository,
        IDashboardversionreportRepository reportDashboardRepository,
        IUsersRepositoryExtended usersRepositoryExtended,
        IUserspreferenceRepository userspreferenceRepository,
        IUsersMapperExtended mapper,
        LoggingHelper logHelper
    ) {
        super(
            dashboarduserRepository,
            reportuserRepository,
            dashboardversionRepository,
            reportversionRepository,
            reportDashboardRepository,
            usersRepositoryExtended,
            userspreferenceRepository,
            mapper,
            logHelper
        );
    }
    //Add your custom code here

}
