# 라이선스 키 생성 및 검증 서비스

이 프로젝트는 소프트웨어 라이선스 키를 간단하고 안전하게 발급하고 검증하는 방법을 제공하는 Spring Boot 애플리케이션입니다. 디지털 서명(SHA256withRSA)이 적용된 비대칭 키 암호화(RSA)를 사용하여 라이선스 키의 무결성과 신뢰성을 보장합니다.

생성된 라이선스 키는 JWT와 유사하게 점으로 구분된 세 부분의 형식(`header.body.signature`)을 가집니다.

## ✨ 주요 기능

-   **라이선스 키 발급**: 고객 및 솔루션 정보를 기반으로 고유한 라이선스 키를 생성합니다.
-   **비대칭 암호화**: 안전한 키 생성을 위해 공개키/개인키 쌍(RSA 2048비트)을 사용합니다.
-   **디지털 서명**: SHA256withRSA를 사용하여 라이선스 키가 변조되지 않았음을 보장합니다.
-   **라이선스 키 디코딩 및 검증**: 라이선스 키를 디코딩하고 검증하여 원본 데이터를 검색하고 유효성을 확인합니다.
-   **RESTful API**: 라이선스 키 발급 및 디코딩을 위한 간단한 REST 엔드포인트를 제공합니다.

---

## 🏗️ 프로젝트 구조

이 프로젝트는 표준적인 계층형 아키텍처를 따릅니다:

-   `Controller`: 들어오는 HTTP 요청과 응답을 처리합니다.
-   `Service`: 라이선스 키 생성 및 검증을 위한 핵심 비즈니스 로직을 포함합니다.
-   `DTO` (Data Transfer Object): 요청 및 응답을 위한 데이터 구조를 정의합니다.
-   `Entity` & `Repository`: Spring Data JPA를 사용하여 라이선스 데이터의 영속성을 관리합니다.

---

## 🛠️ 작동 방식

### 라이선스 생성

1.  **요청**: 클라이언트는 고객 정보(`customerid`, `uuid`), 라이선스 세부 정보(`solution`, `expiration`), 라이선스 버전(`license_v`)이 포함된 JSON 본문으로 `/license/issue`에 `POST` 요청을 보냅니다.
2.  **키 쌍 생성**: 각 라이선스에 대해 새로운 RSA 2048비트 공개키/개인키 쌍이 생성됩니다.
3.  **라이선스 구성**:
    * **헤더(Header)**: 라이선스 버전(`license_v`)을 포함합니다.
    * **본문(Body)**: 만료일, UUID, 고객 ID, 솔루션 이름 및 새로 생성된 **공개키**를 포함합니다.
4.  **인코딩**: 헤더와 본문 모두 Base64Url로 인코딩됩니다.
5.  **서명**: **개인키**를 사용하여 `header.body` 문자열에 서명함으로써 디지털 서명이 생성됩니다.
6.  **최종 키**: 최종 라이선스 키는 인코딩된 헤더, 인코딩된 본문, 인코딩된 서명을 점으로 결합한 것입니다.
7.  **영속성**: 라이선스 정보는 데이터베이스에 저장됩니다.
8.  **응답**: 생성된 라이선스 키가 클라이언트에게 반환됩니다. 개인키는 기본적으로 저장되지 않지만 필요한 경우 서비스 계층에서 사용할 수 있습니다.

### 라이선스 검증

1.  **요청**: 클라이언트는 `licenseKey`를 요청 파라미터로 하여 `/license/decode`에 `GET` 요청을 보냅니다.
2.  **키 분리**: 키는 헤더, 본문, 서명의 세 부분으로 분리됩니다.
3.  **디코딩**: 헤더와 본문은 Base64Url에서 디코딩되어 JSON 표현으로 변환됩니다.
4.  **공개키 추출**: 디코딩된 본문에서 **공개키**가 추출됩니다.
5.  **검증**: 추출된 공개키를 사용하여 원본 `header.body` 내용에 대해 서명을 검증합니다.
6.  **응답**:
    * 서명이 **유효**하면 디코딩된 본문 내용이 `200 OK` 상태와 함께 반환됩니다.
    * 서명이 **유효하지 않으면** 키가 변조되었거나 유효하지 않음을 나타내는 `SecurityException`이 발생합니다.

---

## 🚀 API 엔드포인트

### 라이선스 발급

새로운 라이선스 키를 생성하고 반환합니다.

-   **URL**: `/license/issue`
-   **메서드**: `POST`
-   **요청 본문**:

~~~json
{
  "license_v": "1.0",
  "expiration": "2025-12-31",
  "uuid": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "customerid": "customer-123",
  "solution": "AwesomeApp",
  "publicKey": null
}
~~~

-   **성공 응답 (200 OK)**:
    -   **Content-Type**: `text/plain`
    -   **본문**: 라이선스 키를 나타내는 문자열 (예: `eyJsaWNlbnNlX3YiOiIxLjAifQ.eyJleHBpcmF0aW9uIjoiMjAyNS0xMi0zMSIsInV1aWQiOiJhMWIyYzNkNC1lNWY2LTc4OTAtMTIzNC01Njc4OTBhYmNkZWYiLCJjdXN0b21lcmlkIjoiY3VzdG9tZXItMTIzIiwic29sdXRpb24iOiJBd2Vzb21lQXBwIiwicHVibGljS2V5IjoibGlrZUIifQ.signature`)

---

### 라이선스 디코딩 및 검증

제공된 라이선스 키를 디코딩하고 서명을 검증합니다.

-   **URL**: `/license/decode`
-   **메서드**: `GET`
-   **쿼리 파라미터**: `licenseKey` (string)
-   **요청 예시**: `GET /license/decode?licenseKey=eyJsaWNlbnNlX3YiOiIxLjAifQ.eyJleHBpcmF0aW9uIjoiMjAyNS0xMi0zMSIsInV1aWQiOiJhMWIyYzNkNC1lNWY2LTc4OTAtMTIzNC01Njc4OTBhYmNkZWYiLCJjdXN0b21lcmlkIjoiY3VzdG9tZXItMTIzIiwic29sdXRpb24iOiJBd2Vzb21lQXBwIiwicHVibGljS2V5IjoibGlrZUIifQ.signature`

-   **성공 응답 (200 OK)**:
    -   **Content-Type**: `application/json`
    -   **본문**:

~~~json
{
  "expiration": "2025-12-31",
  "uuid": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "customerid": "customer-123",
  "solution": "AwesomeApp",
  "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
}
~~~

-   **오류 응답**: 서명이 유효하지 않으면 서버 오류가 발생합니다.

---

## ⚙️ 실행 방법

1.  **사전 요구사항**:
    * Java (JDK) 17 이상
    * Maven
    * `application.properties`에 구성된 실행 중인 데이터베이스 인스턴스 (H2, PostgreSQL, MySQL 등).

2.  **리포지토리 복제**:
    ~~~bash
    git clone <repository-url>
    cd <project-directory>
    ~~~

3.  **데이터베이스 구성**:
    `src/main/resources/application.properties` 파일을 열고 데이터베이스 연결 세부 정보를 구성합니다.

4.  **애플리케이션 빌드 및 실행**:
    ~~~bash
    mvn spring-boot:run
    ~~~

애플리케이션은 구성된 포트(8081)에서 시작됩니다.