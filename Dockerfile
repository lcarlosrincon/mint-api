FROM maven:3.8.3-amazoncorretto-17 AS build
COPY src /home/api/src
COPY pom.xml /home/api
RUN mvn -f /home/api/pom.xml clean test package

# the base image
FROM amazoncorretto:17 as run
# the JAR file path
ARG JAR_FILE=target/*.jar
# Copy the JAR file from the build context into the Docker image
COPY --from=build /home/api/target/*.jar api.jar
CMD apt-get update -y
# Set the default command to run the Java application
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/api.jar"]