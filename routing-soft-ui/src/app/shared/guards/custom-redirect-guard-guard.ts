import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserServiceComponent } from '../services/user-service-component';

export const RedirectGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserServiceComponent);
  const router = inject(Router);

  if (userService.user$()) {
    // Logged in → go to plan list
    return router.navigate(['/plan-list-component']);
  } else {
    // Not logged in → go to login
    return router.navigate(['/app-user-login-component']);
  }
};
