package service;

import ui.FilesUI;
import ui.SessionManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileTransferService {
    public String response="";

    public void uploadFileToServer(File file) throws IOException {

        String boundary = "----JavaFXBoundary" + System.currentTimeMillis();

        URL url = new URL("http://localhost:8080/api/file");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Authorization", "Bearer "+ SessionManager.getActiveToken());

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
        URL url=new URL("http://localhost:8080/api/download?fileName="+ filename);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization","Bearer "+ SessionManager.getActiveToken());
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
    public void deleteFromServer(String filename) throws IOException{
        URL url=new URL("http://localhost:8080/api/delete?fileName="+ filename);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();

        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization","Bearer "+ SessionManager.getActiveToken());

        int responseCode=connection.getResponseCode();
        if(responseCode!=HttpURLConnection.HTTP_OK){
            throw new IOException("Failed to delete file, server responded: "+ responseCode);
        }
    }
    public List<FilesUI.FileEntry> fetchFilesFromServer() throws IOException {
        URL url = new URL("http://localhost:8080/api/files");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + SessionManager.getActiveToken());

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException("Failed to fetch files");

        String response = new String(connection.getInputStream().readAllBytes());

        List<FilesUI.FileEntry> entries = new ArrayList<>();
        response = response.trim().replaceAll("^\\[|\\]$", "");
        String[] objects = response.split("\\},\\{");

        for (String obj : objects) {
            obj = obj.replaceAll("[{}]", "");
            String filename = extractValue(obj, "filename");
            String size = extractValue(obj, "size") + " bytes";
            String lastModified = extractValue(obj, "lastModified");
            String permission = extractValue(obj, "permission");
            entries.add(new FilesUI.FileEntry(filename, size, lastModified, permission));
        }
        return entries;
    }

    private String extractValue(String obj, String key) {
        String search = "\"" + key + "\":";
        int start = obj.indexOf(search) + search.length();
        String rest = obj.substring(start).trim();
        if (rest.startsWith("\"")) {
            return rest.substring(1, rest.indexOf("\"", 1));
        }
        int end = rest.indexOf(",");
        return end == -1 ? rest.trim() : rest.substring(0, end).trim();
    }
}