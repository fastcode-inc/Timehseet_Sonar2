import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ICustomer } from './icustomer';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class CustomerService extends GenericApiService<ICustomer> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'customer');
  }
}
