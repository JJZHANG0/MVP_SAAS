# ToolFix 技术设计文档

## 1. 系统概述

ToolFix 是一个基于 AI 的电动工具售后诊断 SaaS 平台 Demo，旨在通过智能对话诊断自动拦截假故障，减少跨境电商卖家的非故障退货率和物流损失。

### 1.1 核心目标

- 假故障拦截率：≥ 30%（行业平均 15% 非故障退货）
- 高危场景转人工率：100%（零容错）
- AI 响应速度：≤ 5 秒/轮
- 系统可用性：99% （Demo 阶段）

---

## 2. 架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      Presentation Layer                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐              ┌──────────────────┐         │
│  │  Admin Frontend  │              │   H5 Frontend    │         │
│  │  (Vue 3 +        │              │   (Vue 3 +       │         │
│  │   Element Plus)  │              │    Vant 4)       │         │
│  └────────┬─────────┘              └────────┬─────────┘         │
│           │                                 │                   │
│           │         REST API (JSON)         │                   │
│           └─────────────────┬───────────────┘                   │
└──────────────────────────────┼──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│                     Application Layer                           │
├─────────────────────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────────────────┐     │
│  │           Spring Boot REST Controllers                 │     │
│  │  - ShopController (店铺管理)                            │     │
│  │  - ProductController (产品管理)                         │     │
│  │  - ManualController (说明书管理)                        │     │
│  │  - DiagnosisController (诊断对话)                       │     │
│  │  - SessionController (会话监控)                         │     │
│  │  - AdminController (后台管理)                           │     │
│  └────────────────────────────────────────────────────────┘     │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│                      Business Layer                             │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────┐  ┌──────────────────────┐             │
│  │  SecurityService    │  │  HazardDetection     │             │
│  │  (HMAC签名/验证)     │  │  Service             │             │
│  └─────────────────────┘  │  (硬规则高危检测)      │             │
│                           └──────────────────────┘             │
│  ┌─────────────────────┐  ┌──────────────────────┐             │
│  │  MockAIDiagnosis    │  │  MockManualParsing   │             │
│  │  Service            │  │  Service             │             │
│  │  (AI诊断Mock)        │  │  (PDF解析Mock)        │             │
│  └─────────────────────┘  └──────────────────────┘             │
│  ┌─────────────────────┐  ┌──────────────────────┐             │
│  │  MockShopify        │  │  Notification        │             │
│  │  Service            │  │  Service             │             │
│  │  (Shopify OAuth)    │  │  (邮件通知)           │             │
│  └─────────────────────┘  └──────────────────────┘             │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│                    Data Access Layer                            │
├─────────────────────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────────────────┐     │
│  │           Spring Data JPA Repositories                 │     │
│  │  - ShopRepository                                      │     │
│  │  - ProductRepository                                   │     │
│  │  - ManualRepository                                    │     │
│  │  - DiagnosisSessionRepository                          │     │
│  │  - MessageRepository                                   │     │
│  │  - KnowledgeBaseRepository                             │     │
│  │  - HazardKeywordRepository                             │     │
│  │  - AIUsageLogRepository                                │     │
│  └────────────────────────────────────────────────────────┘     │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│                    Infrastructure Layer                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  MySQL 8.0   │  │  Redis 7.x   │  │  MailHog     │          │
│  │  (关系数据库)  │  │  (缓存)       │  │  (邮件)       │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 技术选型理由

| 组件 | 技术 | 理由 |
|------|------|------|
| **后端框架** | Spring Boot 3.2.5 | 成熟稳定、生态完善、支持 Java 17 |
| **ORM** | Spring Data JPA | 减少样板代码、类型安全、易于测试 |
| **数据库** | MySQL 8.0 | 事务支持、成熟稳定、易于运维 |
| **缓存** | Redis 7.x | 高性能、支持多种数据结构 |
| **管理后台** | Vue 3 + Element Plus | 组件丰富、中文文档完善、开发效率高 |
| **消费者端** | Vue 3 + Vant 4 | 移动端专精、轻量级、主题美观 |
| **容器化** | Docker + Docker Compose | 环境一致性、快速部署、易于扩展 |

---

## 3. 核心功能设计

### 3.1 安全链接生成与验证

#### 3.1.1 设计目标

- 防止链接篡改
- 自动过期
- 无需消费者登录

#### 3.1.2 实现方案

