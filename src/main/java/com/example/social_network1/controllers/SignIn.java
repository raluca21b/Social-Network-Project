package com.example.social_network1.controllers;

import com.example.social_network1.HelloApplication;
import com.example.social_network1.domain.User;
import com.example.social_network1.repository.db.FriendRequestDBRepo;
import com.example.social_network1.repository.db.FriendshipDBRepo;
import com.example.social_network1.repository.db.MessageDBRepo;
import com.example.social_network1.repository.db.UserDBRepo;
import com.example.social_network1.service.Service;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignIn {
    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
    @FXML
    private PasswordField psswdConfrimTF;
    Service service = Service.getInstance(new FriendshipDBRepo(), new UserDBRepo(), new FriendRequestDBRepo(),new MessageDBRepo());
    @FXML
    private TextField userTF;
    @FXML
    private PasswordField psswdTF;
    @FXML
    private TextField nameTF;
    @FXML
    private TextField ageTF;

    @FXML
    public void onAddNewUser(MouseEvent mouseEvent) throws IOException {
        String fname = nameTF.getText();
        String passwd = psswdTF.getText();
        String passwdConf = psswdConfrimTF.getText();
        String email = userTF.getText();
        int age = 0;
        try{
            age = Integer.parseInt(ageTF.getText());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        if (Objects.equals(age,0))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("INVALID DATA");
            alert.setContentText("Age must be a number!");
            alert.show();
            ageTF.setText("");
        } else if (fname.isEmpty() || passwd.isEmpty() || email.isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("INVALID DATA");
            alert.setContentText("You must complete all fields!");
            alert.show();
            nameTF.setText("");
            psswdTF.setText("");
            psswdConfrimTF.setText("");
            userTF.setText("");
            ageTF.setText("");
        }else if(! patternMatches(email,"^(.+)@(\\S+)$")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("INVALID DATA");
            alert.setContentText("Invalid email address!");
            alert.show();
            nameTF.setText("");
            psswdTF.setText("");
            psswdConfrimTF.setText("");
            userTF.setText("");
            ageTF.setText("");
        } else if (!passwd.equals(passwdConf)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("INVALID DATA");
            alert.setContentText("Your password doesn't match!");
            alert.show();
            nameTF.setText("");
            psswdTF.setText("");
            psswdConfrimTF.setText("");
            userTF.setText("");
            ageTF.setText("");
        } else {
            int ok = 1;
            for (User user: service.getUsers())
                if (user.getEmail().equals(email)){
                    ok = 0;
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("");
                    alert.setHeaderText("INVALID DATA");
                    alert.setContentText("You already have an account, please log in");
                    alert.show();
                    nameTF.setText("");
                    psswdTF.setText("");
                    psswdConfrimTF.setText("");
                    userTF.setText("");
                    ageTF.setText("");
                }
            if (ok == 1){
                User u = new User(fname, passwd, email, age);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("Action completed successfully");
                alert.setContentText("You have created your new accont!");
                alert.show();
                service.setUser(u);
                service.addUser(u);
                nameTF.setText("");
                psswdTF.setText("");
                psswdConfrimTF.setText("");
                userTF.setText("");
                ageTF.setText("");
                Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start-app.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                stage.setScene(scene);
                stage.show();
            }

        }


    }


}
