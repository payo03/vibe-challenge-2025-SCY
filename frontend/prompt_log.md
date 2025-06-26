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
