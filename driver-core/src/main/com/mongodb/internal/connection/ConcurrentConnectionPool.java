/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.internal.connection;

import com.mongodb.MongoTimeoutException;
import com.mongodb.diagnostics.logging.Logger;
import com.mongodb.diagnostics.logging.Loggers;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A concurrent pool implementation.
 *
 * <p>This class should not be considered a part of the public API.</p>
 */
public class ConcurrentConnectionPool extends ConcurrentPool<UsageTrackingInternalConnection> {

    private final int incrementSize;

    private final String incrementType;

    private static final Logger LOGGER = Loggers.getLogger("connection");

    private AtomicInteger powerCount = new AtomicInteger(1);


    /**
     * Factory for creating and closing pooled items.
     *
     * @param <T>
     */
    public interface ItemFactory<T> {
        T create(boolean initialize);

        void close(T t);

        Prune shouldPrune(T t);
    }

    private class addConnectionInPool implements Callable<Boolean> {
        private final long timeout;
        private final TimeUnit timeUnit;


        addConnectionInPool(final long timeout, final TimeUnit timeUnit) {
            this.timeout = timeout;
            this.timeUnit = timeUnit;
        }

        // The call() method is called in order to execute the asynchronous task.
        @Override
        public Boolean call() throws Exception {
            int incrementAmount = incrementSize;
            if (incrementType.equals("exponential")) {
                incrementAmount = (int) Math.pow(Math.max(2, incrementSize), powerCount.get());
            }
            try {
                int i = 0;
                while (i < incrementAmount && getCount() < maxSize) {
                    LOGGER.info("ConConPool 186 potentialCount " + getPotentialCount());
//                if (this.getPotentialCount() <= 0) {
//                    this.powerCount--;
//                    break;
//                }
                    LOGGER.info("Incrementing pool size " + i + " out of " + incrementAmount);
//                T t2 = itemFactory.create(true);
//                available.addLast(t2);
//                try to aquire permit
//                if (!acquirePermit(timeout, timeUnit)) {
//                    ConcurrentConnectionPool.powerCount--;
//                    break;
//                } else {
//                    T t2 = createNewAndReleasePermitIfFailure(false);
//                    available.addLast(t2);
//                }
                    if (!acquirePermit(timeout, timeUnit)) {
                        throw new InterruptedException();
                    } else {
                        LOGGER.info("Concurrent Connection pool: 97");
                        try {
                            UsageTrackingInternalConnection t2 = createNewAndReleasePermitIfFailure(false);
                            available.addLast(t2);
                            powerCount.incrementAndGet();
                        } catch (RuntimeException e) {
                            LOGGER.info("Concurrent Connection pool: 100");
                            throw e;
                        }
                    }
                    ++i;
                }

            } catch (InterruptedException e) {
                LOGGER.info("Concurrent Connection pool: 102");
                Thread.currentThread().interrupt();
            }
            return true;
        }
    }

    /**
     * Initializes a new pool of objects.
     *
     * @param maxSize     max to hold to at any given time. if < 0 then no limit
     * @param itemFactory factory used to create and close items in the pool
     */
    public ConcurrentConnectionPool(final int maxSize, final ItemFactory<UsageTrackingInternalConnection> itemFactory, final int incrementSize, final String incrementType) {
        super(maxSize, (ConcurrentPool.ItemFactory<UsageTrackingInternalConnection>) itemFactory);
        this.incrementSize = incrementSize;
        this.incrementType = incrementType;
    }

    /**
     * Return an instance of T to the pool.  This method simply calls {@code release(t, false)}
     *
     * @param t item to return to the pool
     */


    /**
     * Gets an object from the pool - will block if none are available
     *
     * @param timeout  negative - forever 0        - return immediately no matter what positive ms to wait
     * @param timeUnit the time unit of the timeout
     * @return An object from the pool, or null if can't get one in the given waitTime
     * @throws MongoTimeoutException if the timeout has been exceeded
     */
    @Override
    public UsageTrackingInternalConnection get(final long timeout, final TimeUnit timeUnit) {

        if (closed) {
            throw new IllegalStateException("The pool is closed");
        }

        if (!acquirePermit(timeout, timeUnit)) {
            throw new MongoTimeoutException(String.format("Timeout waiting for a pooled item after %d %s", timeout, timeUnit));
        }

        UsageTrackingInternalConnection t = available.pollLast();


        if (t == null) {

//            when pool is empty, create a new item and add it to the pool
//            also create additional items restricted by the maxSize
//            additional items are created by incrementing the powerCount

            t = createNewAndReleasePermitIfFailure(false);
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            Future<Boolean> future = executorService.submit(new addConnectionInPool(timeout, timeUnit));
            LOGGER.info("ConConPool 197: increase power count from :" + powerCount.get());


        }
        return t;
    }


    /**
     * Clears the pool of all objects.
     */


    public int getPotentialCount() {
        return maxSize - getCount();
    }


    // swallow exceptions from ItemFactory.close()

}
