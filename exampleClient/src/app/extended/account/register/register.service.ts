import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { RegisterService } from 'src/app/account/register/register.service';

@Injectable({
  providedIn: 'root',
})
export class RegisterExtendedService extends RegisterService {
  constructor(public httpclient: HttpClient) {
    super(httpclient);
  }
}
