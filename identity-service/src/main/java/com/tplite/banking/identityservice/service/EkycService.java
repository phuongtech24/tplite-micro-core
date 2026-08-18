package com.tplite.banking.identityservice.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EkycService {

    public boolean verifyIdentity(String idCardUrl, String selfieUrl) {
        log.info("🤖 AI is processing eKYC... ID: {}, Selfie: {}", idCardUrl, selfieUrl);
        
        // Giả lập OCR và Face Matching
        // Nếu URL chứa từ "fake" hoặc "spoof" thì đánh rớt
        if (idCardUrl.toLowerCase().contains("fake") || selfieUrl.toLowerCase().contains("fake") || 
            idCardUrl.toLowerCase().contains("spoof") || selfieUrl.toLowerCase().contains("spoof")) {
            log.warn("❌ Liveness check failed! Spoofing detected.");
            return false;
        }

        log.info("✅ Face matched! Liveness score: 0.98. Result: PASS");
        return true;
    }
}
