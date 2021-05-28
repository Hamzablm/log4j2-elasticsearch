package org.appenders.log4j2.elasticsearch;

import io.netty.buffer.ByteBuf;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReusableOutputStreamProviderTest {

    @Test
    public void returnsReusedOutputStreamInstance() {

        // given
        final OutputStreamSource source1 = ByteBufItemSourceTest.createTestItemSource();
        final OutputStreamSource source2 = ByteBufItemSourceTest.createTestItemSource();

        final OutputStreamProvider<ByteBuf> provider = new ReusableOutputStreamProvider<>();

        // when
        final OutputStream os1 = provider.asOutputStream(source1);
        final OutputStream os2 = provider.asOutputStream(source2);

        // then
        assertSame(os1, os2);

    }

    @Test
    public void throwsOnIncompatibleItemSource() {

        // given
        final ItemSource<ByteBuf> source = () -> null;

        final OutputStreamProvider<ByteBuf> provider = new ReusableOutputStreamProvider<>();

        // when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> provider.asOutputStream(source));

        // then
        assertThat(exception.getMessage(), IsEqual.equalTo("Not an instance of " + OutputStreamSource.class.getSimpleName()));

    }

}