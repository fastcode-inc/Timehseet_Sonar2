import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ResourceViewService {
  constructor(private httpclient: HttpClient) {}

  getResource(id): Observable<any> {
    return this.httpclient.get(`${environment.apiUrl}/reporting/viewResource/${id}`);
  }
}
