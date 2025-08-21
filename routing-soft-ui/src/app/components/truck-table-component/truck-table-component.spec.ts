import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TruckTableComponent } from './truck-table-component';

describe('TruckTableComponent', () => {
  let component: TruckTableComponent;
  let fixture: ComponentFixture<TruckTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TruckTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TruckTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
