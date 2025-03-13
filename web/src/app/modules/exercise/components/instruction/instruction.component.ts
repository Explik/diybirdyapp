import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-instruction',
  standalone: true,
  imports: [],
  templateUrl: './instruction.component.html'
})
export class InstructionComponent {
  @Input() title: string = '';
  @Input() instruction: string = '';
}
