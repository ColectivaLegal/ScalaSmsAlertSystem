# --- !Ups

create table Publisher (
 id bigint AUTO_INCREMENT not null primary key,
 phone_number VARCHAR(20) not null UNIQUE
);

create table Subscriber (
  id bigint AUTO_INCREMENT not null primary key,
  phone_number VARCHAR(20) not null UNIQUE,
  language varchar(3),
  state varchar(20) not null
);

# --- !Downs