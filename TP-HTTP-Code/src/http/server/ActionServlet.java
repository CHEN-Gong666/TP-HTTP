package http.server;

import javax.servlet.http.*;
import java.io.*;

public class ActionServlet extends HttpServlet {

    public void doGet(PrintWriter out, String requestUrl){

        switch (requestUrl) {
            case "/adder":
                String filePath = "TP-HTTP-Code/sources/Adder.html";
                sendResponse(out, filePath, "text/html");
                break;
            case "":

        }
        out.flush();
    }

    public void doHead(PrintWriter out, String requestUrl){
        // Send the headers
        String filePath = "TP-HTTP-Code/sources" + requestUrl;
        File resource = new File(filePath);
        if(resource.exists())
        out.print(makeHeader(true, "text/html"));
        else
        out.print(makeHeader(false, "text/html"));
        out.flush();
    }

    public void doPost(PrintWriter out, String requestUrl, String body){

        String filePath = "TP-HTTP-Code/sources" + requestUrl;
        try {
            FileOutputStream fos = new FileOutputStream(filePath, true);
            fos.write(body.getBytes());
            fos.write("\r\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendResponse(out, filePath, "text/html");
        out.flush();
    }

    public void doPut(PrintWriter out, String requestUrl, String body){
        System.out.println("in the do put");
        String filePath = "TP-HTTP-Code/sources" + requestUrl;
        try {
            // if the file exists, we append the content,
            // otherwise we create the file and then append the content
            File file = new File (filePath);
            if (file.exists()) {
                System.out.printf("file exists");
            } else {
                System.out.printf("creating file");
                file = new File("TP-HTTP-Code/sources", filePath);
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(filePath, false);
            fos.write(body.getBytes());
            fos.write("\r\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendResponse(out, filePath, "text/html");
        out.flush();
    }
    public void doDelete(PrintWriter out, String requestUrl){

        String filePath = "TP-HTTP-Code/sources" + requestUrl;
        File file = new File (filePath);
        if (file.delete()) {
            out.println(makeHeader(true, "text"));
            out.println("The file is successfully deleted");
        }else{
            out.println(makeHeader(false, "text"));
            out.println("Deletion has failed");
        }

        out.flush();
    }

    public void sendResponse(PrintWriter out, String filePath, String contentType) {
        final File file = new File(filePath);
        // Send the headers
        out.println(makeHeader(true, contentType));

        // Send the HTML page
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            // br.reset();
            String line = br.readLine();
            while (line != null) {
                out.println(line);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String makeHeader(boolean status, String contentType){
        String header = "HTTP/1.0 ";
        if (status) header += "200 OK";
        else header += "404 NOT FOUND";
        header += "\r\n";
        header += "Content-Type: " + contentType + "\r\n";
        header += "Server: Bot\r\n";
        // this blank line signals the end of the headers
        header += "\r\n";
        return header;
    }



}
