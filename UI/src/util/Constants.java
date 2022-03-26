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
    //"http://localhost:8080/Web_Web_exploded/user-login"
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
    public final static String GENERIC_DATA = FULL_SERVER_PATH + "/live-data";
    public final static String FIND_CIRCLE =FULL_SERVER_PATH +"/find-circle" ;
    public final static String GET_ALL_PATHS =FULL_SERVER_PATH+ "/get-all-paths" ;
    public static final String GET_USERS = FULL_SERVER_PATH+"/get-users";
    public static final String USER_LOGIN = FULL_SERVER_PATH+"/user-login";
    public static final String GET_GRAPHS_NAMES = FULL_SERVER_PATH +"/get-graphs-names";
    public static final String GET_EXECUTION_GRAPHS = FULL_SERVER_PATH +"/get-execution-graphs";
    public static final String EXE_DATA = FULL_SERVER_PATH +"/exe-data";
    public static final String PROGRESS_BAR = FULL_SERVER_PATH +"/get-progressbar";
    public static final String FINAL_RESULT = FULL_SERVER_PATH + "/get-final-data";
    public static final String UPLOAD_NEW_TASK = FULL_SERVER_PATH + "/upload-new-task";
    public static final String LAYOUT_TASK = FULL_SERVER_PATH + "/layout-task";
    public static final String UPLOAD_NEW_TASK_FROM_OLD = FULL_SERVER_PATH + "/upload-new-task-from-old";
    public static final String RUN_EXECUTION = FULL_SERVER_PATH + "/run-execution";
    public static final String UPDATE_TASK_STATUS = FULL_SERVER_PATH + "/update-task-status";
    public static final String GET_LOG = FULL_SERVER_PATH + "/get-log";

    public static final String NEW_RUN = "New Run";
    public static final String ACTIVATED = "Activated";
    public static final String PAUSED  = "Paused";
    public static final String STOPPED = "Stopped";
    public static final String OVER = "Over";

    public static final String TYPE_OF_ACTION = "type of action";
    public static final String RUN = "Run";
    public static final String STOP = "Stop";
    public static final String PAUSE  = "Pause";
    public static final String RESUME = "Resume";

    public static final String USERNAME = "username";
    public static final String NEW_TASK = "new_Task";
    public static final String OLD_TASK= "old_Task";
    public static final String RUN_WAY="run_Way";
    public static final String FROM_SCRATCH="From Scratch";
    public static final String INCREMENTAL="Incremental";

    public final static int NOT_EXIST = 0;

    public static final String TASK_STATUS = "task_status";
    public static final String EXECUTION_NAME = "execution_name";
    public static final String INDEX = "index";
    public static final String MONEY = "money";
    public static final String NUM_OF_THREADS = "num_of_threads";
    public static final String USER_TYPE = "user_type";
    public static final String ADMIN = "admin";
    public static final String NOT_FINISHED="NOTFINISHED";
    public static final String SUCCESS_WITH_WARNING ="SUCCESS_WITH_WARNINGS";
    public static final String  FAILURE="FAILURE";
    public static final String  SUCCESS="SUCCESS";
    public static final String  IN_PROCESS="INPROCESS";
    public static final String  FINISHED="FINISHED";
    public static final String  WAITING="WAITING";
    public static final String  SKIPPED="SKIPPED";
    public static final String  FROZEN="FROZEN";
    public static final String GRAPH_NAME = "graph_name";
    public static final String TARGET_STATUS = "target_Status";
    public static final String RATIO = "ratio";
    public static final String TARGET_NAME = "target_name";
    public static final String FIRST_TARGET = "first_target";
    public static final String SECOND_TARGET = "second_target";
    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
