import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ITimesheetstatus } from './itimesheetstatus';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class TimesheetstatusService extends GenericApiService<ITimesheetstatus> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'timesheetstatus');
  }
}
