import { TestBed } from '@angular/core/testing';

import { UserServiceComponent } from './user-service-component';

describe('UserServiceComponent', () => {
  let service: UserServiceComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserServiceComponent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
