#FROM java:8
FROM openjdk:8

MAINTAINER oddfar

# 创建目录
RUN mkdir -p /home/campus/conf

WORKDIR /home/campus

ENV SERVER_PORT=8160

EXPOSE ${SERVER_PORT}

ADD ./target/campus-modular.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-jar", "app.jar"]
