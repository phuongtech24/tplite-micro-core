package com.tplite.banking.identityservice.service;

import java.util.UUID;

public interface AdminUserService {
    String lockUser(UUID userId);
    String unlockUser(UUID userId);
    String resetPassword(UUID userId, String newPassword);
}
