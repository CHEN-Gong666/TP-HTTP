package http.server;

import javax.imageio.ImageIO;
import javax.servlet.http.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class ActionServlet extends HttpServlet {
    Socket remote;
    PrintWriter out;
    byte[] imgByte;
    ActionServlet(Socket r) throws IOException {
        this.remote = r;
        this.out = new PrintWriter(remote.getOutputStream());
    }

    /**
     * method which loads different element on a web page
     * @param requestUrl String: the url of the request
     * @throws IOException the input and output errors
     */

    public void doGet(String requestUrl) throws IOException {
        String filePath = "";
        String requestParam = "";
        String requestBody = "";
        if (requestUrl.contains("?")){
            requestBody = requestUrl.split("\\?")[0];
            System.out.println("body: " + requestBody);
            requestParam = requestUrl.split("\\?")[1];
            System.out.println("param: " + requestParam);
        } else {
            requestBody = requestUrl;
        }
        switch (requestBody) {
            case "/adder.html":
                filePath = "TP-HTTP-Code/sources/adder.html";
                out.println(makeHeader(200, "text/html"));
                out.flush();
                sendResponse(filePath, "text/html");
                break;
            case "/text.txt":
                filePath = "TP-HTTP-Code/sources/text.txt";
                out.println(makeHeader(200, "text/html"));
                out.flush();
                sendResponse(filePath, "text/html");
                break;
            case "/logo.png":
                filePath = "TP-HTTP-Code/sources/logo.png";

                BufferedImage img = ImageIO.read(new File(filePath));
                System.out.println("img: " + img);
                out.flush();
                ByteArrayOutputStream pngBaos = new ByteArrayOutputStream();
                ImageIO.write(img,"png", pngBaos);
                pngBaos.flush();
                imgByte = pngBaos.toByteArray();

                pngBaos.close();
                out.println(makeHeader(200, "image/png"));
                out.flush();
                sendResponse(filePath, "image/png");
                break;
            case "/music.mp3":
                filePath = "TP-HTTP-Code/sources/music.mp3";
                out.println(makeHeader(200, "audio/mp3"));
                out.flush();
                sendResponse(filePath, "audio/mp3");
                break;
            case "/video.mp4":
                filePath = "TP-HTTP-Code/sources/video.mp4";
                out.println(makeHeader(200, "video/mpeg4"));
                out.flush();
                sendResponse(filePath, "video/mpeg4");
                break;
            case "/admin.txt":
                filePath = "TP-HTTP-Code/sources/admin.txt";
                out.println(makeHeader(403, "text/html"));
                out.println("<html>");
                out.println("<head><title>403 Forbidden</title></head>");
                out.println("<body><h1>403 Forbidden</h1>");
                out.println("<p>You access to this page is denied.</p></body>");
                out.println("</html>");
                break;
            case "/http.multiplier.class":
                byte[] result = new byte[256];
                int a=0;
                int b=0;
                int c=0;
                int i=0;
                if(!requestParam.equals("")) {
                    a = Character.getNumericValue(requestParam.split("&")[0].charAt(2));
                    b =  Character.getNumericValue(requestParam.split("&")[1].charAt(2));
                    System.out.println(a);
                    System.out.println(b);
                } else {
                    // handle errors
                }
                Process process = Runtime.getRuntime().exec("java -classpath TP-HTTP-Code\\sources http.multiplier "+a+" "+b);
                while( (c = process.getInputStream().read()) != -1 ){
                    System.out.println(c);
                    result[i] = (byte)c;
                    ++i;
                }
                out.println(makeHeader(200, "text/html"));
                System.out.println("result:" + new String (result));
                out.println(new String (result));
                break;

            default:
                out.println(makeHeader(404, "text/html"));
                out.println("<html>");
                out.println("<head><title>404 NOT FOUND</title></head>");
                out.println("<body><h1>404 NOT FOUND</h1>");
                out.println("<p>The URL is not recognized by the server</p></body>");
                out.println("</html>");
        }
        out.flush();
    }
    /**
     * method which calls makeHead when knowing a request
     * @param requestUrl String: the url of the request
     */
    public void doHead(String requestUrl){
        // Send the headers
        String filePath = "TP-HTTP-Code/sources" + requestUrl;
        File resource = new File(filePath);
        if(resource.exists()) {
            out.println(makeHeader(204, "text/html"));
        }
        else {
            out.println(makeHeader(404, "text/html"));
            out.println("<html>");
            out.println("<head><title>404 The file doesn't exist</title></head>");
            out.println("</html>");
        }
        out.flush();
    }

    /**
     * method which posts a file on the server
     * @param requestUrl String: the url of the request
     * @param body the body of a web page to be written
     */
    public void doPost(String requestUrl, String body){
        String filePath = "TP-HTTP-Code/sources" + requestUrl;
        File file = new File(filePath);
        try {
            if (file.exists()) {
                FileOutputStream fos = new FileOutputStream(filePath, true);
                fos.write(body.getBytes());
                fos.write("\r\n".getBytes());
                out.println(makeHeader(200, "text/html"));
                sendResponse(filePath, "text/html");
            } else {
                out.println(makeHeader(404, "text/html"));
                out.println(makeHeader(404, "text/html"));
                out.println("<html>");
                out.println("<head><title>404 The file doesn't exist</title></head>");
                out.println("</html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.flush();
    }

    /**
     * method which puts a file on the server
     * @param requestUrl String: the url of the request
     * @param body the body of a web page to be written
     */
    public void doPut(String requestUrl, String body){
        String filePath = "TP-HTTP-Code/sources" + requestUrl;
        try {
            // if the file exists, we append the content,
            // otherwise we create the file and then append the content
            File file = new File (filePath);
            if (file.exists()) {
                System.out.printf("file exists");
                out.println(makeHeader(200, "text/html"));

            } else {
                System.out.printf("creating file");
                file = new File("./TP-HTTP-Code/sources", requestUrl);
                file.createNewFile();
                out.println(makeHeader(201, "text/html"));

            }

            FileOutputStream fos = new FileOutputStream(filePath, false);
            fos.write(body.getBytes());
            fos.write("\r\n".getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendResponse(filePath, "text/html");
        out.flush();
    }

    /**
     * method who deletes a file on the server
     * @param requestUrl String: the url of the request
     */
    public void doDelete(String requestUrl){

        String filePath = "TP-HTTP-Code/sources" + requestUrl;
        File file = new File (filePath);
        System.out.println(file.isFile());
        if ( !file.exists()) {
            out.println(makeHeader(404, "text"));
            out.println("The file doesn't exist");
        } else if (file.delete()) {
            out.println(makeHeader(200, "text"));
            out.println("The file is successfully deleted");
        }else{
            out.println(makeHeader(500, "text"));
            out.println("Deletion has failed due to internal error");
        }
        out.flush();
    }

    /**
     * method that sends a response to the web
     * @param filePath the address of a file to be opened
     * @param contentType String: type of the file to be opened
     */
    public void sendResponse(String filePath, String contentType) {
        final File file = new File(filePath);

        if( contentType.equals("text/html")) {
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
        } else if (contentType.equals("image/png")) {
            try {
                remote.getOutputStream().write(imgByte, 0, imgByte.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         else {
            try{
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
                BufferedOutputStream bos = new BufferedOutputStream(remote.getOutputStream());

                byte[] buf = new byte[1024];
                while (bis.read(buf) != -1)  {
                    bos.write(buf);
                }
                bos.flush();
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * method that make a web head
     * @param status int: state of the server
     * @param contentType String: type of the file to be opened
     * @return string the read who is linked to the state of the web
     */
    public String makeHeader(int status, String contentType){

        String header = "";
        header += "HTTP/1.0 ";
        if (status == 200) header += "200 OK";
        else if (status == 201) header += "201 CREATED";
        else if (status == 204) header += "204 NON CONTENT";
        else if (status == 403) header += "404 FORBIDDEN";
        else if (status == 404) header += "404 NOT FOUND";
        else if (status == 500) header += "500 INTERNAL ERROR";
        else header += "501 NOT IMPLEMENTED";
        header += "\r\n";
        header += "Content-Type: " + contentType + "\r\n";
        if (contentType.equals("image/png")){
            header += "Content-length: " + imgByte.length + "\r\n";
        }
        header += "Server: Bot\r\n";

        // this blank line signals the end of the headers
        header += "\r\n";
        return header;
    }



}
