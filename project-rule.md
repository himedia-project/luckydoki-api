# 프로젝트 규칙 및 베스트 프랙티스 가이드

## 📋 프로젝트 개요

- **프레임워크**: Spring Boot 3.4.2
- **Java 버전**: JDK 17
- **빌드 도구**: Gradle 8.x
- **데이터베이스**: MySQL 8.x + JPA + QueryDSL 5.0.0
- **아키텍처**: Domain-Driven Design (DDD)
- **설정 관리**: application.yml + .env

---

## 🏗️ 프로젝트 구조

### 1. 디렉토리 구조 규칙

```
src/main/java/com/himedia/luckydokiapi/
├── config/                 # 설정 클래스
├── domain/                 # 도메인별 패키지
│   ├── {domain}/
│   │   ├── controller/     # REST API 컨트롤러
│   │   ├── service/        # 비즈니스 로직 서비스
│   │   ├── repository/     # 데이터 접근 레이어
│   │   ├── entity/         # JPA 엔티티
│   │   ├── dto/            # 데이터 전송 객체
│   │   └── enums/          # 열거형 (필요시)
├── security/               # 보안 관련 클래스
├── util/                   # 유틸리티 클래스
├── exception/              # 예외 클래스
├── props/                  # 설정 Properties 클래스
└── dto/                    # 공통 DTO 클래스
```

### 2. 도메인 중심 설계 원칙

- **단일 책임 원칙**: 각 도메인은 독립적인 비즈니스 로직을 담당
- **계층 분리**: Controller → Service → Repository → Entity
- **도메인 응집도**: 관련 기능들을 하나의 도메인 패키지로 그룹화

---

## 📝 네이밍 컨벤션

### 1. 패키지 네이밍

```java
// 올바른 예
com.himedia.luckydokiapi.domain.member.controller
com.himedia.luckydokiapi.domain.product.service

// 잘못된 예
com.himedia.luckydokiapi.Controller.Member
com.himedia.luckydokiapi.memberController
```

### 2. 클래스 네이밍

```java
// Controller: {Domain}Controller
public class MemberController {}

// Service: {Domain}Service / {Domain}ServiceImpl
public class MemberService {}
public class MemberServiceImpl {}

// Repository: {Domain}Repository
public interface MemberRepository {}

// Entity: {Domain} (단수형)
public class Member {}

// DTO: {Purpose}{Domain}DTO
public class LoginRequestDTO {}
public class MemberResponseDTO {}
```

### 3. 메서드 네이밍

```java
// 조회: get{Entity} / find{Entity} / retrieve{Entity}
public Member getMember(Long id) {}
public List<Member> findMembersByStatus(String status) {}

// 생성: create{Entity} / save{Entity} / add{Entity}
public Member createMember(MemberRequestDTO dto) {}

// 수정: update{Entity} / modify{Entity}
public Member updateMember(Long id, UpdateMemberDTO dto) {}

// 삭제: delete{Entity} / remove{Entity}
public void deleteMember(Long id) {}
```

---

## 🎯 코딩 컨벤션

### 1. 어노테이션 순서

```java
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"password"})
public class Member extends BaseEntity {
    // 클래스 내용
}
```

### 2. 필드 순서

```java
public class Member {
    // 1. 상수
    private static final String DEFAULT_ROLE = "USER";

    // 2. 기본 필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 3. 연관관계 필드
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
```

### 3. import 순서

```java
// 1. Java 표준 라이브러리
import java.util.List;
import java.time.LocalDateTime;

// 2. Spring 관련
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 3. 외부 라이브러리
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 4. 프로젝트 내부 클래스
import com.himedia.luckydokiapi.domain.member.entity.Member;
```

---

## 🗄️ 데이터베이스 설계 규칙

### 1. 엔티티 설계 원칙

```java
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    // 연관관계는 지연 로딩 사용
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}
```

### 2. BaseEntity 활용

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
```

### 3. 테이블 네이밍

- **테이블명**: 복수형, snake_case (members, product_categories)
- **컬럼명**: snake_case (created_at, member_id)
- **외래키**: {참조테이블명}\_id (member_id, product_id)

---

## 🔄 API 설계 규칙

### 1. REST API 컨벤션

```java
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Member API", description = "회원 관련 API")
public class MemberController {

    // GET /api/member/{id}
    @GetMapping("/{id}")
    @Operation(summary = "회원 조회", description = "회원 ID로 회원 정보를 조회합니다.")
    public ResponseEntity<MemberResponseDTO> getMember(@PathVariable Long id) {
        // 구현
    }

