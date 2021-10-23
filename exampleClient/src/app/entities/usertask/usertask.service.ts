import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IUsertask } from './iusertask';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class UsertaskService extends GenericApiService<IUsertask> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'usertask');
  }
}
