DROP DATABASE IF EXISTS ALARM_SYSTEM;
CREATE DATABASE ALARM_SYSTEM;
USE ALARM_SYSTEM;

CREATE TABLE users(
	id INT primary key auto_increment,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE modules(
    id INT primary key not null,
    userId int not null,
    foreign key (userId) references users(id) on update cascade on delete cascade
);

CREATE TABLE moduleData(
	id INT primary key auto_increment,
    eventTime datetime not null,
    moduleId INT not null,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    imuEvent INT,
    shockEvent INT,
    rfidEvent INT,
    foreign key (moduleId) references modules(id) on update cascade on delete cascade
);

#Create table userModules(
#	recordId int primary key auto_increment,
 #   moduleId INT not null,
#    userId INT not null,
 #   
#    foreign key (moduleId) references modules(id) ON update cascade,
 #   foreign key (userId) references users(id) On update cascade
#);

#insert into users(username,password) values('mitko','ggg');
#insert into users(username,password) values('gosho','ggg');
#insert into modules(id,userId) values(43653546,1);
#insert into userModules(moduleId,userId) values(43653546,1);

#select * from userModules where userId