# 🗺️ 여행 도우미 챗봇 (Travel Assistant Chatbot)

**생성형 AI를 활용한 여행 계획 도우미 챗봇**  
사용자의 요청에 맞춘 여행지 추천과 일정 계획을 대화 형식으로 제공하는 웹 애플리케이션입니다.

---

## 🤖 프로젝트 설정 도우미 (Project Setup Assistant)
이 프로젝트의 **Spring Boot 백엔드와 Vue.js 프론트엔드의 초기 구조 설정**, 주요 코드 생성, 그리고 OpenAI(ChatGPT) 및 Ollama에서 Gemini API로의 전환 과정은 AI 코딩 어시스턴트인 **Cursor(Gemini)**의 도움을 받아 진행되었습니다. 이를 통해 초기 개발 시간을 단축하고, 프로젝트 구조의 일관성을 유지할 수 있었습니다.

---

## ✨ 주요 기능

- 🤖 **AI 기반 여행 상담**: Google Gemini API의 **gemini-2.0-flash** 모델을 활용한 자연스러운 한국어/영어 대화
- 🗺️ **맞춤형 여행지 추천**: 예산, 계절, 선호도에 따른 개인화된 추천
- 📅 **스마트 일정 계획**: 최적의 여행 일정 자동 생성
- 💬 **대화형 인터페이스**: 직관적이고 친근한 챗봇 UI
- 📱 **반응형 디자인**: 모바일/데스크톱 모든 기기에서 최적화
- 🔄 **실시간 대화**: Vue Router와 Pinia를 활용한 상태 관리
- 🌤️ **날씨 정보 연동**: 여행지 날씨 정보 제공
- 📊 **대화 히스토리 관리**: 사용자별 대화 기록 저장 및 관리

---

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.5.3** - REST API 서버 및 웹 애플리케이션 프레임워크
- **Java 21** - 백엔드 프로그래밍 언어
- **Google Gemini API** - AI 모델 (gemini-2.0-flash)
- **Gradle** - 빌드 자동화 도구
- **SQLite** - 경량 데이터베이스
- **MyBatis** - SQL 매핑 프레임워크
- **Spring Web** - RESTful API 구현
- **Spring Boot DevTools** - 개발 편의 도구

### Frontend
- **Vue.js 3** - 반응형 프론트엔드 프레임워크
- **Vue Router 4** - 클라이언트 사이드 라우팅
- **Pinia** - 상태 관리 라이브러리
- **Vite** - 빠른 빌드 도구 및 개발 서버
- **Axios** - HTTP 클라이언트 라이브러리
- **CSS3** - 스타일링 및 반응형 디자인

### 개발 도구
- **Cursor (Gemini)** - AI 코딩 어시스턴트
- **Git** - 버전 관리
- **Node.js** - JavaScript 런타임 환경

---

## 🤖 AI 활용 내역

### 1. 프로젝트 전체 구조 구성
- **Vue.js + Spring Boot 아키텍처 설계**: AI를 활용하여 프론트엔드와 백엔드의 전체적인 구조를 설계하고 구현
- **컴포넌트 구조 설계**: Vue 컴포넌트들의 계층 구조와 데이터 흐름 설계
- **API 엔드포인트 설계**: RESTful API 구조 및 데이터 전송 객체(DTO) 설계
- **데이터베이스 스키마 설계**: 사용자 정보, 대화 기록 등을 위한 테이블 구조 설계

### 2. AI 모델 선택 및 전환 과정
- **초기**: OpenAI ChatGPT API 사용
- **중간**: Ollama 로컬 모델 사용 (llama3)
- **최종 선택**: Google Gemini API (gemini-2.0-flash)

### 3. 데이터 Parser 처리
- **AI 응답 파싱**: Gemini API의 응답을 구조화된 데이터로 변환
- **JSON 파싱**: AI 응답의 JSON 형태 데이터를 Java 객체로 매핑
- **에러 처리**: API 응답 실패 시 대체 응답 생성 및 로깅
- **응답 포맷팅**: AI 응답을 사용자 친화적인 형태로 가공

### 4. Gemini API 설정 및 활용
- **모델**: gemini-2.0-flash (빠른 응답, 한국어/영어 지원)
- **장점**: 
  - 무료 사용량 제공
  - 빠른 응답 속도
  - 안정적인 서비스
  - 한국어/영어 자연스러운 지원

### 5. AI 프롬프트 엔지니어링
```java
// 여행 전문가 역할 부여 및 응답 형식 지정
String prompt = "당신은 친근한 여행 도우미 챗봇입니다. 질문에 사용된 언어로 답변해 주세요!";
```

### 6. AI 응답 처리 및 최적화
- **실시간 응답**: Gemini API 호출 및 응답 처리
- **에러 핸들링**: API 연결 실패 시 대체 응답
- **로깅**: API 호출 로그 기록 및 모니터링
- **응답 캐싱**: 동일한 질문에 대한 응답 최적화

### 7. 코드 생성 및 리팩토링
- **컨트롤러 클래스 생성**: REST API 엔드포인트 구현
- **서비스 클래스 구현**: 비즈니스 로직 및 AI API 연동
- **DTO 클래스 설계**: 데이터 전송 객체 구조 설계
- **매퍼 클래스 생성**: MyBatis를 활용한 데이터베이스 연동
- **Vue 컴포넌트 생성**: 프론트엔드 UI 컴포넌트 구현
- **상태 관리 구현**: Pinia를 활용한 전역 상태 관리

