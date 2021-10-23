import { TestBed } from '@angular/core/testing';

import { RegisterService } from './register.service';
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

  let service: RegisterService;
  let httpMock;
  let baseUrl = environment.apiUrl + '/register';
  let url = `${baseUrl}?clientUrl=${location.origin}`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(RegisterService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should verify email', () => {
    let token = 'sample token';
    let sampleRes = { message: 'email verified' };
    service.verifyEmail(token).subscribe((response) => {
      expect(response).toEqual(sampleRes);
    });

    const req = httpMock.expectOne(
      (req) => req.method === 'POST' && req.url === `${baseUrl}/verifyEmail?token=${token}`
    );
    req.flush(sampleRes);
  });

  it('should register new user', () => {
    let sampleRes = { message: 'user registered' };
    service.register(data, location.origin).subscribe((response) => {
      expect(response).toEqual(sampleRes);
    });

    const req = httpMock.expectOne((req) => req.method === 'POST' && req.url === url);
    req.flush(sampleRes);
  });

  it('should propagate error response in case of server side error', () => {
    service.register(data, location.origin).subscribe(null, (errorMessage: any) => {
      expect(errorMessage.length).toBeGreaterThan(0);
    });

    const req = httpMock.expectOne((req) => req.method === 'POST' && req.url === url);
    req.flush('Invalid request parameters', {
      status: 404,
      statusText: 'Bad Request',
    });
  });

  it('should propagate error response in case of client side error', () => {
    service.register(data, location.origin).subscribe(null, (errorMessage: any) => {
      expect(errorMessage.length).toBeGreaterThan(0);
    });

    httpMock.expectOne((req) => req.method === 'POST' && req.url === url).error(new ErrorEvent('network error'));
  });
});
