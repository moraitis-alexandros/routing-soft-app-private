import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationnodeFormComponent } from './locationnode-form-component';

describe('LocationnodeFormComponent', () => {
  let component: LocationnodeFormComponent;
  let fixture: ComponentFixture<LocationnodeFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationnodeFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LocationnodeFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