```java
// 生成安全链接
1. 生成 UUID v4 作为 session_uuid
2. 计算过期时间 expiry_time = now + 7 days
3. 生成 HMAC-SHA256 签名：
   payload = session_uuid + ":" + expiry_timestamp
   token = HMAC-SHA256(payload, secret_key)
4. 构造链接：
   http://localhost:5173/diagnosis/{session_uuid}?token={token}

// 验证链接
1. 从 URL 提取 session_uuid 和 token
2. 从数据库查询 session 和 expiry_time
3. 重新计算 token' = HMAC-SHA256(session_uuid + ":" + expiry_time, secret_key)
4. 校验：
   - token == token'
   - now < expiry_time
   - session 存在
```

#### 3.1.3 安全性分析

| 攻击类型 | 防御措施 |
|---------|---------|
| **链接篡改** | HMAC-SHA256 签名，任何修改都会导致验证失败 |
| **重放攻击** | 过期时间戳，7 天后自动失效 |
| **暴力破解** | 256 位密钥，计算复杂度 2^256 |
| **会话劫持** | UUID v4 随机性，猜测概率 1/2^122 |

---

### 3.2 高危场景检测

#### 3.2.1 设计原则

- **确定性兜底**：不依赖 AI 自觉判断
- **零容错**：100% 转人工率
- **实时检测**：每条用户消息都检查
- **独立于 AI**：硬规则优先级高于 AI

#### 3.2.2 实现流程

```
用户输入消息
     │
     ▼
┌─────────────────────┐
│  HazardDetection    │
│  Service            │
│  (正则匹配)          │
└──────┬──────────────┘
       │
       ├─ 是 ─→ 立即终止对话
       │       发送安全响应
       │       转人工（标记urgent）
       │       发送邮件通知
       │       记录日志
       │
       └─ 否 ─→ 继续调用 AI 诊断
```

#### 3.2.3 高危关键词列表（部分）

| 类别 | 关键词 | 安全响应 |
|------|-------|---------|
| **火灾** | smoke, fire, burning smell | ⚠️ STOP IMMEDIATELY! Disconnect battery... |
| **电击** | electric shock, shocked, electrocuted | 🚨 CRITICAL! Do not use again... |
| **爆炸** | explosion, battery swelling | ⚠️ BATTERY SAFETY ALERT! Remove carefully... |
| **过热** | abnormal heat, extremely hot, melting | ⚠️ TEMPERATURE WARNING! Stop and cool down... |

#### 3.2.4 代码示例

```java
@Service
public class HazardDetectionService {
    private List<HazardKeyword> activeKeywords;
    private List<Pattern> patterns;
    
    public HazardDetectionResult detectHazards(String text) {
        String lowerText = text.toLowerCase();
        
        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            if (pattern.matcher(lowerText).find()) {
                HazardKeyword keyword = activeKeywords.get(i);
                return new HazardDetectionResult(true, keyword, keyword.getSafetyResponse());
            }
        }
        
        return new HazardDetectionResult(false, null, null);
    }
}
```

---

### 3.3 AI 诊断流程（Mock）

#### 3.3.1 多轮对话策略

```
轮次 1-2：收集基本信息
  - 电池是否充满？
  - 是否尝试过其他电池？
  - 开关位置？

轮次 3-4：匹配知识库
  - 根据对话历史匹配场景
  - 计算置信度 > 0.7
  - 发送引导页链接

轮次 5：强制转人工
  - 无论是否匹配，都转人工
  - 附带诊断摘要
```

#### 3.3.2 置信度计算

```java
if (keywordMatches >= 2 || (keywordCount <= 3 && keywordMatches >= 1)) {
    confidence = 0.75 + random(0.15);  // 0.75 - 0.90
    return GUIDE_LINK;
}

if (round == 3) {
    confidence = 0.5;
    return CONTINUE;
}

if (round >= 5) {
    confidence = 0.5;
    return TRANSFER_TO_HUMAN;
}
```

#### 3.3.3 知识库匹配算法

```java
private KnowledgeBase findMatchingKnowledge(String message, List<Message> history, List<KnowledgeBase> kb) {
    String fullConversation = combineMessages(message, history);
    
    for (KnowledgeBase k : kb) {
        String[] keywords = k.getKeywords().split(",");
        int matches = 0;
        
        for (String keyword : keywords) {
            if (fullConversation.contains(keyword.trim().toLowerCase())) {
                matches++;
            }
        }
        
        if (matches >= 2) {  // 至少匹配 2 个关键词
            return k;
        }
    }
    
    return null;
}
```

---

### 3.4 说明书解析（Mock）

#### 3.4.1 解析流程

