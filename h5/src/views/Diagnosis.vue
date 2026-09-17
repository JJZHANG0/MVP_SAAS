<template>
  <div class="chat-container">
    <van-nav-bar title="ToolFix Support" fixed>
      <template #left>
        <div style="font-size: 12px; color: #969799;">
          Round {{ session.roundCount || 0 }}/{{ session.maxRounds || 5 }}
        </div>
      </template>
    </van-nav-bar>
    
    <div class="chat-messages" ref="messagesContainer" style="margin-top: 46px;">
      <van-empty v-if="!validated" description="Validating session..." />
      
      <div v-else>
        <div style="text-align: center; padding: 16px 0; color: #969799; font-size: 14px;">
          <div style="font-weight: 600; color: #323233; margin-bottom: 4px;">{{ session.productName }}</div>
          <div>Tell us what's happening with your tool</div>
        </div>
        
        <div v-for="(message, index) in messages" :key="index" 
             :class="['message-item', message.role === 'user' ? 'message-user' : 'message-assistant', message.isHazard ? 'message-hazard' : '']">
          <div class="message-bubble">
            <div style="white-space: pre-wrap;">{{ message.content }}</div>
            <div v-if="message.images && message.images.length > 0" style="margin-top: 8px;">
              <img v-for="(img, idx) in message.images" :key="idx" :src="img" 
                   style="max-width: 100%; border-radius: 8px; margin-bottom: 4px;" />
            </div>
            <div v-if="message.guideUrl" style="margin-top: 12px;">
              <van-button type="success" size="small" @click="openGuide(message.guideUrl)" block>
                View Self-Check Guide
              </van-button>
            </div>
            <div class="message-meta">{{ message.time }}</div>
          </div>
        </div>
        
        <div v-if="loading" class="message-item message-assistant">
          <div class="message-bubble">
            <span class="loading-dots">Analyzing</span>
          </div>
        </div>
        
        <div v-if="transferred" style="text-align: center; padding: 24px; color: #969799;">
          <van-icon name="service" size="48" color="#ee0a24" />
          <div style="margin-top: 12px; font-size: 16px; font-weight: 600; color: #323233;">
            Transferred to Human Support
          </div>
          <div style="margin-top: 8px; font-size: 14px;">
            Our team will contact you within 24 hours
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input-area" v-if="validated && !transferred">
      <van-uploader v-model="uploadFiles" :max-count="3" :after-read="afterRead" multiple>
        <van-button icon="photograph" size="small" />
      </van-uploader>
      
      <van-field
        v-model="userInput"
        type="textarea"
        placeholder="Describe the issue..."
        :autosize="{ minHeight: 36, maxHeight: 100 }"
        style="flex: 1;"
      />
      
      <van-button type="primary" size="small" @click="sendMessage" :loading="loading" :disabled="!userInput.trim()">
        <van-icon name="send" />
      </van-button>
    </div>
    
    <van-action-sheet v-model:show="showFeedback" title="How was the support?">
      <div style="padding: 24px;">
        <van-button type="success" size="large" block @click="submitFeedback(true)" icon="like">
          Helpful
        </van-button>
        <van-button type="warning" size="large" block @click="submitFeedback(false)" icon="like-o" style="margin-top: 12px;">
          Not Helpful
        </van-button>
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { showToast, showDialog } from 'vant'
import api from '../api'

const route = useRoute()
const sessionUuid = route.params.sessionUuid
const token = route.query.token

const validated = ref(false)
const session = ref({})
const messages = ref([])
const userInput = ref('')
const uploadFiles = ref([])
const loading = ref(false)
const transferred = ref(false)
const showFeedback = ref(false)
const messagesContainer = ref(null)

const validateSession = async () => {
  if (!token) {
    showToast('Invalid link')
    return
  }
  
  try {
    const response = await api.diagnosis.validate(sessionUuid, token)
    if (response.success && response.data.valid) {
      session.value = response.data
      validated.value = true
      loadMessages()
    } else {
      showToast('Session invalid or expired')
    }
  } catch (error) {
    showToast('Failed to validate session')
  }
}

const loadMessages = async () => {
  try {
    const response = await api.diagnosis.getSession(sessionUuid)
    if (response.success && response.data.messages) {
      messages.value = response.data.messages.map(msg => ({
        role: msg.role.toLowerCase(),
        content: msg.content,
        time: new Date(msg.createdAt).toLocaleTimeString(),
        images: msg.imageUrls ? msg.imageUrls.split(',') : [],
        isHazard: msg.isHazardWarning
      }))
      
      scrollToBottom()
      
      if (response.data.session.transferredToHuman) {
        transferred.value = true
      }
    }
  } catch (error) {
    console.error('Failed to load messages:', error)
  }
}

const afterRead = (file) => {
  console.log('Image uploaded:', file)
}

const sendMessage = async () => {
  if (!userInput.value.trim()) return
  
  const messageText = userInput.value.trim()
  const imageFiles = uploadFiles.value.map(f => f.file)
  
  messages.value.push({
    role: 'user',
    content: messageText,
    time: new Date().toLocaleTimeString(),
    images: []
  })
  
  userInput.value = ''
  uploadFiles.value = []
  loading.value = true
  
  await scrollToBottom()
  
  try {
    const response = await api.diagnosis.chat(sessionUuid, token, messageText, imageFiles)
    
    if (response.success && response.data) {
      const aiMessage = {
        role: 'assistant',
        content: response.data.message,
        time: new Date().toLocaleTimeString(),
        isHazard: response.data.hazardDetected,
        guideUrl: response.data.guideUrl
      }
      
      messages.value.push(aiMessage)
      session.value.roundCount = response.data.roundNumber
      
      if (response.data.needsTransfer) {
        transferred.value = true
      } else if (response.data.guideUrl) {
        showFeedback.value = true
      }
      
      await scrollToBottom()
    }
  } catch (error) {
    showToast('Failed to send message')
  } finally {
    loading.value = false
  }
}

const submitFeedback = async (thumbsUp) => {
  showFeedback.value = false
  
  try {
    await api.diagnosis.feedback(sessionUuid, thumbsUp)
    
    if (thumbsUp) {
      showDialog({
        title: 'Thank you!',
        message: 'We\'re glad we could help. Feel free to contact us if you need further assistance.'
      })
    } else {
      showDialog({
        title: 'We\'re sorry',
        message: 'Our team will reach out to provide additional support.'
      })
      transferred.value = true
    }
  } catch (error) {
    showToast('Failed to submit feedback')
  }
}

const openGuide = (url) => {
  window.location.href = url
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

onMounted(() => {
  validateSession()
})
</script>
