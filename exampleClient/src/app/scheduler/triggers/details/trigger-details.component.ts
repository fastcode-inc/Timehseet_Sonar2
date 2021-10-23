import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { TriggerService } from '../trigger.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { ITrigger } from '../trigger';
import { JobData } from '../../jobs/jobData';
import { ExecutionHistory } from '../../execution-history/executionHistory';
import { plainToClass } from 'class-transformer';
import { TranslateService } from '@ngx-translate/core';
import { ListColumnType, IListColumn } from 'src/app/common/shared';
import { ErrorService } from 'src/app/core/services/error.service';
import { Globals } from 'src/app/core/services/globals';

@Component({
  selector: 'app-trigger-details',
  templateUrl: './trigger-details.component.html',
  styleUrls: ['./trigger-details.component.scss'],
})
export class TriggerDetailsComponent implements OnInit {
  trigger: ITrigger;
  selectJobDialogRef: MatDialogRef<any>;

  isMediumDeviceOrLess: boolean;
  mediumDeviceOrLessDialogSize: string = '80%';
  largerDeviceDialogWidthSize: string = '65%';
  largerDeviceDialogHeightSize: string = '65%';

  errorMessage = '';
  triggerForm: FormGroup;
  loading = false;
  submitted = false;

  triggerTypes: string[] = ['Simple', 'Cron'];

  // table data for triggerMapData
  triggerMapData: JobData[] = [];
  displayedColumns: string[] = ['position', 'name', 'actions'];
  dataSourceJobData = of(this.triggerMapData);
  triggerNameParam = this.route.snapshot.paramMap.get('triggerName');
  triggerGroupParam = this.route.snapshot.paramMap.get('triggerGroup');
  // table data for execution history
  executionHistorycolumns: IListColumn[] = [
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
  // displayedColumnsExecutionHistory: string[] = ['triggerName', 'triggerGroup']
  executionHistory: ExecutionHistory[] = [];
  dataSourceExecutionHistory = of(this.executionHistory);

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private global: Globals,
    private triggerService: TriggerService,
    private route: ActivatedRoute,
    public dialog: MatDialog,
    private translate: TranslateService,
    private errorService: ErrorService
  ) {}

  ngOnInit() {
    this.global.isMediumDeviceOrLess$.subscribe((value) => {
      this.isMediumDeviceOrLess = value;
      if (this.selectJobDialogRef)
        this.selectJobDialogRef.updateSize(
          value ? this.mediumDeviceOrLessDialogSize : this.largerDeviceDialogWidthSize,
          value ? this.mediumDeviceOrLessDialogSize : this.largerDeviceDialogHeightSize
        );
    });

    this.triggerForm = this.formBuilder.group({
      jobName: ['', Validators.required],
      jobGroup: ['', Validators.required],
      triggerName: ['', Validators.required],
      triggerGroup: ['', Validators.required],
      triggerType: ['', Validators.required],
      startDate: [''],
      startTime: [''],
      endDate: [''],
      endTime: [''],
      lastExecutionTime: [''],
      nextExecutionTime: [''],
      cronExpression: [''],
      repeatInterval: ['', Validators.required],
      repeatIndefinitely: [''],
      repeatCount: [''],
      description: [''],
    });
    if (this.triggerNameParam && this.triggerGroupParam) {
      this.getTrigger(this.triggerNameParam, this.triggerGroupParam);
      this.getTriggerExecutionHistory(this.triggerNameParam, this.triggerGroupParam);
    }
  }

  getTrigger(triggerName: string, triggerGroup: string) {
    this.triggerService.get(triggerName, triggerGroup).subscribe(
      (trigger) => {
        if (trigger) {
          this.trigger = trigger;
          let triggerobj = {
            ...trigger,
            lastExecutionTime: trigger.lastExecutionTime ? new Date(trigger.lastExecutionTime) : null,
            nextExecutionTime: trigger.nextExecutionTime ? new Date(trigger.nextExecutionTime) : null,
            startTime: this.formatDateStringToAMPM(trigger.startTime),
            startDate: trigger.startTime ? new Date(trigger.startTime) : null,
            endTime: this.formatDateStringToAMPM(trigger.endTime),
            endDate: trigger.endTime ? new Date(trigger.endTime) : null,
          };

          this.triggerForm.patchValue(triggerobj);
          this.setJobData();
        }
      },
      (error) => (this.errorMessage = <any>error)
    );
  }

