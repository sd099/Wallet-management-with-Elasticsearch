package org.paytm.milestone2._Milestone2.repository;

import org.paytm.milestone2._Milestone2.models.Wallet;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet,Integer> {
    Wallet findByMobileNumber(String mobileNumber);
}
