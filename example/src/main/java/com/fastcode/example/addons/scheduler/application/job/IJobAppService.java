package com.fastcode.example.addons.scheduler.application.job;

import com.fastcode.example.addons.scheduler.application.job.dto.*;
import com.fastcode.example.commons.search.SearchCriteria;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Pageable;

public interface IJobAppService {
    List<JobListOutput> listAllJobs(SearchCriteria search, Pageable pageable) throws Exception;

    List<FindByTriggerOutput> returnTriggersForAJob(JobKey jobKey) throws SchedulerException;

    List<GetExecutingJob> currentlyExecutingJobs() throws SchedulerException;

    List<String> listAllJobClasses() throws URISyntaxException;

    List<String> listAllJobGroups() throws SchedulerException, IOException;

    boolean pauseJob(String jobName, String jobGroup) throws SchedulerException;

    boolean resumeJob(String jobName, String jobGroup) throws SchedulerException;

    boolean deleteJob(String jobName, String jobGroup) throws SchedulerException;

    FindByJobOutput returnJob(String jobName, String jobGroup) throws SchedulerException;

    boolean updateJob(UpdateJobInput obj) throws SchedulerException;

    boolean createJob(CreateJobInput obj) throws SchedulerException, ClassNotFoundException;

    List<GetJobOutput> executionHistoryByJob(String jobName, String jobGroup);

    List<GetJobOutput> executionHistory(SearchCriteria search, Pageable pageable) throws Exception;
}
