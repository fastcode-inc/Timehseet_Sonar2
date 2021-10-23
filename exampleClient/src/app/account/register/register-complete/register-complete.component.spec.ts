import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterCompleteComponent } from './register-complete.component';
import { TestingModule } from 'src/testing/utils';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

describe('RegisterCompleteComponent', () => {
  let component: RegisterCompleteComponent;
  let fixture: ComponentFixture<RegisterCompleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterCompleteComponent],
      imports: [TestingModule],
      providers: [],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterCompleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    const aroutes = TestBed.inject(ActivatedRoute);
    aroutes.snapshot.queryParams['email'] = 'test@test.com';
    expect(component).toBeTruthy();
  });
});
