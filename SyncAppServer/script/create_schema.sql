--
-- Funambol is a mobile platform developed by Funambol, Inc.
-- Copyright (C) 2008 Funambol, Inc.
--
-- This program is free software; you can redistribute it and/or modify it under
-- the terms of the GNU Affero General Public License version 3 as published by
-- the Free Software Foundation with the addition of the following permission
-- added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
-- WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
-- WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
--
-- This program is distributed in the hope that it will be useful, but WITHOUT
-- ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
-- FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
-- details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program; if not, see http://www.gnu.org/licenses or write to
-- the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
-- MA 02110-1301 USA.
--
-- You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
-- 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
--
-- The interactive user interfaces in modified source and object code versions
-- of this program must display Appropriate Legal Notices, as required under
-- Section 5 of the GNU Affero General Public License version 3.
--
-- In accordance with Section 7(b) of the GNU Affero General Public License
-- version 3, these Appropriate Legal Notices must retain the display of the
-- "Powered by Funambol" logo. If the display of the logo is not reasonably
-- feasible for technical reasons, the Appropriate Legal Notices must display
-- the words "Powered by Funambol".
--

--
-- Initialization data for the Foundation module
--

-- -----------------------------------------------------------------------------
-- Module structure registration
--
-- Change the code below with the information that best describe your project.
-- However, pay particular attention to the changes you apply or the server will
-- not be able to reconstruct the module-connector-syncsourcetype hierarchy
--
use borqs_sync;

-- module

DROP PROCEDURE IF EXISTS p_fnbl_module; 

DELIMITER $$ 

CREATE PROCEDURE p_fnbl_module () 

  BEGIN 
  DECLARE a INT; 
  select count(*) into a from fnbl_module where id='com.borqs.sync'; 
  IF a = 0 THEN 
  insert into fnbl_module (id, name, description) values ('com.borqs.sync','com.borqs.sync','Borqs Sync Module'); 
  END IF; 

  END; 

  $$

  DELIMITER ; 

call p_fnbl_module (); 
DROP PROCEDURE IF EXISTS p_fnbl_module;
-- ----------------------------------------------------------------------------
-- connector 

DROP PROCEDURE IF EXISTS p_fnbl_connector; 

DELIMITER $$ 

CREATE PROCEDURE p_fnbl_connector () 

  BEGIN 
  DECLARE b INT; 
  select count(*) into b from fnbl_connector where id='borqsconnector'; 
  IF b = 0 THEN
  insert into fnbl_connector(id, name, description) values ('borqsconnector','borqsconnector','borqsconnector Connector');
  END IF;

  END; 
  $$

  DELIMITER ;

call p_fnbl_connector ();
DROP PROCEDURE IF EXISTS p_fnbl_connector;

-- -------------------------------------------------------------------------------------------------
-- SyncSource Types
DROP PROCEDURE IF EXISTS p_fnbl_sync_source_type;

DELIMITER $$

CREATE PROCEDURE p_fnbl_sync_source_type ()

  BEGIN
  DECLARE c INT;
  select count(*) into c from fnbl_sync_source_type where id='contact_borqs';
  IF c = 0 THEN
  insert into fnbl_sync_source_type(id, description, class, admin_class) values('contact_borqs','Borqs Contact        SyncSource','com.borqs.sync.server.contact.engine.source.JsonSyncSource','com.borqs.sync.server.contact.admin.ContactSyncSourceAdminPanel');
  END IF;

  END; 
  $$

  DELIMITER ;

call p_fnbl_sync_source_type ();
drop procedure if exists p_fnbl_sync_source_type;
-- ---------------------------------------------------------------------------------------------------
-- Connector source types
DROP PROCEDURE IF EXISTS p_fnbl_connector_source_type;

DELIMITER $$

CREATE PROCEDURE p_fnbl_connector_source_type ()

  BEGIN
  DECLARE d INT;
  select count(*) into d from fnbl_connector_source_type where connector='borqsconnector' and sourcetype='contact_borqs';
  IF d = 0 THEN
  insert into fnbl_connector_source_type(connector, sourcetype) values ('borqsconnector','contact_borqs');
  END IF;

  END; 
  $$

  DELIMITER ;

call p_fnbl_connector_source_type ();
drop procedure if exists p_fnbl_connector_source_type ;

-- ---------------------------------------------------------------------------------------------------
-- Module - Connector
DROP PROCEDURE IF EXISTS p_fnbl_module_connector;

DELIMITER $$

