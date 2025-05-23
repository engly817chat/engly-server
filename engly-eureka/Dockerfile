FROM container-registry.oracle.com/graalvm/native-image:24 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY ../pom.xml ../pom.xml
RUN microdnf install maven && mvn clean package -DskipTests

FROM container-registry.oracle.com/graalvm/jdk:24
WORKDIR /app
COPY --from=build /app/target/*.jar ./eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "engly-eureka.jar"]