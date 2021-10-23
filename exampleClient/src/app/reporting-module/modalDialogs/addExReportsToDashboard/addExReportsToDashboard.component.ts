import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-addReportsToDashboard',
  templateUrl: './addExReportsToDashboard.component.html',
  styleUrls: ['./addExReportsToDashboard.component.scss'],
})
export class AddExReportsToDashboardComponent {
  choosedashboard = 'new';
  dashboard = {
    title: '',
    description: '',
    id: null,
  };
  report = {
    title: '',
    width: 'mediumchart',
    description: '',
  };
  response = {};
  constructor(
    public dialogRef: MatDialogRef<AddExReportsToDashboardComponent>,
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
      chartSize: this.report.width,
      id: this.dashboard.id,
    };
    this.dialogRef.close(this.response);
  }
}
