package com.vertigrated.memoize;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class Memoizes (caches) the results of a Function that takes a type T
 * and returns a R without having to recalculating R over and over.
 *
 * Specifically it can take an object and create a String representation and
 * cache that representation without having to re-convert it over and over.
 *
 * @param <T> Type of source to be memoized
 * @param <R> Type of the value that is memoized
 */
@JsonIgnoreType
@Immutable
public class Memoizer<T, R> implements Supplier<R>
{
    @Nonnull
    private final ReentrantLock lock = new ReentrantLock();
    @Nonnull
    private final Predicate<R> predicate;
    @Nonnull
    private final Supplier<T> supplier;
    @Nonnull
    private final Function<T, R> function;
    @Nonnull
    private final AtomicReference<WeakReference<R>> value;

    public Memoizer(@Nonnull final Function<T,R> function, @Nonnull final T source)
    {
        this(function, new Supplier<T>() {
            @Override
            public T get()
            {
                return checkNotNull(source);
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

    private Memoizer(@Nonnull final Predicate<R> predicate, @Nonnull final Function<T,R> function, @Nonnull final Supplier<T> Supplier)
    {
        this.predicate = predicate;
        this.supplier = Supplier;
        this.function = function;
        this.value = new AtomicReference<>(new WeakReference<R>(null));
    }

    @Nonnull
    @Override
    public R get()
    {
        this.lock.lock();
        try
        {
            if (this.predicate.test(this.value.get().get()))
            {
                this.value.set(new WeakReference<>(this.function.apply(this.supplier.get())));
            }
            return checkNotNull(this.value.get().get());
        }
        finally
        {
            this.lock.unlock();
        }
    }

    @Override
    public boolean equals(@Nullable final Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        return Objects.equal(this.value.get().get(), ((Memoizer<?, ?>) o).value.get().get());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.value.get().get());
    }

    @Nonnull
    @Override
    public String toString()
    {
        return this.value.get().get().toString();
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
