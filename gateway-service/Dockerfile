FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only pom.xml first (better caching)
COPY pom.xml .

# Force dependency re-download
RUN mvn dependency:go-offline -U

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -U

# Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
