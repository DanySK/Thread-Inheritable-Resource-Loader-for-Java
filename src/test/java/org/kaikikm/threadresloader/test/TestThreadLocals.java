package org.kaikikm.threadresloader.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import org.junit.Test;

/**
 *
 *
 */
public class TestThreadLocals {

    // False positive, using the diamond operator is not possible with this version of Java
    private static final InheritableThreadLocal<Integer> THREAD_LOCAL = new InheritableThreadLocal<Integer>() { // NOPMD

        @Override
        protected Integer initialValue() {
            return 0;
        }

        @Override
        protected Integer childValue(final Integer parentValue) {
            return parentValue + 1;
        }
    };

    /**
     * @throws InterruptedException never
     */
    @Test
    public void testThreadLocals() throws InterruptedException {
        final List<Integer> arr = new ArrayList<>(10);
        arr.add(0, THREAD_LOCAL.get());
        assertEquals(Integer.class, arr.get(0).getClass());
        assertEquals(0, (int) arr.get(0));
        final CountDownLatch cl = new CountDownLatch(2);
        new AbstractTestThread(1, cl, arr, () -> null) {
            @Override
            public void operation() {
                new AbstractTestThread(2, cl, arr, THREAD_LOCAL::get) {
                    @Override
                    public void operation() {
                    }
                }.start();
            }
        }.start();
        cl.await();
        assertEquals(Integer.class, arr.get(2).getClass());
        assertEquals(2, (int) arr.get(2));
    }

    /**
     * @throws InterruptedException never
     */
    @Test
    public void testThreadLocalsInit() throws InterruptedException {
        final List<Integer> arr = new ArrayList<>(10);
        arr.add(0, null);
        final CountDownLatch cl = new CountDownLatch(2);
        new AbstractTestThread(1, cl, arr, THREAD_LOCAL::get) {
            @Override
            public void operation() {
                new AbstractTestThread(2, cl, arr, THREAD_LOCAL::get) {
                    @Override
                    public void operation() {
                    }
                }.start();
            }
        }.start();
        cl.await();
        assertEquals(Integer.class, arr.get(2).getClass());
        assertEquals(1, (int) arr.get(2));
    }

    private abstract static class AbstractTestThread extends Thread {
        private final int id;
        private final CountDownLatch cl;
        private final List<Integer> res;
        private final Supplier<Integer> sup;

        AbstractTestThread(final int id, final CountDownLatch cl, final List<Integer> res, final Supplier<Integer> sup) {
            this.id = id;
            this.cl = cl;
            this.res = res;
            this.sup = sup;
        }

        @Override
        public void run() {
            this.res.add(id, sup.get());
            this.operation();
            cl.countDown();
        }

        public abstract void operation();
    }
}
