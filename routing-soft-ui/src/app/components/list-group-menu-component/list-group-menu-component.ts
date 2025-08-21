import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-list-group-menu-component',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './list-group-menu-component.html',
  styleUrl: './list-group-menu-component.css'
})
export class ListGroupMenuComponent {
   menu = [{text: 'My Plans', linkName: 'plan-list-component'},
    {text: 'My Trucks', linkName: 'truck-list-component'},
    {text: 'My Locations', linkName: 'location-list-component'}
  ]
}



