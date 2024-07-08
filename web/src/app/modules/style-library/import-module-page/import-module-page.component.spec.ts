import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportModulePageComponent } from './import-module-page.component';

describe('ImportModulePageComponent', () => {
  let component: ImportModulePageComponent;
  let fixture: ComponentFixture<ImportModulePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportModulePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImportModulePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
