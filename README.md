# ToolFix - 电动工具 AI 售后诊断系统

> 🔧 基于 AI 的智能售后诊断平台 Demo，有效拦截假故障，提升客户满意度

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-42b883.svg)](https://vuejs.org/)

## 📋 项目概述

ToolFix 是一个面向跨境电商卖家的 AI 售后诊断 SaaS 平台 Demo。通过智能对话诊断，自动识别并拦截"假故障"（用户误操作），将真故障可靠转给人工客服，从而降低退货率、物流损失和客服压力。

### 核心价值

- **假故障拦截率**：目标从行业平均 15% 降低到 10%
- **高危场景 100% 转人工**：冒烟、起火、漏电等安全问题强制转人工
- **多轮智能诊断**：最多 5 轮对话，结合产品说明书和知识库
- **成本透明**：实时 AI Token 用量统计，月度成本可观测

---

## 🏗️ 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                     ToolFix 系统架构                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐         ┌──────────────┐                │
│  │  管理后台     │◄───────►│  Spring Boot  │                │
│  │  (Vue3 +     │         │  REST API     │                │
│  │  Element Plus)│         └───────┬──────┘                │
│  └──────────────┘                 │                        │
│                                   │                        │
│  ┌──────────────┐                 │                        │
│  │  消费者 H5    │◄────────────────┤                        │
│  │  (Vue3 +     │                 │                        │
│  │  Vant 4)     │                 │                        │
│  └──────────────┘                 │                        │
│                                   │                        │
│                         ┌─────────▼─────────┐              │
│                         │  Business Layer   │              │
│                         ├───────────────────┤              │
│                         │ • AI Diagnosis    │              │
│                         │ • Hazard Detection│              │
│                         │ • Manual Parsing  │              │
│                         │ • Shopify Mock    │              │
│                         └─────────┬─────────┘              │
│                                   │                        │
│                         ┌─────────▼─────────┐              │
│                         │   Data Layer      │              │
│                         ├───────────────────┤              │
│                         │ MySQL 8.0         │              │
│                         │ Redis 7.x         │              │
│                         └───────────────────┘              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

| 层级 | 技术选型 | 说明 |
|------|---------|------|
| **管理后台** | Vue 3 + Vite + Element Plus + Pinia | 中文界面，现代化 SaaS 风格 |
| **消费者端** | Vue 3 + Vite + Vant 4 | 英文界面，移动端优先 |
| **后端 API** | Java 17 + Spring Boot 3.2.5 | RESTful API，分层架构 |
| **AI 诊断** | Mock AI Service（可替换为 Qwen） | 多轮对话、置信度评估 |
| **数据库** | MySQL 8.0 | 存储店铺、产品、会话数据 |
| **缓存** | Redis 7.x | 会话状态、热点数据缓存 |
| **容器化** | Docker + Docker Compose | 一键启动全栈环境 |
| **邮件** | MailHog（开发环境） | 本地邮件调试 |

---

## 🚀 快速开始

### 前置要求

- **Docker** 20.10+ 和 **Docker Compose** 2.0+
- **或** 本地环境：
  - JDK 17+
  - Node.js 18+
  - MySQL 8.0+
  - Redis 7.x
  - Maven 3.9+

### 方式一：Docker Compose（推荐）

```bash
# 克隆仓库
git clone <repository-url>
cd toolfix

# 一键启动所有服务
docker-compose up -d

# 等待服务启动（约 2-3 分钟）
docker-compose logs -f backend

# 访问应用
# 管理后台：http://localhost:3000
# 消费者 H5：http://localhost:5173
# 后端 API：http://localhost:8080/api
# MailHog：http://localhost:8025
```

### 方式二：本地开发环境

```bash
# 1. 启动 MySQL 和 Redis
docker-compose up -d mysql redis mailhog

# 2. 启动后端
cd backend
mvn clean spring-boot:run

# 3. 启动管理后台（新终端）
cd admin
npm install
npm run dev

# 4. 启动消费者 H5（新终端）
cd h5
npm install
npm run dev
```

### 初始数据

系统启动后会自动初始化以下数据：

- **Demo 店铺**：`demo-tools-shop.myshopify.com`
- **产品**：3 款电动工具（钻机、冲击扳手、圆锯）
- **知识库**：15+ 高频假故障场景
- **高危关键词**：17 个安全场景关键词

---

## 📱 功能演示

### 1. 管理后台功能

#### 仪表盘
- 实时统计：总会话数、假故障拦截数/率、转人工数、高危场景数
- 最近会话列表
- 快速操作入口

#### 店铺管理
- 一键连接 Shopify 店铺（Mock OAuth）
- 订单自动同步（Webhook 模拟）

#### 产品与说明书
- 产品列表管理
- PDF 说明书上传（≤50MB）
- 自动解析关键信息（产品名称、型号、电池、安全警告、保修条款）
- 卖家确认后**锁定**（安全与保修条款不可修改）

#### 诊断会话监控
- 会话列表（支持状态、结果筛选）
- 会话详情（完整对话记录、置信度、诊断摘要）
- 高危场景醒目标记
- 人工客服聊天接管

#### 知识库
- 查看平台预置的 15+ 假故障场景
- 症状描述、根本原因、排查步骤

#### AI 用量统计
- Token 用量、成本统计
- 月度成本预估（1k/5k/20k 会话）

---

### 2. 消费者 H5 诊断流程

#### 安全链接访问
```
格式：http://localhost:5173/diagnosis/{UUID}?token={HMAC-SHA256}
有效期：7天（可配置）
安全性：UUID v4 + HMAC-SHA256 + 过期时间戳
```

#### 对话诊断
1. **用户描述故障**（文字 + 图片）
2. **AI 多轮追问**（最多 5 轮）
   - 每轮检查高危关键词（硬规则，AI 之外）
   - 评估诊断置信度
3. **结果处理**：
   - **假故障** → 发送自查引导页链接
   - **真故障 / 低置信度 / 满 5 轮** → 转人工
   - **高危场景** → 立即转人工 + 安全提示

#### 高危关键词示例
- `smoke`, `fire`, `spark`, `electric shock`
- `battery swelling`, `abnormal heat`, `burning smell`
- 检测到任一关键词 → **强制转人工 + 安全响应**

#### 自查引导页
- 分步排查图文说明
- 视频演示占位符
- "已解决 / 未解决" 反馈

#### 反馈机制
- 👍 / 👎 评价
- 👎 负反馈 → 触发转人工评估

---

## 🗂️ 项目结构

```
toolfix/
├── admin/                    # 管理后台（Vue 3 + Element Plus）
│   ├── src/
│   │   ├── api/              # API 接口封装
│   │   ├── layouts/          # 布局组件
│   │   ├── router/           # 路由配置
│   │   ├── views/            # 页面组件
│   │   │   ├── Dashboard.vue
│   │   │   ├── Shops.vue
│   │   │   ├── Products.vue
│   │   │   ├── Manuals.vue
│   │   │   ├── Sessions.vue
│   │   │   ├── SessionDetail.vue
│   │   │   ├── Knowledge.vue
│   │   │   └── AIUsage.vue
│   │   └── main.js
│   ├── Dockerfile
│   └── package.json
│
├── h5/                       # 消费者 H5（Vue 3 + Vant 4）
│   ├── src/
│   │   ├── api/
│   │   ├── router/
│   │   ├── views/
│   │   │   ├── Diagnosis.vue     # 诊断对话
│   │   │   ├── Guide.vue         # 自查引导
│   │   │   └── NotFound.vue
│   │   └── main.js
│   ├── Dockerfile
│   └── package.json
│
├── backend/                  # 后端 API（Spring Boot）
│   ├── src/main/java/com/toolfix/
│   │   ├── domain/           # 实体类（JPA）
│   │   │   ├── Shop.java
│   │   │   ├── Product.java
│   │   │   ├── Manual.java
│   │   │   ├── DiagnosisSession.java
│   │   │   ├── Message.java
│   │   │   ├── KnowledgeBase.java
│   │   │   ├── HazardKeyword.java
│   │   │   └── AIUsageLog.java
│   │   ├── repository/       # 数据访问层
│   │   ├── service/          # 业务逻辑
│   │   │   ├── SecurityService.java           # HMAC 签名
│   │   │   ├── HazardDetectionService.java    # 高危检测
│   │   │   ├── MockAIDiagnosisService.java    # AI 诊断 Mock
│   │   │   ├── MockManualParsingService.java  # PDF 解析 Mock
│   │   │   ├── MockShopifyService.java        # Shopify Mock
│   │   │   └── NotificationService.java       # 邮件通知
│   │   ├── controller/       # REST 控制器
│   │   │   ├── ShopController.java
│   │   │   ├── ProductController.java
│   │   │   ├── ManualController.java
│   │   │   ├── DiagnosisController.java
│   │   │   ├── SessionController.java
│   │   │   └── AdminController.java
│   │   ├── config/
│   │   │   ├── DataSeeder.java               # 初始数据
│   │   │   └── WebConfig.java                # CORS 配置
│   │   └── ToolFixApplication.java
│   ├── Dockerfile
│   └── pom.xml
│
├── docker/                   # Docker 配置
│   ├── mysql/
│   └── redis/
│
├── docs/                     # 文档
│   └── TECH_DESIGN.md        # 技术设计文档
│
├── docker-compose.yml        # Docker Compose 配置
└── README.md                 # 本文件
```

---

## 🔗 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| **管理后台** | http://localhost:3000 | 卖家后台（中文） |
| **消费者 H5** | http://localhost:5173 | 诊断对话（英文） |
| **后端 API** | http://localhost:8080/api | RESTful API |
| **MailHog** | http://localhost:8025 | 邮件调试工具 |
| **MySQL** | localhost:3306 | 数据库（toolfix/toolfix123） |
| **Redis** | localhost:6379 | 缓存服务 |

---

## 🧪 测试流程（E2E）

### 1. 创建诊断会话

```bash
# 在管理后台操作
1. 访问 http://localhost:3000
2. 进入"产品管理" → 确认已有 3 款产品
3. 进入"诊断会话" → 点击右上角"创建新会话"（如有此功能）
   或使用 API 创建：
   
   curl -X POST http://localhost:8080/api/diagnosis/create-session \
     -H "Content-Type: application/json" \
     -d '{
       "shopId": 1,
       "productId": 1,
       "orderId": "shopify_order_1000",
       "customerEmail": "test@example.com",
       "customerName": "Test Customer"
     }'
   
# 响应示例
{
  "success": true,
  "data": {
    "sessionUuid": "550e8400-e29b-41d4-a716-446655440000",
    "secureLink": "http://localhost:5173/diagnosis/550e8400-e29b-41d4-a716-446655440000?token=abc123...",
    "expiryTime": "2026-09-24T06:46:00"
  }
}
```

### 2. 消费者诊断对话

```bash
# 访问安全链接
打开浏览器，访问上面返回的 secureLink

# 模拟对话
1. 输入："My drill won't start, no power at all"
   → AI 回复：询问电池是否充满、是否正确插入

2. 输入："Battery is charged and inserted"
   → AI 回复：询问是否尝试过其他电池

3. 输入："smoke coming out"（高危关键词！）
   → AI 立即回复：⚠️ 安全警告，停止使用，转人工

# 预期结果
- 后台"诊断会话"列表显示该会话
- 状态：已转人工
- 结果：高危检测
- 高危标记：⚠️ 红色醒目
```

### 3. 假故障拦截测试

```bash
# 访问新的诊断会话链接

1. 输入："Tool won't start"
   → AI：Is the battery fully charged and inserted?

2. 输入："Yes, battery is good"
   → AI：Have you checked the forward/reverse switch?

3. 输入："Switch is in the middle"
   → AI：识别到 "forward-reverse-lock" 场景
   → 发送自查引导页链接

# 点击引导页链接
- 显示详细排查步骤
- 图片/视频占位符
- "已解决 / 未解决" 按钮

# 预期结果
- 后台统计：假故障拦截 +1
- 会话状态：等待反馈
- 结果：假故障拦截
```

### 4. 人工接管

```bash
# 在管理后台
1. 进入"诊断会话" → 筛选"已转人工"
2. 点击某个会话 → 查看详情
3. 在"人工接管"区域输入回复
4. 点击"发送回复"

# 预期结果
- 消费者 H5 端收到人工回复（聊天气泡标记"人工回复"）
- 邮件通知发送到 MailHog (http://localhost:8025)
```

---

## 💰 成本预估

基于通义千问 API 定价（输入 $0.02/1M tokens，输出 $0.06/1M tokens）：

| 月会话量 | 预估月成本 | 单次会话成本 | 说明 |
|---------|-----------|------------|------|
| **1,000** | $12 - $18 | $0.012 - $0.018 | 小型卖家，日均 30-40 会话 |
| **5,000** | $60 - $90 | $0.012 - $0.018 | 中型卖家，日均 150-200 会话 |
| **20,000** | $240 - $360 | $0.012 - $0.018 | 大型卖家，日均 600-700 会话 |

**假设**：
- 平均每次会话 3 轮对话
- 每轮输入 ~50 tokens，输出 ~100 tokens
- 总计 ~150 输入 + ~300 输出 = ~450 tokens/会话

**优化建议**：
- 缩短 AI 回复长度 → 降低输出 tokens
- 提高一次诊断准确率 → 减少轮次
- 缓存高频问答 → 减少 API 调用

---

## 🔐 安全特性

1. **安全链接**：
   - UUID v4 随机生成
   - HMAC-SHA256 签名防篡改
   - 过期时间戳（7 天可配）
   - 三重校验：签名、过期、存在性

2. **高危检测**：
   - **硬规则兜底**：不依赖 AI 自觉判断
   - 正则匹配 17+ 关键词
   - 检测到 → 立即终止对话 + 转人工

3. **数据保护**：
   - 消费者信息最小化收集
   - 敏感字段脱敏存储
   - 店铺凭证加密存储

4. **说明书锁定**：
   - 安全警告和保修条款确认后 **LOCKED**
   - 任何 API 修改/删除均返回 403
   - AI 引用必须与锁定原文一致

---

## 🎯 核心业务逻辑

### 1. 诊断流程状态机

```
开始 → 校验链接 → 进行中 → ┬→ 假故障 → 等待反馈 → 已解决 / 未解决
                          ├→ 真故障 → 已转人工
                          ├→ 高危场景 → 已转人工（紧急）
                          ├→ 低置信度 → 已转人工
                          ├→ 满 5 轮 → 已转人工
                          └→ 负反馈 → 已转人工
```

### 2. 高危检测机制

```java
// 伪代码
for each user_message:
    hazard_result = HazardDetectionService.detect(user_message)  // 硬规则
    
    if hazard_result.detected:
        send_safety_response(hazard_result.safetyText)
        transfer_to_human(reason="Hazard: " + hazard_result.keyword, urgent=true)
        send_email_notification(hazard=true)
        break
    
    // 仅当无高危时，才调用 AI
    ai_response = AI_Diagnosis(user_message, history, manual, knowledge)
    ...
```

### 3. AI Mock 诊断逻辑

```java
// 简化逻辑
if (round <= 2) {
    return generateInitialQuestion();
} else if (round >= 5) {
    return transferToHuman("Max rounds reached");
} else {
    matched_knowledge = matchKnowledge(conversation);
    if (matched_knowledge && confidence > 0.7) {
        return sendGuideLink(matched_knowledge);
    } else {
        return continueOrTransfer();
    }
}
```

---

## 📊 数据模型（核心表）

### shops - 店铺
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| shopify_domain | VARCHAR(255) | Shopify 域名 |
| access_token | VARCHAR(500) | 访问令牌（加密） |
| owner_email | VARCHAR(255) | 店主邮箱 |
| active | BOOLEAN | 是否激活 |

### products - 产品
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| shop_id | BIGINT | 所属店铺 |
| sku | VARCHAR(100) | SKU |
| product_name | VARCHAR(255) | 产品名称 |
| battery_voltage | VARCHAR(50) | 电池电压 |
| has_manual | BOOLEAN | 是否有说明书 |

### manuals - 说明书
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| product_id | BIGINT | 关联产品 |
| status | ENUM | UNCONFIRMED / LOCKED |
| extracted_safety_warnings | TEXT | 安全警告（锁定后不可改） |
| extracted_warranty_terms | TEXT | 保修条款（锁定后不可改） |

### diagnosis_sessions - 诊断会话
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| session_uuid | VARCHAR(36) | UUID v4 |
| secure_token | VARCHAR(255) | HMAC-SHA256 |
| expiry_time | TIMESTAMP | 过期时间 |
| status | ENUM | IN_PROGRESS / TRANSFERRED / RESOLVED |
| outcome | ENUM | FALSE_FAULT_INTERCEPTED / HAZARD_DETECTED / ... |
| hazard_detected | BOOLEAN | 是否检测到高危 |
| transferred_to_human | BOOLEAN | 是否已转人工 |
| final_confidence | DOUBLE | 最终置信度 |

### messages - 消息
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| session_id | BIGINT | 所属会话 |
| role | ENUM | USER / ASSISTANT / SYSTEM |
| content | TEXT | 消息内容 |
| is_hazard_warning | BOOLEAN | 是否为安全警告 |
| is_from_human | BOOLEAN | 是否来自人工客服 |

### knowledge_base - 知识库
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| scenario_name | VARCHAR(255) | 场景名称 |
| symptom_description | TEXT | 症状描述 |
| troubleshooting_steps | TEXT | 排查步骤 |
| guide_page_slug | VARCHAR(100) | 引导页路径 |
| keywords | TEXT | 关键词（逗号分隔） |

### hazard_keywords - 高危关键词
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| keyword | VARCHAR(100) | 关键词 |
| level | ENUM | CRITICAL / HIGH / MEDIUM |
| safety_response | TEXT | 安全响应文本 |

---

## 🔧 配置说明

### 环境变量（backend/src/main/resources/application.yml）

```yaml
toolfix:
  security:
    hmac-secret: toolfix-super-secret-key-for-demo-only-change-in-production
    link-expiry-days: 7
  
  ai:
    mock-enabled: true              # true = Mock，false = 真实 AI
    max-conversation-rounds: 5
    confidence-threshold: 0.7
    response-delay-ms: 1500         # 模拟 AI 延迟
  
  shopify:
    mock-enabled: true              # true = Mock OAuth
    client-id: ${SHOPIFY_CLIENT_ID}
    client-secret: ${SHOPIFY_CLIENT_SECRET}
  
  storage:
    upload-dir: /tmp/toolfix/uploads
  
  notification:
    seller-email: seller@toolfix.demo
```

### 切换真实 AI

```yaml
# 修改 application.yml
toolfix:
  ai:
    mock-enabled: false
    alibaba:
      api-key: ${ALIBABA_AI_API_KEY}
      model: qwen-max
      embedding-model: text-embedding-v3
      dashvector-endpoint: ${DASHVECTOR_ENDPOINT}
```

然后实现 `RealAIDiagnosisService` 替换 `MockAIDiagnosisService`。

---

## 🌐 未来演进方向（架构预留）

### 第二阶段：Amazon 接入
- 多平台适配器模式
- 统一订单抽象层
- 平台特有字段扩展表

### 第三阶段：多租户 SaaS
- 租户隔离（Schema / Row Level）
- 订阅计费模块
- 细粒度权限（RBAC）
- 数据分析与 ROI 报表

### 第四阶段：知识库自主编辑
- 卖家上传 SKU 专属知识
- 知识版本管理
- A/B 测试不同知识效果

**架构原则**：
- ✅ 预留扩展点，不过度设计
- ✅ 接口隔离，降低耦合
- ✅ 配置驱动，灵活切换
- ❌ 不为未确定需求写代码

---

## 🐛 故障排查

### 1. 后端无法启动
```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 查看后端日志
docker-compose logs backend

# 常见问题：
# - MySQL 未启动 → 等待 healthcheck 通过
# - 端口占用 → 修改 docker-compose.yml 端口映射
```

### 2. 前端无法访问后端
```bash
# 检查 CORS 配置
# backend/src/main/java/com/toolfix/config/WebConfig.java

# 检查代理配置
# admin/vite.config.js 和 h5/vite.config.js 的 proxy 设置
```

### 3. MailHog 无法接收邮件
```bash
# 查看 MailHog 日志
docker-compose logs mailhog

# 访问 http://localhost:8025 查看界面
```

---

## 📝 开发备忘

### 添加新的高危关键词

```sql
INSERT INTO hazard_keywords (keyword, level, safety_response, active, created_at, updated_at)
VALUES ('explosion', 'CRITICAL', '🚨 CRITICAL! Stop using immediately...', true, NOW(), NOW());
```

### 添加新的知识库场景

```sql
INSERT INTO knowledge_base (scenario_name, symptom_description, root_cause, troubleshooting_steps, guide_page_slug, keywords, type, active, created_at, updated_at)
VALUES ('New Scenario', 'Symptoms...', 'Cause...', 'Steps...', 'new-scenario', 'keyword1,keyword2', 'PLATFORM_PRESET', true, NOW(), NOW());
```

### 手动创建诊断会话

```bash
curl -X POST http://localhost:8080/api/diagnosis/create-session \
  -H "Content-Type: application/json" \
  -d '{
    "shopId": 1,
    "productId": 1,
    "orderId": "test_order_001",
    "customerEmail": "customer@test.com",
    "customerName": "Test User"
  }'
```

---

## 📄 许可证

MIT License

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📮 联系方式

- **项目仓库**：[GitHub](https://github.com/your-org/toolfix)
- **问题反馈**：[Issues](https://github.com/your-org/toolfix/issues)

---

## 🙏 致谢

- Spring Boot Team
- Vue.js Team
- Element Plus Team
- Vant UI Team
- 通义千问（Qwen）

---

**祝您使用愉快！如有问题，欢迎提 Issue！** 🎉
