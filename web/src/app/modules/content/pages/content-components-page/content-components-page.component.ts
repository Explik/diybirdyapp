import { Component } from '@angular/core';
import { AudioInputComponent } from "../../components/audio-input/audio-input.component";
import { ImageInputComponent } from "../../components/image-input/image-input.component";

@Component({
  selector: 'app-content-components-page',
  imports: [AudioInputComponent, ImageInputComponent],
  templateUrl: './content-components-page.component.html',
  styleUrl: './content-components-page.component.css'
})
export class ContentComponentsPageComponent {

}
