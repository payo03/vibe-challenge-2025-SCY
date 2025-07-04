<template>
  <div class="chatbot-container">
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
      <div v-for="(msg, idx) in chatStore.messages" 
        :key="idx" 
        :class="['message', msg.isUser ? 'user-message' : 'bot-message']">
        <div class="message-bubble">
          <div class="message-avatar" v-if="!msg.isUser">🤖</div>
          <div class="message-content">
            <!-- pending 상태일 때 애니메이션 점 -->
            <p v-if="msg.status === 'pending'" class="typing-dots">…</p>
            
            <!-- 완료된 메시지일 때 마크다운 렌더링 -->
            <div v-else v-html="renderMarkdown(msg.text)"></div>
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
        <textarea
          v-model="input"
          class="message-input"
          placeholder="여행 관련 질문을 입력하세요..."
          @keydown.enter.prevent="handleEnter">
        </textarea>
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
import MarkdownIt from 'markdown-it'
import mila from 'markdown-it-link-attributes'
import '../styles/Chatbot.css'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
}).use(mila, {
  attrs: {
    target: '_blank',
    rel: 'noopener'
  }
});
const chatStore = useChatStore()
const input = ref('')
const messagesContainer = ref(null)

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
  await chatStore.sendMessage(message)
  scrollToBottom()
}

function clearChat() {
  if (confirm('대화 기록을 모두 지우시겠습니까?')) {
    chatStore.clearMessages()
  }
}

function renderMarkdown(text) {
  return md.render(text)
}

function handleEnter(event) {
  if (event.shiftKey) {
    // Shift + Enter: 줄바꿈 추가
    input.value += '\n'
  } else {
    // Enter: 메시지 전송
    sendMessage()
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