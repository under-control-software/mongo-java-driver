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
package com.mongodb.client.model.search;

import com.mongodb.annotations.Beta;
import com.mongodb.annotations.Evolving;

/**
 * @see SearchCount#lowerBound()
 * @since 4.6
 */
@Beta
@Evolving
public interface LowerBoundSearchCount extends SearchCount {
    /**
     * Creates a new {@link LowerBoundSearchCount} that instructs to count documents up to the {@code threshold} exactly,
     * then to count roughly.
     *
     * @param threshold The number of documents to include in the exact count.
     * @return A new {@link LowerBoundSearchCount}.
     */
    LowerBoundSearchCount threshold(int threshold);
}