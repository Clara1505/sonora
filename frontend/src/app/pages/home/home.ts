import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {
  isLoggedIn = false;

  features = [
    { icon: 'library_music', title: 'Catalogue Musical', description: 'Parcourez des milliers de titres, albums et artistes.' },
    { icon: 'queue_music', title: 'Playlists', description: 'Créez vos playlists manuelles ou laissez Sonora en générer une pour vous.' },
    { icon: 'auto_awesome', title: 'Smart Playlist', description: 'Générez automatiquement une playlist selon votre mood et vos goûts.' },
    { icon: 'favorite', title: 'Favoris', description: 'Retrouvez vos titres préférés en un clic.' },
  ];

  constructor(private authService: AuthService) {
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
    });
  }
}