FROM node:16 AS build
WORKDIR /app
COPY ./vue_campus_admin .
RUN npm i && npm run build:prod

FROM nginx:1.23.4
LABEL MAINTAINER="K8sCat <k8scat@gmail.com>"
ENV TZ=Asia/Shanghai
RUN mkdir -p /app
COPY --from=build /app/dist/ /app/
