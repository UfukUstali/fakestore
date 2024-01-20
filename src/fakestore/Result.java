package fakestore;

public class Result<T, E> {
    private final T value;
    private final E error;
    private final EResult isSuccess;

    private Result(T value, E error, EResult isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T, E> Result<T, E> ok(T value) {
        return new Result<>(value, null, EResult.OK);
    }

    public static <T, E> Result<T, E> error(E error) {
        return new Result<>(null, error, EResult.ERROR);
    }

    public EResult status() {
        return isSuccess;
    }

    public T value() {
        if (isSuccess == EResult.ERROR) {
            throw new IllegalStateException("Cannot get value from an error Result");
        }
        return value;
    }

    public E error() {
        if (isSuccess == EResult.OK) {
            throw new IllegalStateException("Cannot get error from a success Result");
        }
        return error;
    }
}

