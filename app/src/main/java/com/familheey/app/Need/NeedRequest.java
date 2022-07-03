package com.familheey.app.Need;

import java.util.ArrayList;
import java.util.List;

public class NeedRequest {
    public List<Need> getNeeds() {
        List<Need> needs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            needs.add(new Need());
        }
        return needs;
    }
}
