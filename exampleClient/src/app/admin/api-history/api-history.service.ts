import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { IApiHistory } from './apiHistory';
@Injectable({
  providedIn: 'root',
})
export class ApiHistoryService {
  url = environment.apiUrl;

  constructor(private httpclient: HttpClient) {}

  public getAll(search: string, offset: number, limit: number): Observable<IApiHistory[]> {
    return this.httpclient
      .get<IApiHistory[]>(this.url + '/audit/apis' + '?search=' + search + '&offset=' + offset + '&limit=' + limit)
      .pipe();
  }
}
