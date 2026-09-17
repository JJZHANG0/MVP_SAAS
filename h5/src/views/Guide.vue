<template>
  <div class="guide-page">
    <div class="guide-header">
      <h2 style="margin: 0; font-size: 24px;">Self-Check Guide</h2>
      <p style="margin: 8px 0 0; opacity: 0.9; font-size: 14px;">{{ guide.scenarioName }}</p>
    </div>
    
    <van-notice-bar color="#1989fa" background="#ecf9ff" left-icon="info-o">
      Follow these steps carefully to resolve the issue
    </van-notice-bar>
    
    <div style="margin-top: 24px;">
      <h3 style="font-size: 16px; margin-bottom: 12px;">What's Happening?</h3>
      <div style="background: #fff3cd; padding: 16px; border-radius: 8px; color: #856404; line-height: 1.6;">
        {{ guide.symptomDescription }}
      </div>
    </div>
    
    <div style="margin-top: 24px;">
      <h3 style="font-size: 16px; margin-bottom: 12px;">Why This Happens</h3>
      <div style="background: #d1ecf1; padding: 16px; border-radius: 8px; color: #0c5460; line-height: 1.6;">
        {{ guide.rootCause }}
      </div>
    </div>
    
    <div style="margin-top: 24px;">
      <h3 style="font-size: 16px; margin-bottom: 16px;">Troubleshooting Steps</h3>
      
      <div v-for="(step, index) in steps" :key="index" class="guide-step">
        <div style="display: flex; align-items: flex-start;">
          <span class="guide-step-number">{{ index + 1 }}</span>
          <div style="flex: 1;">
            <div style="font-weight: 500; margin-bottom: 8px;">{{ step }}</div>
            
            <div v-if="index === 0" class="placeholder-media">
              <div>
                <van-icon name="photograph" size="32" />
                <div style="margin-top: 8px; font-size: 12px;">Illustration Image</div>
              </div>
            </div>
            
            <div v-if="index === 1" class="placeholder-media">
              <div>
                <van-icon name="play-circle" size="32" />
                <div style="margin-top: 8px; font-size: 12px;">Video Demonstration</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <van-divider style="margin: 32px 0;" />
    
    <div style="text-align: center;">
      <h3 style="font-size: 18px; margin-bottom: 16px;">Did this solve your problem?</h3>
      
      <div class="feedback-buttons">
        <van-button type="success" size="large" @click="markResolved(true)" block icon="success">
          Yes, It's Resolved
        </van-button>
        <van-button type="warning" size="large" @click="markResolved(false)" block icon="cross">
          No, Still Not Working
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'

const route = useRoute()
const router = useRouter()
const slug = route.params.slug

const guide = ref({
  scenarioName: 'Battery in Sleep Mode',
  symptomDescription: 'Battery won\'t charge, LED lights don\'t turn on when pressing battery button, tool shows no power',
  rootCause: 'The battery has entered sleep/protection mode after being unused for a long period or fully discharged. This is a safety feature, not a defect.',
  troubleshootingSteps: `1. Remove battery from tool and charger
2. Press battery button 3-5 times rapidly
3. Firmly reinsert battery into charger until you hear a click
4. If LED still doesn't light up, try a different outlet
5. Wait 30 seconds and check if charging LED appears
6. Once charged, test in tool`
})

const guideData = {
  'battery-sleep-mode': {
    scenarioName: 'Battery in Sleep Mode',
    symptomDescription: 'Battery won\'t charge, LED lights don\'t turn on, tool shows no power',
    rootCause: 'The battery has entered sleep/protection mode after being unused. This is a safety feature, not a defect.',
    troubleshootingSteps: `1. Remove battery from tool and charger
2. Press battery button 3-5 times rapidly
3. Firmly reinsert battery into charger until you hear a click
4. Try a different outlet if LED doesn't light
5. Wait 30 seconds and check charging LED
6. Once charged, test in tool`
  },
  'forward-reverse-lock': {
    scenarioName: 'Forward/Reverse Lock Engaged',
    symptomDescription: 'Tool won\'t start, trigger doesn\'t work, no response when pressing',
    rootCause: 'The forward/reverse selector is in the middle (locked) position. This prevents accidental startup.',
    troubleshootingSteps: `1. Locate the forward/reverse switch above trigger
2. Push the switch FULLY to LEFT or RIGHT
3. Feel for the click into position
4. Try the trigger again
5. Switch must be in full forward or reverse to work`
  },
  'speed-control-low': {
    scenarioName: 'Speed Control Set Too Low',
    symptomDescription: 'Tool runs but seems weak, barely any power, much slower than expected',
    rootCause: 'The variable speed control is set to a low setting. Many users are unaware of this feature.',
    troubleshootingSteps: `1. Look for numbered dial (1-10 or 1-20) on top/side
2. Turn dial to higher number for more speed
3. Squeeze trigger FULLY (variable speed triggers)
4. For max power: dial highest + trigger fully squeezed
5. Test on actual work material`
  }
}

const steps = computed(() => {
  return guide.value.troubleshootingSteps.split('\n').filter(s => s.trim())
})

const markResolved = async (resolved) => {
  if (resolved) {
    await showDialog({
      title: 'Great!',
      message: 'We\'re happy your issue is resolved. Contact us anytime if you need help!'
    })
  } else {
    await showDialog({
      title: 'We\'ll Help',
      message: 'Our technical team will contact you within 24 hours to provide additional support.'
    })
  }
  
  showToast('Thank you for your feedback')
}

onMounted(() => {
  if (guideData[slug]) {
    guide.value = guideData[slug]
  }
})
</script>

<style scoped>
.guide-page {
  min-height: 100vh;
  background: var(--bg-base);
  padding: var(--space-6) var(--space-4);
  padding-bottom: calc(var(--space-8) + env(safe-area-inset-bottom));
}

/* Premium Header with Gradient */
.guide-header {
  text-align: center;
  padding: var(--space-6) var(--space-4);
  background: linear-gradient(135deg, var(--brand-primary) 0%, var(--brand-primary-hover) 100%);
  border-radius: var(--radius-lg);
  color: var(--text-inverse);
  margin: 0 -16px var(--space-6);
  box-shadow: var(--shadow-lg);
  position: relative;
  overflow: hidden;
}

.guide-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
}

.guide-header h2 {
  position: relative;
  z-index: 1;
}

.guide-header p {
  position: relative;
  z-index: 1;
}

/* Guide Steps */
.guide-step {
  background: var(--bg-elevated);
  padding: var(--space-4);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-3);
  border: 1px solid var(--neutral-200);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
}

.guide-step:active {
  transform: scale(0.99);
  box-shadow: var(--shadow-xs);
}

.guide-step-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--brand-primary) 0%, var(--brand-primary-hover) 100%);
  color: var(--text-inverse);
  font-weight: var(--font-bold);
  font-size: var(--text-sm);
  margin-right: var(--space-3);
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
}

/* Placeholder Media */
.placeholder-media {
  background: var(--bg-chat);
  border-radius: var(--radius-md);
  padding: var(--space-6);
  text-align: center;
  color: var(--text-tertiary);
  margin-top: var(--space-3);
  border: 2px dashed var(--neutral-300);
}

/* Feedback Buttons */
.feedback-buttons {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  margin-top: var(--space-5);
}

/* Enhanced Information Boxes */
.guide-page h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-3);
}

.guide-page > div {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
