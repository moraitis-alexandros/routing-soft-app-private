import { Routes } from '@angular/router';
import { PlanListComponent } from './components/plan-list-component/plan-list-component';
import { TruckListComponent } from './components/truck-list-component/truck-list-component';
import { LocationListComponent } from './components/location-list-component/location-list-component';
import { UserRegistrationComponent } from './components/user-registration-component/user-registration-component';
import { UserLoginComponent } from './components/user-login-component/user-login-component';
import { authGuard } from './shared/guards/auth-guard';
import { GuestGuard } from './shared/guards/redirect-guard-guard';
import { RedirectGuard } from './shared/guards/custom-redirect-guard-guard';

export const routes: Routes = [
  { path: 'plan-list-component', component: PlanListComponent, canActivate: [authGuard] },
  { path: 'truck-list-component', component: TruckListComponent, canActivate: [authGuard] },
  { path: 'location-list-component', component: LocationListComponent, canActivate: [authGuard] },
  { path: 'app-user-registration-component', component: UserRegistrationComponent, canActivate: [GuestGuard] },
  { path: 'app-user-login-component', component: UserLoginComponent, canActivate: [GuestGuard] },
  { path: '', redirectTo: 'app-user-login-component', pathMatch: 'full' },
  { path: '**', redirectTo: 'plan-list-component', pathMatch: 'full' }
];




