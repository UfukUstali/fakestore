package fakestore.model;

public interface IResponse {
    /**
     * @return "success", "error" or "pending"
     */
    Status status();

    /**
     * @param type expects .class of the type of data to be returned
     *
     * @return the data returned by the API
     * null if the status is "error" or "pending"
     */
     <T> T data(Class<T> type);

    /**
     * @return the HTTP response code returned by the API if there was an error or the exception message if any exceptions were thrown during fetch, null if the status is "success" or "pending"
     */
    String error();

    static IResponse error(String error) {
        return new Response(Status.ERROR, null, error);
    }
}
