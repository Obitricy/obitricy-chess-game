# Obitricy Chess - Render WebSocket Server

This repository contains the Java chess project with the Internet WebSocket server.

## Render
Create a **Web Service** from this repository using the included `Dockerfile` (runtime: Docker).
The included `render.yaml` can also be used as a Blueprint.

The server reads Render's `PORT` environment variable and binds to `0.0.0.0`.
Public clients must use `wss://` when connecting through Render.

## Local
`mvn clean package`
`java -cp target/chess-game-1.0.0.jar chess.multiplayer.ChessWebSocketServer`
