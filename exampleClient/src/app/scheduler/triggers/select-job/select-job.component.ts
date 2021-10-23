import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { JobService, IJob } from 'src/app/scheduler/jobs';

@Component({
  selector: 'app-select-job',
  templateUrl: './select-job.component.html',
  styleUrls: ['./select-job.component.scss'],
})
export class SelectJobComponent implements OnInit {
  loading = true;
  displayedColumns: string[] = ['jobName', 'jobGroup', 'jobClass'];
  jobs: IJob[] = [];
  errorMessage = '';

  constructor(private jobService: JobService, public dialogRef: MatDialogRef<SelectJobComponent>) {}

  ngOnInit() {
    this.jobService.getAll(null, null, null, null).subscribe(
      (perms) => {
        this.loading = false;
        this.jobs = perms;
      },
      (error) => {
        this.loading = false;
        this.errorMessage = error;
      }
    );
  }

  selectJob(job): void {
    this.dialogRef.close(job);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
