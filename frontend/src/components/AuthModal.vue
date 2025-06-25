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
        <!-- 06-25. Logan : 회원가입 성공 시 초록색(success) 메시지, 에러 시 빨간색(error) 메시지 표기 -->
        <div v-if="success" class="success">{{ success }}</div>
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
const success = ref('')

watch(() => props.mode, () => {
  name.value = ''
  id.value = ''
  password.value = ''
  error.value = ''
  success.value = ''
})

// 06-25. Logan : 자동 로그인 완전 제거, 회원가입/로그인 성공/실패 메시지 분리
async function onSubmit() {
  error.value = ''
  success.value = ''
  if (props.mode === 'login') {
    try {
      await userStore.login(id.value, password.value)
      emit('close') // 06-25. Logan : 로그인 성공 시에만 모달 닫기(챗봇 시작)
    } catch (e) {
      error.value = e.response?.data?.message || e.message // 06-25. Logan : 로그인 실패 시 에러 메시지 표기
    }
  } else {
    try {
      await userStore.register(name.value, id.value, password.value)
      success.value = '회원가입이 완료되었습니다!' // 06-25. Logan : 회원가입 성공 시 초록색 메시지 표기, 모달은 닫지 않음
    } catch (e) {
      error.value = e.response?.data?.message || e.message // 06-25. Logan : 회원가입 실패 시 에러 메시지 표기
    }
  }
}
</script>

<style>
.success {
  color: #2ecc40;
  margin-top: 8px;
  font-size: 0.95em;
  text-align: center;
}
.error {
  color: #ff4136;
  margin-top: 8px;
  font-size: 0.95em;
  text-align: center;
}
</style>