```
PDF 上传
   │
   ▼
┌──────────────────┐
│  保存文件         │
│  状态：UNCONFIRMED│
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  异步解析任务     │
│  (3 秒延迟模拟)   │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  PDFBox 提取文本  │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  正则提取字段     │
│  - 产品名称       │
│  - 型号           │
│  - 电池信息       │
│  - 安全警告       │
│  - 保修条款       │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  卖家审核确认     │
│  状态：LOCKED     │
└──────────────────┘
```

#### 3.4.2 锁定机制

```java
@PostMapping("/{id}/confirm")
public ApiResponse<Manual> confirmManual(@PathVariable Long id, @RequestBody ConfirmManualRequest request) {
    Manual manual = manualRepository.findById(id).orElseThrow();
    
    if (manual.getStatus() == Manual.ManualStatus.LOCKED) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify locked manual");
    }
    
    // 允许修改非安全字段
    manual.setExtractedProductName(request.getExtractedProductName());
    manual.setExtractedModel(request.getExtractedModel());
    // ...
    
    // 锁定后不可修改
    manual.setStatus(Manual.ManualStatus.LOCKED);
    
    return ApiResponse.success(manual);
}

@PutMapping("/{id}")
public ApiResponse<Manual> updateManual(@PathVariable Long id) {
    Manual manual = manualRepository.findById(id).orElseThrow();
    
    if (manual.getStatus() == Manual.ManualStatus.LOCKED) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify locked manual");
    }
    
    // ...
}

@DeleteMapping("/{id}")
public ApiResponse<Void> deleteManual(@PathVariable Long id) {
    Manual manual = manualRepository.findById(id).orElseThrow();
    
    if (manual.getStatus() == Manual.ManualStatus.LOCKED) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete locked manual");
    }
    
    // ...
}
```

---

## 4. 数据库设计

### 4.1 ER 图（核心表）

```
┌──────────────┐         ┌──────────────┐
│   shops      │1       *│  products    │
│──────────────│◄────────│──────────────│
│ id (PK)      │         │ id (PK)      │
│ domain       │         │ shop_id (FK) │
│ access_token │         │ sku          │
└──────────────┘         │ product_name │
                         └──────┬───────┘
                                │1
                                │
                                │*
                         ┌──────▼───────┐
                         │   manuals    │
                         │──────────────│
                         │ id (PK)      │
                         │ product_id   │
                         │ status       │
                         │ safety_warn  │
                         │ warranty     │
                         └──────────────┘

┌──────────────────┐         ┌──────────────┐
│ diagnosis_      │1       *│  messages    │
│ sessions         │◄────────│──────────────│
│──────────────────│         │ id (PK)      │
│ id (PK)          │         │ session_id   │
│ session_uuid     │         │ role         │
│ secure_token     │         │ content      │
│ expiry_time      │         │ is_hazard    │
│ status           │         └──────────────┘
│ outcome          │
│ hazard_detected  │
│ transferred      │
└──────────────────┘

┌──────────────────┐
│ knowledge_base   │
│──────────────────│
│ id (PK)          │
│ scenario_name    │
│ symptoms         │
│ troubleshooting  │
│ keywords         │
└──────────────────┘

┌──────────────────┐
│ hazard_keywords  │
│──────────────────│
│ id (PK)          │
│ keyword          │
│ level            │
│ safety_response  │
└──────────────────┘
```

### 4.2 索引策略

```sql
-- 高频查询优化
CREATE INDEX idx_session_uuid ON diagnosis_sessions(session_uuid);
CREATE INDEX idx_session_status ON diagnosis_sessions(status);
CREATE INDEX idx_session_shop_created ON diagnosis_sessions(shop_id, created_at DESC);
CREATE INDEX idx_messages_session ON messages(session_id, created_at ASC);
CREATE INDEX idx_products_shop_sku ON products(shop_id, sku);

-- 全文搜索（可选）
ALTER TABLE knowledge_base ADD FULLTEXT INDEX ft_keywords(keywords);
```

---

## 5. 性能优化

### 5.1 数据库优化

| 优化项 | 措施 | 预期收益 |
|-------|------|---------|
| **连接池** | HikariCP，max=10, min=5 | 减少连接开销 |
| **批量插入** | `saveAll()` 替代循环 `save()` | 减少 Round Trip |
| **N+1 查询** | `@EntityGraph` 或 JOIN FETCH | 减少 SQL 数量 |
| **分页查询** | `Pageable` 限制结果集 | 减少内存占用 |
| **索引** | 关键字段索引 | 加速查询 |

### 5.2 缓存策略

