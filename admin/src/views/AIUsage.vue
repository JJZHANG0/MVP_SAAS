<template>
  <div>
    <el-card class="card-shadow" style="margin-bottom: 24px;">
      <template #header>
        <span style="font-weight: 600;">AI用量统计</span>
      </template>
      
      <el-row :gutter="24">
        <el-col :span="8">
          <div class="stat-card">
            <div style="font-size: 14px; opacity: 0.9;">总Token数</div>
            <div style="font-size: 32px; font-weight: 700; margin-top: 8px;">
              {{ stats.totalTokens?.toLocaleString() || 0 }}
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card success-card">
            <div style="font-size: 14px; opacity: 0.9;">总成本</div>
            <div style="font-size: 32px; font-weight: 700; margin-top: 8px;">
              ${{ stats.totalCost || '0.00' }}
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card warning-card">
            <div style="font-size: 14px; opacity: 0.9;">预估月成本</div>
            <div style="font-size: 32px; font-weight: 700; margin-top: 8px;">
              ${{ stats.estimatedMonthlyCost || '0.00' }}
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="card-shadow" style="margin-bottom: 24px;">
      <template #header>
        <span style="font-weight: 600;">成本预估</span>
      </template>
      
      <el-descriptions :column="1" border>
        <el-descriptions-item label="1,000 会话/月">$12-18</el-descriptions-item>
        <el-descriptions-item label="5,000 会话/月">$60-90</el-descriptions-item>
        <el-descriptions-item label="20,000 会话/月">$240-360</el-descriptions-item>
      </el-descriptions>
      
      <el-alert
        title="成本说明"
        type="info"
        style="margin-top: 16px;"
        :closable="false"
      >
        以上预估基于通义千问API定价，实际成本取决于对话轮次和内容长度
      </el-alert>
    </el-card>
    
    <el-card class="card-shadow">
      <template #header>
        <span style="font-weight: 600;">详细日志</span>
      </template>
      
      <el-table :data="logs" style="width: 100%">
        <el-table-column prop="modelName" label="模型" width="150" />
        <el-table-column prop="operation" label="操作" width="120" />
        <el-table-column prop="totalTokens" label="总Token" width="120" />
        <el-table-column prop="estimatedCost" label="成本($)" width="100">
          <template #default="{ row }">
            ${{ row.estimatedCost?.toFixed(4) }}
          </template>
        </el-table-column>
        <el-table-column prop="responseTimeMs" label="响应时间(ms)" width="120" />
        <el-table-column prop="requestSummary" label="请求摘要" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const stats = ref({})
const logs = ref([])

const loadData = async () => {
  try {
    const [statsRes, logsRes] = await Promise.all([
      api.aiUsage.getStats(30),
      api.aiUsage.getAll(0, 50)
    ])
    
    if (statsRes.success) stats.value = statsRes.data || {}
    if (logsRes.success) logs.value = logsRes.data.content || []
  } catch (error) {
    ElMessage.error('加载AI用量数据失败')
  }
}

onMounted(() => {
  loadData()
})
</script>
