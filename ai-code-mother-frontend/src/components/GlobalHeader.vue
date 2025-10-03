<template>
  <a-layout-header class="global-header">
    <div class="header-content">
      <!-- 左侧 Logo 和标题 -->
      <div class="header-left">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <h1 class="site-title">AI代码生成平台</h1>
      </div>

      <!-- 中间菜单 -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        class="header-menu"
        :items="menuItems"
        @click="handleMenuClick"
      />

      <!-- 右侧用户信息 -->
      <div class="header-right">
        <a-button type="primary" @click="handleLogin">
          登录
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedKeys = ref<string[]>(['home'])

// 菜单配置
const menuItems = reactive([
  {
    key: 'home',
    label: '首页',
    path: '/'
  },
  {
    key: 'code-generate',
    label: '代码生成',
    path: '/code-generate'
  },
  {
    key: 'about',
    label: '关于我们',
    path: '/about'
  }
])

// 菜单点击处理
const handleMenuClick = ({ key }: { key: string }) => {
  const menuItem = menuItems.find(item => item.key === key)
  if (menuItem) {
    router.push(menuItem.path)
  }
}

// 登录处理
const handleLogin = () => {
  // TODO: 实现登录逻辑
  console.log('登录按钮被点击')
}
</script>

<style scoped>
.global-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
  line-height: 64px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.site-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.header-menu {
  flex: 1;
  justify-content: center;
  border-bottom: none;
  background: transparent;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }
  
  .site-title {
    font-size: 16px;
  }
  
  .header-menu {
    display: none;
  }
}

@media (max-width: 480px) {
  .header-left {
    gap: 8px;
  }
  
  .logo {
    width: 24px;
    height: 24px;
  }
  
  .site-title {
    font-size: 14px;
  }
}
</style>
