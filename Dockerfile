# 기존 FROM openjdk:17-jdk-slim 주석 처리 또는 삭제 후 아래 줄로 교체
FROM amazoncorretto:17-alpine-jdk

# 2. 컨테이너 내부의 작업 디렉토리를 /app 으로 설정합니다.
WORKDIR /app

# 3. 빌드 시 생성된 jar 파일을 컨테이너 내부로 복사합니다.
COPY build/libs/*.jar app.jar

# 4. 컨테이너가 켜질 때 스프링 부트를 실행하는 명령어입니다.
ENTRYPOINT ["java", "-jar", "app.jar"]