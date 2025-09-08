plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.5"
	id("com.google.protobuf") version "0.9.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("commons-codec:commons-codec:1.16.0")
	//프로토콜 버퍼
	implementation("com.google.protobuf:protobuf-java:4.27.2")
	implementation("com.google.protobuf:protobuf-java-util:4.27.2") // JSON 변환 등 유틸리티 포함
}

protobuf {
	protoc {
		// .proto 파일을 컴파일할 protoc 실행 파일을 자동으로 다운로드합니다.
		// 버전은 의존성 라이브러리와 호환되는 것을 권장합니다.
		artifact = "com.google.protobuf:protoc:3.25.3"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
