import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSourcesManagement } from './data-sources-management';

describe('DataSourcesManagement', () => {
  let component: DataSourcesManagement;
  let fixture: ComponentFixture<DataSourcesManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataSourcesManagement]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DataSourcesManagement);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
