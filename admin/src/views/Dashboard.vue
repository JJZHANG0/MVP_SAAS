<template>
  <div>
    <el-row :gutter="24" style="margin-bottom: 24px;">
      <el-col :span="6">
        <div class="stat-card">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <div style="font-size: 14px; opacity: 0.9; margin-bottom: 8px;">总会话数</div>
              <div style="font-size: 32px; font-weight: 700;">{{ stats.totalSessions }}</div>
            </div>
            <el-icon style="font-size: 48px; opacity: 0.3;"><ChatDotRound /></el-icon>
          </div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card success-card">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <div style="font-size: 14px; opacity: 0.9; margin-bottom: 8px;">假故障拦截</div>
              <div style="font-size: 32px; font-weight: 700;">{{ stats.interceptedCount }}</div>
              <div style="font-size: 12px; opacity: 0.9; margin-top: 4px;">
                拦截率 {{ stats.interceptRate?.toFixed(1) }}%
              </div>
            </div>
            <el-icon style="font-size: 48px; opacity: 0.3;"><Check /></el-icon>
          </div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card warning-card">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <div style="font-size: 14px; opacity: 0.9; margin-bottom: 8px;">转人工</div>
              <div style="font-size: 32px; font-weight: 700;">{{ stats.transferredCount }}</div>
            </div>
            <el-icon style="font-size: 48px; opacity: 0.3;"><User /></el-icon>
          </div>
        </div>
      </el-col>
      
      <el-col :span="6">
        <div class="stat-card danger-card">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <div style="font-size: 14px; opacity: 0.9; margin-bottom: 8px;">高危场景</div>
              <div style="font-size: 32px; font-weight: 700;">{{ stats.hazardCount }}</div>
            </div>
            <el-icon style="font-size: 48px; opacity: 0.3;"><Warning /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="24">
      <el-col :span="16">
        <el-card class="card-shadow" style="margin-bottom: 24px;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-weight: 600;">最近会话</span>
              <el-button type="primary" size="small" @click="$router.push('/sessions')">
                查看全部
              </el-button>
            </div>
          </template>
          
          <el-table :data="recentSessions" style="width: 100%">
            <el-table-column prop="customerEmail" label="客户" width="200" />
            <el-table-column prop="product.productName" label="产品" />
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <span :class="`badge-${getStatusColor(row.status)}`">
                  {{ getStatusText(row.status) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="结果" width="120">
              <template #default="{ row }">
                <span v-if="row.outcome" :class="`badge-${getOutcomeColor(row.outcome)}`">
                  {{ getOutcomeText(row.outcome) }}
                </span>
                <span v-else class="badge-info">进行中</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="viewSession(row.id)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="card-shadow" style="margin-bottom: 24px;">
          <template #header>
            <span style="font-weight: 600;">快速操作</span>
          </template>
          
          <div style="display: flex; flex-direction: column; gap: 12px;">
            <el-button type="primary" :icon="Shop" @click="$router.push('/shops')" style="justify-content: flex-start;">
              连接店铺
            </el-button>
            <el-button type="success" :icon="Document" @click="$router.push('/manuals')" style="justify-content: flex-start;">
              上传说明书
            </el-button>
            <el-button type="warning" :icon="ChatDotRound" @click="$router.push('/sessions?status=TRANSFERRED')" style="justify-content: flex-start;">
              待处理转人工
            </el-button>
            <el-button :icon="Reading" @click="$router.push('/knowledge')" style="justify-content: flex-start;">
              查看知识库
            </el-button>
          </div>
        </el-card>
        
        <el-card class="card-shadow">
          <template #header>
            <span style="font-weight: 600;">系统信息</span>
          </template>
          
          <div style="font-size: 14px; color: #606266; line-height: 2;">
            <div><strong>版本：</strong>v1.0.0 Demo</div>
            <div><strong>店铺数：</strong>{{ shops.length }}</div>
            <div><strong>产品数：</strong>{{ products.length }}</div>
            <div><strong>知识库：</strong>15+ 场景</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, Check, User, Warning, Shop, Document, Reading } from '@element-plus/icons-vue'
import api from '../api'

const router = useRouter()

const stats = ref({
  totalSessions: 0,
  interceptedCount: 0,
  transferredCount: 0,
  hazardCount: 0,
  interceptRate: 0
})

const recentSessions = ref([])
const shops = ref([])
const products = ref([])

const loadDashboard = async () => {
  try {
    const [statsRes, sessionsRes, shopsRes, productsRes] = await Promise.all([
      api.sessions.getStats(1),
      api.sessions.getAll({ page: 0, size: 5 }),
      api.shops.getAll(),
      api.products.getAll()
    ])
    
    if (statsRes.success) stats.value = statsRes.data
    if (sessionsRes.success) recentSessions.value = sessionsRes.data.content || []
    if (shopsRes.success) shops.value = shopsRes.data || []
    if (productsRes.success) products.value = productsRes.data || []
  } catch (error) {
    console.error('Failed to load dashboard:', error)
  }
}

const viewSession = (id) => {
  router.push(`/sessions/${id}`)
}

const getStatusColor = (status) => {
  const colors = {
    IN_PROGRESS: 'info',
    AWAITING_FEEDBACK: 'warning',
    TRANSFERRED: 'danger',
    RESOLVED: 'success',
    CLOSED: 'info'
  }
  return colors[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    IN_PROGRESS: '进行中',
    AWAITING_FEEDBACK: '等待反馈',
    TRANSFERRED: '已转人工',
    RESOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return texts[status] || status
}

const getOutcomeColor = (outcome) => {
  const colors = {
    FALSE_FAULT_INTERCEPTED: 'success',
    TRANSFERRED_TO_HUMAN: 'warning',
    LOW_CONFIDENCE: 'warning',
    MAX_ROUNDS_REACHED: 'info',
    NEGATIVE_FEEDBACK: 'warning',
    HAZARD_DETECTED: 'danger'
  }
  return colors[outcome] || 'info'
}

const getOutcomeText = (outcome) => {
  const texts = {
    FALSE_FAULT_INTERCEPTED: '假故障拦截',
    TRANSFERRED_TO_HUMAN: '转人工',
    LOW_CONFIDENCE: '低置信度',
    MAX_ROUNDS_REACHED: '达到轮次上限',
    NEGATIVE_FEEDBACK: '负面反馈',
    HAZARD_DETECTED: '高危检测'
  }
  return texts[outcome] || outcome
}

onMounted(() => {
  loadDashboard()
})
</script>
