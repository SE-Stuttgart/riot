package de.uni_stuttgart.riot.commons.test;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.glassfish.jersey.message.internal.NullOutputStream;

/**
 * Helper class for running SQL scripts on JDBC connections.
 */
public class SqlRunner implements Closeable {

    private static final String DELIMITER_LINE_REGEX = "(?i)DELIMITER.+";
    private static final String DELIMITER_LINE_SPLIT_REGEX = "(?i)DELIMITER";
    private static final String DEFAULT_DELIMITER = ";";
    private final boolean autoCommit;
    private final boolean stopOnError;
    private final Connection connection;
    private String delimiter = SqlRunner.DEFAULT_DELIMITER;
    private final PrintWriter out;
    private final PrintWriter err;

    /**
     * Creates a new SqlRunner. Do not forget to close it!
     * 
     * @param connection
     *            The JDBC connection.
     * @param out
     *            Stream for normal logging.
     * @param err
     *            Stream for error logging.
     * @param autoCommit
     *            Whether to run the scripts with auto commit.
     * @param stopOnError
     *            Whether to stop once an error occurs.
     */
    public SqlRunner(final Connection connection, final PrintWriter out, final PrintWriter err, final boolean autoCommit, final boolean stopOnError) {
        if (connection == null) {
            throw new RuntimeException("SqlRunner requires an SQL Connection");
        }
        if (err == null || out == null) {
            throw new RuntimeException("SqlRunner requires both out and err PrintWriters");
        }
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
        this.out = out;
        this.err = err;
    }

    /**
     * Creates a new SqlRunner that redirects to {@link System#out} and {@link System#err}. Do not forget to close it!
     * 
     * @param dataSource
     *            The JDBC data source.
     * @param silent
     *            If set to <tt>true</tt>, the default output is suppressed and only {@link System#err} will be used.
     * @throws SQLException
     *             If the JDBC data source cannot be used for opening a connection.
     */
    public SqlRunner(final DataSource dataSource, final boolean silent) throws SQLException {
        this(dataSource.getConnection(), new PrintWriter(silent ? new NullOutputStream() : System.out), new PrintWriter(System.err), true, false);
    }

    /**
     * Creates a new SqlRunner uses no normal logging and prints errors to {@link System#err}. Do not forget to close it!
     * 
     * @param dataSource
     *            The JDBC data source.
     * @throws SQLException
     *             If the JDBC data source cannot be used for opening a connection.
     */
    public SqlRunner(final DataSource dataSource) throws SQLException {
        this(dataSource, true);
    }

    /**
     * Runs the script with the given file name. The file should be placed on the classpath.
     * 
     * @param resourceName
     *            The name of the classpath resource (must be a file containing SQL command).
     * @throws SQLException
     *             When running the script fails.
     */
    public void runScript(final String resourceName) throws SQLException {
        runScript(new InputStreamReader(SqlRunner.class.getResourceAsStream(resourceName)));
    }

    /**
     * Loads the script from the given URL and runs it.
     * 
     * @param url
     *            The URL to load the script from.
     * @throws SQLException
     *             When running the script fails.
     * @throws IOException
     *             If the URL cannot be resolved.
     */
    public void runScript(final URL url) throws SQLException, IOException {
        runScript(url.openStream());
    }

    /**
     * Reads a script from the given stream and runs it.
     * 
     * @param stream
     *            The stream to read from.
     * @throws SQLException
     *             When running the script fails.
     */
    public void runScript(final InputStream stream) throws SQLException {
        runScript(new InputStreamReader(stream));
    }

    /**
     * Reads a script and runs it.
     * 
     * @param reader
     *            The reader to read from.
     * @throws SQLException
     *             When running the script fails.
     */
    public void runScript(final Reader reader) throws SQLException {
        final boolean originalAutoCommit = this.connection.getAutoCommit();
        try {
            if (originalAutoCommit != this.autoCommit) {
                this.connection.setAutoCommit(this.autoCommit);
            }
            this.runScript(this.connection, reader);
        } finally {
            this.connection.setAutoCommit(originalAutoCommit);
        }
    }

