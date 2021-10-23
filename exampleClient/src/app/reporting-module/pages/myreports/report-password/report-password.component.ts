import { Component, Inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogRef } from '@angular/material/dialog';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-report-password',
  templateUrl: 'report-password.component.html',
  styleUrls: ['report-password.component.scss'],
})
export class ReportPasswordComponent {
  reportpassword: any;
  confirmreportpassword: any = '';
  confirmation: any;
  disableOk = true;
  title: string;
  constructor(
    public dialogRef: MatDialogRef<ReportPasswordComponent>,
    public snackBar: MatSnackBar,
    public translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    if (this.data.type == 'Password') {
      this.title = this.translate.instant('REPORTING.LABELS.PASSWORD');
    } else {
      this.title = this.translate.instant('REPORTING.LABELS.SET-PASSWORD');
    }
  }

  confirm() {
    this.dialogRef.close({ password: this.reportpassword, confirm: true });
  }

  cancel() {
    this.dialogRef.close(null);
  }

  confirmPassword(value: string): void {
    if (this.data.type == 'setPassword') {
      if (value == this.reportpassword) {
        this.confirmation = true;
        this.disableOk = false;
      } else {
        this.disableOk = true;
        this.confirmation = false;
      }
    } else {
      this.disableOk = false;
    }
  }
}
