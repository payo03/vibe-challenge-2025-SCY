<template>
  <div class="chatbot-container">
    <!-- 옵션 입력 영역(숨김) -->
    <!--
    <div class="chat-options">
      <label>
        프롬프트:
        <input v-model="prompt" class="option-input" />
      </label>
      <label>
        모델:
        <input v-model="model" class="option-input" />
      </label>
      <label>
        Max Tokens:
        <input v-model.number="maxTokens" type="number" class="option-input" min="1" max="4096" />
      </label>
      <label>
        Temperature:
        <input v-model.number="temperature" type="number" step="0.01" min="0" max="2" class="option-input" />
      </label>
    </div>
    -->
    <!-- 헤더 -->
    <div class="chat-header">
      <div class="header-content">
        <div class="bot-avatar">🤖</div>
        <div class="header-text">
          <h3>여행 도우미</h3>
          <p>AI 여행 상담사</p>
        </div>
        <div class="header-actions">
          <button @click="clearChat" class="clear-button" title="대화 초기화">
            🔄
          </button>
          <router-link to="/" class="home-button" title="홈으로">
            🏠
          </router-link>
        </div>
      </div>
    </div>

    <!-- 메시지 영역 -->
    <div class="chat-messages" ref="messagesContainer">
      <div 
        v-for="(msg, idx) in chatStore.messages" 
        :key="idx" 
        :class="['message', msg.isUser ? 'user-message' : 'bot-message']"
      >
        <div class="message-bubble">
          <div class="message-avatar" v-if="!msg.isUser">🤖</div>
          <div class="message-content">
            <p>{{ msg.text }}</p>
          </div>
          <div class="message-avatar" v-if="msg.isUser">👤</div>
        </div>
        <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
      </div>
      
      <!-- 에러 메시지 -->
      <div v-if="chatStore.error" class="error-message">
        {{ chatStore.error }}
      </div>
    </div>

    <!-- 입력 영역 -->
    <div class="chat-input">
      <form @submit.prevent="sendMessage" class="input-form">
        <input 
          v-model="input" 
          placeholder="여행 관련 질문을 입력하세요..." 
          class="message-input"
          :disabled="chatStore.isLoading"
        />
        <button 
          type="submit" 
          class="send-button"
          :disabled="!input.trim() || chatStore.isLoading"
        >
          <span v-if="!chatStore.isLoading">📤</span>
          <span v-else class="loading-spinner">⏳</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, watch } from 'vue'
import { useChatStore } from '../stores/chat'

const chatStore = useChatStore()
const input = ref('')
const messagesContainer = ref(null)

// 기본 프롬프트
const defaultPrompt = '당신은 친근한 여행 도우미 챗봇입니다. 질문에 맞는 언어(한글/영어)로 자연스럽게 답변해 주세요.'

function formatTime(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleTimeString('ko-KR', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

async function sendMessage() {
  if (!input.value.trim() || chatStore.isLoading) return
  const message = input.value
  input.value = ''
  await chatStore.sendMessage(message, {
    prompt: defaultPrompt,
    model: 'gemini-2.0-flash',
    maxTokens: 256,
    temperature: 0.7
  })
  scrollToBottom()
}

function clearChat() {
  if (confirm('대화 기록을 모두 지우시겠습니까?')) {
    chatStore.clearMessages()
  }
}

// 메시지가 추가될 때마다 스크롤
watch(() => chatStore.messages.length, () => {
  scrollToBottom()
})

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
/* 기본 컨테이너 - 모바일 스타일 */
.chatbot-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 헤더 스타일 */
.chat-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 1rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.bot-avatar {
  font-size: 2rem;
  background: linear-gradient(45deg, #667eea, #764ba2);
  border-radius: 50%;
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.header-text {
  flex: 1;
}

.header-text h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.header-text p {
  margin: 0;
  font-size: 0.85rem;
  color: #666;
}

.header-actions {
  display: flex;
  gap: 0.5rem;
}

.clear-button, .home-button {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  transition: background-color 0.2s;
  text-decoration: none;
  color: #666;
}

.clear-button:hover, .home-button:hover {
  background: rgba(0, 0, 0, 0.1);
}

/* 메시지 영역 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.user-message {
  align-items: flex-end;
}

.bot-message {
  align-items: flex-start;
}

.message-bubble {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  max-width: 80%;
}

.user-message .message-bubble {
  flex-direction: row-reverse;
}

.message-avatar {
  font-size: 1.5rem;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  flex-shrink: 0;
}

.bot-message .message-avatar {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
}

.user-message .message-avatar {
  background: #007AFF;
  color: white;
}

.message-content {
  background: white;
  padding: 0.75rem 1rem;
  border-radius: 1.2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  word-wrap: break-word;
}

.user-message .message-content {
  background: #007AFF;
  color: white;
}

.bot-message .message-content {
  background: white;
  color: #333;
}

.message-content p {
  margin: 0;
  line-height: 1.4;
  white-space: pre-line;
}

.message-time {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.7);
  padding: 0 0.5rem;
}

.error-message {
  background: rgba(255, 0, 0, 0.1);
  color: #ff4444;
  padding: 0.75rem;
  border-radius: 0.5rem;
  border: 1px solid rgba(255, 0, 0, 0.3);
  text-align: center;
}

/* 입력 영역 */
.chat-input {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 1rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.input-form {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.message-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid #e1e5e9;
  border-radius: 1.5rem;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.2s;
  background: white;
}

.message-input:focus {
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.message-input:disabled {
  background: #f5f5f5;
  color: #999;
}

.send-button {
  width: 3rem;
  height: 3rem;
  border: none;
  border-radius: 50%;
  background: #007AFF;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-button:hover:not(:disabled) {
  background: #0056CC;
  transform: scale(1.05);
}

.send-button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 스크롤바 스타일 */
.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* 태블릿 이상 (768px 이상) - PC용 레이아웃 */
@media (min-width: 768px) {
  .chatbot-container {
    max-width: 800px;
    height: 80vh;
    margin: 2rem auto;
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    overflow: hidden;
  }
  
  .chat-header {
    border-radius: 20px 20px 0 0;
  }
  
  .message-bubble {
    max-width: 70%;
  }
  
  .header-text h3 {
    font-size: 1.3rem;
  }
  
  .header-text p {
    font-size: 0.9rem;
  }
  
  .bot-avatar {
    font-size: 2.5rem;
    width: 3.5rem;
    height: 3.5rem;
  }
}

/* 데스크톱 이상 (1024px 이상) - 더 큰 PC용 레이아웃 */
@media (min-width: 1024px) {
  .chatbot-container {
    max-width: 900px;
    height: 85vh;
    margin: 1.5rem auto;
  }
  
  .message-bubble {
    max-width: 65%;
  }
  
  .chat-messages {
    padding: 1.5rem;
  }
  
  .chat-input {
    padding: 1.5rem;
  }
}

/* 큰 데스크톱 (1440px 이상) */
@media (min-width: 1440px) {
  .chatbot-container {
    max-width: 1000px;
    height: 90vh;
  }
  
  .message-bubble {
    max-width: 60%;
  }
}

/* 모바일 최적화 (768px 미만) */
@media (max-width: 767px) {
  .chatbot-container {
    height: 100vh;
    border-radius: 0;
    margin: 0;
  }
  
  .message-bubble {
    max-width: 90%;
  }
  
  .header-text h3 {
    font-size: 1rem;
  }
  
  .header-text p {
    font-size: 0.8rem;
  }
  
  .chat-messages {
    padding: 0.75rem;
  }
  
  .chat-input {
    padding: 0.75rem;
  }
}
</style> 