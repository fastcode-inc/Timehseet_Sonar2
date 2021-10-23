import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { TriggerService } from '../trigger.service';
import { Observable } from 'rxjs';
import { of } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { SelectJobComponent } from '../select-job/select-job.component';

@Component({
  selector: 'app-trigger-new',
  templateUrl: './trigger-new.component.html',
  styleUrls: ['./trigger-new.component.scss'],
})
export class TriggerNewComponent implements OnInit {
  selectJobDialogRef: MatDialogRef<any>;

  isMediumDeviceOrLess: boolean;
  mediumDeviceOrLessDialogSize: string = '100%';
  largerDeviceDialogWidthSize: string = '65%';
  largerDeviceDialogHeightSize: string = '75%';

  triggerForm: FormGroup;
  loading = false;
  submitted = false;
  jobselected = false;
  triggerMapData = [];

  triggerTypes: string[] = ['Simple', 'Cron'];

  displayedColumns: string[] = ['position', 'name', 'actions'];
  dataSource = of(this.triggerMapData);

  options: string[] = [];
  filteredOptions: Observable<string[]>;
  constructor(
    private formBuilder: FormBuilder,
    private triggerService: TriggerService,
    public dialogRef: MatDialogRef<TriggerNewComponent>,
    public dialog: MatDialog,
    private changeDetectorRefs: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.createForm();
    this.getTriggerGroups();
  }
  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.options.filter((option) => option.toLowerCase().includes(filterValue));
  }

  createForm() {
    this.triggerForm = this.formBuilder.group(
      {
        jobName: [{ value: 'ads', disabled: true }, Validators.required],
        jobGroup: [{ value: 'asd', disabled: true }, Validators.required],
        // jobClass: [{ value: '', disabled: true }, Validators.required],
        triggerName: ['', Validators.required],
        triggerGroup: ['', Validators.required],
        triggerType: ['', Validators.required],
        startDate: [''],
        startTime: [''],
        endDate: [''],
        endTime: [''],
        cronExpression: [''],
        repeatInterval: ['', Validators.required],
        repeatIndefinite: [''],
        repeatCount: [''],
        description: [''],
      },
      { validator: this.jobValidatorFn }
    );

    this.triggerForm.get('triggerType').setValue(this.triggerTypes[0]);
    this.triggerForm.get('repeatIndefinite').setValue(true);

    this.triggerForm.get('triggerType').valueChanges.subscribe((newForm) => {
      let triggerType = this.triggerForm.get('triggerType').value;

      if (triggerType === this.triggerTypes[0]) {
        //making cron expression not required in case of simple trigger
        this.triggerForm.get('cronExpression').setValidators([]);
        this.triggerForm.get('cronExpression').updateValueAndValidity();

        //making repeat Interval required in case of simple trigger
        this.triggerForm.get('repeatInterval').setValidators([Validators.required]);
        this.triggerForm.get('repeatInterval').updateValueAndValidity();
      } else {
        //making cron expression required in case of cron trigger
        this.triggerForm.get('cronExpression').setValidators([Validators.required]);
        this.triggerForm.get('cronExpression').updateValueAndValidity();

        //making repeat Interval not required in case of cron trigger
        this.triggerForm.get('repeatInterval').setValidators([]);
        this.triggerForm.get('repeatInterval').updateValueAndValidity();
      }
    });

    this.triggerForm.get('repeatIndefinite').valueChanges.subscribe((newForm) => {
      let triggerType = this.triggerForm.get('triggerType').value;
      let repeatIndefinite = this.triggerForm.get('repeatIndefinite').value;

      if (triggerType === this.triggerTypes[1] || repeatIndefinite) {
        //making repeat count not required in case of repeating indefinitely
        this.triggerForm.get('repeatCount').setValidators([]);
        this.triggerForm.get('repeatCount').updateValueAndValidity();
      } else if (triggerType === this.triggerTypes[0] && !repeatIndefinite) {
        //making repeat count required in case of not repeating indefinitely
        this.triggerForm.get('repeatCount').setValidators([Validators.required]);
        this.triggerForm.get('repeatCount').updateValueAndValidity();
      }
    });
  }

  getTriggerGroups() {
    this.triggerService.getTriggerGroups().subscribe((groups) => {
      this.options = groups;
      this.filteredOptions = this.triggerForm.get('triggerGroup').valueChanges.pipe(
        startWith(''),
        map((value) => this._filter(value))
      );
    });
  }

  onSubmit() {
    this.submitted = true;
    // stop here if form is invalid
    if (this.triggerForm.invalid) {
      return;
    }

    this.loading = true;
    let newTrigger;
    // using getRawValues to get disabled fields values as well
    newTrigger = this.triggerForm.getRawValue();
    newTrigger['triggerMapData'] = this.getTriggerMapData();
    //to combine both date and time into same object for start and end times of triggers
    newTrigger['startTime'] = this.combineDateAndTime(newTrigger['startDate'], newTrigger['startTime']);
    newTrigger['endTime'] = this.combineDateAndTime(newTrigger['endDate'], newTrigger['endTime']);
    delete newTrigger['startDate'];
    delete newTrigger['endDate'];
    this.triggerService
      .create(newTrigger)
      .pipe(first())
      .subscribe(
        (data) => {
          this.dialogRef.close(data);
        },
        (error) => {
          this.loading = false;
        }
      );
  }

  getTriggerMapData() {
    let triggerMapData: any = {};
    this.triggerMapData.forEach(function (obj) {
      triggerMapData[obj.dataKey] = obj.dataValue;
    });
    return triggerMapData;
  }

  combineDateAndTime(date: string, time: string): Date {
    if (!date) {
      return null;
    }
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

  onCancel(): void {
    this.dialogRef.close();
  }

  addJobData(): void {
    this.triggerMapData.push({
      dataKey: '',
      dataValue: '',
    });
    this.dataSource = of(this.triggerMapData);
  }

  removeJobData(index): void {
    this.triggerMapData.splice(index, 1);
    this.dataSource = of(this.triggerMapData);
  }

  selectJob(): void {
    this.selectJobDialogRef = this.dialog.open(SelectJobComponent, {
      disableClose: true,
      height: this.isMediumDeviceOrLess ? this.mediumDeviceOrLessDialogSize : this.largerDeviceDialogHeightSize,
      width: this.isMediumDeviceOrLess ? this.mediumDeviceOrLessDialogSize : this.largerDeviceDialogWidthSize,
      maxWidth: 'none',
      panelClass: 'fc-modal-dialog',
    });
    this.selectJobDialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.jobselected = true;
        this.triggerForm.get('jobName').setValue(result.jobName);
        this.triggerForm.get('jobGroup').setValue(result.jobGroup);
        this.changeDetectorRefs.detectChanges();
      }
    });
  }

  jobValidatorFn = (group: AbstractControl) => {
    return !group.get('jobName').value || !group.get('jobGroup').value ? { invalidRepository: true } : null;
  };
}
