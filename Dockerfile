#####################
# Build Application #
#####################
FROM openjdk:11-jdk-slim AS builder
# 앱 소스 추가
COPY . .
# 빌드
RUN chmod +x ./gradlew
RUN ./gradlew build

###############
# Dockerizing #
###############
FROM openjdk:11-jdk-slim  
# 복사
COPY --from=builder build/libs/*.jar app.jar
# 실행
EXPOSE 55400 55401 55402 55403
ENTRYPOINT ["java","-jar","app.jar"]
