import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestConnectionModal } from './test-connection-modal';

describe('TestConnectionModal', () => {
  let component: TestConnectionModal;
  let fixture: ComponentFixture<TestConnectionModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestConnectionModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TestConnectionModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
