package com.familheey.app.Utilities;

import com.familheey.app.BuildConfig;

public class Constants {


    public interface Paths {
        String DOCUMENTS = "Documents/";
        String COVER_PIC = "cover_pic/";
        String LOGO = "logo/";
        String PROFILE_PIC = "propic/";
        String EVENT_IMAGE = "event_image/";
        String AGENDA_PIC = "agenda_pic/";
    }

    public interface Fonts {
        String PATH = "font/";
        String REGULAR = "Regular.ttf";
        String MEDIUM = "Medium.ttf";
        String LIGHT = "Light.ttf";
        String SEMIBOLD = "SemiBold.ttf";
        String BOLD = "Bold.ttf";
        String THIN = "Thin.ttf";
    }

    public interface Json {
        String COUNTRY_CODE = "country_json.json";
    }

    public interface Bundle {
        String MEMBER = "member";
        String FAMILIES = "families";
        String DATA = "data";
        String ADDITIONAL_DATA = "AdditionalData";
        String TITLE = "title";
        String DESCRIPTION = "description";
        String ID = "id";
        String IS_ADMIN = "Admin";
        String CAN_CREATE = "CanCreate";
        String CAN_UPDATE = "CanUpdate";
        String FAMILY = "Family";
        String FOR_EDITING = "Edit";
        String ADDRESS = "Address";
        String IDENTIFIER = "identifier";
        String TYPE = "Type";
        String SUB_TYPE = "SubType";
        String FOLDER_ID = "FolderId";
        String RELATIONSHIP = "Relationship";
        String PLACE = "Place";
        String IS_UPDATE_MODE = "UpdateMode";

        String IS_ANONYMOUS = "Anonymous";
        String FAMILY_ID = "FamilyId";
        String IS_INVITATION = "isInvitation";
        String POSITION = "Position";
        String EVENT_ID = "EventId";
        String IS_EDIT = "IsEdit";
        String PROFILE = "Profile";
        String PUSH = "PUSH";
        String NOTIFICATION = "notification";
        String GUEST_INTERESTED = "guest_interested";
        String GUEST_ATTENDING = "guest_attending";
        String GUEST_MAY_ATTEND = "guest_may_attend";
        String GUEST_NOT_ATTENDING = "guest_not_attending";
        String REQUEST = "Request";
        String IS_ALBUM = "IsAlbum";
        String PERMISSION = "Permission";
        String DETAIL = "Detail";
        String PERMISSION_GRANTED_USERS = "PermissionGrantedUsers";
        String IS_GLOBAL_SEARCH_ENABLED = "IsGlobalSearchEnabled";
        String GLOBAL_SEARCH = "GlobalSearch";
        String LINK_FAMILY_REQUEST = "LinkFamilyRequest";
        String LINKED_FAMILIES = "LinkedFamilies";
        String IS_FAMILY_SETTINGS_NEEDED = "IsFamilySettingsNeeded";
        String IS_LOGGED_IN_NOW = "IsLoggedInNow";
        String IS_CREATED_NOW = "IsCreatedNow";
        String VIEWING_ONLY = "ViewingOnly";
        String NOTIFICATION_ID = "notification_id";
        String IS_SUB_FOLDER = "SUB_FOLDER";
        String TO_CREATE_FAMILY="ToCreateFamily";
        String JOIN_FAMILY_ID="JoinFamilyId";
        String IS_JOIN_FAMILY="IsJoinFamily";
        String IS_EXISTING_USER="IsExistingUser";
        String IS_DYNAMIC="IsDynamic";
        String INVITE = "Invite";
    }

    public interface RequestCode {
        int REQUEST_CODE = 680;
    }

    public interface TimeOut {
        int IMAGE_UPLOAD_CONNECTION_TIMEOUT = 120;
        int IMAGE_UPLOAD_SOCKET_TIMEOUT = 120;
        int SOCKET_TIME_OUT = 60;
        int CONNECTION_TIME_OUT = 60;
    }

