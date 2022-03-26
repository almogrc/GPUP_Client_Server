package component.dashboard;

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

public class CreditRefresher extends TimerTask {
    private final Consumer<String> creditConsumer;
    private final BooleanProperty shouldUpdate;


    public CreditRefresher(BooleanProperty shouldUpdate, Consumer<String> creditConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.creditConsumer = creditConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }


    }
}
