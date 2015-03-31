package de.uni_stuttgart.riot.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * This mojo reads .properties files and converts them into Android string XMLs.<br>
 * Note: It is important that this mojo is executed before the android-maven-plugin:generate-sources mojo! Otherwise, Android will use the
 * old string XMLs.
 * 
 * @author Philipp Keck
 */
@Mojo(name = "convert-messages", // This is the Maven goal.
// By default, execute at the end of the initialization phase, so right before the generate-sources phase, because that's when the
// android-maven-plugin needs the XML files.
defaultPhase = LifecyclePhase.INITIALIZE)
public class ConvertMessagesToAndroidMojo extends AbstractMojo {

    @Parameter(required = true)
    List<String> files;

    @Parameter(required = true)
    List<String> languages;

    @Parameter(required = true)
    String defaultLanguage;

    @Parameter(required = false, defaultValue = "${project.basedir}/src/main/res/")
    private String outputBaseDirectory;

    // CHECKSTYLE:OFF NCSS
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        // Check if output directory exists
        File outputDirectory = new File(outputBaseDirectory);
        if (!outputDirectory.exists() || !outputDirectory.isDirectory()) {
            throw new MojoExecutionException("The output directory " + outputBaseDirectory + " does not exist!");
        }

        // Create subdirectories for languages, if necessary
        File defaultDirectory = new File(outputDirectory, "values");
        if (!defaultDirectory.exists()) {
            if (!defaultDirectory.mkdir()) {
                throw new MojoExecutionException("Could not create output directory " + defaultDirectory);
            }
        } else if (!defaultDirectory.isDirectory()) {
            throw new MojoExecutionException("Target " + defaultDirectory + " is not a directory!");
        }
        Map<String, File> languageDirectories = new HashMap<String, File>();
        for (String language : languages) {
            File languageDirectory = new File(outputDirectory, "values-" + language);
            if (!languageDirectory.exists()) {
                if (!languageDirectory.mkdir()) {
                    throw new MojoExecutionException("Could not create output directory " + languageDirectory);
                }
            } else if (!languageDirectory.isDirectory()) {
                throw new MojoExecutionException("Target " + languageDirectory + " is not a directory!");
            }
            languageDirectories.put(language, languageDirectory);
        }

        // Map all files
        for (String file : files) {
            for (String language : languages) {
                File outFile = new File(languageDirectories.get(language), file + "_generated.xml");
                getLog().info("Compiling " + file + " (" + language + ") to " + outFile.getAbsolutePath());

                UTF8ResourceBundle bundle = new UTF8ResourceBundle("languages." + file, new Locale(language));
                Writer fileWriter;
                try {
                    fileWriter = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
                    fileWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
                } catch (IOException e) {
                    throw new MojoExecutionException("Cannot write to target file " + outFile, e);
                }
                try {
                    XMLWriter writer = new PrettyPrintXMLWriter(fileWriter);
                    convert(bundle, writer, file + "_");
                } finally {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        // Fail silently, it is not that important to close the file here.
                    }
                }

                if (language.equals(defaultLanguage)) {
                    File defaultLangFile = new File(defaultDirectory, file + "_generated.xml");
                    try {
                        FileUtils.copyFile(outFile, defaultLangFile);
                    } catch (IOException e) {
                        throw new MojoExecutionException("Could not copy file", e);
                    }
                }
            }
        }
    }

    /**
     * Reads all values from the given resource bundle and writes them to the XML Writer in a Android-compatible format.
     * 
     * @param bundle
     *            The bundle to read from.
     * @param writer
     *            The XML file to write to.
     * @param prefix
     *            Prefix for all string names in the Android XML file.
     */
    private void convert(ResourceBundle bundle, XMLWriter writer, String prefix) {
        writer.startElement("resources");

        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = bundle.getString(key);
            writer.startElement("string");
            writer.addAttribute("name", prefix + key);
            writer.writeText(value);
            writer.endElement();
        }

        writer.endElement();
    }
}
