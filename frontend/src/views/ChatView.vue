<template>
  <div class="popup-overlay">

    <!-- 맞춤형 추천 -->
    <div v-if="isCustomMode" class="popup-center dual">
      <div class="history-popup">
        <div class="popup-header">
          <h3>📋 대화 기록</h3>
        </div>
        <div class="history-content">
          <div v-if="Object.keys(groupedHistory).length === 0" class="no-history">
            <p>대화 기록이 없습니다.</p>
          </div>
          <div class="history-date-group" v-for="(group, date) in groupedHistory" :key="date">
            <div class="date-header">{{ formatDate(date) }}</div>
            <div class="history-item" v-for="(item, index) in group" :key="index" @click="handleHistoryClick(item)">
              <div class="history-time">{{ formatAmPm(item.createdTime) }}</div>
              <div class="history-message">{{ item.summarize }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="chatbot-popup">
        <div class="popup-header">
          <button @click="closeChatbotPopup" class="close-button">×</button>
        </div>
        <Chatbot />
      </div>
    </div>

    <!-- 기본 -->
    <div v-else class="popup-center">
      <div class="chatbot-popup">
        <div class="popup-header">
          <button @click="closeChatbotPopup" class="close-button">×</button>
        </div>
        <Chatbot />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Chatbot from '../components/Chatbot.vue'
import { useUserStore } from '../stores/user'
import { useChatStore } from '../stores/chat'
import '../styles/ChatView.css'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()
const isCustomMode = computed(() => route.query.mode === 'custom')

const groupedHistory = computed(() => {
  const profileLogMap = userStore.user?.profileLogMap
  if (!profileLogMap) return {}
  const sortedDates = Object.keys(profileLogMap).sort((a, b) => new Date(b) - new Date(a))
  const sortedGroups = {}
  sortedDates.forEach(date => {
    sortedGroups[date] = profileLogMap[date]
  })
  return sortedGroups
})

function formatDate(dateString) {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })
}

// Logan : 오전-오후 데이터 
function formatAmPm(createdTime) {
  let date = new Date(createdTime)

  const hour = date.getHours()
  return hour < 12 ? '오전' : '오후'
}

function closeChatbotPopup() {
  router.replace('/')
}

async function handleHistoryClick(item) {

  try {
    // 2. 서버 API 호출
    await chatStore.fetchHistoryDetail({ userId: item.userId, yyyyMMdd: item.yyyyMMdd, seq: item.seq })

  } catch (e) {
    chatStore.messages[chatStore.messages.length - 1] = {
      text: '상세 대화 불러오기 실패',
      isUser: false,
      status: 'done',
      timestamp: new Date()
    }
  }
}
</script>