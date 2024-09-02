import { Component, Input } from '@angular/core';
import { TextContent } from '../../models/content.interface';

@Component({
  selector: 'app-text-quote',
  standalone: true,
  imports: [],
  templateUrl: './text-quote.component.html',
  styleUrl: './text-quote.component.css'
})
export class TextQuoteComponent {
  @Input({ required: true }) content!: TextContent
}
