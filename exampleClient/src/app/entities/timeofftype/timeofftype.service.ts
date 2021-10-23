import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ITimeofftype } from './itimeofftype';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class TimeofftypeService extends GenericApiService<ITimeofftype> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'timeofftype');
  }
}
