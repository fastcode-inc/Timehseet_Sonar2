import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient, HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { SwaggerComponent } from './core/swagger/swagger.component';
import { ErrorPageComponent } from './core/error-page/error-page.component';
import { JwtInterceptor } from './core/services/jwt-interceptor';
/** core components and filters for authorization and authentication **/

import { AuthenticationService } from './core/services/authentication.service';
import { AuthGuard } from './core/guards/auth.guard';
import { JwtErrorInterceptor } from './core/services/jwt-error-interceptor';
import { GlobalPermissionService } from './core/services/global-permission.service';

// import { LoginComponent } from './core/login/login.component';
import { LoginExtendedComponent } from './extended/core/login/login.component';

/** end of core components and filters for authorization and authentication **/
import { ResourceViewComponent } from 'src/app/reporting-module/pages/resourceView/resourceView.component';
import { ReportPasswordComponent } from 'src/app/reporting-module/pages/myreports/report-password/report-password.component';
import { ThemeService } from 'ng2-charts';
import { CubejsClientModule } from '@cubejs-client/ngx';
import { routingModule } from './app.routing';
import { SharedModule } from 'src/app/common/shared';
// import { CoreModule } from './core/core.module';
import { CoreExtendedModule } from './extended/core/core.module';
import { GeneralComponentsExtendedModule } from './extended/common/general-components/general-extended.module';
import { environment } from 'src/environments/environment';

export function HttpLoaderFactory(httpClient: HttpClient) {
  return new TranslateHttpLoader(httpClient);
}

const cubejsOptions = {
  token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.K9PiJkjegbhnw4Ca5pPlkTmZihoOm42w8bja9Qs2qJg',
  options: {
    apiUrl: `${environment.reportingUrl}/cubejs-api/v1`,
  },
};

@NgModule({
  declarations: [ErrorPageComponent, SwaggerComponent, AppComponent, ResourceViewComponent],
  imports: [
    BrowserModule,
    routingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    // CoreModule,
    CoreExtendedModule,
    SharedModule,
    GeneralComponentsExtendedModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    CubejsClientModule.forRoot(cubejsOptions),
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    AuthenticationService,
    GlobalPermissionService,
    { provide: HTTP_INTERCEPTORS, useClass: JwtErrorInterceptor, multi: true },
    AuthGuard,
    ThemeService,
  ],
  bootstrap: [AppComponent],
  entryComponents: [ReportPasswordComponent],
})
export class AppModule {}
