FROM java:8
# 将本地文件夹挂载到当前容器
VOLUME /tmp
# 复制文件到容器
ADD leyou-registry-1.0.0-SNAPSHOT.jar /registry.jar
EXPOSE 10086
ENTRYPOINT ["java","-jar","/registry.jar"]