package com.probashiincltd.probashilive.utils;

public class Configurations {
    //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
    static final String hostName = "server.probashilive.xyz";
    static final int port = 5222;
    public static final String LOGIN_STATUS = "isLoggedIn";
    public static final String LOGIN_USER = "loginUser";
    public static final String LOGIN_PASSWORD = "loginPassword";
    public static final String LOGIN_TYPE = "loginType";
    public static final String LOGIN_TYPE_GOOGLE = "google";
    public static final String LOGIN_TYPE_FACEBOOK = "facebook";
    public static final String CONTENT = "content";
    public static final String LOGIN_TYPE_RAW = "raw";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_PHONE = "userPhone";
    public static final String USER_PROFILE = "userProfile";
    public static final String SUBJECT_TYPE_COMMENT = "comment";
    public static final String SUBJECT_TYPE_VIDEO_INVITATION = "video_invitation";
    public static final String SUBJECT_TYPE_VIDEO_INVITATION_ACCEPTED = "video_invitation_accepted";
    public static final String SUBJECT_TYPE_COMPETITOR_LIST = "competitor_list";
    public static final String SUBJECT_TYPE_VIDEO_INVITATION_DECLINED = "video_invitation_declined";
    public static final String SUBJECT_TYPE_VIDEO_STREAM_JOINED = "video_stream_joined";
    public static final String UPDATE_TYPE_COMPETITOR = "update_competitor";
    public static final String SUBJECT_TYPE_VIEWERS_LIST = "viewers_list";
    public static final String CLOSE_LIVE = "close_live";
    public static boolean isOccupied = false;
    public static final String OPEN_PROFILE = "open_profile";
    public static final String GIFT = "gift";
    public static final String HIDE_COMMENT = "hide_comment";
    public static final String ADD_PERSON = "add_person";
    public static final String OPEN_PROFILE_1 = "open_profile_1";
    public static final String OPEN_PROFILE_2 = "open_profile_2";
    public static final String END_CALL_1 = "end_call_1";
    public static final String END_CALL_2 = "end_call_2";
    public static final String SWITCH_CAMERA = "switch_camera";
    public static final String JOIN_REQUEST = "join_request";

    public static final String SUBJECT_TYPE_JOINED_LIVE= "joined_live";
    public static final String SUBJECT_TYPE_LIVE_ACTION= "live_action";
    public static final String SUBJECT_TYPE_COMPETITOR_LEFT= "competitor_left";
    public static final String LIVE_ACTION = "action_title";
    public static final String ACTION_TYPE_LIVE_ENDED = "live_ended";
    public static final String ACTION_TYPE_LIVE_LEFT = "left";
    public static final String DATA = "data";
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
