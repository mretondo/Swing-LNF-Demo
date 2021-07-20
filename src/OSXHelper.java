import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Objects;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * OSXHelper class - Tools to facilitate setting Mac menu and dock Programmatically
 * but starts new process so it's hard to debug the app
 */
public class OSXHelper
{

    static protected final boolean IS_MAC = System.getProperty("os.name").toLowerCase().startsWith("mac os x");
    static protected final File THIS_JAR_FILE = getThisJarFile(new Object()
    {
    }.getClass().getEnclosingClass());

    /**
     * Try various methods to get the name and path of this jar file
     *
     * @param aclass The entry point class of the jar file.
     * @return A File object containing path information if successful otherwise null.
     */
    @SuppressWarnings("rawtypes")
    static private File getThisJarFile(final Class aclass)
    {
        File jarFile = null;
        CodeSource codeSource = null;
        String decoded = "";
        boolean success = false;

        try
        {
            try
            { //the easy way first
                codeSource = aclass.getProtectionDomain().getCodeSource();
                success = null != codeSource.getLocation();
            } catch (final Exception e)
            {
                success = false;
            }

            if (success)
            {
                try
                {
                    jarFile = new File(codeSource.getLocation().toURI());
                    success = jarFile.getPath().endsWith(".jar");
                } catch (final Exception e)
                {
                    jarFile = null;
                    success = false;
                }
            }

            if (!success)
            {
                String path = Objects.requireNonNull(aclass.getResource(aclass.getSimpleName() + ".class")).getPath();
                if (path.startsWith("/"))
                {
                    throw new Exception("This is not a jar file: \n" + path);
                }

                try
                { // to get full path if aclass.getResource().getPath() returned just the resource name
                    path = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(path)).getPath();
                    decoded = new URL(path).toURI().getPath(); //URLDecoder.decode(path, "UTF-8");
                    success = true;
                } catch (final Exception e)
                {
                    success = false;
                }

                if (!success)
                { // Maybe aclass.getResource().getPath() returned the full path initially
                    try
                    { // to decode it
                        decoded = new URL(path).toURI().getPath(); //URLDecoder.decode(path, "UTF-8");
                        success = true;
                    } catch (final Exception e)
                    {
                        success = false;
                    }
                }

                if (success)
                {
                    jarFile = new File(decoded.substring(decoded.indexOf('/'), decoded.lastIndexOf('!')));
                }
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
            jarFile = null;
        }

        return jarFile;
    }

    /**
     * Export a resource embedded in a Jar file to the local file path or
     * if a copy of the resource already exists; return the resource path
     * without overwriting the resource.
     *
     * @param aclass       The entry point class of the jar file.
     * @param resourceName ie.: "/resources/app.ico"
     * @return The path to the exported resource
     */
    @SuppressWarnings("rawtypes")
    static protected String exportResource(final Class aclass, String resourceName) throws Exception
    {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try
        {
            stream = aclass.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if (stream == null)
            {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }
            // Remove the resource path from resource name since we are only writing out the resource
            resourceName = resourceName.substring(resourceName.lastIndexOf('/'));
            // create the resource hidden by appending a period so the user doesn't see it created then deleted
            resourceName = resourceName.replace("/", "/.");

            jarFolder = THIS_JAR_FILE.getParentFile().getAbsolutePath().replace('\\', '/');
            File resource = new File(jarFolder + resourceName);

            if (!resource.exists())
            {
                int readBytes;
                byte[] buffer = new byte[1024 * 16];
                resStreamOut = new FileOutputStream(resource, false);
                while ((readBytes = stream.read(buffer)) > 0)
                {
                    resStreamOut.write(buffer, 0, readBytes);
                }
            }
        } finally
        {
            if (stream != null)
            {
                stream.close();
            }
            if (resStreamOut != null)
            {
                resStreamOut.close();
            }
        }
        return jarFolder + resourceName;
    }

    /**
     * Set the Mac about menu name and dock icon
     *
     * @param applicationArgs - any application specific arguments passed to Jar file
     * @param applicationName - the application name (should be no more than 16 characters long)
     * @param applicationIcon - the path to the application icon (or null for default java icon)
     *                        <dl>
     *                        <dt><strong>Comments:</strong>
     *                        <dd>Example Icon Path: "/jarDirectory/appIcon.png"
     *                        </dl>
     */
    static protected void setMacMenuAboutNameAndDockIcon(final String[] applicationArgs, final String applicationName, final String applicationIcon)
    {
        try
        {
            String iconPath = null;
            String[] launch = {""};
            boolean XdockSet = false;
            for (String arg : applicationArgs)
            {
                if (XdockSet = arg.equals("-XdockSet"))
                {
                    break;
                }
            }

            if (!XdockSet)
            {
                if (applicationIcon != null)
                {
                    iconPath = exportResource(new Object()
                    {
                    }.getClass().getEnclosingClass(), applicationIcon);
                }

                if (iconPath != null)
                {
                    launch = new String[]{
                            "java",
                            "-Xdock:name=" + applicationName,
                            "-Xdock:icon=" + iconPath,
                            "-jar", THIS_JAR_FILE.getAbsolutePath(),
                            "-XdockSet"};
                } else
                {
                    launch = new String[]{
                            "java",
                            "-Xdock:name=" + applicationName,
                            "-jar",
                            THIS_JAR_FILE.getAbsolutePath(),
                            "-XdockSet"};
                }
                String[] command = new String[launch.length + applicationArgs.length];
                System.arraycopy(launch, 0, command, 0, launch.length);
                System.arraycopy(applicationArgs, 0, command, launch.length, applicationArgs.length);
                Runtime.getRuntime().exec(command);
                Runtime.getRuntime().exit(0);
            } else
            {
                // XdocSet so remove temporary copy of icon.
                //
                // We land here after the Jar has been relaunched with our new command line arguments.
                // One would think that an attempt to delete the temporary icon should be safe here,
                // however the call to "Runtime.getRuntime().exec(commands)" above does not return
                // until this section of code (run in another process) is completed. Therefore the
                // temporary icon is deleted before it can be loaded.
                //
                // The call to invokeLater() allows the JVM to finish loading this
                // instance before the deletion of the temporary icon.
                invokeLater(() -> {
                    if (applicationIcon != null)
                    {
                        try
                        {
                            // exportResource checks to see if the resource (in this case the temporary icon) exists and will
                            // not overwrite it if present.  It will however return the path to the existing resource.
                            String iconPath1 = exportResource(new Object()
                            {
                            }.getClass().getEnclosingClass(), applicationIcon);
                            Thread.sleep(1000);
                            new File(iconPath1).delete();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
