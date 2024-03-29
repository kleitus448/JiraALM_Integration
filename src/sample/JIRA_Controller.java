package sample;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class JIRA_Controller {
    private String hostName;
    private String login;
    private String password;

    public JIRA_Controller() {
        hostName = "https://jira.voskhod.ru";
    }

    public void setAuthData(String login, String password) {
        this.login = login;
        this.password = password;
        System.out.println(("Логин: " + login));
        System.out.println(("Пароль: " + password));
    }

    private HttpURLConnection createConnection(String reqMethod, String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(hostName+url).openConnection();
        connection.setRequestMethod(reqMethod);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Connection", "keep-alive");
        return connection;
    }

    public String getIssuesJQL(String filterJQL) throws IOException {
        HttpURLConnection connection = createConnection("POST", "/rest/api/2/search");
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((login+":"+password).getBytes()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write("{\n" +
                "    \"jql\": \""+ filterJQL.replace("\"","\\\"") +"\",\n" +
                "    \"fields\": [\n" +
                "        \"summary\",\n" +
                "        \"description\"\n" +
                "    ]\n" +
                "}");
        writer.close();
        System.out.println("{\n" +
                "    \"jql\": \"" + new String(filterJQL.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8) + "\",\n" +
                "    \"fields\": [\n" +
                "        \"summary\",\n" +
                "        \"description\"\n" +
                "    ]\n" +
                "}");
        HashMap<String, String> issues = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String temp = ""; StringBuilder response = new StringBuilder();
        while ((temp = reader.readLine()) != null) {
            response.append(temp);
        }
        reader.close();
        return response.toString();
    }

//    public static void main(String[] args) throws IOException {
//        SSLUtilities.trustAllHttpsCertificates();
//        JIRA_Controller jiraController = new JIRA_Controller();
//        jiraController.setAuthData("d.lulava", "PBL520");
//        System.out.println(jiraController.getIssuesJQL("project = EPP_EX_R19 AND affectedVersion = \"релиз 11\" AND reporter in (v.rusakova)"));
//    }
}
