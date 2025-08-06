# Use a imagem oficial do Java 21
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copie o código fonte para dentro do container
COPY . .

# Execute o build do Maven para gerar o jar
RUN ./mvnw clean package -DskipTests

# Copie o jar gerado para o local correto
RUN mv target/spring-0.0.1-SNAPSHOT.jar app.jar

# Exponha a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar o jar
ENTRYPOINT ["java", "-jar", "app.jar"]
