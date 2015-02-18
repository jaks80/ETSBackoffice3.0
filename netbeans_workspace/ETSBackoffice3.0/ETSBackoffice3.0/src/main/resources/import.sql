INSERT INTO bo_user (id,loginID,password,surName,foreName,userType,isActive) VALUES (1,'supertech','1234','Akhond','YS',3,1);

INSERT INTO additionalCharge (id,title,charge,calculationType,isArchived) VALUES (1,'Card Handling','0.00',2,true);
INSERT INTO additionalCharge (id,title,charge,calculationType,isArchived) VALUES (2,'Postage','0.00',1,true);
INSERT INTO additionalCharge (id,title,charge,calculationType,isArchived) VALUES (3,'Other','0.00',1,true);

INSERT INTO category (id,title) VALUES (1,'Hajj');
INSERT INTO category (id,title) VALUES (2,'Umra');
INSERT INTO category (id,title) VALUES (3,'Legal/Visa');
INSERT INTO category (id,title) VALUES (4,'Transport');