import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar-component/navbar-component';
import { ListGroupMenuComponent } from "./components/list-group-menu-component/list-group-menu-component";
import { UserServiceComponent } from './shared/services/user-service-component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, ListGroupMenuComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('routing-soft-ui');
    userService = inject(UserServiceComponent) 
  // Expose the user signal for template
  user = this.userService.user$;
  
  ngOnInit() {
    this.userService.initializeUserFromToken();
  }
  

}
