import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IUserspermission } from './iuserspermission';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class UserspermissionService extends GenericApiService<IUserspermission> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'userspermission');
  }
}
