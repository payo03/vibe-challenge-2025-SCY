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

  // handleSummary 플래그 감지하여 대화 기록 초기화 및 Gemini 요약/질문 메시지 fetch
  watch(() => userStore.handleSummary, async (isExecute) => {
    if (isExecute) {
      clearMessages()

      // 최신 대화로그 추출
      const user = userStore.user
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

        if(latestLog != null) await fetchHistoryDetail({ userId: user.id, yyyyMMdd: latestDate, seq: latestSeq })
      }
    
      userStore.resetLoginFlag() // 플래그 초기화
    }
  })

  // 대화기록 기준 API 호출 
  async function fetchHistoryDetail({ userId, yyyyMMdd, seq }) {
    if (!userId) return
    try {
      const { data } = await axios.post(
        '/api/chat/summary', 
        {userId, yyyyMMdd, seq}
      )

      messages.value = [
        {
          text: (data && data.message) ? data.message : INIT_BOT_MESSAGE,
          isUser: false,
          timestamp: new Date()
        }
      ]
    } catch (e) {
      messages.value = [
        {
          text: INIT_BOT_MESSAGE,
          isUser: false,
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

  return {
    messages,
    isLoading,
    error,
    sendMessage,
    clearMessages,
    fetchHistoryDetail
  }
})
