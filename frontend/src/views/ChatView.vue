<template>
  <div class="popup-overlay">
    <div class="popup-center">
      <div class="popup-header">
        <button @click="closeChatbotPopup" class="close-button">×</button>
      </div>
      <Chatbot />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import Chatbot from '../components/Chatbot.vue'
import '../styles/ChatView.css'

const router = useRouter()
function closeChatbotPopup() {
  router.replace('/')
}
</script>

<style scoped>
.chat-view {
  height: 100vh;
  width: 100vw;
}
</style> 