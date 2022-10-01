FROM openjdk:11-jre-slim

# copy application JAR
COPY target/rss-tracker-bot-*.jar /app/rss-tracker-bot.jar

# Default environment variables
ENV TELEGRAM_BOT_TOKEN "YOUR_TELEGRAM_BOT_TOKEN"
ENV MONGODB_HOST "localhost"
ENV MONGODB_PORT "27017"
ENV MONGODB_USERNAME "admin"
ENV MONGODB_PASSWORD "admin"
ENV MONGODB_DATABASE "rss_tracker_db"
ENV ADMIN_SERVER_USERNAME "admin"
ENV ADMIN_SERVER_PASSWORD "admin"
ENV ADMIN_SERVER_URL "http://admin-server:9090"

# Application entrypoint
CMD ["java", "-jar", "/app/rss-tracker-bot.jar"]