CREATE PROCEDURE p_fnbl_module_connector ()

  BEGIN
  DECLARE e INT;
  select count(*) into e from fnbl_module_connector where module='com.borqs.sync' and connector='borqsconnector';
  IF e = 0 THEN
  insert into fnbl_module_connector(module, connector) values('com.borqs.sync','borqsconnector');
  END IF;

  END; 
  $$

  DELIMITER ;

call p_fnbl_module_connector ();

drop procedure if exists p_fnbl_module_connector;
-- ----------------------------------------------------------------------------------------------

update fnbl_connector set admin_class='com.funambol.json.admin.JsonConnectorConfigPanel' where id = 'borqsconnector';


-- Initialization data for the PIM module
--
-- @version $Id: create_schema.sql,v 1.1.1.1 2008-03-20 21:38:28 stefano_fornari Exp $
--

-- table for contact data

create table if not exists borqs_pim_contact (
    id              bigint PRIMARY KEY AUTO_INCREMENT,
    userid          varchar(255) binary,
    borqsid          varchar(255) binary,
    last_update     bigint,
    status          char,
    photo_type      smallint,

-- contact details
    importance      smallint,
    sensitivity     smallint,
    subject         varchar(255) binary,
    folder          varchar(255) binary,

-- personal details
    anniversary     varchar(16) binary,
    first_name      varchar(64) binary,
    middle_name     varchar(64) binary,
    last_name       varchar(64) binary,
    bfirst_name     varchar(64) binary,
    bmiddle_name    varchar(64) binary,
    blast_name      varchar(64) binary,
    display_name    varchar(128) binary,
    birthday        varchar(16) binary,
    body            text,
    categories      varchar(255) binary,
    children        varchar(255) binary,
    hobbies         varchar(255) binary,
    initials        varchar(16) binary,
    languages       varchar(255) binary,
    nickname        varchar(64) binary,
    spouse          varchar(128) binary,
    suffix          varchar(32) binary,
    title           varchar(32) binary,


-- business details
    assistant       varchar(128) binary,
    company         varchar(255) binary,
    department      varchar(255) binary,
    job_title       varchar(128) binary,
    manager         varchar(128) binary,
    mileage         varchar(16) binary,
    office_location varchar(64) binary,
    profession      varchar(64) binary,
    companies       varchar(255) binary,

    gender          char(1)
)ENGINE = InnoDB
CHARACTER SET utf8;

ALTER TABLE `borqs_pim_contact`
 MODIFY COLUMN `anniversary` VARCHAR(255)  ,
 MODIFY COLUMN `first_name` VARCHAR(255)  ,
 MODIFY COLUMN `middle_name` VARCHAR(255)  ,
 MODIFY COLUMN `last_name` VARCHAR(255)  ,
 MODIFY COLUMN `bfirst_name` VARCHAR(255)  ,
 MODIFY COLUMN `bmiddle_name` VARCHAR(255)  ,
 MODIFY COLUMN `blast_name` VARCHAR(255)  ,
 MODIFY COLUMN `display_name` VARCHAR(255)  ,
 MODIFY COLUMN `birthday` VARCHAR(255)  ,
 MODIFY COLUMN `initials` VARCHAR(255)  ,
 MODIFY COLUMN `nickname` VARCHAR(255)  ,
 MODIFY COLUMN `spouse` VARCHAR(255)  ,
 MODIFY COLUMN `suffix` VARCHAR(255)  ,
 MODIFY COLUMN `title` VARCHAR(255)  ,
 MODIFY COLUMN `assistant` VARCHAR(255)  ,
 MODIFY COLUMN `job_title` VARCHAR(255)  ,
 MODIFY COLUMN `manager` VARCHAR(255)  ,
 MODIFY COLUMN `mileage` VARCHAR(255)  ,
 MODIFY COLUMN `office_location` VARCHAR(255)  ,
 MODIFY COLUMN `profession` VARCHAR(255)  ;

-- table for extra contact data

