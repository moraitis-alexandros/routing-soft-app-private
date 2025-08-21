import { TestBed } from '@angular/core/testing';

import { PlanServiceComponent } from './plan-service-component';

describe('PlanServiceComponent', () => {
  let service: PlanServiceComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlanServiceComponent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
