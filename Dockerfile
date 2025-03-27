# Builder stage
# Stage 1: Build with Maven
FROM maven:3.8.6-openjdk-17 AS builder
WORKDIR /application
COPY pom.xml .
COPY src ./src
# Сборка проекта с помощью Maven
RUN mvn clean package -DskipTests

# Извлечение слоев из JAR-файла с помощью layertools
ARG JAR_FILE=target/*.jar
RUN java -Djarmode=layertools -jar ${JAR_FILE} extract

# Final stage
FROM openjdk:17-jdk-alpine
WORKDIR /application

# Копирование извлечённых слоёв из builder
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

# Точка входа для запуска приложения
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

# Порт, который будет использоваться приложением
EXPOSE 8080