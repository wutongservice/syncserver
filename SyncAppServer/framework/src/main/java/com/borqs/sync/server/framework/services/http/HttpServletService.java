/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.http;

import java.util.logging.Logger;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.borqs.json.JSONArray;
import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.framework.BaseService;
import com.borqs.sync.server.framework.ServiceDescriptor;

/**
 * User: b251
 * Date: 12/28/11
 * Time: 6:19 PM
 * Borqs project
 */
public class HttpServletService extends BaseService {
    private HttpServletDelegate[] mServletImpls;
    private String[] mServletPaths;
    
    private Server mHttpServiceContainer;
    private Logger mLogger;

    public HttpServletService(Context context){
        super(context);
        mLogger = context.getLogger();
    }

    /**
     * call this to init service before start
     *
     * @param descriptor
     * @return
     */
    @Override
    public boolean init(ServiceDescriptor descriptor) {
    	boolean res = false;
        if(super.init(descriptor)){
            String descImpl = descriptor.impl();
            if(null == descImpl) {
            	mLogger.warning("service descriptor impl's value is null!");
            	return false;
            }
            mLogger.info("servlet impl config is "+descImpl);
            
            if(descImpl.trim().startsWith("[")) {
            	res = parseMultiServletConfig(descImpl);
            } else {
            	mServletImpls = new HttpServletDelegate[1];
            	mServletPaths = new String[]{"/"};            	
            	mServletImpls[0] = HttpServletDelegate.fromName(descriptor.impl(), getContext());
            	res = true;
            }
        }

        return (res && mServletImpls!=null && mServletImpls.length>0);
    }

	private boolean parseMultiServletConfig(String descImpl) {
		try {
			JSONArray jsonDescImpls = new JSONArray(descImpl);
			if(jsonDescImpls.length()==0) {
				mLogger.warning("service descriptor impl's value is null or JSON format error!");
				return false;
			}
			
			mServletImpls = new HttpServletDelegate[jsonDescImpls.length()];
			mServletPaths = new String[jsonDescImpls.length()];
			
			for(int i=0; i<jsonDescImpls.length(); i++) {
				JSONObject jsonDescImpl = (JSONObject) jsonDescImpls.get(i);
				String servletClass = null, servletPath = null;
				if(jsonDescImpl.has("class")) {
					servletClass = jsonDescImpl.getString("class");
				}
				if(jsonDescImpl.has("path")) {
					servletPath = jsonDescImpl.getString("path");
				}
				
				if(null == servletClass) {
					mLogger.warning("service descriptor impl's class error!");
			    	return false;
				}
				
				if(null == servletPath) {
					servletPath = "/";
				}
				mLogger.info("servlet URL mappings -- "+servletClass+":"+servletPath);
				mServletPaths[i] = servletPath;
				mServletImpls[i] = HttpServletDelegate.fromName(servletClass, getContext());
			}
		} catch (JSONException e) {
			e.printStackTrace();
			mLogger.warning("service descriptor impl's value is null or JSON format error!");
			return false;
		}
		return true;
	}


    /**
     * check if the service is running or not
     *
     * @return
     */
    @Override
    public boolean isRunning() {
        return mHttpServiceContainer !=null && mHttpServiceContainer.isRunning();
    }

    /**
     * stop the service
     */
    @Override
    public void stop() {
        if(mHttpServiceContainer!=null){
            try {
                mHttpServiceContainer.stop();
            } catch (Exception e) {
                e.printStackTrace();
                //ignore it
            }
        }
    }

    /**
     * implement to make the service is available
     *
     * @param context
     */
    @Override
    protected void runSynchronized(Context context) {
        int servicePort = mDescriptor.getServicePort();

        try {
            mHttpServiceContainer = new Server(servicePort);
            SocketConnector connector = new SocketConnector();
            connector.setPort(servicePort);
            mHttpServiceContainer.setConnectors(new Connector[]{connector});

            ContextHandlerCollection handler = new ContextHandlerCollection();
            ServletContextHandler servletHandler = new ServletContextHandler();
            
            int idx = 0;
            for(HttpServletDelegate mServletImpl : mServletImpls) {
	            WebMethodServlet servlet = new WebMethodServlet(mServletImpl, mServletPaths[idx]);
	            servletHandler.addServlet(new ServletHolder(servlet), mServletPaths[idx]);
	            idx++;
            }
            handler.addHandler(servletHandler);
            
            mHttpServiceContainer.setHandler(handler);
            mHttpServiceContainer.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //wait for completed
        if(mHttpServiceContainer != null){
            try {
                mHttpServiceContainer.join();
            } catch (InterruptedException e) {

            } finally {
                try {
                    mHttpServiceContainer.stop();
                } catch (Exception e) {}
            }
        }
    }

    /**
     * the ID of the service
     *
     * @return
     */
    @Override
    protected String getIdentifier() {
        return mDescriptor.desc();
    }
}
