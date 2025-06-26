import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const user = ref(null) // { name, id }
  const isLoggedIn = ref(false)

  async function register(name, id, password) {
    const res = await axios.post('/api/auth/register', { name, id, password })
    if (!res.data.success) {
      throw new Error(res.data.message)
    }
    // 성공 시 자동 로그인 or 안내
  }

  async function login(id, password) {
    const res = await axios.post('/api/auth/login', { id, password })
    if (!res.data.success) {
      throw new Error(res.data.message)
    }
    user.value = res.data // 필요시 { id, name }만 저장
    isLoggedIn.value = true
  }

  async function logout() {
    if (user.value?.id) {
      try {
        await axios.post('/api/auth/logout', { id: user.value.id })
      } catch (e) {
        console.error('로그아웃 요청 실패:', e)
        // 서버 오류 무시하고 클라이언트 로그아웃 진행
      }
    }
    user.value = null
    isLoggedIn.value = false
  }

  return { user, isLoggedIn, register, login, logout }
}) 