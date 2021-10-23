import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ITrigger } from './trigger';

import { ISearchField, ServiceUtils } from 'src/app/common/shared';
import { IExecutionHistory } from '../execution-history';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TriggerService {
  baseUrl = '';

  resp: any;
  constructor(private httpclient: HttpClient) {
    this.baseUrl = environment.apiUrl + '/scheduler/triggers';
  }

  public getAll(searchFields: ISearchField[], offset: number, limit: number, sort: string): Observable<ITrigger[]> {
    let params = ServiceUtils.buildQueryData(searchFields, offset, limit, sort);
    return this.httpclient
      .get<ITrigger[]>(this.baseUrl, { params })
      .pipe();
  }

  public getTriggerExecutionHistoryByJob(
    triggerName: string,
    triggerGroup: string,
    searchFields: ISearchField[],
    offset: number,
    limit: number,
    sort: string
  ): Observable<IExecutionHistory[]> {
    let params = ServiceUtils.buildQueryData(searchFields, offset, limit, sort);
    return this.httpclient
      .get<IExecutionHistory[]>(`${this.baseUrl}/${triggerName}/${triggerGroup}/jobExecutionHistory`, { params })
      .pipe(catchError(this.handleError));
  }

  public get(triggerName, triggerGroup): Observable<ITrigger> {
    this.resp = this.httpclient.get<ITrigger>(this.baseUrl + '/' + triggerName + '/' + triggerGroup);
    return this.resp;
  }

  public create(item: ITrigger): Observable<ITrigger> {
    return this.httpclient.post<ITrigger>(this.baseUrl, item).pipe();
  }
  public update(item: any, triggerName, triggerGroup): Observable<ITrigger> {
    return this.httpclient.put<ITrigger>(this.baseUrl + '/' + triggerName + '/' + triggerGroup, item).pipe();
  }
  public delete(triggerName, triggerGroup): Observable<null> {
    return this.httpclient.delete(this.baseUrl + '/' + triggerName + '/' + triggerGroup).pipe(map((res) => null));
  }

  public getTriggerGroups(): Observable<string[]> {
    return this.httpclient.get<string[]>(this.baseUrl + '/getTriggerGroups').pipe();
  }

  public pauseTrigger(triggerName, triggerGroup): Observable<boolean> {
    return this.httpclient.get<boolean>(this.baseUrl + '/pauseTrigger/' + triggerName + '/' + triggerGroup).pipe();
  }

  public resumeTrigger(triggerName, triggerGroup): Observable<boolean> {
    return this.httpclient.get<boolean>(this.baseUrl + '/resumeTrigger/' + triggerName + '/' + triggerGroup).pipe();
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
