# prompt_log.md

## 기록

- **요청:**
  1. 회원가입/로그인 DTO를 UserRequest 한 개로 통합 (id, name, password)
  2. XML에서 회원가입 시 동일한 id가 있으면 Validation 처리(이미 가입된 id 존재 안내)
  3. 로그인 쿼리는 id, password로 user 테이블을 조회해 id, name을 반환. 이 값을 프론트에서 사용 예정
- **조치:**
  1. UserRegisterRequest, UserLoginRequest → UserRequest로 통합 (id, name, password)
  2. UserMapper.xml에 id 중복 체크 쿼리 및 회원가입 로직 분리, 중복 시 예외 처리 구조 안내
  3. 로그인 쿼리를 id, password로 조회해 id, name을 반환하도록 수정. UserResponse DTO 추가(프론트 전달용)

- **요청:**
  회원가입에서 입력한 input값을 SQLite Table에 Insert해야 함. DTO를 만들어 값 받게 하고, 기존 Request DTO에서 필요없는 필드를 정리해달라.
- **조치:**
  1. 회원가입용 UserRegisterRequest DTO를 새로 생성하여, 이름/아이디/비밀번호만 받도록 설계함.
  2. 기존 ChatRequest 등 Request DTO에서 회원가입과 무관한 불필요한 필드를 제거함.
  3. 이후 Service/Controller에서 해당 DTO를 받아 DB에 Insert하는 로직을 연동할 예정임.

- **요청:**
  MyBatis를 사용할 수 있도록 의존성 추가하고, 설정 및 샘플 파일을 만들어달라.
- **조치:**
  1. backend/build.gradle에 mybatis-spring-boot-starter, sqlite-jdbc 의존성 추가.
  2. application.properties에 MyBatis 및 SQLite 설정 예시 추가.
  3. config/mapper/UserMapper.java, dto/UserRegisterRequest.java, dto/UserLoginRequest.java, mapper/UserMapper.xml 등 샘플 파일 생성 및 구조 안내.

- **요청:**
  UserMapper.java에서 DML을 직접 하지 말고, Repository 폴더를 만들어 XML과 소통하는 함수(메서드)를 이곳에 생성. 실제 DML은 XML에서 처리하도록 구조를 분리해달라.
- **조치:**
  1. backend/src/main/java/com/example/demo/repository/UserRepository.java 생성.
  2. UserRepository에서 UserMapper를 주입받아, 회원가입/로그인 등 DB 연동 함수 구현.
  3. UserMapper.java는 인터페이스 선언만, 실제 쿼리는 mapper/UserMapper.xml에서 처리하도록 분리.

- **요청:**
  회원가입시 프론트(Vue)에서 호출한 URL 기반으로 Spring에서 DB DML(회원가입) 작업을 하도록 구조를 변경해달라. (현재 404 에러 발생)
- **조치:**
  1. 프론트엔드(Vue)에서 회원가입 폼 제출 시 `/api/auth/register`로 POST 요청을 보내도록 유지(RESTful 구조).
  2. 백엔드(Spring)에서 `/api/auth/register`를 처리하는 Controller/Service/Repository를 구현하여, 실제 DB에 회원정보를 insert(DML)하도록 구현.
  3. 404 에러는 Spring Controller에 해당 엔드포인트가 없어서 발생하므로, 엔드포인트를 추가해 해결.

- **요청:**
  DB 접근 시 한글이 깨지는 현상이 발생하고, SQLException이 발생함. 인코딩 관련 조치가 필요함.
- **조치:**
  1. DB 파일 경로, 존재, 권한을 우선적으로 점검함.
  2. JVM 실행 옵션에 `-Dfile.encoding=UTF-8`을 추가하여 한글 인코딩 문제를 해결함.
  3. Windows 터미널에서 `chcp 65001` 명령어로 코드페이지를 UTF-8로 변경하도록 안내함.
  4. IDE, 터미널, 로그 파일 모두 UTF-8 인코딩을 사용하도록 권장함. 

- **요청:**
  DB는 정상적으로 읽히지만, 회원가입 시 중복 아이디일 때 한글 메시지가 깨져서 Vue로 전달되고, 400 에러가 발생함. 프론트(Vue)에서 "가입한 이력이 있습니다"라는 메시지가 제대로 보여야 함.
- **조치:**
  1. Spring Boot 컨트롤러에서 한글 메시지 반환 시 UTF-8 인코딩을 명확히 지정하도록 개선함.
  2. 400(BAD_REQUEST) 대신 200 OK로 응답하고, JSON 구조로 상태(success)/메시지(message)를 전달하도록 API 응답 구조를 변경함.
  3. 프론트(Vue)에서는 response.data.success, response.data.message로 결과를 처리할 수 있도록 안내함.

