import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IPermission } from './ipermission';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class PermissionService extends GenericApiService<IPermission> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'permission');
  }
}
