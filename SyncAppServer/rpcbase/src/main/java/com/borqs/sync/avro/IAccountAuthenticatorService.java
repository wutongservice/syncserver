/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.borqs.sync.avro;

@SuppressWarnings("all")
public interface IAccountAuthenticatorService {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"IAccountAuthenticatorService\",\"namespace\":\"com.borqs.sync.avro\",\"types\":[{\"type\":\"record\",\"name\":\"XAuthenticatorResponse\",\"fields\":[{\"name\":\"status_code\",\"type\":\"int\"},{\"name\":\"content\",\"type\":\"string\"}]}],\"messages\":{\"login\":{\"request\":[{\"name\":\"user\",\"type\":\"string\"},{\"name\":\"pass\",\"type\":\"string\"}],\"response\":\"XAuthenticatorResponse\"},\"logout\":{\"request\":[{\"name\":\"user\",\"type\":\"string\"},{\"name\":\"pass\",\"type\":\"string\"}],\"response\":\"XAuthenticatorResponse\"},\"getName\":{\"request\":[],\"response\":\"XAuthenticatorResponse\"},\"isSyncing\":{\"request\":[{\"name\":\"username\",\"type\":\"string\"},{\"name\":\"deviceId\",\"type\":\"string\"}],\"response\":\"XAuthenticatorResponse\"},\"enterSyncBeginStatus\":{\"request\":[{\"name\":\"username\",\"type\":\"string\"},{\"name\":\"deviceId\",\"type\":\"string\"}],\"response\":\"XAuthenticatorResponse\"},\"enterSyncEndStatus\":{\"request\":[{\"name\":\"username\",\"type\":\"string\"}],\"response\":\"XAuthenticatorResponse\"}}}");
  com.borqs.sync.avro.XAuthenticatorResponse login(java.lang.CharSequence user, java.lang.CharSequence pass) throws org.apache.avro.AvroRemoteException;
  com.borqs.sync.avro.XAuthenticatorResponse logout(java.lang.CharSequence user, java.lang.CharSequence pass) throws org.apache.avro.AvroRemoteException;
  com.borqs.sync.avro.XAuthenticatorResponse getName() throws org.apache.avro.AvroRemoteException;
  com.borqs.sync.avro.XAuthenticatorResponse isSyncing(java.lang.CharSequence username, java.lang.CharSequence deviceId) throws org.apache.avro.AvroRemoteException;
  com.borqs.sync.avro.XAuthenticatorResponse enterSyncBeginStatus(java.lang.CharSequence username, java.lang.CharSequence deviceId) throws org.apache.avro.AvroRemoteException;
  com.borqs.sync.avro.XAuthenticatorResponse enterSyncEndStatus(java.lang.CharSequence username) throws org.apache.avro.AvroRemoteException;
}
