import { TestBed } from '@angular/core/testing';

import { LocationNodeService } from './location-node-service';

describe('LocationNodeService', () => {
  let service: LocationNodeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocationNodeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
