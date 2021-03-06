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
-- -----------------------------------------------------------------------------
delete from fnbl_module where id='SyncHttpServer';
insert into fnbl_module (id, name, description)
values('SyncHttpServer','SyncHttpServer','SyncHttpServer Module');

delete from fnbl_connector where id='DBOfficer';
insert into fnbl_connector(id, name, description)
values('DBOfficer','DBOfficer','DBOfficer Connector');

--
-- SyncSource Types
--
delete from fnbl_sync_source_type where id='mysyncsource';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('mysyncsource','My SyncSource','SyncHttpServer.MySyncSource','SyncHttpServer.MySyncSourceAdminPanel');

delete from fnbl_sync_source_type where id='mymergeablesyncsource';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('mymergeablesyncsource','My Mergeable SyncSource','SyncHttpServer.MyMergeableSyncSource','SyncHttpServer.MySyncSourceAdminPanel');

--
-- Connector source types
--
delete from fnbl_connector_source_type where connector='DBOfficer' and sourcetype='mysyncsource';
insert into fnbl_connector_source_type(connector, sourcetype)
values('DBOfficer','mysyncsource');

delete from fnbl_connector_source_type where connector='DBOfficer' and sourcetype='mymergeablesyncsource';
insert into fnbl_connector_source_type(connector, sourcetype)
values('DBOfficer','mymergeablesyncsource');

--
-- Module - Connector
--
delete from fnbl_module_connector where module='SyncHttpServer' and connector='DBOfficer';
insert into fnbl_module_connector(module, connector)
values('SyncHttpServer','DBOfficer');
