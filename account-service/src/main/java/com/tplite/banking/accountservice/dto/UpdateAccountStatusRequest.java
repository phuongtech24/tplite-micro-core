package com.tplite.banking.accountservice.dto;

import com.tplite.banking.accountservice.enums.AccountStatus;
import com.tplite.banking.common.validation.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAccountStatusRequest {
    
    @NotBlank(message = "Trạng thái không được để trống")
    @ValueOfEnum(enumClass = AccountStatus.class, message = "Trạng thái chỉ được là: ACTIVE, LOCKED, CLOSED")
    private String status;
    
}
