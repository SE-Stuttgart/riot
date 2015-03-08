package de.uni_stuttgart.riot.clientlibrary;

/**
 * //FIXME.
 * @author tajoa
 *
 */
public class RequestExceptionWrapper {

    private int errorCode;
    private String errorMessage;

    /**
     * Constructor.
     * @param errorCode .
     * @param errorMessage .
     */
    public RequestExceptionWrapper(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Default Constructor.
     */
    public RequestExceptionWrapper() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMassage) {
        this.errorMessage = errorMassage;
    }

   

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Encapsulates into {@link RequestException} and thros it.
     * @throws RequestException .
     */
    public void throwIT() throws RequestException {
        throw new RequestException(this.getErrorCode() + " : " + this.getErrorMessage());
    }

}
