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
**Note**
Need to initialize gradle only one time if you've just cloneed or downloaded source code

3. Add RESTful code into class SpringBootDockerApplication and dependency:
	compile("org.springframework.boot:spring-boot-starter-web")
into build.gradle to test.
add appilcation port, name into file application.properties
**Note**
This step is just for testing

4. Remember to update dependency - Run "./gradlew build --refresh-dependencies"
**Note**
Every time you add/update dependencies in build.gradle, you need to refresh them for gradle to download into your local directory

5. Add bootJar with baseName and version into build.gradle

6. Run "gradle clean" -> "gradle build" or "gradle clean build" 
=> spring-boot-docker-0.1.0.jar generated in build/libs/
**Note**
gradle clean: will clean the folder build
gradle build: will build your source code into .class and jar ... and put these files into build folder
gradle clean build: will clean, then build

7. Run "./gradlew build && java -jar build/libs/spring-boot-docker-0.1.0.jar"
(name must match information in build.gradle/bootJar) -> start app without docker

**Basic Docker File (Run Docker with Jar)**
8. Create new file Dockerfile with the following content at the root directory:
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} spring-boot-docker-0.1.0.jar
ENTRYPOINT ["java","-jar","/spring-boot-docker-0.1.0.jar"]

Of course, Once you have chosen a build system, you don’t need the ARG - you can just hard code the jar location:
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY build/libs/*.jar spring-boot-docker-0.1.0.jar
ENTRYPOINT ["java","-jar","spring-boot-docker-0.1.0.jar"]

9. Then we can simply build an image with: "docker build -t spring-boot-docker-0.1.0 ."
(Note that '.' at the end of the command is mandatory)

10. Run "docker run -p 8083:8083 spring-boot-docker-0.1.0"
create instance of image called container
(make sure the port is the same as the port in application.properties)

**Better Dockerfile (Run Docker with Source Code)**
8. Create new file Dockerfile with the following content at the root directory:
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.mulodo.springbootdocker.SpringBootDockerApplication"]
**Note**
There are now 3 layers, with all the application resources in the later 2 layers. If the application dependencies don’t change, then the first layer (from BOOT-INF/lib) will not change, so the build will be faster, and so will the startup of the container at runtime as long as the base layers are already cached.
**Note**
We used a hard-coded main application class com.mulodo.springbootdocker.SpringBootDockerApplication. This will probably be different for your application. You could parameterize it with another ARG if you wanted. You could also copy the Spring Boot fat JarLauncher into the image and use it to run the app - it would work and you wouldn’t need to specify the main class, but it would be a bit slower on startup.

9. Build a Docker Image with Gradle
Add this code at the beginning of the file build.gradle:
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
**Note - The configuration specifies 4 things:**
- a task to unpack the fat jar file
- the image name (or tag) is set up from the jar file properties, which will end up here as springio/spring-boot-docker
- the location of the unpacked jarfile, which we could have hard coded in the Dockerfile
- a build argument for docker pointing to the jar file

10. Run "./gradlew build docker"
to build docker image with content in build.gradle
**Note**
After this build the dependencies is downloaded into folder build/dependency
and dependencies are also copied into folder build/docker/dependency
(this is effected by build.gradle)

11. Run "docker run -p 8083:8083 -t springio/spring-boot-docker"
create instance of image called container
(make sure the port is the same as the port in application.properties, 8083 should be the port and springio/spring-boot-docker should be [group]/[project_name])

12. Check containers: Run "docker ps"

13. Delete container: "docker stop 81c723d22865" where 81c723d22865 is the id

**After Installed**
After installed, for the next time running, Just need to execute 2 commands below:
1. gradlew build docker
2. docker run -p 8083:8083 -t springio/spring-boot-docker

Reference:
https://spring.io/guides/gs/spring-boot-docker/
https://spring.io/blog/2018/11/08/spring-boot-in-a-container