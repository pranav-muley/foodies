package com.feastora.food_ordering.service;

import com.feastora.food_ordering.entity.VerificationToken;
import com.feastora.food_ordering.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public void saveVerificationToken(VerificationToken verificationToken) {
        if(ObjectUtils.isEmpty(verificationToken)) {
            return;
        }
        verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken getVerificationTokenByUserId(String userId) {
        if(ObjectUtils.isEmpty(userId)) {
            return null;
        }
        return verificationTokenRepository.getVerificationTokensByUserId(userId);
    }

    public void updateVerificationTokenForUserById(String userId, VerificationToken verificationToken) {
        if(ObjectUtils.isEmpty(verificationToken)) {
            return;
        }
        verificationTokenRepository.deleteVerificationTokensByUserId(userId);
        verificationTokenRepository.save(verificationToken);
    }
}