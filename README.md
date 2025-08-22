# 👪 EarDream 백엔드

## 📋 목차
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [개발 환경 설정](#-개발-환경-설정)
- [API 문서](#-api-문서)
- [팀원 가이드](#-팀원-가이드)

## ✨ 주요 기능

### 회원 관리
- 카카오 소셜 로그인
- 프로필 관리 (이름, 사진, 생년월일, 전화번호)
- 가족 관계 설정

### 가족 그룹
- 가족 그룹 생성 및 관리
- 초대 코드 시스템
- 가족 구성원 역할 구분 (리더/멤버)

### 소식 공유
- 사진 업로드 (1~4장)
- 텍스트 작성 (최대 100자)
- 월별 피드 관리 (최대 20개)
- 정기 마감일 기반 리셋

### 구독 관리
- 정기 결제 (PG/이니시스)
- 구독 상태 확인
- 결제 수단 관리

### 소식지 발행
- PDF 생성 및 다운로드
- 배송 상태 추적
- 디지털 열람 서비스

## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 17 LTS
- **Database**: Oracle Database 19c (Oracle Cloud)
- **ORM**: MyBatis 3.0.5
- **Authentication**: Kakao OAuth + JWT

### Infrastructure
- **Cloud**: AWS EC2
- **Storage**: Local File System

### Development Tools
- **Build**: Maven
- **VCS**: Git (GitHub Flow)
- **IDE**: IntelliJ IDEA

## 📁 프로젝트 구조

```
src/main/java/com/eardream/
├── EarDreamApplication.java       # 메인 애플리케이션
├── global/                         # 공통 설정 및 유틸리티
│   ├── common/                     # 공통 응답/요청 DTO
│   │   ├── ApiResponse.java       # API 응답 포맷
│   │   ├── PageRequest.java       # 페이징 요청
│   │   ├── PageResponse.java      # 페이징 응답
│   │   └── ErrorCode.java         # 에러 코드 상수
│   ├── config/                     # 설정 클래스
│   │   ├── SecurityConfig.java    # 보안 설정
│   │   ├── MybatisConfig.java     # MyBatis 설정
│   │   └── WebConfig.java         # Web MVC 설정
│   ├── exception/                  # 예외 처리
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BusinessException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   └── ForbiddenException.java
│   └── util/                       # 유틸리티 클래스
│       ├── DateUtils.java         # 날짜 관련 유틸
│       ├── StringUtils.java       # 문자열 관련 유틸
│       └── FileUtils.java         # 파일 관련 유틸
└── domain/                         # 도메인별 패키지
    ├── auth/                       # 인증/인가
    ├── user/                       # 사용자 도메인
    ├── groups/                     # 가족 그룹 도메인
    ├── subscription/               # 구독 도메인
    ├── publication/                # 소식지 도메인
    ├── order/                      # 주문 도메인
    └── payment/                    # 결제 PG
```

## 🚀 개발 환경 설정

### 환경 변수 설정 (.env)
프로젝트 루트에 `.env` 파일을 생성하고 아래 예시를 참고해 값을 채워주세요.

```
SPRING_PROFILES_ACTIVE=dev

# Oracle
ORACLE_TNS_ADMIN=/path/to/wallet
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT (32바이트 이상)
JWT_SECRET_KEY=change-me-very-long-secret-key-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ

# Kakao OAuth
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
KAKAO_REDIRECT_URI=http://localhost:3000/auth/kakao/callback
```

이 프로젝트는 `spring-dotenv`를 사용하여 `.env` 값을 자동으로 로드합니다.

### 프로젝트 실행
```bash
# Gradle 사용
./gradlew clean build -x test
./gradlew bootRun
```

### 데이터베이스 설정
- `senior_high` 서비스 사용

## 📖 API 문서

### Swagger UI
개발 서버 실행 후: http://localhost:8080/swagger-ui/index.html

### 주요 API 엔드포인트
https://docs.google.com/spreadsheets/d/1_ONnZXwlRquhWwVsUWkuyyT7x76sdlDYdFeXBf_eD1E/edit?usp=sharing

## 👥 팀원 가이드

### 브랜치 전략 (GitHub Flow)
```bash
master          # 배포 가능한 코드
feature/기능명  # 기능 개발 브랜치
```

### 커밋 컨벤션
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드
chore: 빌드 업무 수정
```

### 개발 순서
1. **Phase 1**: DB 스키마 설계 및 기본 API 구현
2. **Phase 2**: 핵심 기능 개발 (회원, 가족, 소식)
3. **Phase 3**: 구독 및 결제 시스템
4. **Phase 4**: 소식지 발행 및 배송
5. **Phase 5**: 테스트 및 배포

### 공통 코드 활용 가이드

#### API 응답 처리
```java
// 성공 응답
return ResponseEntity.ok(ApiResponse.success(data));

// 에러 응답
return ResponseEntity.badRequest()
    .body(ApiResponse.error(ErrorCode.INVALID_INPUT, "잘못된 입력입니다."));
```

#### 예외 처리
```java
// 비즈니스 예외
throw new BusinessException(ErrorCode.FAMILY_MEMBER_LIMIT_EXCEEDED, 
    "가족 구성원은 최대 20명까지 가능합니다.");

// 리소스 없음
throw new ResourceNotFoundException("User", "id", userId);
```

#### 페이징 처리
```java
// Controller
@GetMapping("/posts")
public ResponseEntity<ApiResponse<PageResponse<PostDto>>> getPosts(
    @Valid PageRequest pageRequest) {
    PageResponse<PostDto> response = postService.getPosts(pageRequest);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

#### 날짜 처리
```java
// 다음 마감일 계산
LocalDate nextDeadline = DateUtils.getNextDeadline(2); // 둘째주 일요일

// 현재 월 문자열
String currentMonth = DateUtils.getCurrentMonthString(); // "2024-12"
```

#### 파일 업로드
```java
// 이미지 파일 저장
String filePath = FileUtils.saveFile(multipartFile, uploadDir, "images");

// 파일 유효성 검증
if (!FileUtils.isImageFile(fileName)) {
    throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, "이미지 파일만 업로드 가능합니다.");
}
```