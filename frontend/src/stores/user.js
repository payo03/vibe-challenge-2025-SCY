import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useChatStore } from './chat'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const user = ref(null) // { name, id }
  const isLoggedIn = ref(false)
  const handleSummary = ref(false) // 로그인 직후 신호용 플래그
  const justLoggedIn = ref(false) // 로그인 직후 신호용 플래그

  // 앱 시작 시 localStorage에서 사용자 복원
  function restoreSession() {
    const saved = localStorage.getItem('user')
    if (saved) {
      user.value = JSON.parse(saved)
      isLoggedIn.value = true
      handleSummary.value = true // 복원 시도 시 대화 초기화 신호 발생
    }
  }

  async function register(name, id, password) {
    const res = await axios.post('/api/auth/register', { name, id, password })
    if (!res.data.success) {
      throw new Error(res.data.message)
    }
    // 회원가입 성공 시 자동 로그인 원하면 login() 호출 가능
  }

  async function login(id, password) {
    const res = await axios.post('/api/auth/login', { id, password })
    if (!res.data.success) {
      throw new Error(res.data.message)
    }
    user.value = { 
      id: res.data.id, 
      name: res.data.name,
      profileLogMap: res.data.profileLogMap // profileLogMap 추가
    }
    isLoggedIn.value = true
    justLoggedIn.value = true // 로그인 시 대화 초기화 신호 발생
    localStorage.setItem('user', JSON.stringify(user.value)) // localStorage 저장
  }

  function resetLoginFlag() {
    handleSummary.value = false
  }

  async function logout() {
    if (user.value?.id) {
      try {
        await axios.post('/api/auth/logout', { id: user.value.id })
      } catch (e) {
        console.error('로그아웃 요청 실패:', e)
      }
    }

    // 대화 내용 초기화
    const chatStore = useChatStore()
    chatStore.clearMessages()

    // 사용자 정보 및 localStorage 삭제
    user.value = null
    isLoggedIn.value = false
    handleSummary.value = false
    localStorage.removeItem('user')
  }

  return { user, isLoggedIn, handleSummary, register, login, logout, restoreSession, resetLoginFlag }
})
