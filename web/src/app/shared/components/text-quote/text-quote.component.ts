import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-text-quote',
  standalone: true,
  imports: [],
  templateUrl: './text-quote.component.html',
  styleUrl: './text-quote.component.css'
})
export class TextQuoteComponent {
  @Input() text: string = "" 
}
