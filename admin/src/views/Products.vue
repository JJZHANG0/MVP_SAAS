<template>
  <div>
    <el-card class="card-shadow">
      <template #header>
        <span style="font-weight: 600;">产品列表</span>
      </template>
      
      <el-table :data="products" style="width: 100%">
        <el-table-column prop="sku" label="SKU" width="180" />
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="batteryVoltage" label="电池电压" width="100" />
        <el-table-column label="说明书" width="100">
          <template #default="{ row }">
            <el-tag :type="row.hasManual ? 'success' : 'info'">
              {{ row.hasManual ? '已上传' : '未上传' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="uploadManual(row)">
              上传说明书
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const router = useRouter()
const products = ref([])

const loadProducts = async () => {
  try {
    const response = await api.products.getAll()
    if (response.success) {
      products.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载产品失败')
  }
}

const uploadManual = (product) => {
  router.push(`/manuals?productId=${product.id}`)
}

onMounted(() => {
  loadProducts()
})
</script>
