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


# 라이선스 필드 핸들러 작성 가이드

이 문서는 라이선스 필드의 직렬화(Serialization) 및 역직렬화(Deserialization)를 처리하는 핸들러의 작성 방법을 설명합니다. 각 핸들러는 특정 데이터 필드를 `DataOutputStream`에 쓰거나 `DataInputStream`에서 읽어 `LicenseDTO` 객체를 채우는 역할을 합니다.

---

## 인터페이스

모든 핸들러는 다음 두 인터페이스 중 하나를 구현해야 합니다.

-   `LicenseFieldSerializationHandler`: DTO 객체의 데이터를 스트림에 쓰는(직렬화) 메서드 `serialize`를 정의합니다.
-   `LicenseFieldDeserializationHandler`: 스트림에서 데이터를 읽어 DTO 빌더 객체에 설정하는(역직렬화) 메서드 `deserialize`를 정의합니다.

---

## 1. Deserialization (D) 핸들러 작성 방법

역직렬화 핸들러는 `DataInputStream`으로부터 데이터를 읽어 `LicenseDTO.Builder`에 값을 설정합니다.

### 기본 구조

```java
// LicenseFieldDeserializationHandler 인터페이스 구현
public class FieldNameDeserializationHandler implements LicenseFieldDeserializationHandler {
    // 각 필드를 식별하는 고유 비트마스크
    private static final int BITMASK = {고유 값};

    @Override
    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException {
       // 1. 비트마스크 검사: 현재 라이선스 타입에 해당 필드가 포함되는지 확인
       if ((builder.build().getType() & BITMASK) != 0) {
            // 2. Null 여부 확인: 데이터가 실제로 존재하는지 boolean 값으로 확인
            if (dis.readBoolean()) {
                // 3. 실제 데이터 읽기 및 DTO 빌더에 설정
                builder.fieldName({데이터 읽기});
            }
        }
    }
}
```

### 데이터 타입별 작성법

#### `int` 타입 (예: `CoreCountDeserializationHandler`)

`int` 타입의 필드는 `DataInputStream`의 `readInt()` 메서드를 사용하여 직접 읽습니다.

```java
// CoreCountDeserializationHandler.java

@RequiredArgsConstructor
public class CoreCountDeserializationHandler implements LicenseFieldDeserializationHandler {
    private static final int BITMASK = 1;

    @Override
    public void deserialize(DataInputStream dis, Builder builder) throws IOException {
       if ((builder.build().getType() & BITMASK) != 0) {
            // 스트림의 첫 boolean 값이 true이면 데이터가 존재함을 의미
            if (dis.readBoolean()) {
                // readInt()를 사용해 정수 값을 읽어 빌더에 설정
                builder.coreCount(dis.readInt());
            }
        }
    }
}
```

#### `String` 타입 (예: `BoardSerialDeserializationHandler`)

`String` 타입은 가변 길이 데이터이므로, 별도의 `StringDataReader` 헬퍼 클래스를 사용하여 읽습니다. `StringDataReader`는 먼저 문자열의 길이를 읽고, 그 길이만큼 바이트를 읽어 문자열로 변환합니다.

```java
// BoardSerialDeserializationHandler.java

@RequiredArgsConstructor
public class BoardSerialDeserializationHandler implements LicenseFieldDeserializationHandler {
    private static final int BITMASK = 4;
    private final StringDataReader reader; // StringDataReader 주입

    @Override
    public void deserialize(DataInputStream dis, LicenseDTO.Builder builder) throws IOException {
        if ((builder.build().getType() & BITMASK) != 0) {
            if (dis.readBoolean()) {
                // reader.readString(dis)를 사용해 문자열을 읽어 빌더에 설정
                builder.boardSerial(reader.readString(dis));
            }
        }
    }
}
```

-   **`StringDataReader.readString(dis)` 동작 방식:**
    1.  `dis.readShort()`: 문자열의 길이를 나타내는 2바이트 short 값을 먼저 읽습니다.
    2.  `new byte[length]`: 읽은 길이만큼 바이트 배열을 생성합니다.
    3.  `dis.readFully(bytes)`: 생성된 바이트 배열을 데이터로 채웁니다.
    4.  `new String(bytes, StandardCharsets.UTF_8)`: 바이트 배열을 UTF-8 인코딩으로 문자열 객체로 변환하여 반환합니다.

---

## 2. Serialization (S) 핸들러 작성 방법

직렬화 핸들러는 `LicenseDTO`에서 값을 가져와 `DataOutputStream`에 씁니다.

### 기본 구조

