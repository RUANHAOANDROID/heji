FROM java:8
#定义编译环境路径（该变量适用于idea编译环境,如果在有docker环境os下编译请注意路径）
LABEL version="1.0.0"
LABEL description="This is hejiserver"
LABEL org.opencontainers.image.authors="https://github.com/RUANHAOANDROID/heji"
ENV BUILD_PATH = /target
WORKDIR ${BUILD_PATH}
ADD /target/server-0.0.1-SNAPSHOT.jar hejiserver.jar
EXPOSE 8181/tcp
#EXPOSE 8181/udp
ENTRYPOINT ["java","-jar","hejiserver.jar"]