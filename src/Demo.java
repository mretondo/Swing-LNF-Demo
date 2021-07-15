import sun.awt.OSInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Objects;

/**
 * OSXHelper class demo.
 */
public class Demo {

    private static final String APPLICATION_NAME = "LNF Demo";
    private static final String APPLICATION_ICON = "/resources/app-icon.png";

    static boolean isMacOS;      //osName.contains("os x");
    static boolean isWindows;    //osName.contains("windows");
    static boolean isLinux;      //osName.contains("linux");

    private JFrame frame;

    /**
     * Create the application.
     */
    public Demo(String[] args) {
        initialize(args);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        if (OSXHelper.IS_MAC) {
            // These calls must come before any AWT or Swing code is called,
            // otherwise the Mac menu bar will use the class name as the application name.
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", APPLICATION_NAME);
            System.setProperty("apple.awt.application.name", APPLICATION_NAME);
//            OSXHelper.setMacMenuAboutNameAndDockIcon(args, APPLICATION_NAME, APPLICATION_ICON);
        }

        // Check args passed into application
        for (int i = 0; i < args.length; i++) {
        }

        EventQueue.invokeLater(() -> {
            try {
                Demo window = new Demo(args);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(String[] args) {
        try {
//            String osName = System.getProperty("os.name").toLowerCase();
            OSInfo.OSType osType = OSInfo.getOSType();

            isMacOS     = osType == OSInfo.OSType.MACOSX;
            isWindows   = osType == OSInfo.OSType.WINDOWS;
            isLinux     = osType == OSInfo.OSType.LINUX;

            if (isMacOS) {
                // This code needs to run before any other look and feel code
//                System.setProperty("apple.laf.useScreenMenuBar", "true");
//                System.setProperty("com.apple.mrj.application.apple.menu.about.name", APPLICATION_NAME);
//                System.setProperty("apple.awt.application.name", APPLICATION_NAME);
                //
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("showAboutDialog", (Class<?>[]) null));
                OSXAdapter.setQuitHandler (this, getClass().getDeclaredMethod("exit", (Class<?>[]) null));
//                OSXHelper.setMacMenuAboutNameAndDockIcon(null, APPLICATION_NAME, APPLICATION_ICON);
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else if (isWindows) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else if (!isLinux) {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                UIManager.put("swing.boldMetal", Boolean.FALSE);
            } else {
                // default LookAndFeel
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame();
        frame.setTitle(APPLICATION_NAME);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Demo.class.getResource(APPLICATION_ICON)));
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mFile = new JMenu("File");
        mFile.setMnemonic('F');
        menuBar.add(mFile);

        JMenuItem mItemDoSomething = new JMenuItem("Do Something");
        mItemDoSomething.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mItemDoSomething.addActionListener(event -> {handleDoSomething();});
        mItemDoSomething.setEnabled(true);

        mFile.add(mItemDoSomething);

        JMenu mHelp = new JMenu("Help");
        mHelp.setMnemonic('H');
        menuBar.add(mHelp);

        JButton btnDoSomething = new JButton("Do Something");
        btnDoSomething.setEnabled(true);
        btnDoSomething.addActionListener(event -> {handleDoSomething();});

        frame.getContentPane().add(btnDoSomething, BorderLayout.SOUTH);

        // Window & Linux Menu Items
        if (!OSXHelper.IS_MAC) {
            JMenuItem mItemExit = new JMenuItem("Exit");
            mItemExit.addActionListener(e -> exit());
            mItemExit.setMnemonic('E');
            mFile.add(mItemExit);

            JMenuItem mItemAbout = new JMenuItem("About");
            mItemAbout.setMnemonic('A');
            mItemAbout.addActionListener(event -> showAboutDialog());
            mHelp.add(mItemAbout);
        }
    }

    private void handleDoSomething() {
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Close the application
     *
     * @return always true
     */
    protected boolean exit() {
//        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        return true;
    }

    /**
     * Display the about dialog
     */
    protected void showAboutDialog() {
        JOptionPane.showMessageDialog(frame, APPLICATION_NAME, "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
