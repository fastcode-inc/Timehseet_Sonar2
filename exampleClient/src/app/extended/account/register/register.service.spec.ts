import { TestBed } from '@angular/core/testing';

import { RegisterExtendedService } from './register.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { environment } from 'src/environments/environment';

describe('Register Service', () => {
  let data = {
    emailAddress: 'emailAddress1@test.com',
    firstName: 'firstName1',
    lastName: 'lastName1',
    phoneNumber: 'phoneNumber1',
    userName: 'userName1',
    password: 'password1',
  };

  let service: RegisterExtendedService;
  let httpMock;
  let url: string = environment.apiUrl + '/register';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(RegisterExtendedService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
