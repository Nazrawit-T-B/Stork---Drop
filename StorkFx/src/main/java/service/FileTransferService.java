package service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class FileTransferService {
    public String response="";

    public void uploadFileToServer(File file) throws IOException {

        String boundary = "----JavaFXBoundary" + System.currentTimeMillis();

        URL url = new URL("http://localhost:8080/file");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream outputStream = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"\r\n");
            writer.append("Content-Type: application/octet-stream\r\n\r\n");
            writer.flush();


            Files.copy(file.toPath(), outputStream);
            outputStream.flush();

            writer.append("\r\n");
            writer.append("--").append(boundary).append("--\r\n");
            writer.flush();
        }

        int responseCode = connection.getResponseCode();

        if(responseCode==200){
            response="File Uploaded:- ";
        }else{
            response="Response"+responseCode;
        }
        System.out.println("Response: " + responseCode);
    }
    public void downloadFromServer(String filename) throws IOException{
        URL url=new URL("http://localhost:8080/download?fileName="+ filename);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        try{
        int responseCode=connection.getResponseCode();
        if(responseCode==200){
            InputStream in= connection.getInputStream();

            String downloadsPath=System.getProperty("user.home")+File.separator+"Downloads";
            File downloadsFolder=new File(downloadsPath);
            if(!downloadsFolder.exists()){
                downloadsFolder.mkdirs();
            }
            File outputFile=new File(downloadsFolder,filename);

            try(FileOutputStream fos= new FileOutputStream(outputFile)){
                byte [] buffer=new byte[4096];
                int bytesRead;
                while((bytesRead=in.read(buffer))!=-1){
                    fos.write(buffer,0,bytesRead);
                }
            }
            in.close();

            response="Downloaded to : "+ outputFile.getAbsolutePath();
        }else{
            response="Download failed. Response code: " +responseCode;
        }}finally{
            connection.disconnect();
        }
    }
}
