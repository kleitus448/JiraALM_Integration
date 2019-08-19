package sample;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Controller {

    private ALM_Controller almController;
    private JIRA_Controller jiraController;

    //AUTHORIZATION ALM
    @FXML private TextField loginALM;
    @FXML private PasswordField passALM;
    @FXML private CheckBox rememberALM;

    //AUTHORIZATION JIRA
    @FXML private TextField loginJIRA;
    @FXML private PasswordField passJIRA;
    @FXML private CheckBox rememberJIRA;

    @FXML private TitledPane defectsJIRAtoALM;
        @FXML private TextField dJtA_filterDefectJIRA;
        @FXML public Button dJtA_logsButton;
        @FXML public Button dJtA_executeButton;
        @FXML public TextField dJtA_projectALM;
        @FXML public TextField dJtA_folderALM;
        @FXML public CheckBox dJtA_rememberProject;

    @FXML public void on_dJtA_executeButton() throws IOException {
        jiraController.setAuthData(loginJIRA.getText(), passJIRA.getText());
        almController.signIn(loginALM.getText(), passALM.getText());
        String parentID = almController.createFolder(dJtA_folderALM.getText(), "0");
        JSONArray jsonIssues = new JSONObject(jiraController.getIssuesJQL(dJtA_filterDefectJIRA.getText())).getJSONArray("issues");
        JSONObject jsonTests = new JSONObject();
        jsonTests.put("entities", new JSONArray());
        System.out.println(jsonTests.toString());
        for (int i = 0; i < jsonIssues.length(); i++) {
            String testName = jsonIssues.getJSONObject(i).getJSONObject("fields").get("summary").toString();
            String testDescription = jsonIssues.getJSONObject(i).getJSONObject("fields").get("description").toString();
            LinkedHashMap <String, Object> name = new LinkedHashMap<>();
            name.put("Name", "name");
            name.put("values", new JSONArray().put(new JSONObject().put("value", testName)));
            LinkedHashMap <String, Object> parent_id = new LinkedHashMap<>();
            parent_id.put("Name", "parent-id");
            parent_id.put("values", new JSONArray().put(new JSONObject().put("value", parentID)));
            LinkedHashMap <String, Object> description = new LinkedHashMap<>();
            description.put("Name", "description");
            description.put("values", new JSONArray().put(new JSONObject().put("value", testDescription)));
            LinkedHashMap <String, Object> subtype_id = new LinkedHashMap<>();
            subtype_id.put("Name", "subtype-id");
            subtype_id.put("values", new JSONArray().put(new JSONObject().put("value", "MANUAL")));
            JSONObject test = new JSONObject(Collections.singletonMap(
                    "Fields", new JSONArray()
                        .put(new JSONObject(name))
                        .put(new JSONObject(parent_id))
                        .put(new JSONObject(description))
                        .put(new JSONObject(subtype_id))));
            jsonTests.getJSONArray("entities").put(test);
        }
        System.out.println(jsonTests.toString());
        System.out.println("hi");
        almController.setProjectName(dJtA_projectALM.getText());
        almController.createTestS(jsonTests.toString(), dJtA_folderALM.getText());
    }

    @FXML void initialize() throws UnsupportedEncodingException {
        jiraController = new JIRA_Controller();
        almController = new ALM_Controller();
    }
}
