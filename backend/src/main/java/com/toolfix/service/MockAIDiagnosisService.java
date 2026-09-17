package com.toolfix.service;

import com.toolfix.domain.*;
import com.toolfix.repository.AIUsageLogRepository;
import com.toolfix.repository.KnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockAIDiagnosisService {
    
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final AIUsageLogRepository aiUsageLogRepository;
    private final Random random = new Random();
    
    @Value("${toolfix.ai.max-conversation-rounds}")
    private int maxConversationRounds;
    
    @Value("${toolfix.ai.confidence-threshold}")
    private double confidenceThreshold;
    
    @Value("${toolfix.ai.response-delay-ms}")
    private long responseDelayMs;
    
    public DiagnosisResponse diagnose(DiagnosisSession session, String userMessage, List<Message> conversationHistory, Manual manual) {
        Instant start = Instant.now();
        
        try {
            Thread.sleep(responseDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        int currentRound = session.getRoundCount() + 1;
        String productSku = session.getProduct().getSku();
        
        List<KnowledgeBase> knowledgeList = knowledgeBaseRepository.findActiveKnowledgeForSku(productSku);
        
        String response;
        double confidence;
        DiagnosisDecision decision;
        String matchedScenario = null;
        
        if (currentRound >= maxConversationRounds) {
            response = "I've gathered information from our conversation, but I'd like to ensure you get the best possible support. Let me connect you with our technical team for further assistance.";
            confidence = 0.5;
            decision = DiagnosisDecision.TRANSFER_TO_HUMAN;
        } else if (currentRound <= 2) {
            response = generateInitialQuestion(userMessage, currentRound);
            confidence = 0.4;
            decision = DiagnosisDecision.CONTINUE;
        } else {
            KnowledgeBase matchedKnowledge = findMatchingKnowledge(userMessage, conversationHistory, knowledgeList);
            
            if (matchedKnowledge != null && random.nextDouble() > 0.3) {
                matchedScenario = matchedKnowledge.getScenarioName();
                response = String.format("Based on what you've described, this sounds like %s. " +
                    "I have a step-by-step guide that can help you resolve this. Would you like me to share it with you?",
                    matchedKnowledge.getScenarioName().toLowerCase());
                confidence = 0.75 + (random.nextDouble() * 0.15);
                decision = DiagnosisDecision.FALSE_FAULT_GUIDE;
            } else if (currentRound == 3) {
                response = generateFollowUpQuestion(userMessage, conversationHistory);
                confidence = 0.5;
                decision = DiagnosisDecision.CONTINUE;
            } else {
                response = "Thank you for providing all this information. To ensure you receive the most accurate solution, I'm going to connect you with our specialist team. They'll be able to provide detailed technical support.";
                confidence = 0.6;
                decision = DiagnosisDecision.TRANSFER_TO_HUMAN;
            }
        }
        
        long durationMs = Duration.between(start, Instant.now()).toMillis();
        logAIUsage(session, userMessage, response, durationMs);
        
        return new DiagnosisResponse(response, confidence, decision, matchedScenario, currentRound);
    }
    
    private String generateInitialQuestion(String userMessage, int round) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (round == 1) {
            if (lowerMessage.contains("not work") || lowerMessage.contains("won't start") || lowerMessage.contains("dead")) {
                return "I understand your tool isn't working. Let me help you troubleshoot. First, is the battery fully charged and properly inserted?";
            } else if (lowerMessage.contains("battery") || lowerMessage.contains("charge")) {
                return "I see you're having battery issues. Can you tell me: does the LED on the battery light up when you press the power button on it?";
            } else if (lowerMessage.contains("power") || lowerMessage.contains("weak")) {
                return "I understand you're experiencing power issues. Can you confirm: is the forward/reverse switch in the correct position (not in the middle)?";
            } else if (lowerMessage.contains("speed") || lowerMessage.contains("slow")) {
                return "Let's check the speed setting. Is the speed control dial set to the desired level? Some tools have variable speed triggers as well.";
            } else {
                return "Thanks for reaching out. To help diagnose the issue, can you describe exactly what happens when you try to use the tool?";
            }
        } else {
            if (lowerMessage.contains("yes") || lowerMessage.contains("charged") || lowerMessage.contains("inserted")) {
                return "Good. Now, have you tried the tool with a different battery if you have one available? This will help us determine if it's a battery or tool issue.";
            } else if (lowerMessage.contains("no") || lowerMessage.contains("not charge") || lowerMessage.contains("light")) {
                return "I see. The battery might be in sleep mode. Have you tried removing and reinserting the battery firmly until you hear a click?";
            } else {
                return "Thanks for that information. Can you also check if there's any debris in the battery compartment or around the contacts?";
            }
        }
    }
    
    private String generateFollowUpQuestion(String userMessage, List<Message> history) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("different battery") || lowerMessage.contains("another battery")) {
            return "Have you checked if the forward/reverse selector switch is in the correct position? It should be fully pushed to one side, not in the middle locked position.";
        } else if (lowerMessage.contains("click") || lowerMessage.contains("reinstalled")) {
            return "Great. After reinserting the battery, does the battery indicator show any lights now?";
        } else {
            return "One more thing to check: is there a speed control dial or trigger on your tool, and if so, what position is it in?";
        }
    }
    
    private KnowledgeBase findMatchingKnowledge(String userMessage, List<Message> history, List<KnowledgeBase> knowledgeList) {
        String lowerMessage = userMessage.toLowerCase();
        StringBuilder fullConversation = new StringBuilder(lowerMessage);
        
        for (Message msg : history) {
            if (msg.getRole() == Message.MessageRole.USER) {
                fullConversation.append(" ").append(msg.getContent().toLowerCase());
            }
        }
        
        String fullText = fullConversation.toString();
        
        for (KnowledgeBase kb : knowledgeList) {
            String keywords = kb.getKeywords();
            if (keywords != null) {
                String[] keywordArray = keywords.split(",");
                int matches = 0;
                for (String keyword : keywordArray) {
                    if (fullText.contains(keyword.trim().toLowerCase())) {
                        matches++;
                    }
                }
                
                if (matches >= 2 || (keywordArray.length <= 3 && matches >= 1)) {
                    return kb;
                }
            }
        }
        
        return null;
    }
    
    private void logAIUsage(DiagnosisSession session, String request, String response, long durationMs) {
        AIUsageLog log = new AIUsageLog();
        log.setSession(session);
        log.setOperation("DIAGNOSIS");
        log.setModelName("qwen-max-mock");
        
        int inputTokens = estimateTokens(request);
        int outputTokens = estimateTokens(response);
        log.setInputTokens(inputTokens);
        log.setOutputTokens(outputTokens);
        log.setTotalTokens(inputTokens + outputTokens);
        
        BigDecimal inputCost = BigDecimal.valueOf(inputTokens).multiply(BigDecimal.valueOf(0.00002));
        BigDecimal outputCost = BigDecimal.valueOf(outputTokens).multiply(BigDecimal.valueOf(0.00006));
        log.setEstimatedCost(inputCost.add(outputCost));
        
        log.setResponseTimeMs(durationMs);
        log.setRequestSummary(request.substring(0, Math.min(100, request.length())));
        log.setResponseSummary(response.substring(0, Math.min(100, response.length())));
        
        aiUsageLogRepository.save(log);
    }
    
    private int estimateTokens(String text) {
        return (int) Math.ceil(text.length() / 4.0);
    }
    
    public enum DiagnosisDecision {
        CONTINUE,
        FALSE_FAULT_GUIDE,
        TRANSFER_TO_HUMAN
    }
    
    public static class DiagnosisResponse {
        private final String message;
        private final double confidence;
        private final DiagnosisDecision decision;
        private final String matchedScenario;
        private final int roundNumber;
        
        public DiagnosisResponse(String message, double confidence, DiagnosisDecision decision, 
                                String matchedScenario, int roundNumber) {
            this.message = message;
            this.confidence = confidence;
            this.decision = decision;
            this.matchedScenario = matchedScenario;
            this.roundNumber = roundNumber;
        }
        
        public String getMessage() { return message; }
        public double getConfidence() { return confidence; }
        public DiagnosisDecision getDecision() { return decision; }
        public String getMatchedScenario() { return matchedScenario; }
        public int getRoundNumber() { return roundNumber; }
    }
}
