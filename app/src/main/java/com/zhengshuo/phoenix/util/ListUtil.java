package com.zhengshuo.phoenix.util;

import java.util.List;


public class ListUtil {
    public static <D> boolean isEmpty(List<D> list) {
        return list == null || list.isEmpty();
    }
}
