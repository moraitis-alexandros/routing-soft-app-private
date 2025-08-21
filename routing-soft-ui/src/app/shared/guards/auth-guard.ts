import { CanActivateFn,Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserServiceComponent } from '../services/user-service-component';

export const authGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserServiceComponent);
  const router = inject(Router);

  if (userService.user$()) {
    console.log("ENTERED")
    return true;
  }

  return router.navigate(['app-user-login-component']);
};
