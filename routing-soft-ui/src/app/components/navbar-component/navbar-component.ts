import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { UserServiceComponent } from '../../shared/services/user-service-component';

@Component({
  selector: 'app-navbar-component',
  standalone: true, // Required if you are using `imports` in component
  imports: [MatIconModule, RouterLink],
  templateUrl: './navbar-component.html',
  styleUrls: ['./navbar-component.css'] // fixed typo
})
export class NavbarComponent {
  // Inject the service
  userService = inject(UserServiceComponent);
  user = this.userService.user$;

  // Call logout in the service
  logout() {
    console.log("logout");
    this.userService.logout();
  }
}
