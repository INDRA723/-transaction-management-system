FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY src ./src
RUN wget -O mysql-connector.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
RUN mkdir -p out && find src -name "*.java" > sources.txt && javac -cp mysql-connector.jar -d out @sources.txt

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/out ./out
COPY --from=build /app/mysql-connector.jar ./mysql-connector.jar
COPY frontend ./frontend
EXPOSE 8080
ENTRYPOINT ["java", "-cp", "out:mysql-connector.jar", "com.tms.handler.AppServer"]
