ARG IMAGE_TAG_MAVEN=maven:3.9.9-eclipse-temurin-21-alpine
ARG IMAGE_TAG_JRE=eclipse-temurin:21-jre-alpine
#Build stage
FROM $IMAGE_TAG_MAVEN AS build
WORKDIR /build/
COPY  pom.xml .
COPY src /build/src/
RUN mvn package -DskipTests clean package

#EXPOSE 8090

#Run stage
FROM $IMAGE_TAG_JRE
LABEL authors="onforus"
RUN mkdir -p /opt/taskservice/some
COPY --from=build /build/target/*.jar /opt/taskservice/app.jar
ENTRYPOINT ["java", "-jar", "/opt/taskservice/app.jar"]