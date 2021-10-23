import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IPermalink } from './ipermalink';
import { GenericApiService } from 'src/app/common/shared';
import { environment } from '../../../../environments/environment';
import { catchError } from 'rxjs/internal/operators/catchError';

@Injectable({
  providedIn: 'root',
})
export class PermalinkService extends GenericApiService<IPermalink> {
  constructor(private httpclient: HttpClient) {
    super(httpclient, 'reporting/permalink');
  }

  getByResrouce(resource: string, resourceId: number) {
    return this.http.get<IPermalink>(`${this.url}/${resource}/${resourceId}`);
  }
}
