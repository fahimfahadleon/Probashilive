package com.probashiincltd.probashilive.utils;

public class Configurations {
    static final String hostName = "server.probashilive.xyz";
    static final int port = 5222;
    public static final String LOGIN_STATUS = "isLoggedIn";
    public static final String LOGIN_USER = "loginUser";
    public static final String LOGIN_PASSWORD = "loginPassword";
    public static final String LOGIN_TYPE = "loginType";
    public static final String LOGIN_TYPE_GOOGLE = "google";
    public static final String LOGIN_TYPE_FACEBOOK = "facebook";
    public static final String LOGIN_TYPE_RAW = "raw";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_PHONE = "userPhone";
    public static final String USER_PROFILE = "userProfile";
    public static final String SUBJECT_TYPE_COMMENT = "comment";
    public static final String SUBJECT_TYPE_VIEWERS_LIST = "viewers_list";
    public static final String CLOSE_LIVE = "close_live";
    public static final String OPEN_PROFILE = "open_profile";
    public static final String SUBJECT_TYPE_JOINED_LIVE= "joined_live";
    public static final String SUBJECT_TYPE_LIVE_ACTION= "live_action";
    public static final String LIVE_ACTION = "action_title";
    public static final String ACTION_TYPE_LIVE_ENDED = "live_ended";
    public static final String ACTION_TYPE_LIVE_LEFT = "left";
    public static final String RTMP_URL = "rtmp://server.probashilive.xyz/live/";
    public static final String INITIAL_COMMENT = "Joined the live";


    public static final String LIVE_USER_TYPE_HOST = "host";
    public static final String LIVE_USER_TYPE_AUDIENCE = "audience";
    public static final String LIVE_USER_TYPE_COMPETITOR = "competitor";





    public static final String ACTION = "action";
    public static final String LIVE_TYPE = "live_type";
    public static final String LIVE_TYPE_AUDIO = "audio_call";
    public static final String LIVE_TYPE_VIDEO = "video_call";

    public static String getHostName(){
        return hostName;
    }
    public static int getPort(){
        return port;
    }
}
