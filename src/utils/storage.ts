import { Player, GameSession } from '../types';

// Local storage keys
const PLAYERS_KEY = 'arcade_game_players';
const SESSIONS_KEY = 'arcade_game_sessions';
const CURRENT_PLAYER_KEY = 'arcade_game_current_player';

// Initialize storage
const initializeStorage = () => {
  if (!localStorage.getItem(PLAYERS_KEY)) {
    localStorage.setItem(PLAYERS_KEY, JSON.stringify([]));
  }
  if (!localStorage.getItem(SESSIONS_KEY)) {
    localStorage.setItem(SESSIONS_KEY, JSON.stringify([]));
  }
};

// Player methods
export const getPlayers = (): Player[] => {
  initializeStorage();
  return JSON.parse(localStorage.getItem(PLAYERS_KEY) || '[]');
};

export const getPlayer = (id: string): Player | undefined => {
  const players = getPlayers();
  return players.find(player => player.id === id);
};

export const getPlayerByUsername = (username: string): Player | undefined => {
  const players = getPlayers();
  return players.find(player => player.username === username);
};

export const savePlayer = (player: Player): Player => {
  const players = getPlayers();
  const existingPlayerIndex = players.findIndex(p => p.id === player.id);
  
  if (existingPlayerIndex >= 0) {
    players[existingPlayerIndex] = player;
  } else {
    players.push(player);
  }
  
  localStorage.setItem(PLAYERS_KEY, JSON.stringify(players));
  return player;
};

export const setCurrentPlayer = (playerId: string) => {
  localStorage.setItem(CURRENT_PLAYER_KEY, playerId);
};

export const getCurrentPlayer = (): Player | null => {
  const playerId = localStorage.getItem(CURRENT_PLAYER_KEY);
  if (!playerId) return null;
  
  return getPlayer(playerId) || null;
};

export const logoutCurrentPlayer = () => {
  localStorage.removeItem(CURRENT_PLAYER_KEY);
};

// Game session methods
export const getSessions = (): GameSession[] => {
  initializeStorage();
  return JSON.parse(localStorage.getItem(SESSIONS_KEY) || '[]');
};

export const getPlayerSessions = (playerId: string): GameSession[] => {
  const sessions = getSessions();
  return sessions.filter(session => session.playerId === playerId);
};

export const saveSession = (session: GameSession): GameSession => {
  const sessions = getSessions();
  const existingSessionIndex = sessions.findIndex(s => s.id === session.id);
  
  if (existingSessionIndex >= 0) {
    sessions[existingSessionIndex] = session;
  } else {
    sessions.push(session);
  }
  
  localStorage.setItem(SESSIONS_KEY, JSON.stringify(sessions));
  return session;
};

// Get high scores
export const getHighScores = (difficulty: 'easy' | 'medium' | 'hard', limit = 10): Array<{ player: Player; score: number }> => {
  const sessions = getSessions();
  const players = getPlayers();
  
  return sessions
    .filter(session => session.difficulty === difficulty && session.completed)
    .sort((a, b) => b.score - a.score)
    .slice(0, limit)
    .map(session => ({
      player: players.find(p => p.id === session.playerId) as Player,
      score: session.score
    }));
};