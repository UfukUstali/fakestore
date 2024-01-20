package fakestore.model;
import com.google.gson.Gson;

public class Response implements IResponse {
    private static final Gson gson = new Gson();
    private Status status;
    private String data;
    private Object dataObject;
    private String error;

    protected Response(Status status, String data, String error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public Status status() {
        return status;
    }

    public <T> T data(Class<T> type) {
        return dataObject != null ? (T) dataObject : (T) (dataObject = gson.fromJson(data, type));
    }

    public String error() {
        return error;
    }

    protected void set(Status status, String data, String error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }
}
