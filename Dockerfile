FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/history-excerptor-*.jar app.jar