    public interface ApiFlags {
        int VALIDATE_MOBILE_NUMBER = 0;
        int CONFIRM_OTP = 1;
        int RESEND_OTP = 2;
        int COMPLETE_USER_REGISTRATION = 3;
        int CREATE_FAMILY = 7;
        int UPDATE_FAMILY = 8;
        int FETCH_FAMILY_FOR_LINKING = 9;
        int FETCH_SIMILAR_FAMILIES = 10;
        int JOIN_FAMILY = 11;
        int REQUEST_FAMILY_LINKING = 12;
        int LIST_FAMILY = 13;
        int GET_FAMILY_DETAILS = 15;
        int SEARCH_DATA = 14;
        int ADD_TO_FAMILY = 16;
        int VIEW_FAMILY_MEMBERS = 17;
        int VIEW_MEMBER_REQUEST = 18;
        int MEMBER_REQUEST_ACTION = 19;
        int GET_ALL_RELATIONS = 20;
        int ADD_RELATION = 21;
        int UPDATE_RELATIONS = 22;
        int UPDATE_USER_RESTRICTIONS = 23;
        int LIST_MEMBER = 24;
        int LIST_GROUP_FOLDERS = 25;
        int CREATE_FOLDERS = 26;
        int UPLOAD_IMAGE = 27;
        int VIEW_CONTENTS = 28;
        int FOLLOW = 27;
        int UNFOLLOW = 28;
        int VIEW_USER_PROFILE = 29;
        int EVENT_CATEGORY = 31;
        int REQUEST_LINK_FAMILY = 32;
        int CREATE_EVENTS = 33;
        int UPDATE_USER_PROFILE = 34;
        int FETCH_USER_INVITATION = 35;
        int RESPOND_FAMILY_INVITATION = 36;
        int INVITE_VIA_SMS = 37;
        int EXPLORE_EVENTS = 38;
        int CREATE_AGENDA = 39;
        int LIST_AGENDA = 40;
        int UPDATE_AGENDA = 41;
        int CREATED_BY_ME = 42;
        int GET_EVENT_BY_ID = 43;
        int EVENT_INVITATION = 44;
        int RESPOND_TO_RSVP = 44;
        int FETCH_GROUPS_BASED_ON_USER_ID = 45;
        int GET_EVENT_ITEMS = 46;
        int ADD_EVENT_SIGNUP = 47;
        int UPDATE_EVENT_SIGNUP = 48;
        int ADD_HISTORY = 49;
        int FETCH_GROUP_EVENTS = 50;
        int LIST_EVENT_ALBUMS = 51;
        int MAKE_PIC_COVER = 52;
        int DELETE_FILE = 53;
        int GUEST_COUNT = 54;
        int FETCH_LINKED_FAMILIES = 56;
        int FETCH_CALENDAR = 57;
        int FETCH_BLOCKED_USERS = 58;
        int EVENT_GROUP_INVITE = 59;
        int LEAVE_FAMILY = 60;
        int LIST_USER_FOLDERS = 61;
        int REQUEST_UNLINK_FAMILY = 62;
        int VIEW_PEOPLE_FOR_SHARE = 63;
        int ADD_EVENTS_SIGNUP = 64;
        int UPDATE_EVENTS_SIGNUP = 65;
        int GET_EVENT_SIGNUP_CONTRIBUTORS = 66;
        int ADD_FEEDBACK = 67;
        int CHECK_LINK = 68;
        int GET_MUTUAL_CONNECTIONS = 69;
        int GET_OTHER_USERS_FAMILY = 70;
        int UPDATE_EVENT = 71;
        int UPDATE_FAMILY_HISTORY_IMAGE = 72;
        int MAY_ATTENDING_GUEST_LIST = 73;
        int GET_INVITED_GUEST_LIST = 74;
        int DELETE_CANCEL_EVENT = 75;
        int GET_MUTUAL_CONNECTION_LIST = 77;
        int ADD_EVENT_CONTACTS = 78;
        int DEL_EVENT_CONTACTS = 79;
        int EDIT_EVENT_CONTACTS = 80;
        int EDIT_FOLDERS = 81;

        int POST_COMMENTS = 82;
        int GET_COMMENTS = 83;
        int REMOVE_FOLDER = 84;
        int MUTUAL_FAMILIES = 85;
        int UPDATE_FILE_NAME = 88;
        int PENDING_REQUEST = 89;
        int DELETE_PENDING_REQUEST = 90;
        int BULK_UPDATE_CONTACTS = 91;
        int DELETE_AGENDA = 92;
        int VERIFIED_MOBILE_NUMBER = 93;

        int MOBILE_NUMBER_DETAILS=94;
        int CREATE_POST=95;
    }

    public interface ErrorClass {
        String CODE = "code";
        String STATUS = "status";
        String MESSAGE = "message";
        String DEVELOPER_MESSAGE = "developerMessage";
    }

    public interface ImageUpdate {
        int USER_PROFILE_LOGO = 0;
        int USER_PROFILE_COVER = 1;
        int FAMILY_LOGO = 2;
        int FAMILY_COVER = 3;
        int EVENT_LOGO = 4;
        int EVENT_COVER = 5;
        int GENERAL = 6;

    }

    public interface FileUpload {
        int TYPE_EVENTS = 0;
        int TYPE_FAMILY = 1;
        int TYPE_USER = 2;
    }


    public interface ErrorCodes {
        int INTERNAL_ERROR = 500;
    }

    public interface ErrorNames {
        String USER_ALREADY_EXISTS = "User already exists in db//local";
        String UPDATE_REQUIRED = "please update your app";
    }


    public interface Selections {
        String SELECTED = "s";
        String UN_SELECTED = "us";
    }

    public interface FamilySettings {

        interface MemberJoining {
            String INVITATION_ONLY = "1";
            String ANYONE_CAN_JOIN_WITH_APPROVAL = "2";
            String ANYONE_CAN_JOIN = "3";
        }

        interface MemberApproval {
            String MEMBER_APPROVAL = "4";
            String ANY_MEMBER = "5";
        }

        interface ViewRequest {
            String ADMIN = "26";
            String MEMBERS_ONLY = "27";
        }

