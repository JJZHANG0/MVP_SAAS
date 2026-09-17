package com.toolfix.service;

import com.toolfix.domain.HazardKeyword;
import com.toolfix.repository.HazardKeywordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class HazardDetectionService {
    
    private final HazardKeywordRepository hazardKeywordRepository;
    private List<HazardKeyword> activeKeywords;
    private List<Pattern> patterns;
    
    @PostConstruct
    public void init() {
        refreshKeywords();
    }
    
    public void refreshKeywords() {
        this.activeKeywords = hazardKeywordRepository.findByActiveTrue();
        this.patterns = new ArrayList<>();
        
        for (HazardKeyword keyword : activeKeywords) {
            String regex = "\\b" + Pattern.quote(keyword.getKeyword().toLowerCase()) + "\\b";
            patterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
        }
        
        log.info("Loaded {} active hazard keywords", activeKeywords.size());
    }
    
    public HazardDetectionResult detectHazards(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new HazardDetectionResult(false, null, null);
        }
        
        String lowerText = text.toLowerCase();
        
        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            if (pattern.matcher(lowerText).find()) {
                HazardKeyword keyword = activeKeywords.get(i);
                log.warn("Hazard detected! Keyword: {} in text: {}", keyword.getKeyword(), text);
                return new HazardDetectionResult(true, keyword, keyword.getSafetyResponse());
            }
        }
        
        return new HazardDetectionResult(false, null, null);
    }
    
    public static class HazardDetectionResult {
        private final boolean detected;
        private final HazardKeyword keyword;
        private final String safetyResponse;
        
        public HazardDetectionResult(boolean detected, HazardKeyword keyword, String safetyResponse) {
            this.detected = detected;
            this.keyword = keyword;
            this.safetyResponse = safetyResponse;
        }
        
        public boolean isDetected() {
            return detected;
        }
        
        public HazardKeyword getKeyword() {
            return keyword;
        }
        
        public String getSafetyResponse() {
            return safetyResponse;
        }
    }
}
