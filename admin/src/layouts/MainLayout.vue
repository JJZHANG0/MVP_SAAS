<template>
  <el-container class="layout-container">
    <el-aside width="240px" style="background: white; box-shadow: 2px 0 8px rgba(0,0,0,0.05);">
      <div style="padding: 20px; text-align: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
        <h2 style="color: white; margin: 0; font-size: 24px;">🔧 ToolFix</h2>
        <p style="color: rgba(255,255,255,0.9); margin: 4px 0 0; font-size: 12px;">AI售后诊断系统</p>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        router
        style="border-right: none;"
      >
        <el-menu-item index="/">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/shops">
          <el-icon><Shop /></el-icon>
          <span>店铺管理</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Box /></el-icon>
          <span>产品管理</span>
        </el-menu-item>
        <el-menu-item index="/manuals">
          <el-icon><Document /></el-icon>
          <span>说明书管理</span>
        </el-menu-item>
        <el-menu-item index="/sessions">
          <el-icon><ChatDotRound /></el-icon>
          <span>诊断会话</span>
        </el-menu-item>
        <el-menu-item index="/knowledge">
          <el-icon><Reading /></el-icon>
          <span>知识库</span>
        </el-menu-item>
        <el-menu-item index="/ai-usage">
          <el-icon><Coin /></el-icon>
          <span>AI用量统计</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header style="background: white; box-shadow: 0 1px 4px rgba(0,0,0,0.05); padding: 0 24px; display: flex; align-items: center; justify-content: space-between;">
        <div style="font-size: 18px; font-weight: 600; color: #303133;">
          {{ pageTitle }}
        </div>
        <el-badge :value="transferredCount" :hidden="transferredCount === 0" type="danger">
          <el-button type="danger" :icon="Bell" @click="showTransferred" circle />
        </el-badge>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import api from '../api'

const route = useRoute()
const router = useRouter()

const transferredCount = ref(0)

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titleMap = {
    '/': '仪表盘',
    '/shops': '店铺管理',
    '/products': '产品管理',
    '/manuals': '说明书管理',
    '/sessions': '诊断会话',
    '/knowledge': '知识库',
    '/ai-usage': 'AI用量统计'
  }
  return titleMap[route.path] || '详情'
})

const loadTransferredCount = async () => {
  try {
    const response = await api.sessions.getStats(1)
    if (response.success) {
      transferredCount.value = response.data.transferredCount || 0
    }
  } catch (error) {
    console.error('Failed to load transferred count:', error)
  }
}

const showTransferred = () => {
  router.push('/sessions?status=TRANSFERRED')
}

onMounted(() => {
  loadTransferredCount()
  setInterval(loadTransferredCount, 30000)
})
</script>