- **요청:**
  Map 대신 Response DTO 객체(UserResponse)를 만들어서 Vue에 전달하고 싶음. 또한, 로그에 Writing [한글깨짐] 현상이 나타나는데 실제 문제는 없는지 점검 필요.
- **조치:**
  1. UserResponse의 id, name은 DB에서 조회한 값을, success, message는 컨트롤러에서 추가로 세팅하는 구조로 명확히 정의함.
  2. AuthController에서 UserResponse 객체 생성 후 setter로 값 세팅하는 방식으로 통일하고, 생성자 직접 호출을 모두 제거함.
  3. register/login 등 공통된 응답 생성/세팅 로직을 리팩토링하여 코드 중복을 최소화할 수 있도록 개선함.

- **요청:**
  1. Spring에서 로그인 실패 시에도 message를 추가해달라.
  2. Vue에서 회원가입과 로그인 에러 표기란에 Response의 값을 보여주도록 개선해달라.
- **조치:**
  1. Spring AuthController의 로그인 API에서 실패 시에도 UserResponse(success, message) 형태로 일관성 있게 응답하도록 수정함.
  2. Vue 회원가입/로그인 컴포넌트에서 에러 메시지 표기란에 Response의 message 값을 보여주도록 로직을 개선함.

- **요청:**
  1. 자동 로그인 없이, 회원가입 성공 시에는 초록색 메시지만 표기하고 모달은 닫지 않게 해달라.
  2. 로그인 성공 시에는 챗봇 시작(모달 닫기/리다이렉트), 실패 시에는 빨간색 에러 메시지를 표기해달라.
- **조치:**
  1. Vue AuthModal 컴포넌트에서 회원가입 성공 시 success(초록색) 메시지만 표기하고, 자동 로그인 및 모달 닫기 로직을 제거함.
  2. 로그인 성공 시에만 모달을 닫고 챗봇 시작, 실패 시에는 error(빨간색) 메시지를 표기하도록 로직을 분리함.
  3. 템플릿에 성공/실패 메시지 영역을 그대로 유지하여, 각 상황에 맞는 메시지가 명확히 안내되도록 개선함.

- **요청:**
  1. Vue Response에서 id, name은 DB값이고, success, message는 Vue에서 필요한 '추가'값임을 명확히 해달라.
  2. DTO(UserResponse)에 값을 생성자로 주지 말고 setter를 이용하도록 리팩토링해달라.
  3. 공통된 부분만 리팩토링해서 코드 중복을 줄여달라.
- **조치:**
  1. UserResponse의 id, name은 DB에서 조회한 값을, success, message는 컨트롤러에서 추가로 세팅하는 구조로 명확히 정의함.
  2. AuthController에서 UserResponse 객체 생성 후 setter로 값 세팅하는 방식으로 통일하고, 생성자 직접 호출을 모두 제거함.
  3. register/login 등 공통된 응답 생성/세팅 로직을 리팩토링하여 코드 중복을 최소화할 수 있도록 개선함.

- **요청:**
  UserResponse에 필드가 추가될 때 확장성이 떨어지므로,
  1. return에서 즉각 response 객체를 생성해 반환하지 말고,
  2. createUserResponse처럼 정해진 파라미터 방식이 아닌 더 유연한 방안을 찾아달라.
- **조치/고려:**
  1. UserResponse의 필드가 변경/확장될 때마다 createUserResponse 파라미터와 호출부를 모두 수정해야 하는 단점이 있음.
  2. Builder 패턴, Map 기반 동적 생성, 또는 BeanUtils.copyProperties 등 다양한 확장성 높은 방법을 고려할 수 있음.
  3. 추후 확장성/유지보수성을 위해 Builder 패턴 도입, 또는 DTO를 동적으로 생성/세팅하는 구조로 리팩토링하는 방안을 검토할 예정임.

- **요청:**
  UserResponse에 필드가 추가될 때마다 createUserResponse 함수와 모든 호출부를 수정해야 하는 구조의 한계를 해결하고, 컨트롤러에서 builder 패턴을 직접 사용하도록 리팩토링해달라.
- **조치:**
  1. createUserResponse 등 중간 함수는 제거하고, 컨트롤러에서 UserResponse.builder()를 직접 사용해 필요한 필드만 세팅하도록 리팩토링함.
  2. UserResponse에 필드가 추가/변경되어도 각 응답에서 builder에만 추가하면 되므로, 확장성과 유지보수성이 크게 향상됨.

