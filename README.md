# Spring Boot Application calling RESTful API from Github, SonarQube and Redmine for Viewers

### Install
+ Install java, gradle; skip this step if you've aready done

- gradle build
- cd build/libs
- java -jar spring-boot-docker-0.1.0.jar

### Docker
- ./gradlew build docker
- docker run -p 8083:8083 -t springio/spring-boot-docker

### Check RESTful data:
- Github: /api/git/repos/
- Sonar: /api/sonar/projects/
- Redmine: /api/redmine/projects/

### Note:
- You may need to change the configuration in application.properties

### References:
- https://spring.io/guides/gs/spring-boot-docker/ https://spring.io/blog/2018/11/08/spring-boot-in-a-container