```java
// Redis 缓存高频数据
@Cacheable(value = "knowledge", key = "#sku")
public List<KnowledgeBase> getKnowledgeForSku(String sku) { ... }

@Cacheable(value = "hazardKeywords")
public List<HazardKeyword> getActiveKeywords() { ... }

// 缓存失效
@CacheEvict(value = "knowledge", allEntries = true)
public void refreshKnowledge() { ... }
```

### 5.3 异步处理

```java
@Async
public void parseManualAsync(Long manualId) {
    // PDF 解析耗时操作
    // 不阻塞主线程
}

@Async
public void sendTransferNotification(DiagnosisSession session, boolean isHazard) {
    // 邮件发送不阻塞 HTTP 响应
}
```

---

## 6. 成本预估模型

### 6.1 假设条件

- **模型**：通义千问 qwen-max
- **定价**：输入 $0.02/1M tokens，输出 $0.06/1M tokens
- **平均对话**：3 轮
- **每轮 tokens**：输入 50，输出 100
- **月会话量**：1,000 / 5,000 / 20,000

### 6.2 计算公式

```
单次会话 Token 数 = (50 输入 + 100 输出) × 3 轮 = 450 tokens
单次会话成本 = (150 × 0.02 + 300 × 0.06) / 1,000,000 = $0.015

月成本 = 单次成本 × 月会话量
```

### 6.3 成本明细表

| 月会话量 | 月 Token 数 | 输入成本 | 输出成本 | 总成本 | 单次成本 |
|---------|-----------|---------|---------|--------|---------|
| **1,000** | 450,000 | $3.00 | $9.00 | **$12** | $0.012 |
| **5,000** | 2,250,000 | $15.00 | $45.00 | **$60** | $0.012 |
| **20,000** | 9,000,000 | $60.00 | $180.00 | **$240** | $0.012 |

### 6.4 优化建议

| 优化方向 | 措施 | 预期节省 |
|---------|------|---------|
| **减少轮次** | 提高首轮命中率 | -33% tokens |
| **缩短回复** | 优化 Prompt，要求简短回复 | -20% 输出 tokens |
| **缓存高频问答** | Redis 缓存重复问题 | -10% 总调用 |
| **批量处理** | 合并多个短请求 | -15% API 调用开销 |

---

## 7. 扩展性设计

### 7.1 多平台接入（Amazon）

```java
// 平台适配器接口
public interface PlatformAdapter {
    String generateAuthUrl(String domain, String state);
    Shop connectShop(String domain, String code);
    List<Order> syncOrders(Shop shop);
}

// Shopify 实现
@Service
public class ShopifyAdapter implements PlatformAdapter { ... }

// Amazon 实现（未来）
@Service
public class AmazonAdapter implements PlatformAdapter { ... }

// 工厂模式
public class PlatformAdapterFactory {
    public static PlatformAdapter getAdapter(String platform) {
        if ("SHOPIFY".equals(platform)) return new ShopifyAdapter();
        if ("AMAZON".equals(platform)) return new AmazonAdapter();
        throw new UnsupportedPlatformException(platform);
    }
}
```

### 7.2 多租户架构

#### 方案一：Schema 隔离

```sql
-- 每个租户独立 Schema
CREATE SCHEMA tenant_1001;
CREATE SCHEMA tenant_1002;

-- 数据完全隔离，安全性高
-- 但扩展性差，运维复杂
```

#### 方案二：Row Level 隔离（推荐）

```sql
-- 所有表增加 tenant_id
ALTER TABLE shops ADD COLUMN tenant_id BIGINT NOT NULL;
ALTER TABLE products ADD COLUMN tenant_id BIGINT NOT NULL;
...

-- 所有查询自动过滤
SELECT * FROM shops WHERE tenant_id = :current_tenant_id;

-- Spring Data JPA 拦截器
@Component
public class TenantInterceptor implements Interceptor {
    public String onPrepareStatement(String sql) {
        // 自动注入 tenant_id 过滤条件
    }
}
```

### 7.3 知识库自主编辑

```sql
-- 增加知识库类型
ALTER TABLE knowledge_base ADD COLUMN type ENUM('PLATFORM_PRESET', 'SKU_SPECIFIC');

-- SKU 专属知识
INSERT INTO knowledge_base (scenario_name, type, related_sku, ...)
VALUES ('Custom Scenario', 'SKU_SPECIFIC', 'TD-20V-DRILL-001', ...);

-- 查询逻辑
SELECT * FROM knowledge_base 
WHERE (type = 'PLATFORM_PRESET' OR related_sku = :sku) 
  AND active = true;
```

