<template>
  <div>
    <el-card class="card-shadow">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: 600;">诊断会话</span>
          <div>
            <el-select v-model="filters.status" placeholder="状态筛选" clearable style="width: 150px; margin-right: 12px;" @change="loadSessions">
              <el-option label="进行中" value="IN_PROGRESS" />
              <el-option label="已转人工" value="TRANSFERRED" />
              <el-option label="已解决" value="RESOLVED" />
            </el-select>
            <el-select v-model="filters.outcome" placeholder="结果筛选" clearable style="width: 150px;" @change="loadSessions">
              <el-option label="假故障拦截" value="FALSE_FAULT_INTERCEPTED" />
              <el-option label="转人工" value="TRANSFERRED_TO_HUMAN" />
              <el-option label="高危检测" value="HAZARD_DETECTED" />
            </el-select>
          </div>
        </div>
      </template>
      
      <el-table :data="sessions" style="width: 100%">
        <el-table-column prop="customerEmail" label="客户邮箱" width="200" />
        <el-table-column prop="product.productName" label="产品" />
        <el-table-column prop="roundCount" label="轮次" width="80" />
        <el-table-column label="置信度" width="100">
          <template #default="{ row }">
            <el-progress v-if="row.finalConfidence" :percentage="Math.round(row.finalConfidence * 100)" :color="getConfidenceColor(row.finalConfidence)" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="高危" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.hazardDetected" type="danger" effect="dark">
              ⚠️
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewDetail(row.id)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="loadSessions"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const router = useRouter()

const sessions = ref([])
const filters = ref({
  status: null,
  outcome: null
})
const pagination = ref({
  page: 1,
  size: 20,
  total: 0
})

const loadSessions = async () => {
  try {
    const params = {
      shopId: 1,
      page: pagination.value.page - 1,
      size: pagination.value.size,
      ...(filters.value.status && { status: filters.value.status }),
      ...(filters.value.outcome && { outcome: filters.value.outcome })
    }
    
    const response = await api.sessions.getAll(params)
    if (response.success && response.data) {
      sessions.value = response.data.content || []
      pagination.value.total = response.data.totalElements || 0
    }
  } catch (error) {
    ElMessage.error('加载会话失败')
  }
}

const viewDetail = (id) => {
  router.push(`/sessions/${id}`)
}

const getStatusType = (status) => {
  const types = {
    IN_PROGRESS: 'primary',
    AWAITING_FEEDBACK: 'warning',
    TRANSFERRED: 'danger',
    RESOLVED: 'success',
    CLOSED: 'info'
  }
  return types[status] || 'info'
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

const getConfidenceColor = (confidence) => {
  if (confidence >= 0.8) return '#67c23a'
  if (confidence >= 0.6) return '#e6a23c'
  return '#f56c6c'
}

onMounted(() => {
  loadSessions()
})
</script>
