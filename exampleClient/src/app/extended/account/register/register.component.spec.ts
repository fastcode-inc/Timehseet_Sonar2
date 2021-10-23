import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { TestingModule } from 'src/testing/utils';
import { RegisterExtendedComponent } from './register.component';
import { RegisterExtendedService } from './register.service';

describe('RegisterExtendedComponent', () => {
  let component: RegisterExtendedComponent;
  let fixture: ComponentFixture<RegisterExtendedComponent>;
  let el: HTMLElement;

  let relationData: any = {};
  let data = {
    emailAddress: 'emailAddress1@test.com',
    firstName: 'firstName1',
    id: 1,
    isActive: true,
    lastName: 'lastName1',
    password: 'password1',
    confirmPassword: 'password1',
    phoneNumber: 'phoneNumber1',
    userName: 'userName1',
    ...relationData,
  };

  describe('Unit Tests', () => {
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [RegisterExtendedComponent],
        imports: [TestingModule],
        providers: [RegisterExtendedService],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(RegisterExtendedComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  });
});
