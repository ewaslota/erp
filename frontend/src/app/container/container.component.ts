import { Component } from '@angular/core';
import { CategoriesComponent } from './categories/categories.component';
import { DocumentsComponent } from './documents/documents.component';
import { RouterOutlet } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { TemplatesComponent } from './templates/templates.component';
import { SettingsComponent } from './settings/settings.component';

@Component({
  selector: 'app-container',
  standalone: true,
  imports: [
    HomeComponent,
    DocumentsComponent,
    CategoriesComponent,
    TemplatesComponent,
    SettingsComponent,
    RouterOutlet,
  ],
  templateUrl: './container.component.html',
  styleUrl: './container.component.css',
})
export class ContainerComponent {}
