package org.paytm.milestone2._Milestone2.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionP2pRequestBody {
    private String payerMobileNumber;
    private String payeeMobileNumber;
    private float amount;
}
