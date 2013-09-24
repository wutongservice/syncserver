/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.contact.convert;

import com.borqs.pim.jcontact.JContact;
import com.funambol.json.converter.Converter;
import com.funambol.json.domain.JsonItem;

/**
 * Date: 3/12/12
 * Time: 5:03 PM
 * Borqs project
 */
public class JContactConverter implements Converter<JsonItem<JContact>> {
    @Override
    public void setServerTimeZoneID(String serverTimeZoneID) {
    }

    @Override
    public String toJSON(JsonItem<JContact> obj) {
        return obj.getItem().toJsonString();
    }

    @Override
    public JsonItem<JContact> fromJSON(String jsonContent) {
        JsonItem<JContact> item = new JsonItem<JContact>();
        item.setItem(JContact.fromJsonString(jsonContent));

        return item;
    }

    @Override
    public String toRFC(JsonItem<String> jsonRFC) {
        return null;
    }

    @Override
    public JsonItem<String> fromRFC(String jsonRFC) {
        return null;
    }
}