- **요청:**
  대화 흐름 및 문맥 유지를 위해, 사용자가 추가 질문이나 세부 변경 요청을 할 경우 이전 대화 내용을 기억하고 반영해야 한다. 예시: 첫 답변으로 파리 일정을 추천한 뒤 "2일로 줄이면?" 질문 시, 앞서 제시한 3일 일정을 참고해 2일 분량으로 조정된 일정을 제시. 이때도 앞서 대화한 맥락을 유지하여 일관성 있는 답변을 해야 한다.
  이를 위해 DB에 user_profile_log 테이블을 사용해 데이터를 저장할 계획이며, 로그인하지 않은 유저는 insert하지 않는다.

  - Case. 로그인한 User가 1분 이상 input값이 없을 시
    1. user_id를 기반으로 user_profile_log의 데이터 GET
    2. (결과 존재 시) trait, last_answer, age_group의 데이터를 기반으로 API 호출
       - 단답으로 답해야 함. (예: "나는 예전엔 [2번.last_answer의 답변을 받았고, 너가 나를 2번.trait와 2번.age_group으로 분석했어] 오늘한 대화까지 포함해서 나의 성향과 나이대를 다시 추측 및 분석해줘 (ex, 성향: 외향적 / 나이: 30대)")
    3. (결과 미존재 시) 단답으로 답해야 함. (예: "나의 성향과 나이대를 다시 추측 및 분석해줘 (ex, 성향: 외향적 / 나이: 30대)")
    4. Gemini의 마지막 호출의 답변 + 2번의 RETURN 값을 table에 insert

- **설계/조치:**
  1. user_profile_log 테이블 설계 및 로그인한 유저만 insert하도록 로직 분기 필요
  2. 1분 이상 입력 없을 때 user_id로 DB 조회, 결과에 따라 trait/last_answer/age_group 기반 분석 프롬프트 생성
  3. Gemini API 호출 후, 마지막 답변과 분석 결과를 user_profile_log에 저장하는 흐름 설계

- **요청:**
  Gemini API의 최신 request 양식(systemInstruction, contents 등)을 ChatbotService에 반영해달라. (curl 예시 참고)
- **조치:**
  1. ChatbotService에서 Gemini API 호출 시, systemInstruction과 contents 구조를 curl 예시와 동일하게 맞추도록 requestBody 생성 로직을 수정함.

- **요청:**
  세션 변수 관리를 Map<String, Map<String, Object>> 구조로, 첫번째 Map의 Key는 userId, 두번째 Map은 해당 유저의 데이터로 관리해야 한다. (여러 명이 동시에 사용할 수 있기 때문)
- **조치/고려:**
  1. 기존에는 HttpSession에 단일 키로 값을 저장했으나, 여러 유저가 동시에 사용할 경우 데이터가 섞일 수 있음.
  2. Map<String, Map<String, Object>> 구조로 userId별로 독립적인 세션 데이터 관리가 가능해짐.
  3. 세션 내에서 userId별로 데이터 분리/초기화/조회가 용이해져, 멀티유저 환경에서 안전하게 동작할 수 있음.

- **요청:**
  ChatbotService의 generateResponse 함수가 너무 많은 책임을 지고 있으므로, 아래와 같이 모듈화 및 리팩토링 요청
  1. Gemini API 요청 본문 생성 로직 분리
  2. Gemini API 호출 로직 분리

- **조치:**
  1. `GeminiHelper` 유틸 클래스를 생성하여, `buildRequestBody(prompt, message)` 메서드로 요청 본문 생성을 모듈화함.
  2. `callGeminiApi(restTemplate, url, requestBody)` 메서드를 만들어 API 호출을 별도로 분리함.
  3. `generateResponse` 함수에서는 로깅, 로직 흐름 유지, 예외 처리 등 핵심 로직만 담당하도록 정리함.
  4. `GeminiHelper`는 테스트 가능하고, 재사용 가능한 구조로 설계됨.

- **요청:**
  user_profile_log. yyyyMMdd 기준으로 seq의 최댓값을 자주 조회하기위한 DB Index DDL요청

- **조치:**
  1. `yyyyMMdd`는 조회 조건 (WHERE) 으로 자주 사용되므로 인덱스의 첫 번째 컬럼으로 설정
  2. `seq`는 정렬 (ORDER BY seq DESC) 또는 최댓값 조회 (MAX(seq)) 에 쓰이므로 두 번째
  3. `DESC` 지정은 옵티마이저가 내림차순 정렬된 인덱스를 바로 이용할 수 있도록 도와줍니다 (RDBMS에 따라 영향 있음, 아래 참고)

- **요청:**
  WeatherAPIService. 63번 Line. if절 구문 주석보고 로직 구성. 현재일 + 5일까지 add하기

- **조치:**
  LocalDate를 기준으로 데이터 ADD

- **요청:**
  날씨응답에 대해 이모티콘과 markdown 적용해줘

- **조치:**
  적절한 이모티콘 및 markdown문법 적용