import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-instruction',
  standalone: true,
  imports: [],
  templateUrl: './instruction.component.html'
})
export class InstructionComponent {
  @Input() title: string = 'Translate the sentence';
  @Input() instruction: string = 'Select the correct translation below';
}
