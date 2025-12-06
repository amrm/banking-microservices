import { bootstrapApplication } from '@angular/platform-browser';
import { enableProdMode } from '@angular/core';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

if (process.env['NODE_ENV'] === 'production') {
  enableProdMode();
}

bootstrapApplication(AppComponent, appConfig).catch(err => console.error(err));
