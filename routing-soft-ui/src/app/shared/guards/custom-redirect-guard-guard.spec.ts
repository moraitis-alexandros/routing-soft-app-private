import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { customRedirectGuardGuard } from './custom-redirect-guard-guard';

describe('customRedirectGuardGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => customRedirectGuardGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
