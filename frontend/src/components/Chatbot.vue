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
import '../styles/Chatbot.css'

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