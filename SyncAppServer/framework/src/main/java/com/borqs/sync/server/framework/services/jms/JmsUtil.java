package com.borqs.sync.server.framework.services.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.borqs.sync.server.common.runtime.Context;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Properties;

public class JmsUtil {

	private static String mUsername;
	private static String mPassword;
	private static String mUrl;
	private static final String JMS_AUTH_USERNAME_KEY = "username";
	private static final String JMS_AUTH_PASSWORD_KEY = "password";
	private static final String JMS_AUTH_URL_KEY = "url";

	public static Connection createConnection(Context context) {
		try {
            Properties jmsPro = context.getConfig().getJMSServiceConfig();
		mUsername = jmsPro.getProperty(JMS_AUTH_USERNAME_KEY);
		mPassword = jmsPro.getProperty(JMS_AUTH_PASSWORD_KEY);
		mUrl = jmsPro.getProperty(JMS_AUTH_URL_KEY);
						// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					mUsername, mPassword, mUrl);
			// Create a Connection
			Connection connection = null;
			connection = connectionFactory.createConnection();
            return connection;
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

    public static void close(Connection connection,Session session,MessageProducer producer){
            try {
                if(producer != null){
                    producer.close();
                 }
                if(session != null){
                    session.close();
                }
                if(connection != null){
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
        }
    }

	public static Session createSession(Connection connection,
			boolean transacted, int ackMode) {
		try {
			return connection.createSession(transacted, ackMode);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

}
