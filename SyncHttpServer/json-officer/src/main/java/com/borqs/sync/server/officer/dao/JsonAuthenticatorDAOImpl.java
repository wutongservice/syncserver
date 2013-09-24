package com.borqs.sync.server.officer.dao;

import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;
import com.borqs.sync.avro.IAccountAuthenticatorService;
import com.borqs.sync.avro.XAuthenticatorResponse;
import com.borqs.sync.avro.XResponse;
import com.borqs.sync.server.officer.BCSConfig;
import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.naming.NamingServiceProxy;
import com.borqs.sync.server.rpc.base.naming.NotFoundException;
import com.borqs.sync.server.rpc.base.naming.RemoteService;
import com.funambol.json.converter.AuthenticatorRequestConverter;
import com.funambol.json.converter.AuthenticatorResponseConverter;
import com.funambol.json.dao.AuthenticatorDAO;
import com.funambol.json.domain.JsonAuthRequest;
import com.funambol.json.domain.JsonAuthResponse;
import com.funambol.json.domain.JsonResponse;
import com.funambol.json.security.JsonUser;
import com.funambol.json.util.Logger;
import com.funambol.json.util.Utility;
import org.apache.commons.httpclient.HttpException;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 3/27/12
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class JsonAuthenticatorDAOImpl implements AuthenticatorDAO{

    private AuthenticatorRequestConverter mAuthenticatorRequestConverter;


    public JsonAuthenticatorDAOImpl() throws RPCException, NotFoundException {
        mAuthenticatorRequestConverter = new AuthenticatorRequestConverter();
    }

    @Override
    public JsonResponse login(String authRequest) throws HttpException, IOException {
        //{"data":{"credentials":{"user":"","pass":""}}}
        //TODO control the remote service life
        RemoteService rs = null;
        try {
            rs = createAccountAuthenticatorService();
            IAccountAuthenticatorService accountAuthenticatorService = rs.asInterface();
            JsonAuthRequest jsonAuthRequest = mAuthenticatorRequestConverter.fromJSON(authRequest);
            XAuthenticatorResponse resp = accountAuthenticatorService.login(jsonAuthRequest.getUser(),jsonAuthRequest.getPass());
            Logger.info("login response content:" + resp.content.toString());
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally{
            if(rs != null){
                rs.destroy();
            }
        }
        return new JsonResponse(200, new String());
    }

    @Override
    public JsonResponse logout(JsonUser user) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountAuthenticatorService();
            IAccountAuthenticatorService accountAuthenticatorService = rs.asInterface();
            XAuthenticatorResponse resp = accountAuthenticatorService.logout(user.getUsername(),user.getPassword());
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally{
            if(rs != null){
                rs.destroy();
            }
        }
        return new JsonResponse(200, new String());
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JsonResponse isSyncing(String username, String deviceId) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountAuthenticatorService();
            IAccountAuthenticatorService accountAuthenticatorService = rs.asInterface();
            XAuthenticatorResponse resp = accountAuthenticatorService.isSyncing(username, deviceId);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally{
            if(rs != null){
                rs.destroy();
            }
        }
        return new JsonResponse(200, new String());
    }

    @Override
    public JsonResponse enterSyncBeginStatus(String username, String deviceId) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountAuthenticatorService();
            IAccountAuthenticatorService accountAuthenticatorService = rs.asInterface();
            XAuthenticatorResponse resp = accountAuthenticatorService.enterSyncBeginStatus(username, deviceId);
            return new JsonResponse(resp.status_code, new String());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally{
            if(rs != null){
                rs.destroy();
            }
        }
        return new JsonResponse(200, new String());
    }

    @Override
    public JsonResponse enterSyncEndStatus(String username) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountAuthenticatorService();
            IAccountAuthenticatorService accountAuthenticatorService = rs.asInterface();
            XAuthenticatorResponse resp = accountAuthenticatorService.enterSyncEndStatus(username);
            return new JsonResponse(resp.status_code, new String());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally{
            if(rs != null){
                rs.destroy();
            }
        }
        return new JsonResponse(200, new String());
    }

    private RemoteService createAccountAuthenticatorService() throws RPCException, NotFoundException {
        String naming_host = BCSConfig.getConfigString(BCSConfig.NAMING_HOST);
        int naming_port = BCSConfig.getConfigInt(BCSConfig.NAMING_PORT);

        NamingServiceProxy ns = new NamingServiceProxy(naming_host, naming_port);
        RemoteService rs = new RemoteService(IAccountAuthenticatorService.class,ns);
        return rs;
    }

}
