import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { IDashboard } from './idashboard';
import { ShareApiService } from '../../services/share-api.service';

@Injectable({
  providedIn: 'root',
})
export class DashboardService extends ShareApiService<IDashboard> {
  constructor(protected http: HttpClient) {
    super(http, 'reporting/dashboard');
  }

  removeReport(id: number, reportId: number): Observable<any> {
    return this.http.delete(`${this.url}/${id}/report/${reportId}`, {}).pipe(catchError(this.handleError));
  }

  // report dashboard calls

  addNewReporttoNewDashboard(data: any) {
    return this.http.post<any>(this.url + '/addNewReportToNewDashboard', data);
  }
  addNewReporttoExistingDashboard(data: any) {
    return this.http.put<any>(this.url + '/addNewReportToExistingDashboard', data);
  }
  addExistingReportToNewDashboard(data: any) {
    return this.http.post<any>(this.url + '/addExistingReportToNewDashboard', data);
  }
  addExistingReportToExistingDashboard(data: any) {
    return this.http.put<any>(this.url + '/addExistingReportToExistingDashboard', data);
  }
}
