import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';


@Component({
  selector: 'app-icon',
  standalone: true,
  imports: [],
  templateUrl: './icon.component.html',
  styleUrls: ['./icon.component.css']
})
export class IconComponent implements OnChanges {
  @Input() icon: string = ''; // The mdi icon name, e.g., 'mdi-account', 'key-A', 'key-left', 'mark-correct'
  @Input() size: string = 'text-base'; // Tailwind size classes
  @Input() color: string = 'text-black'; // Tailwind color classes
  
  isKeyIcon = false;
  isMarkIcon = false;
  keyValue = '';
  markType = ''; // 'correct' or 'incorrect'
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['icon']) {
      this.processIcon();
    }
  }
  
  private processIcon(): void {
    // Reset states
    this.isKeyIcon = false;
    this.isMarkIcon = false;
    this.keyValue = '';
    this.markType = '';
    
    if (!this.icon) {
      return;
    }
    
    // Check if the icon is a key icon (follows pattern key-X)
    const keyMatch = this.icon.match(/^key-(.+)$/);
    if (keyMatch) {
      this.isKeyIcon = true;
      
      // Extract the value after 'key-'
      const value = keyMatch[1];
      this.keyValue = this.formatKeyValue(value);
      return;
    }
    
    // Check if the icon is a mark icon (mark-correct or mark-incorrect)
    const markMatch = this.icon.match(/^mark-(correct|incorrect)$/);
    if (markMatch) {
      this.isMarkIcon = true;
      this.markType = markMatch[1];
      return;
    }
  }

  private formatKeyValue(value: string): string {
    switch (value.toLowerCase()) {
      case 'left':
        return '\u2190';
      case 'right':
        return '\u2192';
      case 'up':
        return '\u2191';
      case 'down':
        return '\u2193';
      default:
        return value.toUpperCase();
    }
  }
}
