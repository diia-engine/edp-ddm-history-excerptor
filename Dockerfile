FROM nexus-docker-registry.apps.cicd2.mdtu-ddm.projects.epam.com/openjdk:11.0.16-jre-slim
WORKDIR /app
COPY target/history-excerptor-*.jar app.jar