---

## 🚀 프로젝트 실행 방법

### 사전 요구사항
- **Java 21** 이상 설치
- **Node.js 18** 이상 설치
- **Google Gemini API 키** 발급
- **OpenCage(위도-경도 추적) API 키** 발급
- **AccuWeather(날씨 추적) API 키** 발급

### 1. Gemini API 키 설정

#### Google AI Studio에서 API 키 발급
1. [Google AI Studio](https://makersuite.google.com/app/apikey) 접속
2. Google 계정으로 로그인
3. "Create API Key" 클릭
4. API 키 복사

#### application.properties 설정
```properties
# backend/src/main/resources/application.properties
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
spring.datasource.url=jdbc:sqlite:database/[YOUR_DB]
spring.datasource.driver-class-name=org.sqlite.JDBC
```

### 2. 백엔드 실행

```bash
# 프로젝트 루트 디렉토리에서
cd backend

# 윈도우 환경
gradlew.bat bootRun

# macOS/Linux 환경
./gradlew bootRun

# 백엔드 서버는 기본적으로 http://localhost:8080 에서 실행됩니다
```

### 3. 프론트엔드 개발 서버 실행

```bash
# 새 터미널에서
cd frontend

# 의존성 설치 (최초 1회)
npm install

# 개발 서버 실행
npm run dev

# 프론트엔드는 기본적으로 http://localhost:5173 에서 실행됩니다
```

### 4. 프로덕션 빌드

```bash
# Vue 빌드 및 Spring Boot 실행 (자동화됨)
cd backend
gradlew bootRun

# 자동으로 frontend 빌드 후 static 파일 복사
# 프로덕션 환경에서는 http://localhost:8080 에서 전체 애플리케이션 접근 가능
```

### 5. 데이터베이스 초기화 (선택사항)

```bash
# SQLite 데이터베이스 파일이 자동으로 생성됩니다
# backend/database/SQLite.db
```

#### 데이터베이스 스키마
```sql
-- "user" definition
CREATE TABLE user (
    id TEXT PRIMARY KEY,
    password TEXT NOT NULL,
    name TEXT NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- user_profile_log definition
CREATE TABLE user_profile_log (
    yyyyMMdd DATE,
    seq INTEGER DEFAULT 1,
    user_id TEXT NOT NULL,
    trait TEXT,
    age_group TEXT,
    summarize TEXT,
    created_time DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, '+9 hours')),
    destination TEXT,
    travel_date TEXT,
    CONSTRAINT PK_user_profile_log PRIMARY KEY (yyyyMMdd, seq, user_id),
    CONSTRAINT FK_user_profile_log_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);
```

---

## 🎯 사용 예시

### 일정 계획
```
사용자: "부산 2박 3일 여행 일정 만들어줘"
AI: "부산 2박 3일 여행 일정을 제안드립니다...
     
     📅 1일차: 해운대 해수욕장 → 광안대교 야경
     📅 2일차: 감천문화마을 → 국제시장 → 용두산공원
     📅 3일차: 태종대 → 해동용궁사..."
```

---

## 📝 개발 가이드

### 새로운 기능 추가
1. **백엔드**: Service 클래스에 비즈니스 로직 구현
2. **프론트엔드**: Vue 컴포넌트 생성 및 API 연동
3. **AI 프롬프트**: Gemini API 프롬프트 엔지니어링

### 코드 구조
```
backend/
├── src/main/java/com/example/demo/
│   ├── controller/     # REST API 컨트롤러
│   ├── service/        # 비즈니스 로직
│   ├── dto/           # 데이터 전송 객체
│   ├── mapper/        # MyBatis 매퍼
│   └── config/        # 설정 클래스

frontend/
├── src/
│   ├── components/    # Vue 컴포넌트
│   ├── views/         # 페이지 뷰
│   ├── stores/        # Pinia 상태 관리
│   └── router/        # 라우터 설정
```

---

## 💰 비용 정보
### Gemini API 요금 (Free Tier)
`gemini-2.0-flash` 모델은 현재 무료로 사용할 수 있으며, 다음과 같은 넉넉한 사용량 한도를 제공합니다.

| 항목 | 한도 |
|---|---|
| **분당 요청 (RPM)** | 15 requests/min |
| **일일 요청 (RPD)** | 200 requests/day |
| **분당 토큰 처리량 (TPM)** | 1,000,000 tokens/min |

더 자세한 정보는 [Gemini API 요금 정책](https://ai.google.dev/gemini-api/docs/pricing) 및 [사용량 한도](https://ai.google.dev/gemini-api/docs/rate-limits)를 참고하세요.

---

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

---

## 🔗 관련 링크

- [Google AI Studio](https://makersuite.google.com/app/apikey)
- [Gemini API 문서](https://ai.google.dev/gemini-api/docs)
- [OpenCage Geocoding API](https://opencagedata.com/api)
- [AccuWeather API](https://developer.accuweather.com/)
- [Vue.js 공식 문서](https://vuejs.org/)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)

---

**즐거운 여행 계획 되세요! ✈️** 
