import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IRole } from './irole';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class RoleService extends GenericApiService<IRole> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'role');
  }
}
