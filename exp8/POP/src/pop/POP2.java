/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pop;

/**
 *
 * @author bala9
 */
import java.io.*;
import java.util.*;
import javax.mail.*;

public class POP2 {

   public static void fetch(String pop3Host, String storeType, String user,
         String password) {
      try {
         // create properties field
         Properties properties = new Properties();
         properties.put("mail.store.protocol", "pop3");
         properties.put("mail.pop3.host", pop3Host);
         properties.put("mail.pop3.port", "995");
         properties.put("mail.pop3.starttls.enable", "true");
         Session emailSession = Session.getDefaultInstance(properties);

         Store store = emailSession.getStore("pop3s");

         store.connect(pop3Host, user, password);

         Folder emailFolder = store.getFolder("INBOX");
         emailFolder.open(Folder.READ_ONLY);

         BufferedReader reader = new BufferedReader(new InputStreamReader(
               System.in));
         Message[] messages = emailFolder.getMessages();
         System.out.println("messages.length---" + messages.length);

         for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            System.out.println("---------------------------------");
            writePart(message);
            String line = reader.readLine();
            if ("YES".equals(line)) {
               message.writeTo(System.out);
            } else if ("QUIT".equals(line)) {
               break;
            }
         }
         emailFolder.close(false);
         store.close();

      } catch (NoSuchProviderException e) {
         e.printStackTrace();
      } catch (MessagingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {

      String host = "pop.gmail.com";
      String mailStoreType = "pop3";
      String username = "jawajava748@gmail.com";
      String password = "ctjijzbhtytkgwum";

      fetch(host, mailStoreType, username, password);

   }

   public static void writePart(Part p) throws Exception {
      if (p instanceof Message)
         writeEnvelope((Message) p);

      System.out.println("----------------------------");
      System.out.println("CONTENT-TYPE: " + p.getContentType());

      if (p.isMimeType("text/plain")) {
         System.out.println("This is plain text");
         System.out.println("---------------------------");
         System.out.println((String) p.getContent());
      } else if (p.isMimeType("multipart/*")) {
         System.out.println("This is a Multipart");
         System.out.println("---------------------------");
         Multipart mp = (Multipart) p.getContent();
         int count = mp.getCount();
         for (int i = 0; i < count; i++)
            writePart(mp.getBodyPart(i));
      } else if (p.isMimeType("message/rfc822")) {
         System.out.println("This is a Nested Message");
         System.out.println("---------------------------");
         writePart((Part) p.getContent());
      } else if (p.isMimeType("image/jpeg")) {
         System.out.println("--------> image/jpeg");
         Object o = p.getContent();

         InputStream x = (InputStream) o;
         System.out.println("x.length = " + x.available());
         int i = 0;
         byte[] bArray = new byte[x.available()];

         while ((i = (int) ((InputStream) x).available()) > 0) {
            int result = (int) (((InputStream) x).read(bArray));
            if (result == -1)
               break;
         }
         FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
         f2.write(bArray);
      } else if (p.getContentType().contains("image/")) {
         System.out.println("content type" + p.getContentType());
         File f = new File("image" + new Date().getTime() + ".jpg");
         DataOutputStream output = new DataOutputStream(
               new BufferedOutputStream(new FileOutputStream(f)));
         com.sun.mail.util.BASE64DecoderStream test = (com.sun.mail.util.BASE64DecoderStream) p
               .getContent();
         byte[] buffer = new byte[1024];
         int bytesRead;
         while ((bytesRead = test.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
         }
      } else {
         Object o = p.getContent();
         if (o instanceof String) {
            System.out.println("This is a string");
            System.out.println("---------------------------");
            System.out.println((String) o);
         } else if (o instanceof InputStream) {
            System.out.println("This is just an input stream");
            System.out.println("---------------------------");
            InputStream is = (InputStream) o;
            is = (InputStream) o;
            int c;
            while ((c = is.read()) != -1)
               System.out.write(c);
         } else {
            System.out.println("This is an unknown type");
            System.out.println("---------------------------");
            System.out.println(o.toString());
         }
      }

   }

   public static void writeEnvelope(Message m) throws Exception {
      System.out.println("This is the message envelope");
      System.out.println("---------------------------");
      Address[] a;
      if ((a = m.getFrom()) != null) {
         for (int j = 0; j < a.length; j++)
            System.out.println("FROM: " + a[j].toString());
      }
      if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
         for (int j = 0; j < a.length; j++)
            System.out.println("TO: " + a[j].toString());
      }
      if (m.getSubject() != null)
         System.out.println("SUBJECT: " + m.getSubject());

   }

}
