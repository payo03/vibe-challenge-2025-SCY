<template>
  <div class="modal-backdrop">
    <div class="modal">
      <h3>{{ mode === 'login' ? '로그인' : '회원가입' }}</h3>
      <form @submit.prevent="onSubmit">
        <input
          v-if="mode==='register'"
          v-model="name"
          placeholder="이름"
          class="input"
          required
        />
        <input v-model="id" placeholder="아이디" class="input" required />
        <input v-model="password" type="password" placeholder="비밀번호" class="input" required />
        <div class="button-group">
          <button type="submit" class="group-btn main-btn">{{ mode==='login' ? '로그인' : '회원가입' }}</button>
          <button type="button" class="group-btn cancel-btn" @click="$emit('close')">취소</button>
        </div>
        <div v-if="error" class="error">{{ error }}</div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useUserStore } from '../stores/user'
import '../styles/AuthModal.css'
const props = defineProps({ mode: String })
const emit = defineEmits(['close'])
const userStore = useUserStore()

const name = ref('')
const id = ref('')
const password = ref('')
const error = ref('')

watch(() => props.mode, () => {
  name.value = ''
  id.value = ''
  password.value = ''
  error.value = ''
})

async function onSubmit() {
  try {
    if (props.mode === 'login') {
      await userStore.login(id.value, password.value)
    } else {
      await userStore.register(name.value, id.value, password.value)
      await userStore.login(id.value, password.value)
    }
    emit('close')
  } catch (e) {
    error.value = '로그인/회원가입 실패: ' + (e.response?.data?.message || e.message)
  }
}
</script> 