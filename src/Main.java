import com.apple.eawt.Application;
import com.apple.eawt.QuitStrategy;
import sun.awt.OSInfo;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.stream.Collectors;

public class Main {
    static final String APPLICATION_NAME = "LNF Demo";
    static final String APPLICATION_ICON = "resources/images/app-icon.png";

    static boolean isMacOS;      //osName.contains("os x");
    static boolean isWindows;    //osName.contains("windows");
    static boolean isLinux;      //osName.contains("linux");

    public static void setUIFont (javax.swing.plaf.FontUIResource fontUIResource) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);

            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, fontUIResource);
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        OSInfo.OSType osType = OSInfo.getOSType();

        isMacOS     = osType == OSInfo.OSType.MACOSX;
        isWindows   = osType == OSInfo.OSType.WINDOWS;
        isLinux     = osType == OSInfo.OSType.LINUX;

        isMacOS     = false;//osType == OSInfo.OSType.MACOSX;
        isWindows   = false;//osType == OSInfo.OSType.WINDOWS;
        isLinux     = true;//osType == OSInfo.OSType.LINUX;

        try {
            if (isMacOS) {
                // These calls must come before any AWT or Swing code is called,
                // otherwise the Mac menu bar will use the class name as the application name.
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", APPLICATION_NAME);
                System.setProperty("apple.awt.application.name", APPLICATION_NAME);

                // quits app the same way as if you closed main window i.e. it
                // does a dispose() on the window instead of a System.exit()
                Application.getApplication().setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);

                // loading an image from a file
                final URL imageResource = Main.class.getClassLoader().getResource(APPLICATION_ICON);
                final Image image = Toolkit.getDefaultToolkit().getImage(imageResource);
                Application.getApplication().setDockIconImage(image);

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else if (isWindows) {
                // loading an image from a file
                final URL imageResource = Main.class.getClassLoader().getResource(APPLICATION_ICON);
                final Image image = Toolkit.getDefaultToolkit().getImage(imageResource);
                Application.getApplication().setDockIconImage(image);

                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else if (isLinux) {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                UIManager.put("swing.boldMetal", Boolean.FALSE);
            } else {
                // default LookAndFeel
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }

            // use same font on all platforms
            setUIFont(new javax.swing.plaf.FontUIResource("System Font", Font.PLAIN, 12));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check args passed into application
        for (int i = 0; i < args.length; i++) {
        }

        EventQueue.invokeLater(() -> {
            try {
                AppWindow window = new AppWindow(args);
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
