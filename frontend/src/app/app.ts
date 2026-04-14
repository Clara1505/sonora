import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Navbar } from './layout/navbar/navbar';
import { PlayerBar } from './layout/player-bar/player-bar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, PlayerBar, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  title = 'frontend';

  constructor(private router: Router) {}

  isAuthPage(): boolean {
    const url = this.router.url;
    return url.includes('/login') || url.includes('/register');
  }
}