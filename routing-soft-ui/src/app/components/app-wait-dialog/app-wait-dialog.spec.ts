import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppWaitDialog } from './app-wait-dialog';

describe('AppWaitDialog', () => {
  let component: AppWaitDialog;
  let fixture: ComponentFixture<AppWaitDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppWaitDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppWaitDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
