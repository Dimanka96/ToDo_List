package Controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {
    public Label lblWelcomeNote;
    public Label lblId;
    public Pane pane;
    public TextField txtName;
    public Label lblTaskName;
    public Button btnAddToList;
    public AnchorPane root;
    public ListView<ToDoTM> lstToDoList;
    public Button btnUpdate;
    public Button btnDelete;
    public TextField txtUpdateToDo;

    public String id;
    public Label lblCheckText;

    public void initialize(){

        lblId.setText(LoginPageFormController.enteredUserId);
        lblWelcomeNote.setText("Hi "+ LoginPageFormController.enteredUserName + " Welcome to ToDo List");

        pane.setVisible(false);
        loadList();
        txtUpdateToDo.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        lblCheckText.setVisible(false);

        lstToDoList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                txtUpdateToDo.setDisable(false);
                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);
                txtUpdateToDo.requestFocus();
                pane.setVisible(false);

                if(newValue == null){
                    return;
                }

                ToDoTM selectedItem = lstToDoList.getSelectionModel().getSelectedItem();
                String description = selectedItem.getDescription();
                txtUpdateToDo.setText(description);

                id = selectedItem.getId();

            }
        });

    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        pane.setVisible(true);
        txtName.requestFocus();

        txtUpdateToDo.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want To Log Out", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){

            try {
                Parent parent = FXMLLoader.load(this.getClass().getResource("../View/LoginPageForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login Form");
                primaryStage.centerOnScreen();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{

        }
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        if (txtName.getText().trim().isEmpty()){
            lblCheckText.setVisible(true);
            txtName.requestFocus();
        }else {
            lblCheckText.setVisible(false);
            String id = autoGenerateID();
            String description = txtName.getText();
            String user_id = lblId.getText();

            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values (?,?,?)");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,description);
                preparedStatement.setObject(3,user_id);

                int i = preparedStatement.executeUpdate();
                pane.setVisible(false);


            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadList();
            txtName.clear();
        }
    }
    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        String newID = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");
            boolean isExit = resultSet.next();
            if(isExit){
                String oldID = resultSet.getString(1);
                oldID = oldID.substring(1, oldID.length());
                int intId = Integer.parseInt(oldID);
                intId++;

                if(intId < 10){
                    newID = "T00"+ intId;
                }else if(intId < 100){
                    newID = "T0"+ intId;
                }else{
                    newID = "T" + intId;
                }

            }else{
                newID = "T001";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }

    public void loadList(){
        ObservableList<ToDoTM> ToDo = lstToDoList.getItems();
        ToDo.clear();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id =? ");
            preparedStatement.setObject(1,LoginPageFormController.enteredUserId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                ToDoTM toDoTM = new ToDoTM(id,description,user_id);
                ToDo.add(toDoTM);
            }
            lstToDoList.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtUpdateToDo.getText();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);
            preparedStatement.executeUpdate();
            loadList();
            txtUpdateToDo.clear();
            txtUpdateToDo.setDisable(true);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        String description = txtUpdateToDo.getText();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want To Delete ToDo...?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id =?");
                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();
                loadList();

                txtUpdateToDo.clear();
                txtUpdateToDo.setDisable(true);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}

