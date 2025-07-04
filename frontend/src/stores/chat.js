import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import { useUserStore } from './user'
import axios from 'axios'

export const useChatStore = defineStore('chat', () => {
  const isLoading = ref(false)
  const error = ref(null)
  const messages = ref([
    { 
      text: '안녕하세요! 여행 도우미 챗봇입니다. 🗺️\n\n어떤 여행을 계획하고 계신가요?\n• 여행지 추천\n• 일정 계획\n• 예산 안내\n• 여행 팁\n\n무엇이든 물어보세요!', 
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
      status: 'done',
      timestamp: new Date()
    })
    const botMessageIndex = messages.value.length

    // 봇 메시지 pending 상태 추가
    messages.value.push({
      text: '',
      isUser: false,
      status: 'pending',
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
        status: 'done',
        timestamp: new Date()
      }
    } catch (err) {
      error.value = '메시지 전송 중 오류가 발생했습니다.'
      messages.value[botMessageIndex] = {
        text: '죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
        isUser: false,
        status: 'done',
        timestamp: new Date()
      }
    } finally {
      isLoading.value = false
    }
  }

  function clearMessages() {
    messages.value = [
      { 
        text: '안녕하세요! 여행 도우미 챗봇입니다. 🗺️\n\n어떤 여행을 계획하고 계신가요?\n• 여행지 추천\n• 일정 계획\n• 예산 안내\n• 여행 팁\n\n무엇이든 물어보세요!', 
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
