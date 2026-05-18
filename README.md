# Poker Hand Analyzer

Upload your online poker hand-history files and get back a read on how you play:
classic player stats (VPIP / PFR / aggression), a player-type classification, and a
list of likely leaks in your game with the specific hands that triggered them.

> Personal project. Hand histories are parsed locally by the backend and never stored.

## Why I built this

I have experience in software development but all my work was done in my professional roles. I made this as a passion project because I really enjoy playing poker with friends and the math behind it. The game mixed with psychology, statistics and bluffing really excites me.

My main skills are in Java and React so I am creating this to demonstrate my ability. I am using Claude to build the boilerplate and I am going to code the hand analysis myself with the help of LLMs.

## Architecture

```
poker-hand-analyzer/
├── backend/    Spring Boot (Java 17) — parsing + analysis engine, REST API
└── frontend/   React + TypeScript (Vite) — upload UI + dashboards
```

- **Parser** (`com.pokeranalyzer.parser`) turns raw PokerStars text into typed
  `HandHistory` objects.
- **Analysis** (`com.pokeranalyzer.analysis`) aggregates per-player stats,
  classifies player types, and flags leaks.
- **Web** (`com.pokeranalyzer.web`) exposes `POST /api/analyze`.
- The React app uploads a file, calls the API, and renders the results.

## Tech stack

| Layer    | Choice                                  |
|----------|-----------------------------------------|
| Backend  | Java 17, Spring Boot, JUnit 5           |
| Frontend | React, TypeScript, Vite                 |
| Build    | Maven (wrapper included), npm           |

## Running it

Backend:

```bash
cd backend
./mvnw spring-boot:run        # http://localhost:8080
./mvnw test                   # run the test suite
```

Frontend:

```bash
cd frontend
npm install
npm run dev                   # http://localhost:5173
```

## Status / roadmap

- [ ] PokerStars hand-history parser
- [ ] Stats engine (VPIP, PFR, aggression factor, 3-bet%)
- [ ] Player-type classification
- [ ] Leak detection with hand references
- [ ] REST API
- [ ] React dashboard

## Input format

Currently targets the PokerStars No-Limit Hold'em cash-game text format.
A sample is in `backend/src/test/resources/cash-6max.txt`.
