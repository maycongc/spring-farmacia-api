# Use a imagem oficial do Java 21
FROM eclipse-temurin:21-jdk

# Copie o jar gerado para dentro do container
COPY target/spring-0.0.1-SNAPSHOT.jar app.jar

# Exponha a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar o jar
ENTRYPOINT ["java", "-jar", "app.jar"]
