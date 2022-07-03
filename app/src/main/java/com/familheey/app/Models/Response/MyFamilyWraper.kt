package com.familheey.app.Models.Response

import com.familheey.app.Discover.model.DiscoverGroups

data class MyFamilyWraper(var pendingCount: Int, var data: List<DiscoverGroups>)