```java
// LicenseFieldSerializationHandler 인터페이스 구현
public class FieldNameSerializationHandler implements LicenseFieldSerializationHandler {
    // 각 필드를 식별하는 고유 비트마스크
    private static final int BITMASK = {고유 값};

    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        // 1. 비트마스크 검사: 라이선스 타입에 해당 필드가 포함되는지 확인
        // 2. DTO 필드 값 Null 여부 확인
        if ((dto.getType() & BITMASK) != 0 && dto.getFieldName() != null) {
            // 3. 데이터 존재 여부 Flag 쓰기 (true)
            dos.writeBoolean(true);
            // 4. 실제 데이터 쓰기
            // {데이터 쓰기}
        } else if ((dto.getType() & BITMASK) != 0) {
            // 5. 데이터가 Null인 경우 Flag 쓰기 (false)
            dos.writeBoolean(false);
        }
    }
}
```

### 데이터 타입별 작성법

#### `int` 타입 (예: `CoreCountSerializationHandler`)

`int` 타입 필드는 `DataOutputStream`의 `writeInt()` 메서드를 사용하여 직접 씁니다.

```java
// CoreCountSerializationHandler.java

@RequiredArgsConstructor
public class CoreCountSerializationHandler implements LicenseFieldSerializationHandler {
    private static final int BITMASK = 1;

    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        // 비트마스크에 포함되고, DTO의 coreCount 값이 null이 아닐 때
        if ((dto.getType() & BITMASK) != 0 && dto.getCoreCount() != null) {
            dos.writeBoolean(true); // 데이터가 있음을 표시
            dos.writeInt(dto.getCoreCount()); // 실제 int 값을 씀
        } else if ((dto.getType() & BITMASK) != 0) {
            // 비트마스크에는 포함되지만 값이 null일 경우
            dos.writeBoolean(false); // 데이터가 없음을 표시
        }
    }
}
```

#### `String` 타입 (예: `BoardSerialSerializationHandler`)

`String` 타입은 `StringDataReader`의 `writeString` 메서드를 사용하여 씁니다. 이 메서드는 문자열을 바이트로 변환하고, 길이를 먼저 쓴 뒤 실제 데이터 바이트를 씁니다.

```java
// BoardSerialSerializationHandler.java

@RequiredArgsConstructor
public class BoardSerialSerializationHandler implements LicenseFieldSerializationHandler {
    private static final int BITMASK = 4;
    private final StringDataReader reader; // StringDataReader 주입

    @Override
    public void serialize(DataOutputStream dos, LicenseDTO dto) throws IOException {
        if ((dto.getType() & BITMASK) != 0 && dto.getBoardSerial() != null) {
            dos.writeBoolean(true); // 데이터가 있음을 표시
            reader.writeString(dos, dto.getBoardSerial()); // 실제 String 값을 씀
        } else if ((dto.getType() & BITMASK) != 0) {
            dos.writeBoolean(false); // 데이터가 없음을 표시
        }
    }
}
```

-   **`StringDataReader.writeString(dos, str)` 동작 방식:**
    1.  `str.getBytes(StandardCharsets.UTF_8)`: 문자열을 UTF-8 바이트 배열로 변환합니다.
    2.  `dos.writeShort(bytes.length)`: 변환된 바이트 배열의 길이를 2바이트 short 값으로 먼저 씁니다.
    3.  `dos.write(bytes)`: 실제 바이트 배열 데이터를 씁니다.

---

## 3. `int`와 `String`의 주요 차이점 요약

| 구분          | `int`                                                        | `String`                                                     |
| :------------ | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **길이** | **고정 길이** (4바이트)                                      | **가변 길이** |
| **읽기/쓰기** | `DataInputStream.readInt()`<br>`DataOutputStream.writeInt()` | 별도의 **`StringDataReader` 헬퍼 클래스** 사용               |
| **직렬화 구조** | `[데이터 존재 여부(1바이트)]` `[실제 int 값(4바이트)]`         | `[데이터 존재 여부(1바이트)]` `[문자열 길이(2바이트)]` `[실제 문자열 바이트]` |
| **구현** | 핸들러 내에서 직접 처리                                      | `StringDataReader`에 의존하여 처리                             |


## 4. 작성이 끝난후

작성이 완료되면 해당 헨들러들은 `LicanseData.java` 에 등록되어야 합니다

```java
 @PostConstruct
    public void initialize() {
        sHandlers = List.of(
                // 새로운 필드 핸들러를 여기에 추가!
                new CoreCountSerializationHandler(),
                new SocketCountSerializationHandler(),
                new BoardSerialSerializationHandler(stringDataReader),
                new MacAddressSerializationHandler(stringDataReader),
                new ExpireDateSerializationHandler()
        );

        dHandlers = List.of(
                // 새로운 필드 핸들러를 여기에 추가!
                new CoreCountDeserializationHandler(),
                new SocketCountDeserializationHandler(),
                new BoardSerialDeserializationHandler(stringDataReader),
                new MacAddressDeserializationHandler(stringDataReader),
                new ExpireDateDeserializationHandler()
        );
    }
```
추가하는 헨들러는 쌍을 이루어야합니다.
