/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.borqs.sync.avro;

@SuppressWarnings("all")
public interface IRPCNamingService {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"IRPCNamingService\",\"namespace\":\"com.borqs.sync.avro\",\"types\":[],\"messages\":{\"register\":{\"request\":[{\"name\":\"serviceURI\",\"type\":\"string\"},{\"name\":\"multiInstance\",\"type\":\"boolean\"}],\"response\":\"string\"},\"unregister\":{\"request\":[{\"name\":\"serviceURI\",\"type\":\"string\"}],\"response\":\"null\",\"one-way\":true},\"lookup\":{\"request\":[{\"name\":\"serviceProtocol\",\"type\":\"string\"}],\"response\":\"string\"}}}");
  java.lang.CharSequence register(java.lang.CharSequence serviceURI, boolean multiInstance) throws org.apache.avro.AvroRemoteException;
  void unregister(java.lang.CharSequence serviceURI);
  java.lang.CharSequence lookup(java.lang.CharSequence serviceProtocol) throws org.apache.avro.AvroRemoteException;
}
