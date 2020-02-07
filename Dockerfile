FROM openjdk:11

COPY . /app
RUN cd app && ./gradlew bootJar
RUN cp app/build/libs/ground-control-server-0.0.1.jar .

# For John
EXPOSE 8080

ENTRYPOINT java -jar ground-control-server-0.0.1.jar