  getTriggerExecutionHistory(triggerName: string, triggerGroup: string) {
    this.triggerService.getTriggerExecutionHistoryByJob(triggerName, triggerGroup, [], 0, 30, null).subscribe(
      (executionHistory) => {
        this.executionHistory = executionHistory;
        this.dataSourceExecutionHistory = of(this.executionHistory);
      },
      (error) => (this.errorMessage = <any>error)
    );
  }

  formatDateStringToAMPM(d) {
    if (d) {
      let date = new Date(d);
      let hours = date.getHours();
      let minutes = date.getMinutes();
      let ampm = hours >= 12 ? 'pm' : 'am';
      hours = hours % 12;
      hours = hours ? hours : 12; // the hour '0' should be '12'
      let minutes_str = minutes < 10 ? '0' + minutes : minutes;
      let strTime = hours + ':' + minutes_str + ' ' + ampm;
      return strTime;
    }
    return null;
  }

  private setJobData() {
    let triggerMapData = this.trigger['triggerMapData'];
    if (triggerMapData) {
      let jobDataKeys = Object.keys(triggerMapData);
      jobDataKeys.forEach((key) => {
        this.triggerMapData.push(
          plainToClass(JobData, {
            dataKey: key,
            dataValue: triggerMapData[key],
          } as Object)
        );
      });

      this.dataSourceJobData = of(this.triggerMapData);
    }
  }

  onSubmit() {
    this.submitted = true;

    // stop here if form is invalid
    if (this.triggerForm.invalid) {
      return;
    }

    this.loading = true;
    console.log(this.triggerForm.value);
    let newTrigger = {};
    newTrigger = this.triggerForm.value;
    newTrigger['triggerMapData'] = {};
    this.triggerMapData.forEach(function (obj) {
      let tmp = {};
      tmp[obj.dataKey] = obj.dataValue;
      newTrigger['triggerMapData'][obj.dataKey] = obj.dataValue;
    });

    //to combine both date and time into same object for start and end times of triggers
    newTrigger['startTime'] = this.combineDateAndTime(newTrigger['startDate'], newTrigger['startTime']);
    newTrigger['endTime'] = this.combineDateAndTime(newTrigger['endDate'], newTrigger['endTime']);
    delete newTrigger['startDate'];
    delete newTrigger['endDate'];

    this.triggerService
      .update(newTrigger, this.trigger.triggerName, this.trigger.triggerGroup)
      .pipe(first())
      .subscribe(
        (data) => {
          this.errorService.showError(this.translate.instant('TRIGGERS.MESSAGES.UPDATED'));
          this.router.navigate(['/scheduler/triggers']);
        },
        (error) => {
          this.loading = false;
        }
      );
  }

  combineDateAndTime(date: string, time: string): Date {
    let tmpDate = new Date(date);
    let hours: number = parseInt(time.substring(0, 2));
    let minutes = parseInt(time.substring(3, 5));
    let ampm = time.substring(6, 8) ? time.substring(6, 8) : 'am';
    if (ampm.toLocaleLowerCase() == 'pm') {
      hours = hours + 12;
    } else if (ampm.toLocaleLowerCase() == 'am' && hours === 12) {
      hours = 0;
    }
    tmpDate.setHours(hours ? hours : 0);
    tmpDate.setMinutes(minutes ? minutes : 0);
    return tmpDate;
  }

  onCancel() {
    this.router.navigate(['/triggers']);
  }
  addJobData(): void {
    this.triggerMapData.push(
      plainToClass(JobData, {
        dataKey: '',
        dataValue: '',
      })
    );
    this.dataSourceJobData = of(this.triggerMapData);
  }

  removeJobData(index): void {
    this.triggerMapData.splice(index, 1);
    this.dataSourceJobData = of(this.triggerMapData);
  }
}
