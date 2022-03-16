package org.paytm.milestone2._Milestone2.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="User_Id", insertable=true, unique=true, nullable=false)
    int userId;
    @Column(name = "User_Name",nullable = false,unique = true)
    String userName;
    @Column(name = "First_Name",nullable = false)
    String firstName;
    @Column(name = "Last_Name",nullable = false)
    String lastName;
    @Column(name = "Mobile_Number",nullable = false,unique = true,updatable = false)
    String  mobileNumber;
    @Column(name = "Email_Id",nullable = false,unique = true)
    String emailId;
    @Column(name = "Password",nullable = false)
    String password;
    @Column(name = "Address_1",nullable = false)
    String address1;
    @Column(name = "Address_2")
    String address2;
}
