import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { JobsComponent } from './jobs/list/jobs.component';
import { ExecutionHistoryComponent } from './execution-history/execution-history.component';
import { TriggersComponent } from './triggers/list/triggers.component';
import { JobNewComponent } from './jobs/new/job-new.component';
import { JobDetailsComponent } from './jobs/details/job-details.component';
import { ExecutingJobsComponent } from './executing-jobs/executing-jobs.component';
import { TriggerJobComponent } from './jobs/trigger-job/trigger-job.component';
import { TriggerNewComponent } from './triggers/new/trigger-new.component';
import { SelectJobComponent } from './triggers/select-job/select-job.component';
import { TriggerDetailsComponent } from './triggers/details/trigger-details.component';

import { SchedulerRoutes } from './scheduler-routing.module';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'src/app/common/shared';

@NgModule({
  imports: [RouterModule.forChild(SchedulerRoutes), HttpClientModule, SharedModule],
  declarations: [
    JobsComponent,
    ExecutionHistoryComponent,
    TriggersComponent,
    JobNewComponent,
    JobDetailsComponent,
    ExecutingJobsComponent,
    TriggerJobComponent,
    TriggerNewComponent,
    SelectJobComponent,
    TriggerDetailsComponent,
  ],
  exports: [
    JobsComponent,
    ExecutionHistoryComponent,
    TriggersComponent,
    JobNewComponent,
    JobDetailsComponent,
    ExecutingJobsComponent,
    TriggerJobComponent,
    TriggerNewComponent,
    SelectJobComponent,
    TriggerDetailsComponent,
  ],
})
export class SchedulerModule {}
