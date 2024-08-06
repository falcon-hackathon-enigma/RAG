FROM maven:3.8.3-openjdk-17
ENV ai71_token=api71-api-ab177020-538d-497f-960f-bceb3ad98cf3
WORKDIR /app
COPY . .
RUN mvn clean install
EXPOSE 8888
CMD ["java", "-jar", "/app/target/genai-llm-creditcards-analyze.jar"]
