FROM java:8
#定义编译环境路径（该变量适用于idea编译环境,如果在有docker环境os下编译请注意路径）
LABEL version="1.0.1"
LABEL description="This is hejiserver"
LABEL org.opencontainers.image.authors="https://github.com/RUANHAOANDROID/heji"
#定义容器工作环境目录
ENV WOKR_PATH /data/
WORKDIR ${WOKR_PATH}
ADD /target/server-0.0.1-SNAPSHOT.jar hejiserver.jar
EXPOSE 8181/tcp
#EXPOSE 8181/udp
# 挂载点
VOLUME /data
#RUN touch /heji
ENTRYPOINT ["java","-jar","hejiserver.jar"]
#CMD ["java","-jar","hejiserver.jar"]

##### 1 edit this dockerfile #####
##### 2 run options --net host #####
#--network bridge
#-v /mnt/disk1/appdata/heji/logs:/data/logs
#-v /mnt/disk1/appdata/heji/storage:/data/storage