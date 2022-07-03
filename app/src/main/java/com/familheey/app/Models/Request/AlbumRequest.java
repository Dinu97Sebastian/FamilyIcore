package com.familheey.app.Models.Request;

import java.util.ArrayList;

public class AlbumRequest {
    private String is_sharable;
    private String folder_id;
    private String user_id;
    private String group_id;
    private String folder_type;

    private ArrayList<Document_file> document_files;

    public void setFolderType(String folder_type) {
        this.folder_type = folder_type;
    }

    public void setIs_sharable(String is_sharable) {
        this.is_sharable = is_sharable;
    }

    public void setFolder_id(String folder_id) {
        this.folder_id = folder_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public void setDocument_files(ArrayList<Document_file> document_files) {
        this.document_files = document_files;
    }

    public static class Document_file{
        private String original_name;
        private String url;
        private String file_type;
        private String file_name;
        private String video_thumb;
        private String width;
        private String height;

        public String getOriginal_name() {
            return original_name;
        }

        public void setOriginal_name(String original_name) {
            this.original_name = original_name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public String getVideo_thumb() {
            return video_thumb;
        }

        public void setVideo_thumb(String video_thumb) {
            this.video_thumb = video_thumb;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }
    }
}


