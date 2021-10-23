import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { TestingModule } from 'src/testing/utils';
import { ConfirmEmailComponent } from './confirm-email.component';
import { RegisterService } from '../register/register.service';

describe('ConfirmEmailComponent', () => {
  let component: ConfirmEmailComponent;
  let fixture: ComponentFixture<ConfirmEmailComponent>;
  let el: HTMLElement;

  describe('Unit Tests', () => {
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [ConfirmEmailComponent],
        imports: [TestingModule],
        providers: [RegisterService],
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(ConfirmEmailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should run #ngOnInit()', async () => {
      const aroutes = TestBed.inject(ActivatedRoute);
      aroutes.snapshot.queryParams['token'] = 'sampleToken';
      spyOn(component, 'verifyEmail').and.returnValue();
      component.ngOnInit();
      expect(component.verifyEmail).toHaveBeenCalledWith();
    });

    it('should call verifyEmail and handle success response', async () => {
      component.resetToken = 'sampleToken';
      spyOn(component.registerService, 'verifyEmail').and.returnValue(of(true));
      component.verifyEmail();
      expect(component.registerService.verifyEmail).toHaveBeenCalledWith('sampleToken');
      expect(component.confirmed).toBe(true);
    });

    it('should call verifyEmail and handle error response', async () => {
      component.resetToken = 'sampleToken';
      spyOn(component.registerService, 'verifyEmail').and.callFake((token) => {
        return throwError('error occurred');
      });
      component.verifyEmail();
      expect(component.registerService.verifyEmail).toHaveBeenCalledWith('sampleToken');
      expect(component.confirmed).toBe(false);
      expect(component.loading).toBe(false);
    });
  });
});
