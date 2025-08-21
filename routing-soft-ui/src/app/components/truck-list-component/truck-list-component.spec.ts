import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TruckListComponent } from './truck-list-component';

describe('TruckListComponent', () => {
  let component: TruckListComponent;
  let fixture: ComponentFixture<TruckListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TruckListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TruckListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
