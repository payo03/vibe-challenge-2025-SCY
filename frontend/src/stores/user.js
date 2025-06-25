import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const user = ref(null) // { name, id }
  const isLoggedIn = ref(false)

  async function register(name, id, password) {
    await axios.post('/api/auth/register', { name, id, password })
    // 성공 시 자동 로그인 or 안내
  }

  async function login(id, password) {
    const res = await axios.post('/api/auth/login', { id, password })
    user.value = res.data // { id, name }
    isLoggedIn.value = true
  }

  function logout() {
    user.value = null
    isLoggedIn.value = false
  }

  return { user, isLoggedIn, register, login, logout }
}) 