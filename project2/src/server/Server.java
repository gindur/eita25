package server;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;

import users.Person;
import util.AuthService;
import util.DatabaseManager;
import util.IOManager;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class Server implements Runnable {
  private ServerSocket serverSocket = null;
  private static int numConnectedClients = 0;
  private static final String serverKeystorePath = "certificates/server/serverkeystore";
  private static final String serverTruststorePath = "certificates/server/servertruststore";

  private DatabaseManager dbm = new DatabaseManager();
  private IOManager iom = new IOManager(new AuthService(dbm));

  
  public Server(ServerSocket ss) throws IOException {
    dbm.initialize();

    serverSocket = ss;
    newListener();
  }

  public void run() {
    SSLSocket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    try {
      socket=(SSLSocket)serverSocket.accept();
      newListener();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      numConnectedClients++;

      System.out.println("client connected");
      System.out.println("client name (cert subject DN field): " + subject);
      //added new
      String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName();
      System.out.println("client certificate issuer: " + issuer);
      BigInteger serialNumber = ((X509Certificate) cert[0]).getSerialNumber();
      System.out.println("client certificate serial number: " + serialNumber);
      System.out.println(numConnectedClients + " concurrent connection(s)\n");

      Person user = dbm.getPersonFromSerial(serialNumber.toString(16));
      System.out.println("Connected: " + user.getId());

      
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      sendInitialMessages(out, user);

        String clientMsg;
        while ((clientMsg = in.readLine()) != null) {  // Read until client disconnects
            if (!clientMsg.isEmpty()) {
                processClientMessage(out, user, clientMsg);
            }
        }
    } catch (IOException e) {
        System.out.println("Error in communication with the client: " + e.getMessage());
        e.printStackTrace();
    } finally {
        closeResources(out, in, socket);
        decrementConnectedClients();
    }
}

private void sendInitialMessages(PrintWriter out, Person user) {
    out.println(iom.startMessage(user));
    out.println("---");
}

private void processClientMessage(PrintWriter out, Person user, String clientMsg) {
    String response = iom.handleInput(user, clientMsg);
    out.println(response);  // Send response
    out.println("Possible commands:");
    out.println(iom.showOptions(user));  // Send additional options
    out.println("---");  // End of message signal
}

private void closeResources(PrintWriter out, BufferedReader in, Socket socket) {
    try {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    } catch (IOException e) {
        System.out.println("Failed to close resources: " + e.getMessage());
    }
}

private void decrementConnectedClients() {
    numConnectedClients--;
    System.out.println("Client disconnected");
    System.out.println(numConnectedClients + " concurrent connection(s)\n");
}
  
  private void newListener() { (new Thread(this)).start(); } // calls run()
  public static void main(String args[]) {
    System.out.println("\nServer Started\n");
    int port = -1;
    if (args.length >= 1) {
      port = Integer.parseInt(args[0]);
    }
    String type = "TLSv1.2";
    try {
      ServerSocketFactory ssf = getServerSocketFactory(type);
      ServerSocket ss = ssf.createServerSocket(port, 0, InetAddress.getByName(null));
      ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
      new Server(ss);
    } catch (IOException e) {
      System.out.println("Unable to start Server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static ServerSocketFactory getServerSocketFactory(String type) {
    if (type.equals("TLSv1.2")) {
      SSLServerSocketFactory ssf = null;
      try { // set up key manager to perform server authentication
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        char[] password = "password".toCharArray();
        // keystore password (storepass)
        ks.load(new FileInputStream(serverKeystorePath), password);  
        // truststore password (storepass)
        ts.load(new FileInputStream(serverTruststorePath), password); 
        kmf.init(ks, password); // certificate password (keypass)
        tmf.init(ts);  // possible to use keystore as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        ssf = ctx.getServerSocketFactory();
        return ssf;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return ServerSocketFactory.getDefault();
    }
    return null;
  }
}
