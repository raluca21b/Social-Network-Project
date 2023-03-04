package com.example.social_network1.controllers;

import com.example.social_network1.HelloApplication;
import com.example.social_network1.domain.FriendRequest;
import com.example.social_network1.domain.Friendship;
import com.example.social_network1.domain.Message;
import com.example.social_network1.domain.User;
import com.example.social_network1.repository.db.FriendRequestDBRepo;
import com.example.social_network1.repository.db.FriendshipDBRepo;
import com.example.social_network1.repository.db.MessageDBRepo;
import com.example.social_network1.repository.db.UserDBRepo;
import com.example.social_network1.service.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelloController {

    Service service = Service.getInstance(new FriendshipDBRepo(), new UserDBRepo(), new FriendRequestDBRepo(), new MessageDBRepo());
    private final ObservableList<User> userModel = FXCollections.observableArrayList();
    private final ObservableList<User> userModelForList = FXCollections.observableArrayList();
    private final ObservableList<FriendRequest> requestModel = FXCollections.observableArrayList();

    private final ObservableList<Message> messageModel = FXCollections.observableArrayList();


    @FXML
    private TableColumn<FriendRequest, String> usernameTF;
    @FXML
    private ListView<User> listUsers;
    @FXML
    private TableColumn<FriendRequest, LocalDateTime> requestTime;
    @FXML
    private TableColumn<FriendRequest, String> status;
    @FXML
    private TableView<FriendRequest> tableRequest;
    @FXML
    private TableColumn<FriendRequest, Long> idRequest;

    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, Long> idColumn;
    @FXML
    private TableColumn<User, String> fnameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, Integer> ageColumn;

    @FXML
    private Pane paneMsg;
    @FXML
    private TextField msgTxtField;
    @FXML
    private ListView<Message> conversationList;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        idRequest.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameTF.setCellValueFactory(new PropertyValueFactory<>("username1"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        requestTime.setCellValueFactory(new PropertyValueFactory<>("requestSentAt"));
        tableRequest.setItems(requestModel);
        tableView.setItems(userModel);
        listUsers.setItems(userModelForList);
        initModelUser();
        initModelRequest();
        initModelUserList();

    }

    private void initModelUser() {
        Long id = service.getUser().getId();
        List<User> users1 = new ArrayList<>();
        for (Friendship fr : service.getFriendships()) {
            if (Objects.equals(fr.getIdUser1(), id)) {
                users1.add(service.findOneUser(fr.getIdUser2()));
            } else if (Objects.equals(fr.getIdUser2(), id)) {
                users1.add(service.findOneUser(fr.getIdUser1()));
            }

        }
        userModel.setAll(users1);
    }

    private void initModelUserList() {
        User u = service.getUser();
        List<User> users = (List<User>) service.getUsers();
        users.remove(u);
        for (Friendship friendship:service.getFriendships())
        {
            if (Objects.equals(friendship.getIdUser1(), u.getId())){
                users.remove(service.findOneUser(friendship.getIdUser2()));
            }

            else if (Objects.equals(friendship.getIdUser2(), u.getId())) {
                users.remove(service.findOneUser(friendship.getIdUser1()));
            }
        }

        userModelForList.setAll(users);
    }

    private void initModelRequest() {
        String username = service.getUser().getEmail();
        List<FriendRequest> requests = new ArrayList<>();
        for (FriendRequest friendRequest : service.getAllRequests()) {
            if (friendRequest.getUsername2().equals(username) && !friendRequest.getStatus().equals("Accepted")) {
                requests.add(friendRequest);
            }
        }
        requestModel.setAll(requests);
    }

    @FXML
    public void onRemoveFriend(MouseEvent mouseEvent) {
        Long idU = tableView.getSelectionModel().getSelectedItem().getId();
        String email = tableView.getSelectionModel().getSelectedItem().getEmail();
        for (FriendRequest request : service.getAllRequests()) {
            if ((request.getUsername2().equals(email) && service.getUser().getEmail().equals(request.getUsername1()) && request.getStatus().equals("Accepted"))
                    || (request.getUsername1().equals(email) && service.getUser().getEmail().equals(request.getUsername2())) && request.getStatus().equals("Accepted")) {
                service.deleteRequest(request.getId());
            }
        }
        for (Friendship f : service.getFriendships()) {
            if (Objects.equals(f.getIdUser1(), idU) || Objects.equals(f.getIdUser2(), idU)) {
                service.deleteFriendship(f.getId());
            }
        }
        for (Message msg: service.getMessages()){
            if ((msg.getUsernameFROM().equals(email) && service.getUser().getEmail().equals(msg.getUsernameTO()))
                    || (msg.getUsernameTO().equals(email) && service.getUser().getEmail().equals(msg.getUsernameFROM()))
            )
            {
                service.deleteMessage(msg.getId());
                paneMsg.setVisible(false);
            }
        }
        initModelUser();
        initModelRequest();
        initModelUserList();
    }

    @FXML
    public void onSendRequest(ActionEvent actionEvent) {
        String email = listUsers.getSelectionModel().getSelectedItem().getEmail();
        if (email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("INVALID DATA");
            alert.setContentText("Field cannot be empty!");
            alert.show();
        }
        int ok = 0;
        int ok1 = 1;
        if (!email.isEmpty()) {
            for (User u : service.getUsers()) {
                if (Objects.equals(u.getEmail(), email)) {
                    ok = 1;
                    break;
                }
            }
            for (FriendRequest request : service.getAllRequests())
                if ((request.getUsername1().equals(email) && request.getUsername2().equals(service.getUser().getEmail()) && !request.getStatus().equals("Rejected"))
                        || (request.getUsername2().equals(email) && request.getUsername1().equals(service.getUser().getEmail())) && !request.getStatus().equals("Rejected")) {
                    ok1 = 0;
                    break;
                }

            if (ok == 1 && ok1 == 1) {
                service.addRequest(service.getUser().getEmail(), email);
                initModelRequest();
                onSentButtonClick(actionEvent);
            } else if (ok == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("");
                alert.setHeaderText("INVALID DATA");
                alert.setContentText("Invalid User!");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("");
                alert.setHeaderText("INVALID DATA");
                alert.setContentText("Request already exists.You cannot send it again!");
                alert.show();
            }
        }
    }


    @FXML
    public void onAcceptButton(ActionEvent actionEvent) {
        FriendRequest friendRequest = tableRequest.getSelectionModel().getSelectedItem();
        String username = tableRequest.getSelectionModel().getSelectedItem().getUsername2();
        if (!username.equals(service.getUser().getEmail())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("ERROR");
            alert.setContentText("You can accept just the requests you received!");
            alert.show();
        } else {
            friendRequest.setStatus("Accepted");
            service.updateRequest(friendRequest);
            Long id = 0L;
            for (User u : service.getUsers())
                if (u.getEmail().equals(tableRequest.getSelectionModel().getSelectedItem().getUsername1()))
                    id = u.getId();

            service.addFriendship(new Friendship(service.getUser().getId(), id, LocalDateTime.now()));
            initModelRequest();
            initModelUser();
        }
        initModelUserList();

    }

    @FXML
    public void onRejectButton(ActionEvent actionEvent) {
        FriendRequest friendRequest = tableRequest.getSelectionModel().getSelectedItem();
        String username = tableRequest.getSelectionModel().getSelectedItem().getUsername2();
        if (!username.equals(service.getUser().getEmail())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("ERROR");
            alert.setContentText("You can reject just the requests you received!");
            alert.show();
        } else {
            friendRequest.setStatus("Rejected");
            service.updateRequest(friendRequest);
            initModelRequest();
        }
        initModelUserList();
    }

    @FXML
    public void onMouseClickedLogOut(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start-app.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Raluca's Social Network");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void onSentButtonClick(ActionEvent actionEvent) {
        String username = service.getUser().getEmail();
        List<FriendRequest> requests = new ArrayList<>();
        for (FriendRequest friendRequest : service.getAllRequests()) {
            if (friendRequest.getUsername1().equals(username) && !friendRequest.getStatus().equals("Accepted")) {
                requests.add(friendRequest);
            }
        }
        requestModel.setAll(requests);
        usernameTF.setCellValueFactory(new PropertyValueFactory<>("username2"));
        tableRequest.setItems(requestModel);
    }

    @FXML
    public void onReceivedButtonClick(ActionEvent actionEvent) {
        initialize();
    }

    @FXML
    public void onDeleteButtonClick(ActionEvent actionEvent) {
        Long friendRequestId = tableRequest.getSelectionModel().getSelectedItem().getId();
        String username = tableRequest.getSelectionModel().getSelectedItem().getUsername1();
        if (!username.equals(service.getUser().getEmail())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("ERROR");
            alert.setContentText("You can remove just the requests sent by you!\n Try reject!");
            alert.show();
        } else {
            service.deleteRequest(friendRequestId);
            initModelRequest();
            onSentButtonClick(actionEvent);
        }

    }

    @FXML
    public void onSendMessage(MouseEvent mouseEvent) {
        String emailTO = tableView.getSelectionModel().getSelectedItem().getEmail();
        String emailFrom = service.getUser().getEmail();
        if (emailTO != null) {
            paneMsg.setVisible(true);
            List<Message> messages = new ArrayList<>();
            for (Message msg : service.getMessages()) {
                if ((msg.getUsernameFROM().equals(emailFrom) && msg.getUsernameTO().equals(emailTO)) ||
                        (msg.getUsernameTO().equals(emailFrom) && msg.getUsernameFROM().equals(emailTO)))
                    messages.add(msg);
            }
            messageModel.setAll(messages);
            conversationList.setItems(messageModel);
        }
    }

    @FXML
    public void onSetMessagePaneInvisible(MouseEvent mouseEvent) {
        paneMsg.setVisible(false);
    }

    @FXML
    public void onSendMessageButton(ActionEvent actionEvent) {
        String emailTO = tableView.getSelectionModel().getSelectedItem().getEmail();
        String emailFrom = service.getUser().getEmail();
        String msgTxt = msgTxtField.getText();
        service.addMessage(emailFrom, emailTO, msgTxt);
        List<Message> messages = new ArrayList<>();
        for (Message msg : service.getMessages()) {
            if ((msg.getUsernameFROM().equals(emailFrom) && msg.getUsernameTO().equals(emailTO)) ||
                    (msg.getUsernameTO().equals(emailFrom) && msg.getUsernameFROM().equals(emailTO)))
                messages.add(msg);
        }
        messageModel.setAll(messages);
        msgTxtField.setText("");
    }

    @FXML
    public void onCloseChat(MouseEvent mouseEvent) {
        paneMsg.setVisible(false);
    }
}