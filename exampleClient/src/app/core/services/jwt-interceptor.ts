import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';
import { CookieService } from './cookie.service';
import { environment } from 'src/environments/environment';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthenticationService, private cookieService: CookieService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // add authorization header with jwt token if available

    const headers: any = {
      Accept: 'application/json',
      'content-type': 'application/json',
    };

    const token = this.authService.token;
    headers['X-XSRF-TOKEN'] = this.cookieService.get('XSRF-TOKEN');
    if (token) {
      headers.Authorization = token;
    }
    request = request.clone({
      setHeaders: headers,
    });

    if (request.url.includes(environment.apiUrl)) {
      request = request.clone({
        withCredentials: true,
      });
    }

    return next.handle(request);
  }
}
