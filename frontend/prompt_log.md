# prompt_log.md

## 기록

- **요청:**
  로그인창의 로그인, 취소 버튼 / 회원가입창의 회원가입, 취소 버튼의 사이즈를 작게해줘
- **조치:**
  AuthModal.vue에서 버튼 그룹(.button-group)의 버튼 크기를 더 작게 조정하고, 기본 배경색을 연보라/연회색 계열로 적용하여 세련된 스타일로 개선함. 버튼의 padding과 font-size를 줄여 작고 모던하게 변경함.

---

- **요청:**
  Chat 시작하기 창에서 하단이 잘려. CSS 수정해줘
- **조치:**
  Chatbot.vue에서 .chatbot-container, .chat-messages, .message-bubble 등의 height, max-width, margin, font-size 등 CSS를 조정하여 하단이 짤리지 않고, 메시지 영역이 더 잘 보이도록 개선함. (height: 85vh, message-bubble max-width 축소, 폰트 크기 축소 등 적용)

---

- **요청:**
  Vue파일들의 CSS를 컴포넌트별로 분리해줘
- **조치:**
  frontend/src/styles/ 폴더를 만들고, Chatbot.vue, AuthModal.vue 등 주요 컴포넌트의 스타일을 각각 Chatbot.css, AuthModal.css 등 별도 파일로 분리하여 관리할 수 있도록 구조와 적용 방법을 안내함. 실제로도 코드 적용을 진행함.

---


- **요청:**
  로그인시, 로그아웃시 LocalStorage를 사용하고 로그인유저에 대한 인증을 유지-삭제해줘
- **조치:**
  로그인 성공 시 사용자 정보를 localStorage에 저장해 새로고침 후에도 유지
  로그아웃 시 서버에 호출하고, localStorage에서 삭제
  앱 초기 실행 시 restoreSession()으로 상태 복원

---


- **요청:**
  로그인, 로그아웃시 대화내용을 초기화해줘야해
- **조치:**
  로그인할 때 handleSummary 플래그를 true로 세팅해서 로그인 신호를 발생
  chatStore에서 이 신호를 감지해 대화 초기화 함수 호출 후 handleSummary 플래그를 false로 초기화
  앱 시작 시 세션 복원 시에도 로그인 신호 발생해서 자동 초기화 가능
  ChatBot.vue에서는 별도 조치 없이 messages가 바뀌는 대로 렌더링됨

---



- **요청:**
  사이드봐의 대화기록의 높이창을 고정높이로 지정해주고(대화하기 팝업창의 크기) 세로 스크롤바를 만들어줘
- **조치:**
  이제 대화기록 팝업의 높이가 챗봇 팝업과 정확히 동일하게 고정되고,
  세로 스크롤바가 생기며,
  카드 스타일과 "기록 없음" 안내문구도 더 예쁘게 개선되었습니다!

---
