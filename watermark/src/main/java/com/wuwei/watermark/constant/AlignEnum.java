package com.wuwei.watermark.constant;

import java.util.Arrays;

public enum AlignEnum {

    left,
    center,
    right;


    public static AlignEnum getType(String align) {
        return Arrays.stream(AlignEnum.values())
                .filter(alignTemp -> alignTemp.name().equals(align))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("not support align"));
    }

}
