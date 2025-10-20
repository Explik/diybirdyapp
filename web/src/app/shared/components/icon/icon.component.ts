import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-icon',
  standalone: true,
  imports: [NgIf],
  templateUrl: './icon.component.html',
  styleUrls: ['./icon.component.css']
})
export class IconComponent implements OnChanges {
  @Input() icon: string = ''; // The mdi icon name, e.g., 'mdi-account'
  @Input() size: string = 'text-base'; // Tailwind size classes
  @Input() color: string = 'text-black'; // Tailwind color classes
  
  isKeyIcon = false;
  keyValue = '';
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['icon']) {
      this.processIcon();
    }
  }
  
  private processIcon(): void {
    // Check if the icon is a key icon (follows pattern key-X)
    const keyMatch = this.icon?.match(/^key-(.+)$/);
    if (keyMatch) {
      this.isKeyIcon = true;
      
      // Extract the value after 'key-'
      let value = keyMatch[1];

      // Ensure letters are always uppercase
      this.keyValue = value.toUpperCase();
    } else {
      this.isKeyIcon = false;
      this.keyValue = '';
    }
  }
}
