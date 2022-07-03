package com.familheey.app.Interfaces;

import com.familheey.app.Models.Response.ListEventAlbumsResponse;

public interface Bottomupinterface {
    void updateCoverPic(ListEventAlbumsResponse.Datum datum);

    void deletedFile();
}