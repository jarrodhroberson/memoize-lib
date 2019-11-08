package com.vertigrated.memoize;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Immutable
public class Memoizer<T, R> implements Supplier<R>
{
    private final ReentrantLock lock = new ReentrantLock();
    private final Predicate<R> predicate;
    private final Supplier<T> supplier;
    private final Function<T, R> function;
    private final AtomicReference<R> value;

    public Memoizer(@Nonnull final Function<T,R> function, @Nonnull final T source)
    {
        this(function, new Supplier<T>() {
            @Override
            public T get()
            {
                return source;
            }
        });
    }

    private Memoizer(@Nonnull final Function<T,R> function, @Nonnull final Supplier<T> Supplier)
    {
        this(new Predicate<R>() {
            @Override
            public boolean test(final R r)
            {
                return r == null;
            }
        }, function, Supplier);
    }

    Memoizer(@Nonnull final Predicate<R> predicate, @Nonnull final Function<T,R> function, @Nonnull final Supplier<T> Supplier)
    {
        this.predicate = predicate;
        this.supplier = Supplier;
        this.function = function;
        this.value = new AtomicReference<>();
    }

    @Nonnull
    @Override
    public R get()
    {
        this.lock.lock();
        try
        {
            if (this.predicate.test(this.value.get()))
            {
                this.value.set(this.function.apply(this.supplier.get()));
            }
            return this.value.get();
        }
        finally
        {
            this.lock.unlock();
        }
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        final Memoizer<?, ?> memoizer = (Memoizer<?, ?>) o;
        return Objects.equal(this.value.get(), memoizer.value.get());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.value.get());
    }

    @Override
    public String toString()
    {
        return this.value.get().toString();
    }

    public static final class ToString<T> extends Memoizer<T,String>
    {
        public ToString(@Nonnull final Function<T, String> function, @Nonnull final T instance)
        {
            super(new Predicate<String>()
            {
                @Override
                public boolean test(@Nullable final String s)
                {
                    return s == null;
                }
            }, function, new Supplier<T>() {
                @Override
                public T get()
                {
                    return instance;
                }
            });
        }
    }
}