create table if not exists  borqs_pim_contact_item (
    id           bigint(20) NOT NULL AUTO_INCREMENT,
    contact      bigint,
    type         smallint,
    value        varchar(255) binary,
    private      tinyint(1) DEFAULT 0,
    last_update  bigint(20) DEFAULT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (contact) REFERENCES borqs_pim_contact (id)
                          ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;

create table if not exists  borqs_pim_sync_status (
    id           bigint(20) NOT NULL AUTO_INCREMENT,
    username     varchar(255) binary,
    deviceid     varchar(255) binary,
    sync_status  tinyint(1) DEFAULT 0 ,
    PRIMARY KEY (id)
)ENGINE = InnoDB
CHARACTER SET utf8;

-- table for contact address data
create table if not exists borqs_pim_address (
    contact          bigint,
    type             smallint,
    street           varchar(128) binary,
    city             varchar(64) binary,
    state            varchar(64) binary,
    postal_code      varchar(16) binary,
    country          varchar(32) binary,
    po_box           varchar(16) binary,
    extended_address varchar(255) binary,
    private          tinyint(1) DEFAULT 0,
    last_update      bigint(20) DEFAULT NULL,

    FOREIGN KEY (contact) REFERENCES borqs_pim_contact (id)
                          ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;

ALTER TABLE `borqs_pim_address`
 MODIFY COLUMN `street` VARCHAR(255)  ,
 MODIFY COLUMN `city` VARCHAR(255)  ,
 MODIFY COLUMN `state` VARCHAR(255)  ,
 MODIFY COLUMN `postal_code` VARCHAR(255) ,
 MODIFY COLUMN `country` VARCHAR(255)  ,
 MODIFY COLUMN `po_box` VARCHAR(255)  ;


-- table for the contact photo

create table if not exists borqs_pim_contact_photo (
    contact      bigint,
    type         varchar(64) binary,
    photo        longblob,
    url          varchar(255),

    PRIMARY KEY (contact),
    FOREIGN KEY (contact) REFERENCES borqs_pim_contact (id)
                          ON DELETE CASCADE
)ENGINE = InnoDB
CHARACTER SET utf8;


-- table for sync_source_version ----
CREATE TABLE if not exists borqs_user_sync_version (
  username VARCHAR(255)  NOT NULL,
  sync_source_version BIGINT(20)  NOT NULL DEFAULT 0,
  PRIMARY KEY (username, sync_source_version)
)ENGINE = InnoDB
CHARACTER SET utf8;

CREATE TABLE if not exists borqs_contact_account_anchor ( 
  borqsid varchar(255) NOT NULL, 
  anchor bigint(20) NOT NULL, 
  PRIMARY KEY (borqsid) 
) ENGINE = InnoDB 
CHARACTER SET utf8;

CREATE TABLE if not exists borqs_contact_group ( 
  id bigint(20) NOT NULL AUTO_INCREMENT, 
  owner_borqs_id varchar(255) NOT NULL, 
  circle_id bigint, 
  name varchar(255) NOT NULL, 
  status char,
  last_update     bigint ,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB 
CHARACTER SET utf8;

CREATE TABLE  if not exists `user_phone_sim` (
  `guid` varchar(64) NOT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `sim` varchar(64) DEFAULT NULL,
  `verifycode` varchar(16) DEFAULT NULL,
  `extra` bigint(20) DEFAULT '0',
  PRIMARY KEY (`guid`)
) ENGINE = InnoDB
CHARACTER SET utf8;


-- procedure for table alert------------------------------------------------------------------------------------
 drop procedure if exists schema_change;  
 delimiter $$  
 create procedure schema_change() begin  
 if not exists (select * from information_schema.columns where table_name = 'fnbl_principal' and column_name = 'sync_version') then 
 ALTER TABLE fnbl_principal ADD COLUMN sync_version BIGINT(20)  NOT NULL DEFAULT 0 AFTER `id`;
 end if;  

 end
 $$  
 delimiter ;
 call schema_change();
 drop procedure if exists schema_change;
  

-- add bpc name column
drop procedure if exists bpc_name_pro;  
 delimiter $$  
 create procedure bpc_name_pro() begin  
 if not exists (select * from information_schema.columns where table_name = 'borqs_pim_contact' and column_name = 'bfirst_name') then  

 ALTER TABLE borqs_pim_contact 
 ADD COLUMN bfirst_name varchar(64) binary AFTER `last_name`,
 ADD COLUMN bmiddle_name varchar(64) binary AFTER `bfirst_name`,
 ADD COLUMN blast_name varchar(64) binary AFTER `bmiddle_name`;

 end if;  

 end
 $$  
 delimiter ;
 call bpc_name_pro();
 drop procedure if exists bpc_name_pro;


-- add last_update,private column for address table.
drop procedure if exists borqs_pim_address_pro;  
 delimiter $$  
 create procedure borqs_pim_address_pro() begin  
 if not exists (select * from information_schema.columns where table_name = 'borqs_pim_address' and column_name = 'private') then  

 ALTER TABLE borqs_pim_address 
 ADD COLUMN private tinyint(1) DEFAULT 0 AFTER `extended_address`,
 ADD COLUMN last_update bigint(20) DEFAULT NULL AFTER `private`;

 end if;  

 end
 $$  
 delimiter ;
 call borqs_pim_address_pro();
 drop procedure if exists borqs_pim_address_pro;


-- add id(primary key),private,last_update column for borqs_pim_contact_item
drop procedure if exists borqs_pim_item_pro;  
 delimiter $$  
 create procedure borqs_pim_item_pro() begin  
 if not exists (select * from information_schema.columns where table_name = 'borqs_pim_contact_item' and column_name = 'id') then 
 
 ALTER TABLE borqs_pim_contact_item
 ADD COLUMN id bigint(20) NOT NULL AUTO_INCREMENT AFTER `value`,
 ADD PRIMARY KEY (`id`);

 end if;  

 end
 $$  
 delimiter ;
 call borqs_pim_item_pro();
 drop procedure if exists borqs_pim_item_pro;

drop procedure if exists borqs_pim_item_pro1;  
 delimiter $$  
 create procedure borqs_pim_item_pro1() begin  
 if not exists (select * from information_schema.columns where table_name = 'borqs_pim_contact_item' and column_name = 'private') then 
 
 ALTER TABLE borqs_pim_contact_item
 ADD COLUMN private tinyint(1) DEFAULT 0 AFTER `value`,
 ADD COLUMN last_update bigint(20) DEFAULT NULL AFTER `private`;
 end if;  

 end
 $$  
 delimiter ;
 call borqs_pim_item_pro1();
 drop procedure if exists borqs_pim_item_pro1;


-- index for borqs_pim_contact
 drop procedure if exists borqs_pim_contact_index;  

 delimiter $$  

 create procedure borqs_pim_contact_index() begin  
 if not exists (select * from information_schema.statistics where table_name = 'borqs_pim_contact' and index_name = 'ind_borqs_pim_contact') then 
 create index ind_borqs_pim_contact  on borqs_pim_contact (userid, last_update, status);
 end if;  
 end
 $$  
 delimiter ;
 call borqs_pim_contact_index();
 drop procedure if exists borqs_pim_contact_index;
-- -----------------------------------------------------------------------------------------------


 
-- add trigger for contact change linstener -------------------------------------------------------

-- update trigger
delimiter $$

drop trigger if exists borqs_contact_update;

CREATE TRIGGER borqs_contact_update after UPDATE ON borqs_pim_contact
  FOR EACH ROW

  BEGIN
  SET @count = (SELECT count(*) FROM borqs_user_sync_version where username=old.userid);
  IF @count > 0 THEN
  update borqs_user_sync_version set sync_source_version=sync_source_version+1 where username=old.userid;
  else
  insert into borqs_user_sync_version (username,sync_source_version) values (old.userid,0);
  END IF;

  END  ;
  $$

-- insert trigger
drop trigger if exists borqs_contact_insert;

CREATE TRIGGER borqs_contact_insert after INSERT ON borqs_pim_contact
  FOR EACH ROW

  BEGIN
  SET @count = (SELECT count(*) FROM borqs_user_sync_version where username=new.userid);

  IF @count > 0 THEN
  update borqs_user_sync_version set sync_source_version=sync_source_version+1 where username=new.userid;
  else
  insert into borqs_user_sync_version (username,sync_source_version) values (new.userid,0);
  END IF;

  END  ;
  $$

-- delete trigger
drop trigger if exists borqs_contact_delete;

CREATE TRIGGER borqs_contact_delete after DELETE ON borqs_pim_contact
  FOR EACH ROW

  BEGIN
  SET @count = (SELECT count(*) FROM borqs_user_sync_version where username=old.userid);

  IF @count > 0 THEN
  update borqs_user_sync_version set sync_source_version=sync_source_version+1 where username=old.userid;
  ELSE
  insert into borqs_user_sync_version (username,sync_source_version) values (old.userid,0);
  END IF;

  END  ;
  $$

delimiter ;

-- --------------------------------------------------------------------------------------------
-- contacts mapping table scheme

CREATE TABLE IF NOT EXISTS `borqs_contactid_borqsid` (
  `ownerid` varchar(255) NOT NULL,
  `contactid` bigint(20) NOT NULL,
  `borqsid` varchar(255) NOT NULL,
  KEY `idx_sns_map_ownerid` (`ownerid`),
  KEY `idx_sns_map_contactid` (`contactid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


