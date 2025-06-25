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
      <div class="feature-card">
        <h3>🎯 맞춤형 추천</h3>
        <p>예산, 기간, 선호도에 따른 개인화된 여행지 추천</p>
      </div>
      <div class="feature-card">
        <h3>📅 스마트 일정</h3>
        <p>최적의 여행 일정을 자동으로 생성</p>
      </div>
    </div>
  </div>
</template>

<script setup>
// HomeView 컴포넌트
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  color: white;
}

.hero-section {
  text-align: center;
  margin-bottom: 4rem;
}

.hero-section h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
  font-weight: 700;
}

.hero-section p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.cta-button {
  display: inline-block;
  background: #007AFF;
  color: white;
  padding: 1rem 2rem;
  border-radius: 50px;
  text-decoration: none;
  font-weight: 600;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 122, 255, 0.3);
}

.cta-button:hover {
  background: #0056CC;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.4);
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  max-width: 900px;
  width: 100%;
}

.feature-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 2rem;
  border-radius: 15px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.feature-card p {
  opacity: 0.8;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .hero-section h1 {
    font-size: 2rem;
  }
  
  .hero-section p {
    font-size: 1rem;
  }
  
  .features {
    grid-template-columns: 1fr;
  }
}
</style> 