import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useUserStore } from './stores/user'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(router)

// 로그인 세션 복원 처리 및 자동 대화 초기화 트리거
const userStore = useUserStore(pinia)
userStore.restoreSession()

app.mount('#app')
