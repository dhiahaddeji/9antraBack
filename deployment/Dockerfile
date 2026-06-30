FROM openjdk:11-bullseye
COPY . /code/

WORKDIR /code/9antraFormationBackV2/
RUN rm -rf target
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests --no-transfer-progress -B
RUN mv /code/9antraFormationBackV2/target/*.jar /code/
RUN rm -rf /code/9antraFormationBackV2/ /root/.m2 /root/.cache /tmp/* /var/tmp/*

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS="" \
    PHASE=""
EXPOSE 8094
CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /code/*.jar