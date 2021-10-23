package com.fastcode.example.domain.extended.timesheetstatus;

import com.fastcode.example.domain.core.timesheetstatus.ITimesheetstatusRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository("timesheetstatusRepositoryExtended")
public interface ITimesheetstatusRepositoryExtended extends ITimesheetstatusRepository {
    //Add your custom code here
}
