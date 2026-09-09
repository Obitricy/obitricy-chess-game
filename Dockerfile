FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/chess-game-1.0.0.jar app.jar
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"
EXPOSE 10000
CMD ["java", "-cp", "app.jar", "chess.multiplayer.ChessWebSocketServer"]
