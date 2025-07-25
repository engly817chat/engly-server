FROM container-registry.oracle.com/graalvm/native-image:24 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN microdnf install maven && mvn clean install -DskipTests

FROM container-registry.oracle.com/graalvm/jdk:24
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]