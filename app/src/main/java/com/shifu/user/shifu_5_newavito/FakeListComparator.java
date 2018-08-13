package com.shifu.user.shifu_5_newavito;

import java.util.Comparator;

public class FakeListComparator implements Comparator<FakeItemEntry> {
    @Override
    public int compare(FakeItemEntry o1, FakeItemEntry o2) {
        return o1.date2.compareTo(o2.date2);
    }
}
