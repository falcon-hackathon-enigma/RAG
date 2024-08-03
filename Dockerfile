FROM maven:3.8.3-openjdk-17
ENV ai71_token=api71-api-cd7bc3cf-053b-44fd-a966-3e050e5f8183
WORKDIR /app
COPY . .
RUN mvn clean install
EXPOSE 8080
CMD ["java", "-jar", "/app/target/genai-llm-creditcards-analyze.jar"]
