FROM java:8
# 将本地文件夹挂载到当前容器
VOLUME /tmp
# 复制文件到容器
ADD leyou-item-service-1.0.0-SNAPSHOT.jar /item-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/item-service.jar"]