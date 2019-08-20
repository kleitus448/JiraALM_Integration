package sample;

import org.json.JSONArray;
import org.json.JSONObject;
import sun.nio.cs.UnicodeEncoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;

public class ALM_Controller {
    private StringBuilder cookies;
    private String projectName;
    private String hostName;

    public ALM_Controller() throws UnsupportedEncodingException {
        this.cookies = new StringBuilder();
        this.hostName = "http://10.215.0.118:8080";
    }

    public void setProjectName(String projectName) throws UnsupportedEncodingException {
        this.projectName = URLEncoder.encode(projectName, "utf-8");
    }

    private HttpURLConnection createConnection(String reqMethod, String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(hostName+url).openConnection();
        connection.setRequestMethod(reqMethod);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        return connection;
    }

    public void signIn(String login, String password) throws IOException {
        System.out.println("signIn_Request:");
        HttpURLConnection connection = createConnection("POST", "/qcbin/api/authentication/sign-in");
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((login+":"+password).getBytes()));
        System.out.println(connection.getResponseCode());
        System.out.println(connection.getResponseMessage());
        for (String cookie : connection.getHeaderFields().get("Set-Cookie")){
            System.out.println(cookie);
            cookies.append(cookie.split(";")[0]).append("; ");
        }
        cookies.delete(cookies.length()-2, cookies.length()-1);
        System.out.println("Cookies: " + cookies.toString() +"\n");
    }

    @Deprecated
    public void createTest(String test, String parentID) throws IOException {
        System.out.println("createTest_Request:");
        HttpURLConnection connection = createConnection("POST", "/rest/domains/DEFAULT/projects/"+projectName+"/tests");
        connection.setRequestProperty("Cookie", cookies.toString());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write("{\n" +
                "    \"Fields\": [\n" +
                "        {\n" +
                "            \"Name\": \"name\",\n" +
                "            \"values\": [\n" +
                "                {\n" +
                "                    \"value\": \"" +test+ "\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"Name\": \"parent-id\",\n" +
                "            \"values\": [\n" +
                "                {\n" +
                "                    \"value\": \""+parentID+"\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"Name\": \"subtype-id\",\n" +
                "            \"values\": [\n" +
                "                {\n" +
                "                    \"value\": \"MANUAL\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "       \n" +
                "    ]\n" +
                "}");
        writer.close();
        System.out.println(connection.getResponseCode());
        System.out.println(connection.getResponseMessage()+"\n");
    }

    public void createTestS(String json, String folderName) throws IOException {
        System.out.println("createTestS_Request:");
        HttpURLConnection connection = createConnection("POST", "/qcbin/rest/domains/DEFAULT/projects/"+projectName+"/tests");
        connection.setRequestProperty("Content-Type", "application/json;type=collection");
        connection.setRequestProperty("Cookie", cookies.toString());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(json);
        writer.close();
        System.out.println(connection.getResponseCode());
        System.out.println(connection.getResponseMessage()+"\n");
    }

    public String createFolder(String folder, String parentID) throws IOException {
        HttpURLConnection connection = createConnection("POST", "/qcbin/rest/domains/DEFAULT/projects/"+projectName+"/test-folders");
        connection.setRequestProperty("Cookie", cookies.toString());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        LinkedHashMap<String, Object> name = new LinkedHashMap<>();
        name.put("Name", "name");
        name.put("values", new JSONArray().put(new JSONObject().put("value", folder)));
        LinkedHashMap <String, Object> parent_id = new LinkedHashMap<>();
        parent_id.put("Name", "parent-id");
        parent_id.put("values", new JSONArray().put(new JSONObject().put("value", parentID)));
        JSONObject folderJSON = new JSONObject(Collections.singletonMap(
                "Fields", new JSONArray()
                        .put(new JSONObject(name))
                        .put(new JSONObject(parent_id))));
        writer.write(folderJSON.toString());
        writer.close();
        System.out.println(folderJSON.toString());
        System.out.println(connection.getResponseMessage());
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        String temp = ""; StringBuilder response = new StringBuilder();
        while ((temp = reader.readLine()) != null) {
            response.append(temp);
        }
        System.out.println(response);
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        temp = ""; response = new StringBuilder();
        while ((temp = reader.readLine()) != null) {
            response.append(temp);
        }
        JSONArray fieldsJSON = new JSONObject(response.toString()).getJSONArray("Fields");
        for (int i = 0; i < fieldsJSON.length(); i++) {
            JSONObject field = fieldsJSON.getJSONObject(i);
            if (field.get("Name").toString().equals("id"))
                return field.getJSONArray("values").getJSONObject(0).get("value").toString();
        }
        return "2";
    }

//    private void findProjectID() throws IOException {
//        HttpURLConnection connection = createConnection("GET", "/qcbin/rest/domains/DEFAULT/projects/"+projectName+"/test-folders");
//        connection.setRequestProperty("Cookie", cookies.toString());
//        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String temp = ""; StringBuilder response = new StringBuilder();
//        while ((temp = reader.readLine()) != null) {
//            response.append(temp);
//        }
//        JSONArray folders = new JSONObject(response.toString()).getJSONArray("entities");
//        for (int i = 0; i < folders.length(); i++) {
//            folders.getJSONObject(i).getJSONArray("Fields").getJSONObject()
//        }
//        reader.close();
//    }
}
