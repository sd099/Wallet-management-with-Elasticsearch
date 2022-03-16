----User table creation
--create table user (user_id integer not null, address_1 varchar(255) not null, address_2 varchar(255), email_id varchar(255) not null, first_name varchar(255) not null, last_name varchar(255) not null, mobile_number varchar(255) not null, password varchar(255) not null, user_name varchar(255) not null, primary key (user_id))
--
----Wallet table creation
--create table wallet (wallet_id integer not null, current_balance float not null, mobile_number varchar(255) not null, primary key (wallet_id))
--
----Transaction table creation
--create table transaction (txn_id integer not null, amount float not null, payee_mobile_number varchar(255) not null, payer_mobile_number varchar(255) not null, status varchar(255), timestamp datetime, primary key (txn_id))


--insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (1,"Sd099","Son","Doe","sondoe@gmail.com","9876543210","Time square, NY, USA","$2a$10$hph6xo16D8ED1ZOHcMo7wuvrCH5ebxiVyTtPFGsHaZmIgAHWa55C2")
--insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (2,"Jd099","John","Doe","johndoe@gmail.com","0123456789","Time square, NY, USA","$2a$10$3IT1NBW1r60UtCPOXGlwweqmHJDwy3bsr4cuO4XkNnUguTtYgA8va")
--insert into user (user_id,user_name,first_name,last_name,email_id,mobile_number,address_1,password) values (300,"Ss099","Steve","Smith","stevesmith@gmail.com","5432109876","Time square, NY, USA","$2a$10$7uLjRL1XR99ovnrin52/puncFBPdMKv79FQ8CZVi8UfGqcxv6Yfge")
--
--insert into wallet (wallet_id,mobile_number,current_balance) values (100,"9876543210",0.0)
--insert into wallet (wallet_id,mobile_number,current_balance) values (1000,"0123456789",0.0)
--
--insert into transaction (txn_id,amount,payee_mobile_number,payer_mobile_number,status,timestamp) values (200,200.0,"0123456789","5432109876","SUCCESS","1998-01-02 00:00:00.000")
