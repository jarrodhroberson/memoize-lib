package com.vertigrated.memoize;

import com.google.common.hash.Hashing;
import com.vertigrated.memoize.Memoizers.ToByteArray;
import com.vertigrated.memoize.Memoizers.ToInteger;
import com.vertigrated.memoize.Memoizers.ToString;
import org.junit.Test;

import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MemoizerTest
{
    @Test
    public void testToInteger()
    {
        final String source = "Hello World!";
        final Memoizer<String,Integer> integerMemoizer = new ToInteger<>(new Function<String,Integer>() {
            @Override
            public Integer apply(final String s)
            {
                return s.hashCode();
            }
        },source);
        assertThat(integerMemoizer.get(), is(source.hashCode()));
    }

    @Test
    public void testToByteArray()
    {
        final String source = "Hello World!";
        final Memoizer<String,byte[]> byteArrayMemoizer = new ToByteArray<>(new Function<String,byte[]>()
        {
            @Override
            public byte[] apply(final String s)
            {
                return s.getBytes(UTF_8);
            }
        }, source);
        assertThat(byteArrayMemoizer.get(), is(source.getBytes(UTF_8)));
    }

    @Test
    public void testToString()
    {
        final String source = "Hello World!";

        final Memoizer<String,String> sha256Memoizer = new ToString<>(new Function<String,String>()
        {
            @Override
            public String apply(final String s)
            {
                return Hashing.sha256().hashString(s, UTF_8).toString();
            }
        }, source);
        assertThat(sha256Memoizer.get(), is("7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"));
    }

    @Test
    public void testWeakReferenceCalculation()
    {
        final String source = "Long running function complete!";
        final Memoizer<String,String> longRunningFunction = new Memoizer<>(new Function<String,String>()
        {
            @Override
            public String apply(final String s)
            {
                try
                {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
                return s;
            }
        }, source);
        assertThat(longRunningFunction.get(), is(source));
        System.gc();
        assertThat(longRunningFunction.get(), is(source));
        assertThat(longRunningFunction.get(), is(source));
    }
}
