# LuckyDoki API

## 프로젝트 소개
![Luckydoki-MZ](https://github.com/user-attachments/assets/98c5b391-0a04-474b-8249-f87335ef8027)

LuckyDoki API는 Spring Boot 기반의 RESTful API 서버입니다. 채팅, 소셜 로그인, 파일 업로드 등 다양한 기능을 제공하는 종합적인 백엔드 서비스입니다.
## 기술 스택

### Backend

- Java 17
- Spring Boot 3.4.2
- Spring Security
- Spring Data JPA
- QueryDSL 5.0.0
- WebSocket (STOMP)
- JWT Authentication

### Database

- MySQL 8.0
- MongoDB

### Storage

- AWS S3

### 외부 서비스 연동

- 카카오 소셜 로그인
- Firebase Admin
- SMTP (Gmail)
- SMS 서비스 (Nurigo)
- Toss Payments

### 개발 도구

- Gradle
- Swagger UI
- Lombok
- Spring Boot Validation

## 주요 기능

- 사용자 인증 및 권한 관리
  - JWT 기반 인증
  - 소셜 로그인 (카카오)
  - 이메일 인증
  - SMS 인증
- 실시간 채팅
  - WebSocket/STOMP 기반 실시간 통신
  - MongoDB를 활용한 채팅 메시지 저장
- 파일 관리
  - AWS S3 기반 파일 업로드
  - 이미지 썸네일 생성
  - WebP 이미지 지원
- 결제 시스템
  - Toss Payments 연동
- 푸시 알림
  - Firebase Cloud Messaging

## 시작하기

### 필수 요구사항

- JDK 17 이상
- MySQL 8.0 이상
- MongoDB
- Gradle 7.x 이상

### 환경 변수 설정

다음 환경 변수들을 `.env` 파일에 설정해야 합니다:

```properties
# Database
LOCAL_MYSQL_URL=
LOCAL_MYSQL_USERNAME=
LOCAL_MYSQL_PASSWORD=
LOCAL_MONGODB_URI=

# AWS
AWS_ACCESS_KEY=
AWS_SECRET_KEY=
AWS_S3_BUCKET_NAME=
AWS_REGION=

# JWT
JWT_SECRET_KEY=

# Social Login
KAKAO_CLIENT_ID=
KAKAO_CLIENT_SECRET=
KAKAO_REDIRECT_URI=

# SMS
SMS_API_KEY=
SMS_API_SECRET=
SMS_FROM=

# Email
MAIL_USERNAME=
MAIL_PASSWORD=

# Payment
TOSS_SECRET_KEY=
```

### 설치 및 실행

1. 저장소 클론

```bash
git clone https://github.com/your-username/luckydoki-api.git
cd luckydoki-api
```

2. 환경 변수 설정

- `.env` 파일을 프로젝트 루트 디렉토리에 생성하고 필요한 환경 변수 설정

3. 프로젝트 빌드

```bash
./gradlew clean build -x test
```

4. 애플리케이션 실행

```bash
./gradlew bootRun
```

## API 문서

API 문서는 Swagger UI를 통해 확인할 수 있습니다.

- 로컬 환경: `http://localhost:8080/swagger-ui.html`

## 프로젝트 구조

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── luckydoki
│   │           ├── config        # 설정 클래스
│   │           ├── controller    # API 컨트롤러
│   │           ├── domain       # 엔티티 클래스
│   │           ├── dto          # 데이터 전송 객체
│   │           ├── repository   # 데이터 접근 계층
│   │           └── service      # 비즈니스 로직
│   └── resources
│       ├── application.yml    # 애플리케이션 설정
│       └── templates         # 이메일 템플릿 등
```

## 개발 가이드라인

- 코드 스타일: Google Java Style Guide 준수
- 커밋 메시지: Conventional Commits 형식 사용
- 브랜치 전략: Git Flow

## 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
