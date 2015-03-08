package de.uni_stuttgart.riot.clientlibrary;

/**
 * All the information that is necessary to connect to the server (without authentication).
 * 
 * @author Philipp Keck
 */
public class ConnectionInformation {

    private final String protocol;
    private final String host;
    private final int port;
    private final String restRootPath;

    /**
     * Creates a new instance.
     * 
     * @param host
     *            The plain host name (IP or DNS).
     * @param port
     *            The port number.
     * @param restRootPath
     *            The path behind the host name. This should start and end with a slash and point to the root of the REST API, e.g.,
     *            <tt>/riot/api/v1/</tt>.
     */
    public ConnectionInformation(String host, int port, String restRootPath) {
        this("https", host, port, restRootPath);
    }

    /**
     * Creates a new instance.
     * 
     * @param protocol
     *            The protocol to use.
     * @param host
     *            The plain host name (IP or DNS).
     * @param port
     *            The port number.
     * @param restRootPath
     *            The path behind the host name. This should start and end with a slash and point to the root of the REST API, e.g.,
     *            <tt>/riot/api/v1/</tt>.
     */
    public ConnectionInformation(String protocol, String host, int port, String restRootPath) {
        if (protocol == null || protocol.isEmpty()) {
            throw new IllegalArgumentException("protocol must not be empty!");
        }
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("host must not be empty!");
        }
        if (restRootPath == null || restRootPath.isEmpty()) {
            throw new IllegalArgumentException("restRootPath must not be empty!");
        } else if (!restRootPath.startsWith("/") || !restRootPath.endsWith("/")) {
            throw new IllegalArgumentException("restRootPath must start and end with a slash!");
        }
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.restRootPath = restRootPath;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getRestRootPath() {
        return restRootPath;
    }

    public String getFullBaseURL() {
        return protocol + "://" + host + ":" + port + restRootPath;
    }

    // CHECKSTYLE:OFF
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((restRootPath == null) ? 0 : restRootPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConnectionInformation other = (ConnectionInformation) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        if (protocol == null) {
            if (other.protocol != null)
                return false;
        } else if (!protocol.equals(other.protocol))
            return false;
        if (restRootPath == null) {
            if (other.restRootPath != null)
                return false;
        } else if (!restRootPath.equals(other.restRootPath))
            return false;
        return true;
    }

}
