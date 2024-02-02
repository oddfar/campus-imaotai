FROM nginx:stable-alpine
# author
MAINTAINER oddfar
# 复制html文件到路径
COPY dist /usr/share/nginx/html
# 复制conf文件到路径
COPY ./doc/docker/nginx/conf/nginx.conf /etc/nginx/nginx.conf
