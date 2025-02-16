# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /build

COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

# Set Maven memory limits to prevent crashes
ENV MAVEN_OPTS="-Xms512m -Xmx1024m"

# Download dependencies using Docker cache
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 ./mvnw clean install -DskipTests

WORKDIR /build

COPY ./src src/

# Package application
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

# Extract layers for Spring Boot optimization
RUN java -Djarmode=layertools -jar target/app.jar extract

################################################################################

FROM eclipse-temurin:21-jre-jammy AS final

# Create a non-privileged user
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

WORKDIR /app

# Copy extracted Spring Boot layers
COPY --from=build /build/target/extracted/dependencies/ ./
COPY --from=build /build/target/extracted/spring-boot-loader/ ./
COPY --from=build /build/target/extracted/snapshot-dependencies/ ./
COPY --from=build /build/target/extracted/application/ ./

ENTRYPOINT [ "java", "org.springframework.boot.loader.launch.JarLauncher" ]