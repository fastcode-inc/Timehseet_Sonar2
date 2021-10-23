import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IJob } from './ijob';
import { IExecutingJob } from '../executing-jobs/executingJob';
import { IExecutionHistory } from '../execution-history/executionHistory';

import { ISearchField, ServiceUtils } from 'src/app/common/shared';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class JobService {
  baseUrl = '';

  resp: any;

  constructor(private httpclient: HttpClient) {
    this.baseUrl = environment.apiUrl + '/scheduler/jobs';
  }

  public getJobExecutionHistoryByJob(
    jobName: string,
    jobGroup: string,
    searchFields: ISearchField[],
    offset: number,
    limit: number,
    sort: string
  ): Observable<IExecutionHistory[]> {
    let params = ServiceUtils.buildQueryData(searchFields, offset, limit, sort);
    return this.httpclient
      .get<IExecutionHistory[]>(`${this.baseUrl}/${jobName}/${jobGroup}/jobExecutionHistory`, { params })
      .pipe(catchError(this.handleError));
  }

  public getJobExecutionHistory(
    searchFields: ISearchField[],
    offset: number,
    limit: number,
    sort: string
  ): Observable<IExecutionHistory[]> {
    let params = ServiceUtils.buildQueryData(searchFields, offset, limit, sort);
    return this.httpclient
      .get<IExecutionHistory[]>(this.baseUrl + '/jobExecutionHistory', { params })
      .pipe(catchError(this.handleError));
  }

  public getExecutingJobs(): Observable<IExecutingJob[]> {
    return this.httpclient.get<IExecutingJob[]>(this.baseUrl + '/executingJobs').pipe(catchError(this.handleError));
  }

  public getAll(searchFields: ISearchField[], offset: number, limit: number, sort: string): Observable<IJob[]> {
    let params = ServiceUtils.buildQueryData(searchFields, offset, limit, sort);
    return this.httpclient
      .get<IJob[]>(this.baseUrl, { params })
      .pipe(catchError(this.handleError));
  }

  public get(jobName, jobGroup): Observable<IJob> {
    this.resp = this.httpclient.get<IJob>(this.baseUrl + '/' + jobName + '/' + jobGroup);
    return this.resp;
  }

  public create(item: IJob): Observable<IJob> {
    return this.httpclient.post<IJob>(this.baseUrl, item).pipe(catchError(this.handleError));
  }
  public update(item: IJob, jobName, jobGroup): Observable<IJob> {
    return this.httpclient
      .put<IJob>(this.baseUrl + '/' + jobName + '/' + jobGroup, item)
      .pipe(catchError(this.handleError));
  }
  public delete(jobName, jobGroup): Observable<null> {
    return this.httpclient.delete(this.baseUrl + '/' + jobName + '/' + jobGroup).pipe(
      map((res) => null),
      catchError(this.handleError)
    );
  }

  public getJobGroups(): Observable<string[]> {
    return this.httpclient.get<string[]>(this.baseUrl + '/getJobGroups').pipe(catchError(this.handleError));
  }

  public getJobClasses(): Observable<string[]> {
    return this.httpclient.get<string[]>(this.baseUrl + '/getJobClasses').pipe(catchError(this.handleError));
  }

  public pauseJob(jobName, jobGroup): Observable<boolean> {
    return this.httpclient
      .get<boolean>(this.baseUrl + '/pauseJob/' + jobName + '/' + jobGroup)
      .pipe(catchError(this.handleError));
  }

  public resumeJob(jobName, jobGroup): Observable<boolean> {
    return this.httpclient
      .get<boolean>(this.baseUrl + '/resumeJob/' + jobName + '/' + jobGroup)
      .pipe(catchError(this.handleError));
  }

  protected handleError(err: HttpErrorResponse) {
    let errorMessage;
    if (err.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      errorMessage = `An error occurred: ${err.error.message}`;
    } else {
      errorMessage = `Server returned code: ${err.status}, error message is: ${err.message}`;
    }
    console.error(errorMessage);
    return throwError(errorMessage);
  }
}
