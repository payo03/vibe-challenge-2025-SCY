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

  // justLoggedIn 플래그 감지하여 대화 기록 초기화
  watch(() => userStore.justLoggedIn, (newVal) => {
    if (newVal) {
      clearMessages()
      userStore.resetLoginFlag() // 플래그 초기화
    }
  })

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
    clearMessages
  }
})
