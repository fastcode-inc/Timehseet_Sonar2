import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { IShareConfig, sharingType } from './ishare-config';
import { ReportService } from '../myreports/report.service';
import { DashboardService } from '../dashboard/dashboard.service';
import { ShareApiService } from '../../services/share-api.service';

@Component({
  selector: 'app-share',
  templateUrl: './share.component.html',
  styleUrls: ['./share.component.scss'],
})
export class ShareComponent implements OnInit {
  rolePrimaryKeys: string[] = ['id'];
  userPrimaryKeys: string[] = ['id'];

  userDescriptiveField: string = 'username';
  roleDescriptiveField: string = 'displayName';

  service: ShareApiService<any>;
  shareData = {
    usersList: [],
    rolesList: [],
  };

  constructor(
    public dialogRef: MatDialogRef<ShareComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IShareConfig,
    public reportService: ReportService,
    public dashboardService: DashboardService
  ) {
    if (this.data.resource == 'report') {
      this.service = this.reportService;
    } else if (this.data.resource == 'dashboard') {
      this.service = this.dashboardService;
    }
  }

  ngOnInit() {}

  cancel() {
    this.dialogRef.close(null);
  }

  select() {
    this.dialogRef.close(this.shareData);
  }

  usersServiceMethod = (searchText, offset: number, limit: number) => {
    if (this.data.type == sharingType.Share)
      return this.service.getAvailableAssociations('user', this.data.id, searchText, offset, limit);
    return this.service.getAssociations('user', this.data.id, searchText, offset, limit);
  };

  roleServiceMethod = (searchText, offset: number, limit: number) => {
    if (this.data.type == sharingType.Share)
      return this.service.getAvailableAssociations('role', this.data.id, searchText, offset, limit);
    return this.service.getAssociations('role', this.data.id, searchText, offset, limit);
  };

  selectionUpdated(data, key) {
    this.shareData[key] = data;
  }
}
