import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { TestingModule } from 'src/testing/utils';
import { ConfirmEmailExtendedComponent } from './confirm-email.component';
import { RegisterExtendedService } from '../register/register.service';

describe('ConfirmEmailExtendedComponent', () => {
  let component: ConfirmEmailExtendedComponent;
  let fixture: ComponentFixture<ConfirmEmailExtendedComponent>;
  let el: HTMLElement;

  describe('Unit Tests', () => {
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [ConfirmEmailExtendedComponent],
        imports: [TestingModule],
        providers: [RegisterExtendedService],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(ConfirmEmailExtendedComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  });
});