    // POST /api/member
    @PostMapping
    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
    public ResponseEntity<MemberResponseDTO> createMember(@Valid @RequestBody MemberRequestDTO dto) {
        // 구현
    }

    // PUT /api/member/{id}
    @PutMapping("/{id}")
    @Operation(summary = "회원 수정", description = "회원 정보를 수정합니다.")
    public ResponseEntity<MemberResponseDTO> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberDTO dto) {
        // 구현
    }

    // DELETE /api/member/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "회원 삭제", description = "회원을 삭제합니다.")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        // 구현
    }
}
```

### 2. HTTP 상태 코드 사용

```java
// 성공
return ResponseEntity.ok(data);                    // 200 OK
return ResponseEntity.status(HttpStatus.CREATED).body(data); // 201 Created
return ResponseEntity.noContent().build();         // 204 No Content

// 클라이언트 오류
return ResponseEntity.badRequest().build();        // 400 Bad Request
return ResponseEntity.notFound().build();          // 404 Not Found

// 서버 오류
return ResponseEntity.internalServerError().build(); // 500 Internal Server Error
```

---

## 🛡️ 보안 규칙

### 1. 인증/인가 처리

```java
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        // 로그인 처리
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MemberResponseDTO> getMyInfo(
            @AuthenticationPrincipal MemberDTO memberDTO) {
        // 내 정보 조회
    }
}
```

### 2. 입력 검증

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDTO {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;
}
```

---

## 🔧 설정 관리

### 1. application.yml 구조

```yaml
# 기본 설정
spring:
  profiles:
    active: local
    group:
      local: [common]
      prod: [common]

---
# 공통 설정
spring:
  config:
    activate:
      on-profile: common
    import: optional:file:.env[.properties]

  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        default_batch_fetch_size: 100

---
# 로컬 환경
spring:
  config:
    activate:
      on-profile: local

  datasource:
    url: ${LOCAL_MYSQL_URL}
    username: ${LOCAL_MYSQL_USERNAME}
    password: ${LOCAL_MYSQL_PASSWORD}

---
# 운영 환경
spring:
  config:
    activate:
      on-profile: prod

  datasource:
    url: ${PROD_MYSQL_URL}
    username: ${PROD_MYSQL_USERNAME}
    password: ${PROD_MYSQL_PASSWORD}
```

### 2. 환경변수 관리

```properties
# .env 파일 예시
# 데이터베이스 설정
LOCAL_MYSQL_URL=jdbc:mysql://localhost:3306/luckydoki
LOCAL_MYSQL_USERNAME=root
LOCAL_MYSQL_PASSWORD=password

# JWT 설정
JWT_SECRET_KEY=your-secret-key

# 외부 API 키
KAKAO_CLIENT_ID=your-kakao-client-id
GOOGLE_CLIENT_ID=your-google-client-id
```

---

## 📊 QueryDSL 사용 규칙

### 1. Repository 구현

```java
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMembersByCondition(MemberSearchCondition condition) {
        return queryFactory
                .selectFrom(member)
                .where(
                        emailContains(condition.getEmail()),
                        statusEq(condition.getStatus())
                )
                .orderBy(member.createdAt.desc())
                .limit(condition.getSize())
                .offset(condition.getOffset())
                .fetch();
    }

    private BooleanExpression emailContains(String email) {
        return hasText(email) ? member.email.contains(email) : null;
    }

    private BooleanExpression statusEq(String status) {
        return hasText(status) ? member.status.eq(status) : null;
    }
}
```

### 2. 동적 쿼리 작성

```java
public List<Product> findProductsByCondition(ProductSearchCondition condition) {
    return queryFactory
            .selectFrom(product)
            .leftJoin(product.category, category).fetchJoin()
            .where(
                    nameContains(condition.getName()),
                    categoryEq(condition.getCategoryId()),
                    priceGoe(condition.getMinPrice()),
                    priceLoe(condition.getMaxPrice())
            )
            .orderBy(getOrderSpecifier(condition.getSort()))
            .limit(condition.getSize())
            .offset(condition.getOffset())
            .fetch();
}
```

---

## 🎯 서비스 계층 규칙

