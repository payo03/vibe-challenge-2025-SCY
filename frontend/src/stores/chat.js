import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import { useUserStore } from './user'
import axios from 'axios'
// 상수 import
import {
  INIT_BOT_MESSAGE,
  ERROR_SEND_MESSAGE,
  STATUS_PENDING,
  STATUS_DONE
} from '../constants/constant'

export const useChatStore = defineStore('chat', () => {
  const isLoading = ref(false)
  const error = ref(null)
  const messages = ref([
    { 
      text: INIT_BOT_MESSAGE,
      isUser: false,
      timestamp: new Date()
    }
  ])

  const userStore = useUserStore()

  // 대화기록 기준 API 호출 
  async function fetchHistoryDetail({ userId, yyyyMMdd, seq }) {
    if (!userId) return
    // 1. pending 메시지 추가
    messages.value = [
      {
        text: '',
        isUser: false,
        status: STATUS_PENDING,
        timestamp: new Date()
      }
    ]
    try {
      const { data } = await axios.post(
        '/api/chat/summary', 
        {userId, yyyyMMdd, seq}
      )
      // 2. 응답 메시지로 교체
      messages.value = [
        {
          text: (data && data.message) ? data.message : INIT_BOT_MESSAGE,
          isUser: false,
          status: STATUS_DONE,
          timestamp: new Date()
        }
      ]
    } catch (e) {
      messages.value = [
        {
          text: INIT_BOT_MESSAGE,
          isUser: false,
          status: STATUS_DONE,
          timestamp: new Date()
        }
      ]
    }
  }

  async function sendMessage(message) {
    if (!message.trim() || isLoading.value) return;
    isLoading.value = true
    error.value = null

    const userId = userStore.user?.id || 'demo-user'

    // 사용자 메시지 추가
    messages.value.push({
      text: message,
      isUser: true,
      status: STATUS_DONE,
      timestamp: new Date()
    })
    const botMessageIndex = messages.value.length

    // 봇 메시지 pending 상태 추가
    messages.value.push({
      text: '',
      isUser: false,
      status: STATUS_PENDING,
      timestamp: new Date()
    })

    try {
      const response = await axios.post('/api/chat', {
        message,
        userId,
      })

      messages.value[botMessageIndex] = {
        text: response.data.message,
        isUser: false,
        status: STATUS_DONE,
        timestamp: new Date()
      }
    } catch (err) {
      error.value = ERROR_SEND_MESSAGE
      messages.value[botMessageIndex] = {
        text: ERROR_SEND_MESSAGE,
        isUser: false,
        status: STATUS_DONE,
        timestamp: new Date()
      }
    } finally {
      isLoading.value = false
    }
  }

  function clearMessages() {
    messages.value = [
      { 
        text: INIT_BOT_MESSAGE,
        isUser: false,
        timestamp: new Date()
      }
    ]
    error.value = null
  }

  // 07-05 Logan : App.vue에 Watch등록하여 누락 제거
  // 로그인/세션복원 시 Gemini 응답 자동 호출 (App.vue에서 호출)
  async function handleLoginSummary(user) {
    clearMessages()
    let latestDate = null
    let latestSeq = null
    let latestLog = null
    if (user && user.profileLogMap) {
      // 날짜 내림차순
      const dates = Object.keys(user.profileLogMap).filter(Boolean).sort((a, b) => new Date(b) - new Date(a))
      for (const date of dates) {
        const logs = user.profileLogMap[date]
        if (Array.isArray(logs) && logs.length > 0) {
          // seq 내림차순
          const sorted = logs.filter(x => x && x.seq != null).sort((a, b) => b.seq - a.seq)
          if (sorted.length > 0) {
            latestLog = sorted[0]
            latestDate = latestLog.yyyyMMdd
            latestSeq = latestLog.seq
            break
          }
        }
      }

      // 대화내용 있을시 로그기반 질문
      if (latestLog != null) {
        await fetchHistoryDetail({ userId: user.id, yyyyMMdd: latestDate, seq: latestSeq })
      }
    }
  }

  return {
    messages,
    isLoading,
    error,
    sendMessage,
    clearMessages,
    fetchHistoryDetail,
    handleLoginSummary
  }
})
