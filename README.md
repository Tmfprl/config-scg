# Web Management Authentication Service

**Spring Boot 기반 사용자 인증 서비스**

---

## 프로젝트 개요
- Spring Boot와 PostgreSQL을 기반으로 한 사용자 인증 및 JWT 기반 토큰 관리 서비스
- 마이크로서비스 환경(Eureka) 지원
- JWT 토큰 만료 및 갱신 기능 포함

---

## 주요 기능
- 사용자 인증 및 권한 관리
- JWT 토큰 생성/검증, 만료 및 갱신
- PostgreSQL 연동 사용자 데이터 관리
- Eureka 서버 등록을 통한 마이크로서비스 환경 지원

---

## 기술 스택
- **Backend:** Java, Spring Boot, Spring Data JPA
- **Database:** PostgreSQL
- **Cloud / Infra:** Eureka (Service Discovery)
- **Security:** JWT

---

## 환경 설정 (application.yml)
```yaml
server:
  port: 8083

token:
  expiration_time: 7200000
  refresh_time: 86400000
  secret: <YOUR_SECRET_KEY>

spring:
  application:
    name: web_mng_authentication
  datasource:
    hikari:
      auto-commit: false
      connection-test-query: select 1
      maximum-pool-size: 10
      pool-name: MyHikariCP
      driver-class-name: org.postgresql.Driver
      jdbc-url: jdbc:postgresql://[HOST]:[PORT]/[DB_NAME]
      username: [USERNAME]
      password: [PASSWORD]
  jpa:
    show_sql: true
    hibernate:
      ddl-auto: none

logging:
  level:
    org.hibernate.orm.jdbc.bind: trace

eureka:
  client:
    register-with-eureka: true
    disable-delta: true
    service-url:
      defaultZone: http://localhost:8761/eureka
````

---

## 설치 및 실행

1. JDK 17 이상 설치
2. PostgreSQL 설정 후 DB 연결
3. Maven 또는 Gradle 빌드
4. SpringApplication 실행 (`./mvnw spring-boot:run` 또는 IDE 실행)

---

## 구조 

```
src/main/java/org/example/web_mng_authentication
├─ config
├─ domain
├─ exception
├─ jwt
└─ user
```

| 패키지         | 역할                         |
| ----------- | -------------------------- |
| `config`    | Spring 설정, DB 설정, JWT 설정 등 |
| `domain`    | 엔티티 정의 (DB 테이블 매핑)         |
| `exception` | 커스텀 예외 처리                  |
| `jwt`       | JWT 토큰 생성, 검증 로직           |
| `user`      | 사용자 관련 서비스, DTO, 컨트롤러      |


---

## 사용 방법

* 사용자 등록, 로그인, 토큰 발급 테스트 가능
* Eureka 등록 여부 확인 후 마이크로서비스 환경에서 통신 가능

---

## 주의사항

* JWT Secret Key는 외부에 노출되지 않도록 관리
* PostgreSQL 접속 정보는 환경별로 맞게 수정 필요

---

## 라이선스

MIT License
