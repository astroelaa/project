export interface Player {
  id: string;
  username: string;
  password: string; // In a real app, this would be hashed
  createdAt: string;
}

export interface GameSession {
  id: string;
  playerId: string;
  difficulty: 'easy' | 'medium' | 'hard';
  score: number;
  duration: number; // in seconds
  completed: boolean;
  createdAt: string;
}

export interface GameState {
  isActive: boolean;
  difficulty: 'easy' | 'medium' | 'hard';
  score: number;
  timeElapsed: number;
  targetPosition: { x: number; y: number };
  targetSize: number;
  targetSpeed: number;
  targetsClicked: number;
  totalTargets: number;
}