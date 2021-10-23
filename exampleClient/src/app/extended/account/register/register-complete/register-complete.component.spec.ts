import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterCompleteExtendedComponent } from './register-complete.component';
import { TestingModule } from 'src/testing/utils';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('RegisterCompleteExtendedComponent', () => {
  let component: RegisterCompleteExtendedComponent;
  let fixture: ComponentFixture<RegisterCompleteExtendedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterCompleteExtendedComponent],
      imports: [TestingModule],
      providers: [],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterCompleteExtendedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
});
