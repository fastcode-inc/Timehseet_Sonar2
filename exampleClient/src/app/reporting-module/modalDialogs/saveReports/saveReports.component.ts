import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-savereports',
  templateUrl: './saveReports.component.html',
  styleUrls: ['./saveReports.component.scss'],
})
export class SaveReportsComponent {
  choosedashboard = 'new';
  report = {
    title: '',
    description: '',
  };
  response = {};
  constructor(public dialogRef: MatDialogRef<SaveReportsComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
    this.report.title = data.title;
    this.report.description = data.description;
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  save() {
    this.response = {
      type: this.choosedashboard,
      reportTitle: this.report.title,
      reportdescription: this.report.description,
    };
    this.dialogRef.close(this.response);
  }
}
