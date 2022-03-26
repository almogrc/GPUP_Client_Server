package component.userList;

import component.api.HttpStatusUpdate;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.Closeable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.REFRESH_RATE;

public class UserListController implements Closeable {
    private Timer timer;
    private TimerTask listRefresher;
    private final IntegerProperty totalUsers;
    private HttpStatusUpdate httpStatusUpdate;
    private final BooleanProperty autoUpdate;

@FXML
private ListView<String> usersListView;

@FXML
private Label usersLabel;

    public UserListController() {
        autoUpdate = new SimpleBooleanProperty(true);
        totalUsers = new SimpleIntegerProperty(0);
    }

    @FXML public void initialize() {
        usersLabel.textProperty().bind(Bindings.concat("Chat Users: (", totalUsers, ")"));
    }

    public void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate) {
        this.httpStatusUpdate = httpStatusUpdate;
    }

    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    private void updateUsersList(List<String> usersNames) {
        Platform.runLater(() -> {
            ObservableList<String> items = usersListView.getItems();
            items.clear();
            items.addAll(usersNames);
            totalUsers.set(usersNames.size());
        });
    }

    public void startListRefresher() {
        listRefresher = new UserListRefresher(
                autoUpdate,
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    @Override
    public void close() {
        usersListView.getItems().clear();
        totalUsers.set(0);
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
    }
}
