package com.vertigrated.memoize;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

@Immutable
public final class Memoizers
{
    public static final class ToByteArray<T> extends Memoizer<T,byte[]>
    {
        public ToByteArray(@Nonnull final Function<T,byte[]> function, @Nonnull final T source)
        {
            super(function, source);
        }
    }

    public static final class ToInteger<T> extends Memoizer<T,Integer>
    {
        public ToInteger(@Nonnull final Function<T,Integer> function, @Nonnull final T source)
        {
            super(function, source);
        }
    }

    public static final class ToString<T> extends Memoizer<T,String>
    {
        public ToString(@Nonnull final Function<T,String> function, @Nonnull final T source)
        {
            super(function, source);
        }
    }
}
