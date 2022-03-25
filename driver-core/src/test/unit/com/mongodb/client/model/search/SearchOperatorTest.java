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

import org.bson.BsonDocument;
import org.junit.jupiter.api.Test;

import static com.mongodb.client.model.search.SearchPath.fieldPath;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class SearchOperatorTest {
    @Test
    void of() {
        assertEquals(
                docExamplePredefined()
                        .toBsonDocument(),
                SearchOperator.of(docExampleCustom())
                        .toBsonDocument()
        );
    }

    @Test
    void exists() {
        assertEquals(
                docExampleCustom(),
                docExamplePredefined()
                        .toBsonDocument()
        );
    }

    private static SearchOperator docExamplePredefined() {
        return SearchOperator.exists(
                fieldPath("fieldName"));
    }

    private static BsonDocument docExampleCustom() {
        return new BsonDocument("exists",
                new BsonDocument("path", fieldPath("fieldName").toBsonValue()));
    }
}
