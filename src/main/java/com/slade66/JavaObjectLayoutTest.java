package com.slade66;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

public class JavaObjectLayoutTest {

    @Test
    void printObjectHeader() {
        System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());
    }

    @Test
    void thinLock() throws InterruptedException {
        Object o = new Object();

        Thread t = new Thread(() -> {
            synchronized (o) {
                System.out.println(ClassLayout.parseInstance(o).toPrintable());
            }
        });
        t.start();
        t.join();
    }

}
