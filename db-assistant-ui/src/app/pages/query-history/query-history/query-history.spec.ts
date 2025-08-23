import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QueryHistory } from './query-history';

describe('QueryHistory', () => {
  let component: QueryHistory;
  let fixture: ComponentFixture<QueryHistory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QueryHistory]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QueryHistory);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
