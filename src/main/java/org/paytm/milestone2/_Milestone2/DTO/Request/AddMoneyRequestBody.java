package org.paytm.milestone2._Milestone2.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddMoneyRequestBody {
    private float money;
    private String mobileNumber;
}
