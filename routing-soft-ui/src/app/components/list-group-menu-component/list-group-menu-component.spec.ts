import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListGroupMenuComponent } from './list-group-menu-component';

describe('ListGroupMenuComponent', () => {
  let component: ListGroupMenuComponent;
  let fixture: ComponentFixture<ListGroupMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListGroupMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListGroupMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
