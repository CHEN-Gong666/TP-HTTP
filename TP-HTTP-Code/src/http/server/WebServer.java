///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Vector;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {
  ServerSocket s;
  Socket remote;
  ActionServlet servlet;

  /**
   * constructor of webServer
   */
  public WebServer(){
  }

  /**
   * WebServer constructor.
   */
  protected void start() {
    System.out.println("Webserver starting up on port 4000");
    System.out.println("(press ctrl-c to exit)");

    try {
      // create the main server socket
      s = new ServerSocket(4000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (; ; ) {
      Vector<String> headers = new Vector();
      String requestType = "";
      String requestUrl = "";

      try {
        // wait for a connection
        remote = s.accept();
        servlet = new ActionServlet(remote);
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String header = ".";
        System.out.println("Headers:********");
        while (header != null && !header.equals("")) {
          header = in.readLine();
          System.out.println(header);
          headers.add(header);
        }
        if (!headers.isEmpty()) {
          requestType = headers.get(0).split(" ")[0];
          System.out.println("RquestType: " + requestType);
          requestUrl = headers.get(0).split(" ")[1];
          System.out.println("RequestUrl: " + requestUrl);
        }

        // read the body
        String body = "the body hasn't been initialized";
        if(in.ready()){
          String line = ".";
          body = "";
          while (in.ready()) {
            line = in.readLine();
            System.out.println("line: " + line);
            body += line + "\r\n";
          }
          System.out.println(body);
        }

        // Send the response
        switch (requestType.toUpperCase()){
          case "GET":
            servlet.doGet(requestUrl);
            break;
          case "HEAD":
            servlet.doHead(requestUrl);
            break;
          case "POST":
            servlet.doPost(requestUrl, body);
            break;
          case "PUT":
            servlet.doPut(requestUrl, body);
            break;
          case "DELETE":
            servlet.doDelete(requestUrl);
            break;
          default:
            out.println(servlet.makeHeader(501,"text/html"));
            out.println("<html>");
            out.println("<head><title>501 NOT IMPLEMENTED</title></head>");
            out.println("<body><h1>501 NOT IMPLEMENTED</h1>");
            out.println("<p>This http request is not implemented yet.</p></body>");
            out.println("</html>");
        }

        out.flush();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Start the application.
   *
   * @param args Command line parameters are not used.
   */
  public static void main(String args[]) {
    http.server.WebServer ws = new http.server.WebServer();
    ws.start();
  }
}
