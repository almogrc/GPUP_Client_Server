package util;

import com.google.gson.Gson;

public class Constants {
    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/main/chat-app-main.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/login/login.fxml";
    public final static String CHAT_ROOM_FXML_RESOURCE_LOCATION = "/chat/client/component/chatroom/chat-room-main.fxml";
    //"http://" + "localhost" + ":8080" + "/Web_Web_exploded" + "/userslist"
    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    public static final String LOGIN_PAGE = "/login";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/Web_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;
    //EX("http://localhost:8080/Web_Web_exploded/admin-login")
    ///public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";
    ///public final static String LOGOUT = FULL_SERVER_PATH + "/chat/logout";
    ///public final static String SEND_CHAT_LINE = FULL_SERVER_PATH + "/pages/chatroom/sendChat";
    ///public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";
    public final static String LOAD_XML= FULL_SERVER_PATH + "/load-XML";
    public final static String GRAPH_DATA = FULL_SERVER_PATH + "/graph-data";
    public final static String WHAT_IF = FULL_SERVER_PATH + "/what-if";
    public final static String GENERIC_DATA = FULL_SERVER_PATH + "/generic-data";
    public final static String FIND_CIRCLE =FULL_SERVER_PATH +"/fins-circle" ;
    public final static String GET_ALL_PATHS =FULL_SERVER_PATH+ "/get-all-paths" ;
    public static final String GET_USERS = FULL_SERVER_PATH+"/get-users";
    public static final String USER_LOGIN = FULL_SERVER_PATH+"/user-login";
    public static final String MONEY_MADE = FULL_SERVER_PATH+"/money-made";
    public static final String GET_EXECUTION_GRAPHS = FULL_SERVER_PATH +"/get-execution-graphs";
    public static final String UPDATE_SIGN = FULL_SERVER_PATH + "/update-sign";
    public static final String UPDATE_WORKER_BREAKS= FULL_SERVER_PATH + "/update-worker-breaks";
    public static final String GET_JOB= FULL_SERVER_PATH + "/get-job";
    public static final String UPDATE_TARGET_STATUS= FULL_SERVER_PATH + "/update-target-status";
    public static final String UPDATE_FINISH_STATUS= FULL_SERVER_PATH + "/update-finish-status";

    public static final String UPDATE_SKIPPED= FULL_SERVER_PATH + "/update-skipped";
    public static final String GET_TARGETS_DONE_BY_WORKER= FULL_SERVER_PATH+ "/get-targets-done-by-worker";
    public static final String ADD_LOG= FULL_SERVER_PATH+"/add-log";
    public static final String GET_OPEN_TARGETS=FULL_SERVER_PATH+"/get-open-targets";
    public static final String WRITE_TASK_TO_FILE=FULL_SERVER_PATH+"/write-task-to-file";
    public static final String GET_NUM_OF_THREADS=FULL_SERVER_PATH+"/get-num-of-threads";

    public static final String EXECUTIONS_TO_WORK="executions to work";
    public static final String MONEY = "money";
    public static final String NUM_OF_THREADS = "num_of_threads";
    public static final String USER_TYPE = "user_type";
    public static final String WORKER = "worker";
    public static final String USERNAME = "username";
    public static final String TARGET_FINISH_STATUS="target finish status";
    public static final String TASK_RUN_SUMMARY="task run summary";

    public static final String GRAPH_NAME = "graph_name";
    public static final String TARGET_STATUS = "target status";
    public static final String RATIO = "ratio";
    public static final String TARGET_NAME = "target_name";
    public static final String EXECUTION_NAME = "execution_name";
    public static final String LOG = "log";
    public static final String FIRST_TARGET = "first_target";
    public static final String SECOND_TARGET = "second_target";
    public static final String NO = "No";
    public static final String YES = "Yes";
    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
