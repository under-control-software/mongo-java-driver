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

package com.mongodb.reactivestreams.client.internal;

import com.mongodb.internal.async.client.AsyncListCollectionsIterable;
import com.mongodb.internal.async.client.Observables;
import com.mongodb.reactivestreams.client.ListCollectionsPublisher;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import static com.mongodb.assertions.Assertions.notNull;


final class ListCollectionsPublisherImpl<TResult> implements ListCollectionsPublisher<TResult> {

    private final AsyncListCollectionsIterable<TResult> wrapped;

    ListCollectionsPublisherImpl(final AsyncListCollectionsIterable<TResult> wrapped) {
        this.wrapped = notNull("wrapped", wrapped);
    }

    @Override
    public ListCollectionsPublisher<TResult> filter(final Bson filter) {
        notNull("filter", filter);
        wrapped.filter(filter);
        return this;
    }

    @Override
    public ListCollectionsPublisher<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        notNull("timeUnit", timeUnit);
        wrapped.maxTime(maxTime, timeUnit);
        return this;
    }

    @Override
    public ListCollectionsPublisher<TResult> batchSize(final int batchSize) {
        wrapped.batchSize(batchSize);
        return this;
    }

    @Override
    public Publisher<TResult> first() {
        return new SingleResultObservableToPublisher<>(wrapped::first);
    }

    @Override
    public void subscribe(final Subscriber<? super TResult> s) {
        new ObservableToPublisher<>(Observables.observe(wrapped)).subscribe(s);
    }
}
