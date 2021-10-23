import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-addReportsToDashboard',
  templateUrl: './addReportsToDashboard.component.html',
  styleUrls: ['./addReportsToDashboard.component.scss'],
})
export class AddReportsToDashboardComponent {
  choosedashboard = 'new';
  dashboard = {
    title: '',
    description: '',
  };
  report = {
    title: '',
    width: 'mediumchart',
    description: '',
  };
  response = {};
  constructor(
    public dialogRef: MatDialogRef<AddReportsToDashboardComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  getDashboardData() {
    this.response = {
      type: this.choosedashboard,
      title: this.dashboard.title,
      description: this.dashboard.description,
      chartTitle: this.report.title,
      reportdescription: this.report.description,
      chartSize: this.report.width,
    };
    this.dialogRef.close(this.response);
  }
}
