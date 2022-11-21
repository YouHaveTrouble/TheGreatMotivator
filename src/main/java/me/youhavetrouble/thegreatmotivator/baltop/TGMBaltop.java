package me.youhavetrouble.thegreatmotivator.baltop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TGMBaltop {

    private final int size;
    private final ArrayList<TGMBaltopEntry> baltop;

    protected TGMBaltop(int size) {
        this.size = size;
        this.baltop = new ArrayList<>(this.size);
    }

    public int getSize() {
        return size;
    }

    public List<TGMBaltopEntry> getBaltop() {
        return Collections.unmodifiableList(baltop);
    }

    public void refresh() {
        this.baltop.clear();
    }
}
