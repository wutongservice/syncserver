package com.borqs.sync.server.rpc.service.datasync.account;

import com.borqs.sync.avro.IAccountAuthenticatorService;
import com.borqs.sync.avro.XAuthenticatorResponse;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.sync.SyncProvider;
import com.borqs.sync.server.datasync.service.AccountAuthenticatorServiceImpl;
import com.borqs.sync.server.rpc.service.datasync.RpcServiceLogger;
import org.apache.avro.AvroRemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 3/27/12
 * Time: 10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class AccountAuthenticatorService implements IAccountAuthenticatorService{
    
    private Context mContext;
    private AccountAuthenticatorServiceImpl mAccountAuthenticatorServiceImpl;
    private SyncProvider mSyncProvider;

    public AccountAuthenticatorService(Context context){
        mContext = context;
        mAccountAuthenticatorServiceImpl = new AccountAuthenticatorServiceImpl(context);
        mSyncProvider = new SyncProvider(context);
        mSyncProvider.setLogger(RpcServiceLogger.getLogger(context));
    }

    @Override
    public XAuthenticatorResponse login(CharSequence user,CharSequence password) throws AvroRemoteException {
        String loginResult = mAccountAuthenticatorServiceImpl.login(user.toString(),password.toString());
        return parseAuthenResult(loginResult,user.toString());
    }

    @Override
    public XAuthenticatorResponse logout(CharSequence user,CharSequence password) throws AvroRemoteException {
        String logoutResult = mAccountAuthenticatorServiceImpl.logout(user.toString(),password.toString());
        return parseAuthenResult(logoutResult,user.toString());
    }

    @Override
    public XAuthenticatorResponse getName() throws AvroRemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public XAuthenticatorResponse isSyncing(CharSequence username, CharSequence deviceId) throws AvroRemoteException {
        boolean isSyncing = mSyncProvider.isSyncing(username.toString(),deviceId.toString());
        XAuthenticatorResponse resp = new XAuthenticatorResponse();
        resp.status_code = AuthenticatorProtocols.StatusCode.SUCCESS;
        resp.content = AuthenticatorProtocols.generateSyncStatus(isSyncing);
        return resp;
    }

    @Override
    public XAuthenticatorResponse enterSyncBeginStatus(CharSequence username, CharSequence deviceId) throws AvroRemoteException {
        mSyncProvider.enterSyncBeginStatus(username.toString(),deviceId.toString());
        XAuthenticatorResponse resp = new XAuthenticatorResponse();
        resp.status_code = AuthenticatorProtocols.StatusCode.SUCCESS;
        resp.content= "";
        return resp;
    }

    @Override
    public XAuthenticatorResponse enterSyncEndStatus(CharSequence username) throws AvroRemoteException {
        mSyncProvider.enterSyncEndStatus(username.toString());
        XAuthenticatorResponse resp = new XAuthenticatorResponse();
        resp.status_code = AuthenticatorProtocols.StatusCode.SUCCESS;
        resp.content= "";
        return resp;
    }

    private XAuthenticatorResponse parseAuthenResult(String result,String userId){
        //TODO default should be fail
        XAuthenticatorResponse resp = new XAuthenticatorResponse();
        resp.status_code = AuthenticatorProtocols.StatusCode.FAIL;
        resp.content = "";
        if(result == null){
            return resp;
        }
        try {
            resp.status_code = AuthenticatorProtocols.StatusCode.SUCCESS;
            resp.content = AuthenticatorProtocols.generateJsonAuthResponse(userId);
        } catch(Exception e){
            e.printStackTrace();
        }
        return resp;
    }



}
