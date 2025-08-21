import { Injectable, inject } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { UserServiceComponent } from '../services/user-service-component';

@Injectable({
  providedIn: 'root'
})
export class GuestGuard implements CanActivate {
  private userService = inject(UserServiceComponent);
  private router = inject(Router);

  canActivate(): boolean {
    if (this.userService.user$()) {
      // If user is logged in → redirect to plan list
      this.router.navigate(['/plan-list-component']);
      return false;
    }
    return true; // Not logged in → allow access
  }
}
