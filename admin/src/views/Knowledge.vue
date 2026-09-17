<template>
  <div>
    <el-card class="card-shadow">
      <template #header>
        <span style="font-weight: 600;">知识库（只读）</span>
      </template>
      
      <el-table :data="knowledge" style="width: 100%">
        <el-table-column prop="scenarioName" label="场景名称" width="200" />
        <el-table-column prop="symptomDescription" label="症状描述" show-overflow-tooltip />
        <el-table-column prop="rootCause" label="根本原因" show-overflow-tooltip />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'PLATFORM_PRESET' ? 'primary' : 'success'">
              {{ row.type === 'PLATFORM_PRESET' ? '平台预置' : 'SKU专属' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewDetail(row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="showDetail" title="知识详情" width="700px">
      <div v-if="currentKnowledge">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="场景名称">{{ currentKnowledge.scenarioName }}</el-descriptions-item>
          <el-descriptions-item label="症状描述">{{ currentKnowledge.symptomDescription }}</el-descriptions-item>
          <el-descriptions-item label="根本原因">{{ currentKnowledge.rootCause }}</el-descriptions-item>
          <el-descriptions-item label="排查步骤">
            <div style="white-space: pre-wrap;">{{ currentKnowledge.troubleshootingSteps }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="关键词">{{ currentKnowledge.keywords }}</el-descriptions-item>
          <el-descriptions-item label="引导页">{{ currentKnowledge.guidePageSlug }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const knowledge = ref([])
const showDetail = ref(false)
const currentKnowledge = ref(null)

const loadKnowledge = async () => {
  try {
    const response = await api.knowledge.getAll()
    if (response.success) {
      knowledge.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载知识库失败')
  }
}

const viewDetail = (item) => {
  currentKnowledge.value = item
  showDetail.value = true
}

onMounted(() => {
  loadKnowledge()
})
</script>
