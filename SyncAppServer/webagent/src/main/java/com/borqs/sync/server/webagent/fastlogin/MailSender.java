package com.borqs.sync.server.webagent.fastlogin;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.rmi.server.UID;
import java.util.Date;
import java.util.Properties;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.util.WebLog;
 
public class MailSender {  
    Context context;
    String myHost = "smtp.bizmail.yahoo.com";
    String myPort = "465";
    String mySubject = "播思帐号注册验证码";
    String myAddress = "borqs.support@borqs.com";
    String myMsgFormat = "<table width=\"600\" align=\"left\" border=\"0\" cellpadding=\"3\" cellspacing=\"3\">"
                        +"<tbody>"
                        +"<tr valign=\"middle\">"
                        +"<td bgcolor=#b1cf92 height=\"22\" valign=\"middle\">"
                        +"<img src=\"http://api.borqs.com/sys/icon/bpc.png\" width=\"40\" border=\"0\">"
                        +"<b>&nbsp;&nbsp;播思帐号注册</b>"
                        +"</td> </tr> <tr><td colspan=\"2\" height=\"10\"></td></tr>"
                        +"<tr><td >%s<br><br></td></tr>"
                        +"<tr><td>北京播思软件技术有限公司<br></td></tr>"
                        +"</tbody></table>";
    
    
    public MailSender(Context ctx){
        context = ctx;
    }
    
    private void log(String msg){
        WebLog.getLogger(context).info(msg);
    }
    
    private boolean rawSend(Message msg)
    {
         try {
             Transport.send(msg);
             return true;
         } catch(MessagingException me){
             log("raw send exception:" + me.getMessage());
             return false;
         }
    }
    
    public boolean sendMessage(String toUser, String message) {
        boolean ok = false;
        
        try {
            message = String.format(myMsgFormat, message);
            String uidString = "";            
            // Set the email properties necessary to send email
            final Properties props = System.getProperties(); 
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", myHost);
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.server", myHost);
            props.put("mail.smtp.auth", "true");
            //props.put("mail.smtp.port", port);
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", myPort);
            props.setProperty("mail.smtp.socketFactory.port", myHost);  
            //props.put("mail.smtp.starttls.required", true);
            Session sess = Session.getInstance(props, new MailAuthentication());
            Message msg = new MimeMessage(sess);
 
            InternetAddress[] address = {new InternetAddress(toUser)};
            msg.setRecipients(Message.RecipientType.TO, address);
 
            InternetAddress from = new InternetAddress(myAddress); 
            msg.setFrom(from);
            msg.setSubject(mySubject);
 
            UID msgUID = new UID();
 
            uidString = msgUID.toString();
 
            msg.setHeader("X-Mailer", uidString);
 
            msg.setSentDate(new Date());
 
            MimeMultipart mp = new MimeMultipart();
 
            // create body part for textarea
            MimeBodyPart mbp1 = new MimeBodyPart(); 
            mbp1.setContent(message, "text/html;charset=utf-8");            
            mp.addBodyPart(mbp1);
            msg.setContent(message, "text/html;charset=utf-8");
            for(int i = 0; i < 3; i++){
             if(rawSend(msg)){
                 ok = true;
                 break;
             }
            }
 
        } catch (Exception eq) {
            eq.printStackTrace();
            log("Messaging Exception: " + eq.getMessage());
        }
        
        if(!ok){
            log("Could not connect to SMTP server.");
        }
        
        return ok;
    }
 
    class MailAuthentication extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(myAddress, "borqsbpc");
        }
    }
     
    /*public  static void  main(String[] args){
     System.out.print("test start \r\n");
     MailSender sendMail = new MailSender();
     sendMail.sendMessage("hui79@163.com", "4567");
     System.out.print("test end \r\n");
    }*/
}