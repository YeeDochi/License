# 소프트웨어 라이선스 키 관리 시스템

## 📋 프로젝트 개요

이 프로젝트는 소프트웨어 라이선스를 동적으로 생성, 발급하고 유효성을 검증하는 기능을 제공하는 백엔드 서버 및 웹 클라이언트입니다.

Spring Boot와 Docker를 기반으로 구축되었으며, 비트마스크를 활용하여 라이선스에 포함될 속성을 유연하게 제어할 수 있습니다. 발급된 라이선스 키는 AES-GCM 대칭키 알고리즘으로 암호화되어 안전하게 관리됩니다.

## ✨ 주요 기능

* **동적 라이선스 생성**: `Core Count`, `MAC 주소` 등 다양한 하드웨어 정보를 선택적으로 포함하여 라이선스를 생성합니다.
* **비트마스크 제어**: 웹 UI의 체크박스를 통해 라이선스에 포함될 속성을 직관적으로 제어하고, 해당 조합을 비트마스크 값으로 자동 계산합니다.
* **안전한 키 발급**: 생성된 라이선스 정보는 **AES-GCM** 방식, 혹은 **RSA** 방식으로 암호화되어 안전한 Base32 포맷의 라이선스 키로 발급됩니다.
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
# AES/GCM
license.SECRET_KEY= 공유키  #반드시 16,32 같이 2의 배수인 바이트여야합니다.
license.GCM_IV_LENGTH=12
license.GCM_TAG_LENGTH=128
# RSA
license.private-key = 비밀키
license.public-key = 공유키

```
### 3. 프로젝트 빌드 및 실행

프로젝트를 실행하는 가장 간단한 방법은 Docker Compose를 사용하는 것입니다.

1.  **프로젝트 빌드 (JAR 파일 생성)**
    프로젝트의 루트 디렉토리에서 아래 명령어를 실행하여 애플리케이션을 빌드합니다.

    ```bash
    ./gradlew build -x test
    ```

2.  **Docker Compose로 실행**
    빌드가 완료되면, `docker-compose.yml` 파일이 있는 루트 디렉토리에서 아래 명령어를 실행합니다.

    ```bash
    docker-compose up -d --build
    ```

    이 명령어는 `mysql` 데이터베이스 컨테이너와 `spring_app` 애플리케이션 컨테이너를 빌드하고 실행합니다.

3.  **실행 확인**
    * 웹 브라우저에서 `http://localhost:8081` 로 접속하여 "License Key System"가 나오면 서버가 정상적으로 실행된 것입니다.

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
[https://github.com/YeeDochi/License/issues/1#issue-3383845106](https://github.com/user-attachments/assets/8af0e98e-f430-4a37-9aa9-7d620d34da00)

## Protocal Buffer

본 프로젝트에서 적용된 프로토콜 버퍼의 사용법에 대해 간략히 기술하겠습니다.




프로토콜 버퍼는 데이터의 직렬화를 위한 라이브러리로 별도의 컴파일러를 가지고 있습니다.

**프로토콜 버퍼**를 사용하는것으로 다음과 같은 이점을 얻습니다.

* **데이터 표준화**: .proto 파일을 통해 라이선스 데이터 구조를 명확하게 정의하여, 데이터 형식의 일관성을 보장합니다.

* **효율성**: JSON이나 직접 구현한 직렬화 방식보다 더 작은 크기의 바이트 배열을 생성하여 라이선스 키의 전체 길이를 줄입니다.

* **안전성**: 타입-세이프(Type-safe)한 빌더(Builder)를 제공하여 데이터 생성 시 오류를 줄입니다.




### 1. 스키마 (license.proto)

라이선스 데이터의 구조는 아래 경로의 파일에 정의되어 있습니다.

`파일 위치: src/main/proto/license.proto`

```Protocol Buffers

syntax = "proto3";

package com.example.License.Proto;

option java_package = "com.example.License.Proto";
option java_outer_classname = "LicenseProtos";

message License {
  int32 core_count = 2;
  int32 socket_count = 3;
  string board_serial = 4;
  string mac_address = 5;
  string expire_date = 6;
}
```

Gradle 빌드 시 이 `.proto` 파일을 기반으로 `build/generated` 경로에 `LicenseProtos.java` 클래스가 자동으로 생성됩니다.




### 2. 프로젝트 내 통합 및 사용 흐름

Protobuf는 라이선스 키 생성 및 검증 과정의 중심에서 데이터를 변환하는 역할을 담당합니다.




**직렬화 (라이선스 키 생성 시)**

* `LicenseController`에서 ID를 통해 `LicenseEntity`를 조회합니다.

* **LicenseEntity**의 `toProto()` 메소드를 호출하여 `Protobuf` 객체인 `LicenseProtos.License`로 변환합니다.

* 이 과정에서 데이터베이스의 NULL 값이 Protobuf의 null 허용 정책 위반으로 **NullPointerException**을 발생시키지 않도록, null인 필드는 타입별 기본값(숫자는 0, 문자열은 "")으로 변환합니다.

* `FormattedLicenseService`는 변환된 License 객체를 받아 `toByteArray()` 메소드를 호출하여 최종 바이트 배열을 생성합니다.

* 이 바이트 배열이 바로 서명 또는 암호화의 대상이 되는 **원본 데이터(Raw Data)**가 됩니다.




**역직렬화 (라이선스 키 검증 시)**

* **FormattedLicenseService**에서 Base32 디코딩 및 서명 검증이 완료된 원본 데이터(바이트 배열)를 얻습니다.

* `License.parseFrom(rawData)` 정적 메소드를 호출하여 바이트 배열로부터 `LicenseProtos.License` 객체를 복원합니다.

* **LicenseController**는 복원된 **License** 객체를 `LicenseResponseDTO.fromProto()`를 통해 클라이언트에게 반환할 DTO로 변환하여 최종 응답을 생성합니다.




**3. 사용시 주의점**

* 태그 번호: 기존 필드의 태그 번호(= 1, = 2 등)는 절대로 변경하거나 재사용해서는 안 됩니다. 이는 이미 발급된 라이선스 키의 호환성을 유지하기 위해 필수적인 규칙입니다.

* 필드 추가: 새로운 필드를 추가하는 것은 안전합니다. 구 버전의 코드는 모르는 필드를 무시하므로 기존 라이선스 검증에 영향을 주지 않습니다.

* 필드 삭제: 필드를 삭제할 경우, 해당 태그 번호를 reserved 키워드로 예약하여 미래에 재사용되는 것을 방지해야 합니다
