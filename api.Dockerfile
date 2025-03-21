FROM maven:3.9.6-amazoncorretto-8-debian AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:8
LABEL MAINTAINER="K8sCat <k8scat@gmail.com>"
ENV TZ=Asia/Shanghai
WORKDIR /app
EXPOSE 8160
COPY --from=build /app/campus-modular/target/campus-modular.jar ./app.jar
ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dspring.config.additional-location=/app/application-prod.yml", \
            "-Dserver.port=8160", \
            "-jar", "/app/app.jar", \
            "--spring.profiles.active=prod"]
