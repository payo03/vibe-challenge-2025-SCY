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

---

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.5.3** - REST API 서버
- **Java 21** - 백엔드 언어
- **Google Gemini API** - AI 모델 (gemini-2.0-flash)
- **Gradle** - 빌드 도구

### Frontend
- **Vue.js 3** - 프론트엔드 프레임워크
- **Vue Router** - 클라이언트 사이드 라우팅
- **Pinia** - 상태 관리
- **Vite** - 빌드 도구
- **Axios** - HTTP 클라이언트

---

## 🤖 AI 활용 내역

### 1. AI 모델 선택 과정
- **초기**: OpenAI ChatGPT API 사용
- **중간**: Ollama 로컬 모델 사용 (llama3)
- **최종 선택**: Google Gemini API (gemini-2.0-flash)

### 2. Gemini API 설정 및 활용
- **모델**: gemini-2.0-flash (빠른 응답, 한국어/영어 지원)
- **장점**: 
  - 무료 사용량 제공 (월 15회)
  - 빠른 응답 속도
  - 안정적인 서비스
  - 한국어/영어 자연스러운 지원

### 3. AI 프롬프트 엔지니어링
```java
// 여행 전문가 역할 부여
String prompt = "당신은 친근한 여행 도우미 챗봇입니다. " +
               "질문에 맞는 언어(한글/영어)로 자연스럽게 답변해 주세요.";
```

### 4. AI 응답 처리
- **실시간 응답**: Gemini API 호출 및 응답 처리
- **에러 핸들링**: API 연결 실패 시 대체 응답
- **로깅**: API 호출 로그 기록

---

## 🚀 시작하기

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
```

### 2. 백엔드 실행

```bash
cd backend
# 윈도우
gradlew.bat bootRun
# macOS/Linux
./gradlew bootRun
```

### 3. 프론트엔드 개발

```bash
cd frontend
npm install
npm run dev
```

### 4. 프로덕션 빌드

```bash
# Vue 빌드 및 Spring Boot 실행 (자동화됨)
cd backend
gradlew bootRun
# 자동으로 frontend 빌드 후 static 파일 복사
```

---

## 📁 프로젝트 구조

```
프로젝트/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/example/demo/
│   │       ├── controller/     # REST API 컨트롤러
│   │       │   ├── ChatbotController.java
│   │       │   └── HomeController.java
│   │       ├── service/        # 비즈니스 로직
│   │       │   └── ChatbotService.java
│   │       └── dto/            # 데이터 전송 객체
│   │           ├── ChatRequest.java
│   │           └── ChatResponse.java
│   └── src/main/resources/
│       └── static/             # Vue 빌드 결과물
├── frontend/                   # Vue.js 프론트엔드
│   ├── src/
│   │   ├── components/         # Vue 컴포넌트
│   │   │   └── Chatbot.vue     # 메인 챗봇 컴포넌트
│   │   ├── views/              # 페이지 뷰
│   │   │   ├── HomeView.vue    # 홈 페이지
│   │   │   └── ChatView.vue    # 챗봇 페이지
│   │   ├── stores/             # Pinia 상태 관리
│   │   │   └── chat.js         # 챗봇 상태 관리
│   │   └── router/             # Vue Router
│   │       └── index.js        # 라우터 설정
│   └── dist/                   # 빌드 결과물
└── database/                   # SQLite 데이터베이스
    └── sqlite
```

---

## 🔧 API 엔드포인트

### 챗봇 API
- `POST /api/chat` - 사용자 메시지 전송 및 AI 응답
- `GET /api/chat/health` - API 상태 확인

---

## 🎯 사용 예시

### 여행지 추천
```
사용자: "예산 100만원으로 가을에 갈만한 국내 여행지 추천해줘"
AI: "가을 여행으로 추천드리는 곳들입니다...
     🍁 강원도 양양 - 설악산 단풍과 해변
     🍂 경주 - 역사와 단풍의 조화
     🍁 부산 - 해운대와 감천문화마을..."
```

### 일정 계획
```
사용자: "부산 2박 3일 여행 일정 만들어줘"
AI: "부산 2박 3일 여행 일정을 제안드립니다...
     
     📅 1일차: 해운대 해수욕장 → 광안대교 야경
     📅 2일차: 감천문화마을 → 국제시장 → 용두산공원
     📅 3일차: 태종대 → 해동용궁사..."
```

---

## 🔐 환경 변수

```properties
# Gemini API 설정
gemini.api.key=YOUR_GEMINI_API_KEY_HERE

# 서버 설정
server.port=8080
spring.profiles.active=dev

# 로깅 설정
logging.level.com.example.demo=DEBUG
```

---

## 📝 개발 가이드

### 새로운 기능 추가
1. **백엔드**: Service 클래스에 비즈니스 로직 구현
2. **프론트엔드**: Vue 컴포넌트 생성 및 API 연동
3. **AI 프롬프트**: Gemini API 프롬프트 엔지니어링

### AI 모델 변경
- Gemini 모델을 변경하려면 ChatbotService.java의 URL을 수정하세요.
- 지원 모델: gemini-2.0-flash, gemini-1.5-flash, gemini-pro 등

### 테스트
```bash
# 백엔드 테스트
cd backend
gradlew test

# 프론트엔드 테스트
cd frontend
npm run test
```

---

## 🔧 문제 해결

### Gemini API 연결 문제
```bash
# API 키 확인
# application.properties에서 gemini.api.key 값 확인

# API 키 유효성 테스트
curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=YOUR_API_KEY" \
  -H 'Content-Type: application/json' \
  -X POST \
  -d '{"contents":[{"parts":[{"text":"Hello"}]}]}'
```

### 빌드 문제
```bash
# 프론트엔드 의존성 재설치
cd frontend
rm -rf node_modules
npm install

# 백엔드 캐시 클리어
cd backend
gradlew clean
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

**즐거운 여행 계획 되세요! ✈️** 