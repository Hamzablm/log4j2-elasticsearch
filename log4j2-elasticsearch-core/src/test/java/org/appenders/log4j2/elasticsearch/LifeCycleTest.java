package org.appenders.log4j2.elasticsearch;

/*-
 * #%L
 * log4j2-elasticsearch
 * %%
 * Copyright (C) 2019 Rafal Foltynski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;

public class LifeCycleTest {

    @Test
    public void returnsLifeCycleOfGivenObjectIfAvailable() {

        // given
        TestObject expected = createDefaultTestLifecycle();

        // when
        LifeCycle lifeCycle = LifeCycle.of(expected);

        // then
        assertEquals(expected, lifeCycle);

    }

    @Test
    public void returnsNoopLifeCycleIfNonLifeCycleObject() {

        // given
        Object expected = new Object();

        // when
        LifeCycle lifeCycle = LifeCycle.of(expected);

        // then
        assertNotEquals(expected, lifeCycle);
        assertSame(LifeCycle.NOOP, lifeCycle);

    }

    @Test
    public void stopDelegatesToParametrizedStop() {

        // given
        LifeCycle lifeCycle = spy(createDefaultTestLifecycle());

        // when
        lifeCycle.stop();

        // then
        Mockito.verify(lifeCycle).stop(anyLong(), anyBoolean());
    }

    public static TestObject createDefaultTestLifecycle() {
        return new TestObject();
    }

    private static class TestObject implements LifeCycle {

        @Override
        public void start() {

        }

        @Override
        public boolean isStarted() {
            return false;
        }

        @Override
        public boolean isStopped() {
            return false;
        }

    }

}