package com.familheey.app.Interfaces;

public interface GlobalSearchListener {
    void requsetGlobalSearch();

    void performSearch(String searchQuery);

    void reloadFamilySearch();

    void fetchFamilies();
}
