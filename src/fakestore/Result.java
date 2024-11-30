package fakestore;

import static fakestore.EResult.*;

public class Result<T, E> {
    private final T value;
    private final E error;
    private final EResult isSuccess;

    private Result(T value, E error, EResult isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    // static factory methods

    /**
     * @param value the value to wrap.
     * @return a Result with {@link EResult#OK} status and the given value.
     */
    public static <T, E> Result<T, E> ok(T value) {
        return new Result<>(value, null, OK);
    }

    /**
     * @param error the error to wrap.
     * @return a Result with {@link EResult#ERROR} status and the given error.
     */
    public static <T, E> Result<T, E> error(E error) {
        return new Result<>(null, error, ERROR);
    }

    /**
     * @return {@link EResult#OK} if the Result is successful, {@link EResult#ERROR} otherwise.
     */
    public EResult status() {
        return isSuccess;
    }

    /**
     * @return the value if the {@link #status} returns {@link EResult#OK}.
     * @throws IllegalStateException if the {@link #status} returns {@link EResult#ERROR}.
     */
    public T value() {
        if (isSuccess == ERROR) {
            throw new IllegalStateException("Cannot get value from an error Result");
        }
        return value;
    }

    /**
     * @return the error if the {@link #status} returns {@link EResult#ERROR}.
     * @throws IllegalStateException if the {@link #status} returns {@link EResult#OK}.
     */
    public E error() {
        if (isSuccess == OK) {
            throw new IllegalStateException("Cannot get error from a success Result");
        }
        return error;
    }
}

