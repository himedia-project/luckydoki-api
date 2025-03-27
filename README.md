
# 🎯 LuckyDoki - 실시간 커머스 플랫폼
![Luckydoki-MZ](https://github.com/user-attachments/assets/98c5b391-0a04-474b-8249-f87335ef8027)

## 프로젝트 핵심 목표
- 대용량 트래픽 처리와 성능 최적화에 중점을 둔 실시간 커머스 플랫폼 구축
- 분산 환경에서의 데이터 일관성과 동시성 제어
- 실시간 데이터 처리 및 검색 기능 고도화

 * 사용자: www.luckydoki.shop
 * 관리자: www.admin.luckydoki.shop

## 📂 프로젝트 구조
```mermaid
graph TD
    A[사용자 웹] --> B[API Gateway]
    C[관리자 웹] --> B
    B --> D[메인 API 서버]
    B --> E[AI API 서버]
    D --> F[(MySQL)]
    D --> G[(Redis)]
    D --> H[(MongoDB)]
    E --> I[(PGVector)]
    E --> J[OpenAI API]
    D --> K[Kafka]
    D --> L[Elasticsearch]
```

### Backend Repositories
1. **[luckydoki-api](https://github.com/himedia-project/luckydoki-api)**
   - 메인 백엔드 API 서버
   - Spring Boot 3.4.2, Java 17 기반
   - 주요 기능: 인증/인가, 상품/주문 관리, 결제, 알림

2. **[luckydoki-ai-api](https://github.com/himedia-project/luckydoki-ai-api)**
   - AI 서비스 전용 백엔드 서버
   - OpenAI, PGVector 기반 RAG 시스템
   - 챗봇, 문서 검색, 컨텍스트 처리

### Frontend Repositories
1. **[luckydoki-user](https://github.com/himedia-project/luckydoki-user)**
   - 사용자용 웹 클라이언트
   - React.js 기반 SPA
   - 기술 스택:
     - JavaScript/React.js
     - CSS Modules
     - WebSocket 클라이언트
     - FCM 클라이언트

2. **[luckydoki-admin](https://github.com/himedia-project/luckydoki-admin)**
   - 관리자용 대시보드
   - React.js 기반 어드민 패널
   - 기술 스택:
     - JavaScript/React.js
     - 실시간 모니터링
     - 데이터 시각화
     - 관리자 권한 관리


## 🔑 KEY SUMMARY
1. **대용량 트래픽 처리**
   - Kafka를 활용한 대용량 쿠폰 발급 시스템 구현
   - Redis Redisson 분산락으로 동시성 제어
   - CloudFront CDN으로 이미지 로딩 최적화

2. **실시간 데이터 처리**
   - WebSocket 기반 실시간 1:1 채팅
   - Redis 기반 실시간 인기 검색어 관리
   - Firebase FCM 실시간 알림

3. **검색 기능 고도화**
   - Elasticsearch 한글 형태소 분석기 적용
   - 상품 및 커뮤니티 통합 검색
   - Google Translation API 다국어 지원

## 🛠 주요 기능

### 1. 실시간 처리 시스템
- WebSocket 기반 실시간 1:1 채팅
- Firebase Cloud Messaging 푸시 알림
- Redis 기반 실시간 인기 검색어 및 최근 본 상품

### 2. 검색 엔진
- Elasticsearch 한글 형태소 분석
- 통합 검색 (상품 + 커뮤니티)
- Google Translation API 자동 번역

### 3. 인증/보안
- JWT + Redis 기반 토큰 관리
- 분산 환경 세션 관리
- Spring Security 기반 인증/인가

### 4. 대용량 처리
- Kafka 기반 쿠폰 발급 시스템
- Redisson 분산락 동시성 제어
- CloudFront CDN 이미지 최적화


## 🔧 기술적 고도화

### 1. 분산 처리 아키텍처
- Redis Sentinel 구성으로 고가용성 확보
- Kafka를 통한 비동기 메시지 처리
- Redisson 분산락으로 데이터 정합성 보장

### 2. 모니터링 및 로깅
- ELK 스택 구축 (Elasticsearch, Logstash, Kibana)
- 로그 중앙화 및 실시간 모니터링
- 성능 메트릭 수집 및 분석

### 3. 캐싱 전략
- Redis 다중 계층 캐싱
- CloudFront CDN 엣지 캐싱
- 검색 결과 캐싱

### 1. AI 챗봇 시스템
- **RAG(Retrieval-Augmented Generation) 아키텍처**
  - OpenAI GPT 모델 기반 자연어 처리
  - PGVector를 활용한 벡터 데이터베이스 구현
  - 컨텍스트 기반 정확한 응답 생성

### 2. 고도화된 검색 시스템
- **벡터 기반 유사도 검색**
  - 텍스트 임베딩을 통한 시맨틱 검색
  - PGVector의 벡터 연산으로 유사 문서 검색
  - 실시간 문서 유사도 분석

### 3. 컨텍스트 인식 시스템
- **맥락 기반 응답 생성**
  - 사용자 이전 대화 기록 분석
  - 상황 맥락을 고려한 개인화된 응답 프롬프트로 개선
  - 실시간 학습 및 응답 최적화


## 🚨 트러블 슈팅

### 1. 대용량 쿠폰 발급 시스템
- **문제**: 동시 다발적 쿠폰 발급 요청으로 인한 성능 저하
- **해결**: 
  - Kafka를 도입하여 비동기 처리
  - Redisson 분산락으로 동시성 제어
  - 처리량 300% 향상

### 2. 실시간 검색 성능
- **문제**: 한글 검색 시 정확도 및 속도 저하
- **해결**:
  - Elasticsearch 한글 형태소 분석기 적용
  - 검색 결과 캐싱 도입
  - 응답 시간 70% 개선

### 3. 이미지 로딩 최적화
- **문제**: 대용량 이미지로 인한 페이지 로딩 지연
- **해결**:
  - CloudFront CDN 도입
  - 이미지 최적화 및 압축
  - 로딩 시간 60% 단축

### 4. AI 응답 생성 최적화
- **문제**: 
  - RAG 시스템의 높은 지연 시간
  - 부정확한 컨텍스트 참조
- **해결**:
  - 벡터 검색 인덱스 최적화
  - 컨텍스트 윈도우 사이즈 조정
  - 응답 시간 50% 개선, 정확도 30% 향상


## CI/CD 배포 아키텍쳐
- GitHub Actions를 통한 자동화된 빌드/배포
- Docker 컨테이너화로 일관된 환경 제공
- AWS Route53를 도메인 기반 라우팅 설정

![image](https://github.com/user-attachments/assets/c4f7c86b-7252-413b-8f57-968f8ecd0657)



## 🛠 기술 스택
- **Backend**: Spring Boot 3.4.2, Java 17
- **Database**: MySQL, MongoDB, Redis
- **Message Queue**: Kafka
- **Search Engine**: Elasticsearch
- **Monitoring**: ELK Stack
- **DevOps**: AWS, CloudFront
- **Others**: WebSocket, FCM, Google Translation API
