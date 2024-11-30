package fakestore.model;

import com.google.gson.Gson;

class Response implements IResponse {
    // we don't want to create a new Gson object for every response instance
    private static final Gson gson = new Gson();
    private Status status;
    private String data;
    private Object dataObject;
    private String error;

    Response(Status status, String data, String error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public Status status() {
        return status;
    }

    @SuppressWarnings("unchecked")
    public <T> T data(Class<T> type) {
        return dataObject != null ? (T) dataObject : (T) (dataObject = gson.fromJson(data, type));
    }

    public String error() {
        return error;
    }

    void set(Status status, String data, String error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }
}