### 1. 트랜잭션 관리

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 조회는 readOnly = true
    public MemberResponseDTO getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));

        return MemberResponseDTO.from(member);
    }

    // 변경 작업은 @Transactional
    @Transactional
    public MemberResponseDTO createMember(MemberRequestDTO dto) {
        // 중복 체크
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 엔티티 생성 및 저장
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .name(dto.getName())
                .role(MemberRole.USER)
                .build();

        Member savedMember = memberRepository.save(member);

        return MemberResponseDTO.from(savedMember);
    }
}
```

---

## 🚨 예외 처리 규칙

### 1. 커스텀 예외 정의

```java
@Getter
public class MemberNotFoundException extends RuntimeException {
    private final String errorCode;

    public MemberNotFoundException(String message) {
        super(message);
        this.errorCode = "MEMBER_NOT_FOUND";
    }
}

@Getter
public class DuplicateEmailException extends RuntimeException {
    private final String errorCode;

    public DuplicateEmailException(String message) {
        super(message);
        this.errorCode = "DUPLICATE_EMAIL";
    }
}
```

### 2. 전역 예외 처리

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e) {
        log.error("Member not found: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getErrorCode())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("입력값 검증에 실패했습니다.")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
```

---

## 📝 로깅 규칙

### 1. 로그 레벨 사용

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    public MemberResponseDTO createMember(MemberRequestDTO dto) {
        log.info("Creating member with email: {}", dto.getEmail());

        try {
            // 비즈니스 로직
            Member savedMember = memberRepository.save(member);

            log.info("Member created successfully: {}", savedMember.getId());
            return MemberResponseDTO.from(savedMember);

        } catch (Exception e) {
            log.error("Failed to create member: {}", e.getMessage(), e);
            throw new MemberCreationException("회원 생성에 실패했습니다.");
        }
    }
}
```

### 2. 로그 출력 주의사항

```java
// 올바른 예 - 개인정보 마스킹
log.info("Login attempt for email: {}", maskEmail(dto.getEmail()));

// 잘못된 예 - 개인정보 노출
log.info("Login attempt: {}", dto); // 비밀번호 등 민감정보 포함
```

---

## 🧪 테스트 규칙

### 1. 단위 테스트

```java
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원 생성 성공")
    void createMember_Success() {
        // given
        MemberRequestDTO dto = MemberRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트")
                .build();

        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded_password");

        // when
        MemberResponseDTO result = memberService.createMember(dto);

        // then
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        assertThat(result.getName()).isEqualTo(dto.getName());

        verify(memberRepository).save(any(Member.class));
    }
}
```

---

## 📋 코드 리뷰 체크리스트

### 1. 필수 체크 항목

- [ ] 네이밍 컨벤션 준수
- [ ] 적절한 어노테이션 사용
- [ ] 트랜잭션 관리 적절성
- [ ] 예외 처리 완료
- [ ] 로그 레벨 적절성
- [ ] 보안 취약점 없음
- [ ] 성능 고려사항 확인

### 2. 성능 고려사항

- [ ] N+1 문제 방지 (fetch join 사용)
- [ ] 페이징 처리 적용
- [ ] 인덱스 활용 쿼리 작성
- [ ] 불필요한 데이터 로딩 방지

---

## 🔄 버전 관리 규칙

### 1. 커밋 메시지 규칙

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅, 세미콜론 누락 등
refactor: 코드 리팩토링
test: 테스트 코드 추가
chore: 빌드 업무 수정, 패키지 매니저 수정

예시:
feat: 회원 가입 기능 추가
fix: 회원 로그인 시 토큰 만료 오류 수정
docs: API 문서 업데이트
```

### 2. 브랜치 전략

```
main: 운영 환경 배포용
develop: 개발 환경 통합
feature/{기능명}: 새로운 기능 개발
hotfix/{버그명}: 긴급 버그 수정
```

---

## 📚 참고 자료

### 1. 공식 문서

- [Spring Boot 3.4.2 Reference](https://docs.spring.io/spring-boot/docs/3.4.2/reference/html/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [QueryDSL Reference](http://querydsl.com/static/querydsl/latest/reference/html/)

### 2. 코드 품질 도구

- **SonarQube**: 코드 품질 분석
- **SpotBugs**: 버그 패턴 검사
- **Checkstyle**: 코딩 스타일 검사

---

## 🎯 최종 점검 사항

1. **아키텍처 준수**: DDD 구조 및 계층 분리
2. **보안 강화**: 인증/인가, 입력 검증, XSS 방지
3. **성능 최적화**: 쿼리 최적화, 캐싱 활용
4. **테스트 커버리지**: 단위 테스트 및 통합 테스트
5. **문서화**: API 문서 및 코드 주석
6. **모니터링**: 로그 수집 및 성능 모니터링

---

_이 문서는 프로젝트 진행 중 지속적으로 업데이트됩니다._
