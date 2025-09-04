# 소프트웨어 라이선스 키 관리 시스템

## 📋 프로젝트 개요

이 프로젝트는 소프트웨어 라이선스를 동적으로 생성, 발급하고 유효성을 검증하는 기능을 제공하는 백엔드 서버 및 웹 클라이언트입니다.

Spring Boot와 Docker를 기반으로 구축되었으며, 비트마스크를 활용하여 라이선스에 포함될 속성을 유연하게 제어할 수 있습니다. 발급된 라이선스 키는 AES-GCM 대칭키 알고리즘으로 암호화되어 안전하게 관리됩니다.

## ✨ 주요 기능

* **동적 라이선스 생성**: `Core Count`, `MAC 주소` 등 다양한 하드웨어 정보를 선택적으로 포함하여 라이선스를 생성합니다.
* **비트마스크 제어**: 웹 UI의 체크박스를 통해 라이선스에 포함될 속성을 직관적으로 제어하고, 해당 조합을 비트마스크 값으로 자동 계산합니다.
* **안전한 키 발급**: 생성된 라이선스 정보는 **AES-GCM** 방식으로 암호화되어 안전한 Base32 포맷의 라이선스 키로 발급됩니다.
* **라이선스 키 검증**: 발급된 키를 다시 해독하여 포함된 라이선스 정보를 확인하고 유효성을 검증합니다.
* **컨테이너 기반 환경**: `Docker`와 `Docker Compose`를 사용하여 개발 및 배포 환경을 일관성 있게 관리합니다.

## 🛠️ 기술 스택

* **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA
* **Database**: MySQL 8.0
* **Dependencies**: Lombok, Commons Codec
* **Build Tool**: Gradle
* **Frontend**: HTML, CSS, JavaScript (API Client)
* **Infrastructure**: Docker, Docker Compose

---

## 🚀 시작하기

### 1. 사전 준비

* [Java 17 (JDK)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Docker](https://www.docker.com/products/docker-desktop/) 및 Docker Compose
* IDE (IntelliJ, VS Code 등)

### 2. 환경 설정

`src/main/resources/application.properties` (또는 `application.yml`) 파일을 열고, 본인의 환경에 맞게 데이터베이스 정보와 라이선스 암호화 키를 설정합니다.

```properties
# application.properties

# ==================================
# =      DATABASE CONFIGURATION    =
# ==================================
# docker-compose.yml에 설정된 값과 일치해야 합니다.
spring.datasource.url=jdbc:mysql://localhost:3307/mydatabase?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=myuser
spring.datasource.password=secret
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA (Hibernate) settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ==================================
# =      LICENSE KEY CONFIGURATION =
# ==================================
# 32바이트 길이의 강력한 비밀 키로 변경하세요.
license.SECRET_KEY=YourSuperSecretKeyForLicenseEncrypt123
license.GCM_IV_LENGTH=12
license.GCM_TAG_LENGTH=128
```
### 3. 프로젝트 빌드 및 실행

프로젝트를 실행하는 가장 간단한 방법은 Docker Compose를 사용하는 것입니다.

1.  **프로젝트 빌드 (JAR 파일 생성)**
    프로젝트의 루트 디렉토리에서 아래 명령어를 실행하여 애플리케이션을 빌드합니다.

    ```bash
    ./gradlew build
    ```

2.  **Docker Compose로 실행**
    빌드가 완료되면, `docker-compose.yml` 파일이 있는 루트 디렉토리에서 아래 명령어를 실행합니다.

    ```bash
    docker-compose up -d --build
    ```

    이 명령어는 `mysql` 데이터베이스 컨테이너와 `spring_app` 애플리케이션 컨테이너를 빌드하고 실행합니다.

3.  **실행 확인**
    * 웹 브라우저에서 `http://localhost:8081` 로 접속하여 "WhiteLabel Error Page"가 나오면 서버가 정상적으로 실행된 것입니다.
    * 함께 제공된 `license-tool-final.html` 파일을 브라우저에서 열어 API를 테스트할 수 있습니다.

---

## 📚 API 엔드포인트

| Method | Endpoint        | 설명                                                                 |
| :----- | :-------------- | :------------------------------------------------------------------- |
| `POST` | `/license`      | 새로운 라이선스 정보를 데이터베이스에 저장하고 고유 ID를 반환합니다. |
| `GET`  | `/license/{id}` | 주어진 ID에 해당하는 라이선스 정보를 암호화하여 라이선스 키를 반환합니다. |
| `GET`  | `/license/decode` | 주어진 라이선스 키를 해독하여 원본 라이선스 정보를 반환합니다.       |

### 요청 예시

#### **1. 라이선스 생성 (`POST /license`)**

**Request Body:**
```json
{
    "coreCount": 8,
    "socketCount": 2,
    "boardSerial": "BSN-12345678",
    "macAddress": "00:1B:44:11:3A:B7",
    "expireDate": "2025-12-31",
    "type": 31
}
```
**Success Response (200 OK):**
```json
1
```
(생성된 라이선스의 ID)

## 🖥️ 프론트엔드 클라이언트
프로젝트에는 API와 상호작용할 수 있는 license-tool-final.html 파일이 포함되어 있습니다. 이 파일을 웹 브라우저에서 직접 열어 라이선스 생성, 발급, 검증의 모든 과정을 그래픽 인터페이스(UI)를 통해 편리하게 테스트하고 사용할 수 있습니다.


## 🖥️ 데모 영상
https://github.com/YeeDochi/License/issues/1#issue-3383845106
