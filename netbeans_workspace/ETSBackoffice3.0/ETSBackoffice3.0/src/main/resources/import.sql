INSERT INTO bo_user (id,loginID,password,surName,foreName,userType,active) VALUES (1,'supertech','1234','Akhond','YS',3,1);
INSERT INTO bo_user (id,loginID,password,surName,foreName,userType,active) VALUES (2,'admin','1234','Admin','Admin',2,1);
INSERT INTO bo_user (id,loginID,password,surName,foreName,userType,active) VALUES (3,'ashik','1234','Rahman','Ashik',2,1);

INSERT INTO additional_charge (id,title,charge,calculationType) VALUES (1,'Card Handling','0.00',2);
INSERT INTO additional_charge (id,title,charge,calculationType) VALUES (2,'Postage','0.00',1);
INSERT INTO additional_charge (id,title,charge,calculationType) VALUES (3,'Other','0.00',1);

INSERT INTO category (id,title) VALUES (1,'Hajj');
INSERT INTO category (id,title) VALUES (2,'Umra');
INSERT INTO category (id,title) VALUES (3,'Visa');
INSERT INTO category (id,title) VALUES (4,'Legal');
INSERT INTO category (id,title) VALUES (5,'Hotel');
INSERT INTO category (id,title) VALUES (6,'Air Transport');
INSERT INTO category (id,title) VALUES (7,'Road Transport');
INSERT INTO category (id,title) VALUES (8,'Passport');

INSERT INTO airline (code,name,calculationType,bspCom) VALUES ('AI','NACIL AIR INDIA',0,0.00);
INSERT INTO airline (code,name,calculationType,bspCom) VALUES ('SV','SAUDI ARABIAN AIRLINES',0,0.00);
INSERT INTO airline (code,name,calculationType,bspCom) VALUES ('8U','AFRIQIYAH AIRWAYS',0,0.00);
INSERT INTO airline (code,name,calculationType,bspCom) VALUES ('RJ','ROYAL JORDANIAN',0,0.00);
INSERT INTO airline (code,name,calculationType,bspCom) VALUES ('BG','BIMAN BANGLADESH AIRLINE',0,0.00);