        interface PostCreate {
            String ADMIN = "7";
            String MEMBERS_ONLY = "6";
            String ANY_MEMBER_WITH_APPROVAL = "20";
        }

        interface PostVisibility {
            String MEMBERS_ONLY = "8";
            String PUBLIC = "9";
        }

        interface PostApproval {
            String NOT_NEEDED = "10";
            String ADMIN = "11";
            String MEMBERS = "12";
        }

        interface LinkFamily {
            String ADMINS = "13";
            String MEMBER = "14";
        }

        interface LinkApproval {
            String NOT_NEEDED = "15";
            String ADMIN = "16";
        }

        interface AnnouncementCreate {
            String ADMIN = "17";
            String ANY_MEMBER = "18";
            String ANY_MEMBER_WITH_APPROVAL = "19";
        }

        interface AnnouncementVisibility {
            String MEMBERS_ONLY = "21";
            String PUBLIC = "22";
        }

        interface AnnouncementApproval {
            String NOT_NEEDED = "23";
            String ADMIN = "24";
            String MEMBERS = "25";
        }
    }

    public interface FamilyDashboardIdentifiers{
        int TypeLinkingFragment= 0;
        int TypeFamilySubscriptionFragment= 1;
        int TypeAlbumFragment= 2;
        int TypeAboutUsFragment= 3;
        int TypeFamilyEventFragment= 4;
        int TypeFoldersFragment= 5;
        int TypeFamilyNeedsListingFragment = 6;
        int TypeAnnouncementListingFragment = 7;
        int TypeFamilyPostFeedFragment = 8;
        int TypeLinkedFamilyFragment = 9;
        int TypeFamilyRequestsFragment = 10;
        int TypeFamilyMembership = 11;
    }

    public static final int COMPRESSION_QUALITY = 50;
    public static final String SOMETHING_WRONG = "Something went wrong! Please try again";
    public static final String UPDATE_APP = "Please update your app to latest version";


    public interface ApiPaths {

//        String S3_DEV_IMAGE_URL_SQUARE = "https://dev-imaginary.familheey.com/crop?width=200&height=200&quality=30&url=";
//        String S3_DEV_IMAGE_URL_COVER = "https://dev-imaginary.familheey.com/crop?width=400&height=300&url=";
//        String S3_DEV_IMAGE_URL_THUMB = "https://dev-imaginary.familheey.com/resize?width=200&quality=30&url=";
//        String S3_DEV_IMAGE_URL_ALBUM = "https://dev-imaginary.familheey.com/crop?width=400&height=400&url=";
//        String S3_DEV_IMAGE_URL_COVER_DETAILED = "https://dev-imaginary.familheey.com/resize?width=700&url=";
//        String S3_DEV_IMAGE_URL_SQUARE_DETAILED = "https://dev-imaginary.familheey.com/resize?width=700&url=";
//        String S3_DEV_IMAGE_URL_PROVIDER_LOGO = "https://dev-imaginary.familheey.com/crop?width=48&height=48&url=";
//        String S3_DEV_IMAGE_URL_ELASTIC = "https://dev-imaginary.familheey.com/resize?width=200&url=";



        String S3_DEV_IMAGE_URL_SQUARE = "https://imaginary.familheey.com/crop?width=200&height=200&url=";
        String S3_DEV_IMAGE_URL_COVER = "https://imaginary.familheey.com/crop?width=400&height=300&url=";
        String S3_DEV_IMAGE_URL_THUMB = "https://imaginary.familheey.com/resize?width=200&url=";
        String S3_DEV_IMAGE_URL_ALBUM = "https://imaginary.familheey.com/crop?width=400&height=400&url=";
        String S3_DEV_IMAGE_URL_COVER_DETAILED = "https://imaginary.familheey.com/resize?width=700&url=";
        String S3_DEV_IMAGE_URL_SQUARE_DETAILED = "https://imaginary.familheey.com/resize?width=700&url=";
        String S3_DEV_IMAGE_URL_PROVIDER_LOGO = "https://imaginary.familheey.com/crop?width=48&height=48&url=";
        String S3_DEV_IMAGE_URL_ELASTIC = "https://imaginary.familheey.com/resize?width=200&url=";

        //Release
      //  String FIREBASE_DATABASE_URL = "https://familheey-255407.firebaseio.com/";

        //Staging
       String FIREBASE_DATABASE_URL = "https://familheeystaging-default-rtdb.firebaseio.com/";


        String BASE_URL = BuildConfig.BASE_URL;
        String SHARE_URL = BuildConfig.SHARE_URL;
        String SOCKET_URL = BuildConfig.SOCKET_URL;
        String SOCKET_COMMENT_URL = BuildConfig.SOCKET_COMMENT_URL;
        String IMAGE_BASE_URL = BuildConfig.IMAGE_BASE_URL;
        String FIREBASE_USER_TYPE = BuildConfig.FIREBASE_USER_TYPE;
        String PRIVACY_URL = BuildConfig.PRIVACY_URL;
    }

}

