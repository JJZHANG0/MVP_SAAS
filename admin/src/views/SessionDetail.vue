<template>
  <div>
    <el-page-header @back="$router.back()">
      <template #content>
        <span style="font-size: 18px; font-weight: 600;">会话详情</span>
      </template>
    </el-page-header>
    
    <el-row :gutter="24" style="margin-top: 24px;">
      <el-col :span="16">
        <el-card class="card-shadow" style="margin-bottom: 24px;">
          <template #header>
            <span style="font-weight: 600;">对话记录</span>
          </template>
          
          <div v-for="message in messages" :key="message.id" :style="{
            display: 'flex',
            justifyContent: message.role === 'USER' ? 'flex-start' : 'flex-end',
            marginBottom: '16px'
          }">
            <div :style="{
              maxWidth: '70%',
              padding: '12px 16px',
              borderRadius: '12px',
              background: message.role === 'USER' ? '#f0f2f5' : (message.isHazardWarning ? '#f56c6c' : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'),
              color: message.role === 'USER' ? '#303133' : 'white'
            }">
              <div style="font-size: 14px; white-space: pre-wrap;">{{ message.content }}</div>
              <div v-if="message.imageUrls" style="margin-top: 8px;">
                <img v-for="(url, idx) in message.imageUrls.split(',')" :key="idx" :src="url" style="max-width: 200px; border-radius: 8px; margin-right: 8px;" />
              </div>
              <div style="font-size: 12px; opacity: 0.7; margin-top: 8px;">
                轮次 {{ message.roundNumber }}
                <span v-if="message.isFromHuman"> · 人工回复</span>
                <span v-if="message.isHazardWarning"> · ⚠️ 安全警告</span>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card v-if="session.transferredToHuman" class="card-shadow">
          <template #header>
            <span style="font-weight: 600;">人工接管</span>
          </template>
          
          <el-input
            v-model="humanReply"
            type="textarea"
            :rows="3"
            placeholder="输入回复内容..."
            style="margin-bottom: 12px;"
          />
          
          <el-button type="primary" @click="sendHumanReply" :loading="sending">
            发送回复
          </el-button>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="card-shadow" style="margin-bottom: 24px;">
          <template #header>
            <span style="font-weight: 600;">会话信息</span>
          </template>
          
          <div style="font-size: 14px; line-height: 2;">
            <div><strong>会话ID:</strong> {{ session.sessionUuid }}</div>
            <div><strong>客户:</strong> {{ session.customerName || session.customerEmail }}</div>
            <div><strong>邮箱:</strong> {{ session.customerEmail }}</div>
            <div><strong>产品:</strong> {{ session.product?.productName }}</div>
            <div><strong>订单:</strong> {{ session.shopifyOrderId }}</div>
            <div><strong>轮次:</strong> {{ session.roundCount }} / 5</div>
            <div v-if="session.finalConfidence">
              <strong>置信度:</strong> {{ (session.finalConfidence * 100).toFixed(1) }}%
            </div>
            <div>
              <strong>状态:</strong>
              <el-tag :type="getStatusType(session.status)" size="small" style="margin-left: 8px;">
                {{ getStatusText(session.status) }}
              </el-tag>
            </div>
            <div v-if="session.outcome">
              <strong>结果:</strong>
              <el-tag :type="getOutcomeType(session.outcome)" size="small" style="margin-left: 8px;">
                {{ getOutcomeText(session.outcome) }}
              </el-tag>
            </div>
          </div>
        </el-card>
        
        <el-card v-if="session.hazardDetected" class="card-shadow" style="margin-bottom: 24px; border: 2px solid #f56c6c;">
          <template #header>
            <span style="font-weight: 600; color: #f56c6c;">⚠️ 高危警告</span>
          </template>
          
          <div style="color: #f56c6c;">
            <div><strong>检测到的关键词:</strong></div>
            <div style="margin-top: 8px; font-size: 16px; font-weight: 600;">
              {{ session.hazardKeywords }}
            </div>
          </div>
        </el-card>
        
        <el-card v-if="session.diagnosisSummary" class="card-shadow">
          <template #header>
            <span style="font-weight: 600;">诊断摘要</span>
          </template>
          
          <div style="font-size: 14px; line-height: 1.8;">
            <div v-if="session.suspectedIssue">
              <strong>疑似问题:</strong> {{ session.suspectedIssue }}
            </div>
            <div v-if="session.diagnosisSummary" style="margin-top: 12px;">
              {{ session.diagnosisSummary }}
            </div>
            <div v-if="session.guidePageUrl" style="margin-top: 12px;">
              <strong>引导页:</strong>
              <el-link :href="session.guidePageUrl" target="_blank" type="primary">
                查看自查引导
              </el-link>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const route = useRoute()
const sessionId = route.params.id

const session = ref({})
const messages = ref([])
const humanReply = ref('')
const sending = ref(false)

const loadSessionDetail = async () => {
  try {
    const response = await api.sessions.get(sessionId)
    if (response.success) {
      session.value = response.data.session
      messages.value = response.data.messages || []
    }
  } catch (error) {
    ElMessage.error('加载会话详情失败')
  }
}

const sendHumanReply = async () => {
  if (!humanReply.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  sending.value = true
  try {
    const response = await api.sessions.sendHumanReply(sessionId, humanReply.value)
    if (response.success) {
      ElMessage.success('回复已发送')
      humanReply.value = ''
      loadSessionDetail()
    }
  } catch (error) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

const getStatusType = (status) => {
  const types = { IN_PROGRESS: 'primary', TRANSFERRED: 'danger', RESOLVED: 'success' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { IN_PROGRESS: '进行中', TRANSFERRED: '已转人工', RESOLVED: '已解决' }
  return texts[status] || status
}

const getOutcomeType = (outcome) => {
  const types = { FALSE_FAULT_INTERCEPTED: 'success', HAZARD_DETECTED: 'danger' }
  return types[outcome] || 'warning'
}

const getOutcomeText = (outcome) => {
  const texts = { FALSE_FAULT_INTERCEPTED: '假故障拦截', TRANSFERRED_TO_HUMAN: '转人工', HAZARD_DETECTED: '高危检测' }
  return texts[outcome] || outcome
}

onMounted(() => {
  loadSessionDetail()
})
</script>
