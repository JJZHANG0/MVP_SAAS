package com.toolfix.service;

import com.toolfix.domain.Manual;
import com.toolfix.domain.Product;
import com.toolfix.repository.ManualRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockManualParsingService {
    
    private final ManualRepository manualRepository;
    
    @Async
    public void parseManualAsync(Long manualId) {
        try {
            Thread.sleep(3000);
            
            Manual manual = manualRepository.findById(manualId)
                .orElseThrow(() -> new RuntimeException("Manual not found: " + manualId));
            
            String extractedText = extractTextFromPdf(manual.getStoredFileName());
            
            manual.setExtractedProductName(extractProductName(extractedText, manual.getProduct()));
            manual.setExtractedModel(extractModel(extractedText, manual.getProduct()));
            manual.setExtractedBatteryInfo(extractBatteryInfo(extractedText));
            manual.setExtractedPowerInfo(extractPowerInfo(extractedText));
            manual.setExtractedCompatibleBatteries(extractCompatibleBatteries(extractedText));
            manual.setExtractedComponentCodes(extractComponentCodes(extractedText));
            manual.setExtractedSafetyWarnings(extractSafetyWarnings(extractedText));
            manual.setExtractedWarrantyTerms(extractWarrantyTerms(extractedText));
            
            manualRepository.save(manual);
            
            log.info("Manual parsing completed for manual ID: {}", manualId);
            
        } catch (Exception e) {
            log.error("Failed to parse manual ID: " + manualId, e);
            
            try {
                Manual manual = manualRepository.findById(manualId).orElse(null);
                if (manual != null) {
                    manual.setParseErrorMessage(e.getMessage());
                    manualRepository.save(manual);
                }
            } catch (Exception ex) {
                log.error("Failed to update manual with error", ex);
            }
        }
    }
    
    private String extractTextFromPdf(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    private String extractProductName(String text, Product product) {
        if (product.getProductName() != null && !product.getProductName().isEmpty()) {
            return product.getProductName();
        }
        
        Pattern pattern = Pattern.compile("(?:产品名称|Product Name)[：:](.*?)(?:\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return "Cordless Power Drill (Extracted from PDF)";
    }
    
    private String extractModel(String text, Product product) {
        if (product.getModel() != null && !product.getModel().isEmpty()) {
            return product.getModel();
        }
        
        Pattern pattern = Pattern.compile("(?:型号|Model)[：:](.*?)(?:\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return "TD-20V-" + product.getSku().substring(0, Math.min(4, product.getSku().length()));
    }
    
    private String extractBatteryInfo(String text) {
        Pattern voltagePattern = Pattern.compile("(?:电池|Battery|电压|Voltage).*?(\\d+)\\s*V", Pattern.CASE_INSENSITIVE);
        Matcher matcher = voltagePattern.matcher(text);
        if (matcher.find()) {
            String voltage = matcher.group(1) + "V";
            if (text.toLowerCase().contains("lithium") || text.contains("锂电")) {
                return voltage + " Lithium-ion";
            }
            return voltage + " Battery";
        }
        
        return "20V Lithium-ion (Default)";
    }
    
    private String extractPowerInfo(String text) {
        Pattern powerPattern = Pattern.compile("(?:功率|Power|额定).*?(\\d+)\\s*W", Pattern.CASE_INSENSITIVE);
        Matcher matcher = powerPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) + "W";
        }
        
        Pattern speedPattern = Pattern.compile("(?:转速|Speed|RPM).*?(\\d+)", Pattern.CASE_INSENSITIVE);
        matcher = speedPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) + " RPM";
        }
        
        return "Max 1500 RPM (Default)";
    }
    
    private String extractCompatibleBatteries(String text) {
        Pattern pattern = Pattern.compile("(?:兼容电池|Compatible.*?Batter(?:y|ies))[：:](.*?)(?:\n\n|$)", 
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return "20V Max series batteries (DCB200, DCB204, DCB206)";
    }
    
    private String extractComponentCodes(String text) {
        Pattern pattern = Pattern.compile("(?:零件|部件|Component|Part).*?(?:编号|Code|Number)[：:](.*?)(?:\n|$)", 
            Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return "Motor: M20-001, Chuck: C13-002, Switch: S15-003";
    }
    
    private String extractSafetyWarnings(String text) {
        Pattern pattern = Pattern.compile("(?:安全|Safety|警告|Warning|注意|Caution)(.*?)(?:保修|Warranty|规格|Specification)", 
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String warnings = matcher.group(1).trim();
            if (warnings.length() > 500) {
                warnings = warnings.substring(0, 500) + "...";
            }
            return warnings;
        }
        
        return "⚠️ IMPORTANT SAFETY WARNINGS:\n" +
               "1. Always wear safety goggles and hearing protection\n" +
               "2. Disconnect battery before maintenance or bit changes\n" +
               "3. Do not use in wet conditions or near flammable materials\n" +
               "4. Keep hands away from moving parts\n" +
               "5. Do not modify the tool or battery\n" +
               "6. Stop use immediately if smoke, unusual heat, or odors occur\n" +
               "7. Store in dry location between 0-40°C";
    }
    
    private String extractWarrantyTerms(String text) {
        Pattern pattern = Pattern.compile("(?:保修|Warranty|质保|Guarantee)(.*?)(?:技术|Technical|联系|Contact|$)", 
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String warranty = matcher.group(1).trim();
            if (warranty.length() > 300) {
                warranty = warranty.substring(0, 300) + "...";
            }
            return warranty;
        }
        
        return "LIMITED WARRANTY:\n" +
               "• 2-year warranty from date of purchase\n" +
               "• Covers manufacturing defects in materials and workmanship\n" +
               "• Does not cover: normal wear, misuse, accidents, unauthorized repairs\n" +
               "• Battery: 1-year warranty\n" +
               "• Proof of purchase required for all warranty claims\n" +
               "• Warranty void if safety seal is tampered with";
    }
}
