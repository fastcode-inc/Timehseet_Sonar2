import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IRolepermission } from './irolepermission';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class RolepermissionService extends GenericApiService<IRolepermission> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'rolepermission');
  }
}