    /**
     * Reads a script and runs it on the given connection.
     * 
     * @param conn
     *            The JDBC connection.
     * @param reader
     *            The reader to read from.
     */
    private void runScript(final Connection conn, final Reader reader) { // NOCS
        StringBuilder command = null;
        try {
            final LineNumberReader lineReader = new LineNumberReader(reader);
            String line = null;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuilder();
                }
                String trimmedLine = line.trim();

                if (trimmedLine.startsWith("--") || trimmedLine.startsWith("//") || trimmedLine.startsWith("#")) {

                    // Line is a comment
                    out.println(trimmedLine);
                    out.flush();

                } else if (trimmedLine.endsWith(this.delimiter)) {

                    // Line is end of statement

                    // Support new delimiter
                    final Pattern pattern = Pattern.compile(SqlRunner.DELIMITER_LINE_REGEX);
                    final Matcher matcher = pattern.matcher(trimmedLine);
                    if (matcher.matches()) {
                        delimiter = trimmedLine.split(SqlRunner.DELIMITER_LINE_SPLIT_REGEX)[1].trim();

                        // New delimiter is processed, continue on next
                        // statement
                        line = lineReader.readLine();
                        if (line == null) {
                            break;
                        }
                        trimmedLine = line.trim();
                    }

                    // Append
                    command.append(line.substring(0, line.lastIndexOf(this.delimiter)));
                    command.append(" ");

                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        stmt = conn.createStatement();
                        out.println();
                        out.println(command);
                        out.flush();
                        boolean hasResults = false;
                        if (this.stopOnError) {
                            hasResults = stmt.execute(command.toString());
                        } else {
                            try {
                                stmt.execute(command.toString());
                            } catch (final SQLException e) {
                                e.fillInStackTrace();
                                err.println("Error on command: " + command);
                                err.println(e);
                                err.flush();
                            }
                        }
                        if (this.autoCommit && !conn.getAutoCommit()) {
                            conn.commit();
                        }
                        rs = stmt.getResultSet();
                        if (hasResults && rs != null) {

                            // Print result column names
                            final ResultSetMetaData md = rs.getMetaData();
                            final int cols = md.getColumnCount();
                            for (int i = 0; i < cols; i++) {
                                final String name = md.getColumnLabel(i + 1);
                                out.print(name + "\t");
                            }
                            out.println("");
                            for (int i = 0; i < md.getColumnCount(); i++) {
                                out.print("---------");
                            }
                            out.println();
                            out.flush();

                            // Print result rows
                            while (rs.next()) {
                                for (int i = 1; i <= cols; i++) {
                                    final String value = rs.getString(i);
                                    out.print(value + "\t");
                                }
                                out.println("");
                            }
                            out.flush();
                        } else {
                            out.println("Updated: " + stmt.getUpdateCount());
                            out.flush();
                        }
                        command = null;
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (final Exception e) {
                                err.println("Failed to close result: " + e.getMessage());
                                err.flush();
                            }
                        }
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (final Exception e) {
                                err.println("Failed to close statement: " + e.getMessage());
                                err.flush();
                            }
                        }
                    }
                } else {

                    // Line is middle of a statement

                    // Support new delimiter
                    final Pattern pattern = Pattern.compile(SqlRunner.DELIMITER_LINE_REGEX);
                    final Matcher matcher = pattern.matcher(trimmedLine);
                    if (matcher.matches()) {
                        delimiter = trimmedLine.split(SqlRunner.DELIMITER_LINE_SPLIT_REGEX)[1].trim();
                        line = lineReader.readLine();
                        if (line == null) {
                            break;
                        }
                        trimmedLine = line.trim();
                    }
                    command.append(line);
                    command.append(" ");
                }
            }
            if (!this.autoCommit) {
                conn.commit();
            }
        } catch (final SQLException e) {
            e.fillInStackTrace();
            err.println("Error on command: " + command);
            err.println(e);
            err.flush();
        } catch (final IOException e) {
            e.fillInStackTrace();
            err.println("Error on command: " + command);
            err.println(e);
            err.flush();
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
