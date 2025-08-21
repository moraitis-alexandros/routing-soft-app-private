import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanReadonlyView } from './plan-readonly-view';

describe('PlanReadonlyView', () => {
  let component: PlanReadonlyView;
  let fixture: ComponentFixture<PlanReadonlyView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanReadonlyView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlanReadonlyView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
