FROM eclipse-temurin:17-jdk-bullseye
COPY . /code/

WORKDIR /code/
RUN rm -rf target
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests --no-transfer-progress -B

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS="" \
    PHASE=""
EXPOSE 8094
CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /code/target/*.jar
