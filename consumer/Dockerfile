# Estágio de compilação
FROM maven:3-amazoncorretto-21-alpine AS builder
WORKDIR /build
# Copiar arquivos pom.xml e src
COPY pom.xml .
COPY src ./src
# Compilar o projeto e criar o JAR
RUN mvn clean package -DskipTests

# Estágio final
FROM amazoncorretto:21-alpine
RUN mkdir /app
WORKDIR /app
# Copiar o JAR do estágio de compilação
COPY --from=builder /build/target/*.jar /app/app.jar
# Executar o JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]