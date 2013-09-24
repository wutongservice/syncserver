package com.borqs.sync.server.framework.services.sync;

import com.borqs.sync.server.framework.BaseService;
import com.borqs.sync.server.common.runtime.Context;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.Server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: b251
 * Date: 12/20/11
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpSyncService extends BaseService {
    private Server mJettyServer;

    public HttpSyncService(Context context){
        super(context);

    }

    /**
     * check if the service is running or not
     *
     * @return
     */
    @Override
    public boolean isRunning() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * stop the service
     */
    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * implement to make the service is available
     *
     * @param context
     */
    @Override
    protected void runSynchronized(Context context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * the ID of the service
     *
     * @return
     */
    @Override
    protected String getIdentifier() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private AbstractHandler mRequestHandler = new AbstractHandler(){
        @Override
        public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };
}
