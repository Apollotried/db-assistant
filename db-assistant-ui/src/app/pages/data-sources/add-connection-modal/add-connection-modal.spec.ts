import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddConnectionModal } from './add-connection-modal';

describe('AddConnectionModal', () => {
  let component: AddConnectionModal;
  let fixture: ComponentFixture<AddConnectionModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddConnectionModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddConnectionModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
