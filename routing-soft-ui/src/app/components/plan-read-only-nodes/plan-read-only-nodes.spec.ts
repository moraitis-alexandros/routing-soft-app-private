import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanReadOnlyNodes } from './plan-read-only-nodes';

describe('PlanReadOnlyNodes', () => {
  let component: PlanReadOnlyNodes;
  let fixture: ComponentFixture<PlanReadOnlyNodes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanReadOnlyNodes]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlanReadOnlyNodes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
