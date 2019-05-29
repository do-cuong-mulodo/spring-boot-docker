**Install Spring Boot (Jar) Gradle Docker**

1. Create Spring boot project
Go to https://start.spring.io/ 
select Gradle
click more option -> select jar
Group: com.mulodo
Artifact: spring-boot-docker
Name: spring-boot-docker
Description: Spring Boot with docker
package: com.mulodo.spring-boot-docker
=>GENERATE

2. Run "gradle init"

3. Add RESTful code into class SpringBootDockerApplication and dependency:
	compile("org.springframework.boot:spring-boot-starter-web")
into build.gradle to test.
add appilcation port, name into file application.properties

4. Remember to update dependency - Run "./gradlew build --refresh-dependencies"

5. Add bootJar with baseName and version into build.gradle

6. Run "gradle clean"

7. Run "gradle build"

8. Run "./gradlew build && java -jar build/libs/spring-boot-docker-0.1.0.jar"
(name must match information in build.gradle/bootJar) -> start app without docker

9. Create new file Dockerfile with the following content at the root directory:
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","hello.Application"]

10. Add this code at the beginning of the file build.gradle:
buildscript {
    dependencies {
        classpath('gradle.plugin.com.palantir.gradle.docker:gradle-docker:0.13.0')
    }
}

Add the following code into build.gradle:

group = 'springio'
apply plugin: 'com.palantir.docker'

task unpack(type: Copy) {
    dependsOn bootJar
    from(zipTree(tasks.bootJar.outputs.files.singleFile))
    into("build/dependency")
}
docker {
    name "${project.group}/${bootJar.baseName}"
    copySpec.from(tasks.unpack.outputs).into("dependency")
    buildArgs(['DEPENDENCY': "dependency"])
}

11. Run "./gradlew build docker"
to build docker with content in build.gradle

11. Run "docker run -p 8083:8083 -t springio/gs-spring-boot-docker"
to startup project with docker
(make sure the port is the same as the port in application.properties)

12. Check containers: Run "docker ps"

13. Delete container: "docker stop 81c723d22865" where 81c723d22865 is the id

Reference:
https://spring.io/guides/gs/spring-boot-docker/