import { Component, OnInit, Inject } from '@angular/core';
import { ReportPasswordComponent } from '../myreports/report-password/report-password.component';
import { Globals } from 'src/app/core/services/globals';
import { ErrorService } from 'src/app/core/services/error.service';
import { IPermalink } from '../permalink/ipermalink';
import { PermalinkService } from '../permalink/permalink.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogRef } from '@angular/material/dialog';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';

export enum AccessOptions {
  Login = 'Login',
  noLogin = 'noLogin',
  Password = 'Password',
}

@Component({
  selector: 'app-permalink',
  templateUrl: './permalink.component.html',
  styleUrls: ['./permalink.component.scss'],
})
export class PermalinkComponent implements OnInit {
  window = window;
  accessOption: string;
  accessOptionIcon: string;
  refreshrate: any = 120;
  accessURL: string = `${window.origin}/resourceView/`;
  embedlink: string =
    '<iframe frameborder=0 width=`${width}` height=`${height}` src= "`${window.origin}`/resourceView/`${resource.data.id}`"';
  accessOptionTitle: string;
  accessPassword: string;
  permalink: IPermalink;
  passwordDialogRef: MatDialogRef<ReportPasswordComponent>;

  title: any;

  constructor(
    public permalinkservice: PermalinkService,
    public _snackBar: MatSnackBar,
    public dialog: MatDialog,
    public global: Globals,
    public dialogRef: MatDialogRef<ReportPasswordComponent>,
    public permalindialogRef: MatDialogRef<PermalinkComponent>,
    public snackBar: MatSnackBar,
    public errorService: ErrorService,
    public translate: TranslateService,

    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.title = 'External Share Options';
  }

  ngOnInit() {
    this.getPermalink();
  }

  getPermalink() {
    this.permalinkservice.getByResrouce(this.data.resource, this.data.resourceId).subscribe(
      (res) => {
        if (res) {
          this.permalink = res;
          this.selectAccessOption(this.permalink.authentication);
        } else {
          this.createPermalink();
        }
      },
      (err) => {
        this.createPermalink();
      }
    );
  }

  createPermalink() {
    this.permalink = {
      authentication: 'Login',
      description: false,
      refreshRate: 10,
      rendering: 'interactive',
      height: 600,
      width: 800,
      resource: this.data.resource,
      password: null,
      resourceId: this.data.resourceId,
      toolbar: false,
    };
    this.permalinkservice.create(this.permalink).subscribe((permalink) => {
      this.permalink = permalink;
      this.selectAccessOption(this.permalink.authentication);
    });
  }

  closeExternalShare() {
    // this.externalShareView.emit(false);
  }

  selectAccessOption(opt) {
    if (AccessOptions.Login == opt) {
      this.accessOptionIcon = 'group';
      this.accessOption = opt;
      this.permalink.authentication = opt;
      this.accessOptionTitle = this.translate.instant('REPORTING.LABELS.PERMALINK.ACCESS-LOGIN');
    }
    if (AccessOptions.noLogin == opt) {
      this.accessOptionIcon = 'link_off';
      this.accessOption = opt;
      this.permalink.authentication = opt;
      this.accessOptionTitle = this.translate.instant('REPORTING.LABELS.PERMALINK.ACCESS-WITHOUT-LOGIN');
    }
    if (AccessOptions.Password == opt) {
      this.accessOptionIcon = 'vpn_key';
      this.accessOption = opt;
      this.permalink.authentication = opt;
      this.accessOptionTitle = this.translate.instant('REPORTING.LABELS.PERMALINK.ACCESS-PASSWORD');
      if (!this.permalink.password) {
        this.passwordDialogRef = this.dialog.open(ReportPasswordComponent, {
          disableClose: true,
          panelClass: 'fc-modal-dialog',
          data: {
            type: 'setPassword',
          },
        });
      }

      this.passwordDialogRef.afterClosed().subscribe((action) => {
        if (action.confirm) {
          this.accessPassword = action.password;
          //password set
        } else {
          this.accessPassword = '';
          //no password set
        }
      });
    }
  }

  saveAndShareExternally() {
    this.permalink.authentication = this.accessOption;
    // this.permalink.refreshRate = this.refreshrate;
    this.permalink.password = this.accessPassword;
    this.permalink.rendering = 'interactive';
    this.permalink.resourceId = this.data.resourceId;
    this.permalinkservice.update(this.permalink, this.permalink.id).subscribe((permalink) => {
      this.permalink = permalink;
      this.permalindialogRef.close({ confirm: true });
    });
  }

  cancel() {
    this.dialogRef.close({ confirm: false });
  }
}
