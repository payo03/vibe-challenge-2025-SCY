<script setup>
// HealthCheck 엔드포인트 추가
const healthCheck = () => {
  return {
    status: 'UP',
    service: 'vibe-challenge-2025-frontend',
    timestamp: new Date().toISOString(),
    version: '1.0.0',
    port: 5173
  }
}

// 전역 함수로 노출
window.healthCheck = healthCheck
</script>

<template>
  <div class="app-container">
    <router-view />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.app-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

/* 태블릿 이상에서는 배경 그라데이션 유지 */
@media (min-width: 768px) {
  .app-container {
    padding: 2rem;
  }
}

/* 모바일에서는 전체 화면 사용 */
@media (max-width: 767px) {
  .app-container {
    padding: 0;
  }
}
</style>

<style scoped>
/* 기존 스타일 제거 - Router View 사용 */
</style>
