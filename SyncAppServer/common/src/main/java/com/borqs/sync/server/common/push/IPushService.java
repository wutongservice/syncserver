package com.borqs.sync.server.common.push;

import java.util.logging.Logger;

public interface IPushService {

	public abstract boolean push(String from, String to, String data);

    public void setLogger(Logger logger);
}