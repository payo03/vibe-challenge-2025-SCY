<template>
  <header class="header-bar">
    <div class="logo">🗺️ 여행 도우미</div>
    <div class="actions">
      <span v-if="userStore.isLoggedIn">
        <b>{{ userStore.user.name }}</b>님
        <button class="logout-btn" @click="logout">로그아웃</button>
      </span>
      <span v-else>
        <button class="login-btn" @click="showLogin = true">로그인</button>
        <button class="register-btn" @click="showRegister = true">회원가입</button>
      </span>
    </div>
    <AuthModal v-if="showLogin" mode="login" @close="showLogin = false" />
    <AuthModal v-if="showRegister" mode="register" @close="showRegister = false" />
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '../stores/user'
import AuthModal from './AuthModal.vue'
import '../styles/HeaderBar.css'

const userStore = useUserStore()
const showLogin = ref(false)
const showRegister = ref(false)

function logout() {
  userStore.logout()
}
</script> 