---

## 8. 安全与合规

### 8.1 GDPR 合规

| 要求 | 实现 |
|------|------|
| **数据最小化** | 只收集 email、name，不存储地址、电话 |
| **脱敏存储** | 敏感字段加密存储 |
| **数据查阅** | 提供 API 查询用户所有数据 |
| **数据删除** | 提供 API 删除用户所有数据 |
| **数据导出** | 提供 JSON 格式导出 |

### 8.2 数据加密

```java
// 店铺凭证加密存储
@Convert(converter = EncryptedStringConverter.class)
private String accessToken;

public class EncryptedStringConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return AES.encrypt(attribute, secret);
    }
    
    @Override
    public String convertToEntityAttribute(String dbData) {
        return AES.decrypt(dbData, secret);
    }
}
```

### 8.3 审计日志

```sql
-- 增加审计字段
ALTER TABLE manuals ADD COLUMN locked_by VARCHAR(255);
ALTER TABLE manuals ADD COLUMN locked_at TIMESTAMP;

-- 记录关键操作
INSERT INTO audit_logs (entity_type, entity_id, action, user_id, timestamp)
VALUES ('MANUAL', 123, 'LOCKED', 'admin@toolfix.com', NOW());
```

---

## 9. 监控与运维

### 9.1 健康检查

```java
@RestController
@RequestMapping("/actuator")
public class HealthController {
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "database", checkDatabase(),
            "redis", checkRedis(),
            "ai", checkAI()
        );
    }
}
```

### 9.2 关键指标

| 指标 | 阈值 | 告警 |
|------|------|------|
| **API 响应时间** | P95 < 500ms | > 1s 告警 |
| **高危检测延迟** | < 50ms | > 100ms 告警 |
| **数据库连接池** | 使用率 < 80% | > 90% 告警 |
| **Redis 内存** | 使用率 < 70% | > 85% 告警 |
| **AI 响应时间** | < 5s | > 8s 告警 |

### 9.3 日志级别

```yaml
logging:
  level:
    root: INFO
    com.toolfix: DEBUG
    com.toolfix.service.HazardDetectionService: WARN  # 高危场景必须记录
    org.hibernate.SQL: DEBUG  # 开发环境
```

---

## 10. 部署架构

### 10.1 开发环境

```
Docker Compose
   │
   ├─ MySQL Container
   ├─ Redis Container
   ├─ MailHog Container
   ├─ Backend Container (Spring Boot JAR)
   ├─ Admin Container (Nginx + Static Files)
   └─ H5 Container (Nginx + Static Files)
```

### 10.2 生产环境（未来）

```
                    ┌─────────────┐
                    │  CDN / OSS  │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │   Nginx LB  │
                    └──────┬──────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
    ┌──────▼──────┐ ┌─────▼──────┐ ┌─────▼──────┐
    │  Backend 1  │ │  Backend 2 │ │  Backend 3 │
    └──────┬──────┘ └─────┬──────┘ └─────┬──────┘
           │               │               │
           └───────────────┼───────────────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
    ┌──────▼──────┐ ┌─────▼──────┐ ┌─────▼──────┐
    │  MySQL主从   │ │  Redis集群  │ │  阿里云AI  │
    └─────────────┘ └────────────┘ └────────────┘
```

---

## 11. 技术债务与改进方向

| 当前状态 | 改进方向 | 优先级 |
|---------|---------|--------|
| **Mock AI** | 接入真实 Qwen API | 高 |
| **Mock PDF 解析** | 接入 Document Mind | 高 |
| **单体应用** | 微服务拆分（按需） | 低 |
| **同步 API** | 引入消息队列异步化 | 中 |
| **本地存储** | 对接 OSS 存储 | 中 |
| **无监控** | Prometheus + Grafana | 高 |
| **无日志聚合** | ELK / 阿里云日志服务 | 中 |

---

## 12. 结语

本文档描述了 ToolFix Demo 阶段的技术设计，重点验证 AI 诊断拦截假故障的可行性。系统架构预留了多平台接入、多租户 SaaS 化的扩展空间，但遵循"不为未确定需求过度设计"的原则。

**下一步行动**：
1. ✅ 完成 Demo 开发
2. ⏳ 接入真实 Shopify 店铺测试
3. ⏳ 收集 15-20 个真实故障测试用例
4. ⏳ 验证假故障拦截率是否达标
5. ⏳ 基于反馈迭代 AI Prompt 和知识库

---

**文档版本**：v1.0  
**最后更新**：2026-09-17  
**作者**：ToolFix 开发团队
