package org.paytm.milestone2._Milestone2.repository;

import org.paytm.milestone2._Milestone2.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer> {

    User findByUserName(String userName);
    User findByEmailId(String emailId);
    User findByMobileNumber(String mobileNumber);
}
