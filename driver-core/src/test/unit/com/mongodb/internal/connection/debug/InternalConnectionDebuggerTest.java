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
package com.mongodb.internal.connection.debug;

import com.mongodb.connection.SocketSettings;
import com.mongodb.connection.SocketStreamFactory;
import com.mongodb.connection.SslSettings;
import com.mongodb.connection.StreamFactory;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public final class InternalConnectionDebuggerTest {
    @Test
    public void debuggableStreamFactory() {
        StreamFactory streamFactory = new SocketStreamFactory(SocketSettings.builder().build(), SslSettings.builder().build());
        assertSame(streamFactory, new InternalConnectionDebugger(Debugger.ReportingMode.OFF)
                .debuggableStreamFactory(streamFactory));
        assertNotSame(streamFactory, new InternalConnectionDebugger(Debugger.ReportingMode.LOG)
                .debuggableStreamFactory(streamFactory));
        assertNotSame(streamFactory, new InternalConnectionDebugger(Debugger.ReportingMode.LOG_AND_THROW)
                .debuggableStreamFactory(streamFactory));
    }

    @Test
    public void report() {
        MongoDebuggingException e = new MongoDebuggingException();
        Reporter.FailureCallback mustNotBeCalled = ignored -> fail();
        assertFalse(new InternalConnectionDebugger(Debugger.ReportingMode.OFF).report(e, null));
        assertFalse(new InternalConnectionDebugger(Debugger.ReportingMode.OFF).report(e, mustNotBeCalled));
        assertFalse(new InternalConnectionDebugger(Debugger.ReportingMode.LOG).report(e, null));
        assertFalse(new InternalConnectionDebugger(Debugger.ReportingMode.LOG).report(e, mustNotBeCalled));
    }

    @Test(expected = MongoDebuggingException.class)
    public void report3() {
        new InternalConnectionDebugger(Debugger.ReportingMode.LOG_AND_THROW).report(new MongoDebuggingException(), null);
    }

    @Test
    public void ringBufferIdx() {
        assertEquals(1, InternalConnectionDebugger.ringBufferIdx(-5, 3));
        assertEquals(2, InternalConnectionDebugger.ringBufferIdx(-4, 3));
        assertEquals(0, InternalConnectionDebugger.ringBufferIdx(-3, 3));
        assertEquals(1, InternalConnectionDebugger.ringBufferIdx(-2, 3));
        assertEquals(2, InternalConnectionDebugger.ringBufferIdx(-1, 3));
        assertEquals(0, InternalConnectionDebugger.ringBufferIdx(0, 3));
        assertEquals(1, InternalConnectionDebugger.ringBufferIdx(1, 3));
        assertEquals(2, InternalConnectionDebugger.ringBufferIdx(2, 3));
        assertEquals(0, InternalConnectionDebugger.ringBufferIdx(3, 3));
        assertEquals(1, InternalConnectionDebugger.ringBufferIdx(4, 3));
        assertEquals(2, InternalConnectionDebugger.ringBufferIdx(5, 3));
    }
}
