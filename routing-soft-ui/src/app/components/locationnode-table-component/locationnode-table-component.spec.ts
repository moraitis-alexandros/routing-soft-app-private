import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationnodeTableComponent } from './locationnode-table-component';

describe('LocationnodeTableComponent', () => {
  let component: LocationnodeTableComponent;
  let fixture: ComponentFixture<LocationnodeTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationnodeTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LocationnodeTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
