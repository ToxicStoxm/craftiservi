package com.x_tornado10.utils.custom_data.reload;

import java.util.List;

public class CustomDataWrapper {

    private final List<CustomData> customDataList;

    public CustomDataWrapper(List<CustomData> customDataList) {
        this.customDataList = customDataList;
    }

    public CustomData getCustomData(int index) {
        return customDataList.get(index);
    }
}
