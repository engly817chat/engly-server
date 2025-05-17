FROM container-registry.oracle.com/graalvm/native-image:21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN microdnf install maven && mvn clean install -DskipTests

FROM container-registry.oracle.com/graalvm/jdk:21
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]