<template>
  <div class="home">
    <div class="hero-section">
      <h1>🗺️ 여행 도우미 챗봇</h1>
      <p>AI와 함께 완벽한 여행을 계획해보세요</p>
      <router-link to="/chat" class="cta-button">
        챗봇 시작하기
      </router-link>
    </div>
    
    <div class="features">
      <div
        class="feature-card clickable"
        @click="handleFeatureClick('custom')">
        <h3>🎯 맞춤형 추천</h3>
        <p>예산, 기간, 선호도에 따른 개인화된 여행지 추천</p>
      </div>

      <div
        class="feature-card clickable"
        @click="handleFeatureClick('/chat')">
        <h3>📅 스마트 일정</h3>
        <p>최적의 여행 일정을 자동으로 생성</p>
      </div>
    </div>
    <AuthModal v-if="showLogin" mode="login" @close="showLogin = false" />
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import AuthModal from '@/components/AuthModal.vue'
import '../styles/HomeView.css'

const userStore = useUserStore()
const router = useRouter()
const showLogin = ref(false)

function handleFeatureClick(type) {
  if (!userStore.isLoggedIn) {
    // alert('로그인이 필요합니다.')
    showLogin.value = true
    // return
  }
  
  if (type === 'custom') {
    // 맞춤형 추천: ChatView로 이동 (2개 팝업창 표시)
    router.push('/chat?mode=custom')
  } else {
    // 기존 챗봇 페이지로 이동
    router.push(type)
  }
}
</script> 