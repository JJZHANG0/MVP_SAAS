<template>
  <div>
    <el-card class="card-shadow" style="margin-bottom: 24px;">
      <template #header>
        <span style="font-weight: 600;">上传产品说明书</span>
      </template>
      
      <el-form :model="uploadForm" label-width="120px">
        <el-form-item label="选择产品">
          <el-select v-model="uploadForm.productId" placeholder="请选择产品" style="width: 100%">
            <el-option v-for="product in products" :key="product.id" :label="`${product.productName} (${product.sku})`" :value="product.id" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="PDF文件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".pdf"
            :on-change="handleFileChange"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">仅支持PDF文件，最大50MB</div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="uploadManual" :loading="uploading">
            上传并解析
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="card-shadow">
      <template #header>
        <span style="font-weight: 600;">说明书列表</span>
      </template>
      
      <el-table :data="manuals" style="width: 100%">
        <el-table-column prop="product.productName" label="产品" />
        <el-table-column prop="originalFileName" label="文件名" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'LOCKED' ? 'success' : 'warning'">
              {{ row.status === 'LOCKED' ? '已确认' : '待确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 'UNCONFIRMED'" type="primary" size="small" @click="confirmManual(row)">
              确认锁定
            </el-button>
            <el-tag v-else type="success">已锁定</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../api'

const products = ref([])
const manuals = ref([])
const uploadForm = ref({
  productId: null
})
const selectedFile = ref(null)
const uploadRef = ref(null)
const uploading = ref(false)

const loadData = async () => {
  try {
    const [productsRes, manualsRes] = await Promise.all([
      api.products.getAll(),
      api.manuals.getAll()
    ])
    
    if (productsRes.success) products.value = productsRes.data || []
    if (manualsRes.success) manuals.value = manualsRes.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const uploadManual = async () => {
  if (!uploadForm.value.productId) {
    ElMessage.warning('请选择产品')
    return
  }
  
  if (!selectedFile.value) {
    ElMessage.warning('请选择PDF文件')
    return
  }
  
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('productId', uploadForm.value.productId)
    
    const response = await api.manuals.upload(formData)
    if (response.success) {
      ElMessage.success('上传成功，正在解析...')
      uploadForm.value.productId = null
      selectedFile.value = null
      uploadRef.value.clearFiles()
      
      setTimeout(() => {
        loadData()
      }, 3000)
    }
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

const confirmManual = async (manual) => {
  try {
    await ElMessageBox.confirm(
      '确认后，安全警告和保修条款将被锁定，无法修改。是否继续？',
      '确认锁定',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await api.manuals.confirm(manual.id, {
      extractedProductName: manual.extractedProductName,
      extractedModel: manual.extractedModel,
      extractedBatteryInfo: manual.extractedBatteryInfo,
      extractedPowerInfo: manual.extractedPowerInfo
    })
    
    if (response.success) {
      ElMessage.success('确认成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>
