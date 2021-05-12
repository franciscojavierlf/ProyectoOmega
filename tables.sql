DROP TABLE MESSAGES;
DROP TABLE DIRECTORY;
DROP TABLE USERS; 
 
------------------------------- 
--     Users
-------------------------------
CREATE TABLE USERS
(
  ID VARCHAR(15) NOT NULL PRIMARY KEY,
  NAME VARCHAR(40) NOT NULL
);

------------------------------- 
--     Messages
-------------------------------
CREATE TABLE MESSAGES
(
  ID INT NOT NULL GENERATED ALWAYS AS IDENTITY 
     (START WITH 1 ,INCREMENT BY 1) PRIMARY KEY,
  SENDER_ID VARCHAR(15) NOT NULL REFERENCES USERS,
  RECEIVER_ID VARCHAR(15) NOT NULL REFERENCES USERS,
  TYPE VARCHAR(20) NOT NULL,
  BODY VARCHAR(100) NOT NULL,
  STATE VARCHAR(10) NOT NULL
);

------------------------------- 
--     Directory
-------------------------------
CREATE TABLE DIRECTORY
(
  SENDER_ID VARCHAR(15) NOT NULL REFERENCES USERS,
  RECEIVER_ID VARCHAR(15) NOT NULL REFERENCES USERS,
  STATE VARCHAR(10) NOT NULL
);
