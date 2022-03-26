package component.chooseGraph;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.Constants.GSON_INSTANCE;

public class ChooseGraphRefresher extends TimerTask {
    private final Consumer<List<String>> xmlList;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;

    public ChooseGraphRefresher(BooleanProperty shouldUpdate, Consumer<List<String>> xmlList) {
        this.shouldUpdate = shouldUpdate;
        this.xmlList = xmlList;
        requestNumber = 0;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        HttpClientUtil.runAsync(Constants.GET_GRAPHS_NAMES, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR,  e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersNames = response.body().string();
                String[] usersNames = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, String[].class);
                if(usersNames!=null)
                xmlList.accept(Arrays.asList(usersNames));
            }
        });
    }

}
