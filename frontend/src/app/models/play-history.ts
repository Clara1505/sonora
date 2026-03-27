import { Track } from './track';

export interface PlayHistory {
  id: number;
  track: Track;
  playedAt: string;
  durationListened?: number;
}