package client;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;

import java.security.KeyStore;
import java.security.cert.*;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */

public class Client {

  private static final String clientKeystorePath = "certificates/client/";
  private static final String clientTruststorePath = "certificates/client/clienttruststore";

  public static void main(String[] args) throws Exception {
    String host = null;
    int port = -1;
    for (int i = 0; i < args.length; i++) {
      System.out.println("args[" + i + "] = " + args[i]);
    }
    if (args.length < 1) {
      System.out.println("USAGE: java client [host] port");
      System.exit(-1);
    }
    try { /* get input parameters */
      if (args.length == 1)
        port = Integer.parseInt(args[0]);
      else {
        host = args[0];
        port = Integer.parseInt(args[1]);
      }
    } catch (IllegalArgumentException e) {
      System.out.println("USAGE: java client [host] port");
      System.exit(-1);
    }

    while (true) {
      Console console = System.console();
      String userId = console.readLine("Enter user ID: ");
      char[] password = console.readPassword("Enter password: ");
      try {
        SSLSocketFactory factory = null;
        try {
          KeyStore ks = KeyStore.getInstance("JKS");
          KeyStore ts = KeyStore.getInstance("JKS");
          KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
          TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
          SSLContext ctx = SSLContext.getInstance("TLSv1.2");
          // keystore password (storepass)
          ks.load(new FileInputStream(clientKeystorePath + userId + "keystore"), password);
          // truststore password (storepass);
          ts.load(new FileInputStream(clientTruststorePath), password);
          kmf.init(ks, password); // user password (keypass)
          tmf.init(ts); // keystore can be used as truststore here
          ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
          factory = ctx.getSocketFactory();
        } catch (Exception e) {
          System.out.println("Invalid login details.");
          continue;
        }
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        System.out.println("\nsocket before handshake:\n" + socket + "\n");

        /*
         * send http request
         *
         * See SSLSocketClient.java for more information about why
         * there is a forced handshake here when using PrintWriters.
         */

        socket.startHandshake();
        SSLSession session = socket.getSession();
        Certificate[] cert = session.getPeerCertificates();
        String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
        System.out
            .println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
        System.out.println("socket after handshake:\n" + socket + "\n");
        System.out.println("secure connection established\n\n");

        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msg;
        for (;;) {
          String input;
          // Read server response until "DONE"
          while (!(input = in.readLine()).equals("---")) {
            System.out.println(input + "\n");
          }

          System.out.print(">");
          msg = read.readLine();
          if (msg.equalsIgnoreCase("quit")) {
            break; // Exit loop if user types "quit"
          }
          out.println(msg);
        }
        in.close();
        out.close();
        read.close();
        socket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
