package com.familheey.app.Models.Response;

public class ConversationMute {
    private String message;
    private ConversationMuteData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ConversationMuteData getData() {
        return data;
    }

    public void setData(ConversationMuteData data) {
        this.data = data;
    }


    public class ConversationMuteData {
        private Boolean is_active;
        private String id;
        private String user_id;
        private String post_id;

        public Boolean getIs_active() {
            return is_active;
        }

        public void setIs_active(Boolean is_active) {
            this.is_active = is_active;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
        }
    }
}

