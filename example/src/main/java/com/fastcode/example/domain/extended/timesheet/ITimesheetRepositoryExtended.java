package com.fastcode.example.domain.extended.timesheet;

import com.fastcode.example.domain.core.timesheet.ITimesheetRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository("timesheetRepositoryExtended")
public interface ITimesheetRepositoryExtended extends ITimesheetRepository {
    //Add your custom code here
}
