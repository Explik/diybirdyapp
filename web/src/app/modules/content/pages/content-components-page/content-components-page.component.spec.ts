import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentComponentsPageComponent } from './content-components-page.component';

describe('ContentComponentsPageComponent', () => {
  let component: ContentComponentsPageComponent;
  let fixture: ComponentFixture<ContentComponentsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentComponentsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContentComponentsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
