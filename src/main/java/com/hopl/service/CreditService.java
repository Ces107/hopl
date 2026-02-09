package com.hopl.service;

import com.hopl.model.User;
import com.hopl.model.enums.PlanType;
import com.hopl.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditService {

    private final UserRepository userRepository;

    public CreditService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks if user can generate a document based on their plan/credits.
     *
     * @param userId user ID
     * @return true if the user has available credits or unlimited plan
     */
    public boolean canGenerate(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getPlanType().isUnlimited() || user.getCredits() > 0)
                .orElse(false);
    }

    /**
     * Consumes one credit after document generation.
     *
     * @param userId user ID
     */
    @Transactional
    public void consumeCredit(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            if (!user.getPlanType().isUnlimited() && user.getCredits() > 0) {
                user.setCredits(user.getCredits() - 1);
                userRepository.save(user);
            }
        });
    }

    /**
     * Grants credits to a user after purchase.
     *
     * @param userId user ID
     * @param planType the purchased plan
     */
    @Transactional
    public void grantCredits(Long userId, PlanType planType) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPlanType(planType);
            if (!planType.isUnlimited()) {
                user.setCredits(user.getCredits() + planType.getDocumentLimit());
            }
            userRepository.save(user);
        });
    }
}
