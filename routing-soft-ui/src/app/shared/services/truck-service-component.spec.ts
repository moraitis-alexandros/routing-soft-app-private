import { TestBed } from '@angular/core/testing';

import { TruckServiceComponent } from './truck-service-component';

describe('TruckServiceComponent', () => {
  let service: TruckServiceComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TruckServiceComponent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
