import { Component, OnInit } from '@angular/core';
import { JobService } from '../job.service';
import { IJob } from '../ijob';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { of } from 'rxjs';
import { JobData } from '../jobData';
import { ExecutionHistory } from '../../execution-history/executionHistory';
import { plainToClass } from 'class-transformer';
import { ListColumnType, IListColumn } from 'src/app/common/shared';
import { ErrorService } from 'src/app/core/services/error.service';
import { TranslateService } from '@ngx-translate/core';
import { ITrigger } from '../../triggers';

@Component({
  selector: 'app-job-details',
  templateUrl: './job-details.component.html',
  styleUrls: ['./job-details.component.scss'],
})
export class JobDetailsComponent implements OnInit {
  errorMessage = '';
  job: IJob;
  jobForm: FormGroup;
  loading = false;
  submitted = false;

  jobClasses: string[];

  // table data for jobData
  ELEMENT_DATA: JobData[] = [];
  displayedColumns: string[] = ['position', 'name', 'actions'];
  dataSourceJobData = of(this.ELEMENT_DATA);

  // table data for triggers
  displayedColumnsTriggers: string[] = ['triggerName', 'triggerGroup', 'type', 'startTime', 'endTime'];
  triggers: ITrigger[] = [];
  dataSourceTriggers = of(this.triggers);

  // table data for execution history
  executionHistorycolumns: IListColumn[] = [
    {
      column: 'triggerName',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.TRIGGER-NAME'),
      sort: true,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'triggerGroup',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.TRIGGER-GROUP'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'jobClass',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.JOB-CLASS'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'jobStatus',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.STATUS'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'duration',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.DURATION'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'firedTime',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.FIRE-TIME'),
      sort: false,
      filter: true,
      type: ListColumnType.Date,
    },
    {
      column: 'finishedTime',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.FINISHED-TIME'),
      sort: false,
      filter: true,
      type: ListColumnType.Date,
    },
  ];
  displayedColumnsExecutionHistory: string[] = this.executionHistorycolumns.map((obj) => {
    return obj.column;
  });

  executionHistory: ExecutionHistory[] = [];
  dataSourceExecutionHistory = of(this.executionHistory);
  jobNameParam;
  jobGroupParam;
  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private jobService: JobService,
    private translate: TranslateService,
    private errorService: ErrorService
  ) {
    this.jobNameParam = this.route.snapshot.paramMap.get('jobName');
    this.jobGroupParam = this.route.snapshot.paramMap.get('jobGroup');
  }

  ngOnInit() {
    this.jobForm = this.formBuilder.group({
      jobName: [{ value: '', disabled: true }, Validators.required],
      jobGroup: [{ value: '', disabled: true }, Validators.required],
      jobClass: [{ value: '', disabled: true }, Validators.required],
      isDurable: [false],
      jobDescription: [''],
    });
    if (this.jobNameParam && this.jobGroupParam) {
      this.getJob(this.jobNameParam, this.jobGroupParam);
      this.getJobExecutionHistory(this.jobNameParam, this.jobGroupParam);
    }

    this.jobService.getJobClasses().subscribe((jobClasses) => {
      this.jobClasses = jobClasses;
    });
  }

  getJob(jobName: string, jobGroup: string) {
    this.jobService.get(jobName, jobGroup).subscribe(
      (job) => {
        this.job = job;
        this.jobForm.patchValue(job);
        // setting jobMapData
        this.setJobData();

        this.triggers = this.job.triggerDetails;
        this.dataSourceTriggers = of(this.triggers);

        this.executionHistory = this.job.executionHistory;
        this.dataSourceExecutionHistory = of(this.executionHistory);
      },
      (error) => (this.errorMessage = <any>error)
    );
  }

  getJobExecutionHistory(jobName: string, jobGroup: string) {
    this.jobService.getJobExecutionHistoryByJob(jobName, jobGroup, [], 0, 30, null).subscribe(
      (executionHistory) => {
        this.executionHistory = executionHistory;
        this.dataSourceExecutionHistory = of(this.executionHistory);
      },
      (error) => (this.errorMessage = <any>error)
    );
  }

  private setJobData() {
    let jobMapData = this.job['jobMapData'];
    if (jobMapData) {
      let jobDataKeys = Object.keys(jobMapData);
      jobDataKeys.forEach((key) => {
        this.ELEMENT_DATA.push(
          plainToClass(JobData, {
            dataKey: key,
            dataValue: jobMapData[key],
          } as Object)
        );
      });

      this.dataSourceJobData = of(this.ELEMENT_DATA);
    }
  }

  addJobData(): void {
    this.ELEMENT_DATA.push(
      plainToClass(JobData, {
        dataKey: '',
        dataValue: '',
      })
    );
    this.dataSourceJobData = of(this.ELEMENT_DATA);
  }

  removeJobData(index): void {
    this.ELEMENT_DATA.splice(index, 1);
    this.dataSourceJobData = of(this.ELEMENT_DATA);
  }

  compareFn: ((f1: any, f2: any) => boolean) | null = this.compareByValue;

  compareByValue(f1: any, f2: any) {
    return f1 && f2 && f1 === f2;
  }

  onSubmit() {
    this.submitted = true;

    // stop here if form is invalid
    if (this.jobForm.invalid) {
      return;
    }

    this.loading = true;
    let newJob = {};
    newJob = this.jobForm.value;
    newJob['jobMapData'] = {};
    this.ELEMENT_DATA.forEach(function (obj) {
      let tmp = {};
      tmp[obj.dataKey] = obj.dataValue;
      newJob['jobMapData'][obj.dataKey] = obj.dataValue;
    });
    this.jobService
      .update(newJob, newJob['jobName'], newJob['jobGroup'])
      .pipe(first())
      .subscribe(
        (data) => {
          this.errorService.showError(this.translate.instant('JOBS.MESSAGES.UPDATED'));
          this.router.navigate(['/scheduler/jobs']);
        },
        (error) => {
          this.loading = false;
        }
      );
  }

  back() {
    this.router.navigate(['/jobs']);
  }
}
