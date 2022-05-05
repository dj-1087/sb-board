# [SB-Board]
> Spring Boot 게시판

## Environment
* Java 11
* SprinBoot 2.6.7
* Mysql
* Spring-Data-JPA


## Configuration
* application.yml
> application-sample.json을 복사하여 application-dev.json, application-test.json, application-prod.json 생성
* Intellij - Tyhmeleaf live reload
> 1. application.yml 설정: `spring.thymeleaf.cache=false` *이미 설정되어 있음
> 2. Intellij - **Run > Edit Configurations**  
> On 'Update': `Update classes and resources`   
> On frame deactivation: `Update classes and resources`

* profile (Intellij - **Run > Edit Configurations**)
> * 개발 환경   
> Active Profile: `dev`
> * 테스트 환경   
> Edit configuration templates... > JUnit > Environment variables: `SPRING_PROFILES_ACTIVE=test`

## Database Setting
```mysql
create database sbb;
create database `sbb-test`;
```

## Build
* Intellij: 우측 상단 > Maven
> 1. Profiles > `prod` 선택
> 2. sb-board > Lifecycle > install 클릭
* Mac terminal
```shell
./mvnw -Dspring.profiles.active=dev install
```

## Deploy
```shell
nohup java -jar -Dspring.profiles.active=prod ./target/sb-board-0.0.1-SNAPSHOT.jar &
```
