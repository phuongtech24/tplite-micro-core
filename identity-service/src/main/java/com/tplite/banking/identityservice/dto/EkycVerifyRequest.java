package com.tplite.banking.identityservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class EkycVerifyRequest {

    private UUID userId;

    @NotBlank(message = "Tên đầy đủ không được để trống")
    private String fullName;

    @NotBlank(message = "Số CCCD không được để trống")
    private String idCardNumber;

    @NotBlank(message = "URL ảnh CCCD không được để trống")
    private String idCardUrl;

    @NotBlank(message = "URL ảnh Selfie không được để trống")
    private String selfieUrl;
}
