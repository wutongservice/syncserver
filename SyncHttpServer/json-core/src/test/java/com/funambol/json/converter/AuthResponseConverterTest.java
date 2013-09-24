/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.json.converter;

import com.funambol.json.converter.AuthenticatorResponseConverter;
import junit.framework.TestCase;
import net.sf.json.test.JSONAssert;

import com.funambol.json.domain.JsonAuthResponse;

public class AuthResponseConverterTest extends TestCase {
    private String JSON_OBJ = "{\"data\":{\"sessionid\":\"dasdsadsad\"}}";

    public void testToJSON() {
        JsonAuthResponse  authResponse = new JsonAuthResponse ();
        authResponse.setSessionID("dasdsadsad");

        AuthenticatorResponseConverter converter = new AuthenticatorResponseConverter();
        String jsonResult = converter.toJSON(authResponse);
        JSONAssert.assertEquals(JSON_OBJ, jsonResult);
    }

    public void testFromJSON() {
        AuthenticatorResponseConverter converter = new AuthenticatorResponseConverter();
        JsonAuthResponse authResponse = converter.fromJSON(JSON_OBJ);
        assertEquals(authResponse.getSessionID(),"dasdsadsad");
    }

}