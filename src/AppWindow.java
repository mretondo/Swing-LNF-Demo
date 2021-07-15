import sun.awt.OSInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * OSXHelper class demo.
 */
public class AppWindow extends JFrame {

    /**
     * Create the application.
     */
    public AppWindow(String[] args) {
        initialize(args);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(String[] args) {
        try {
            if (Main.isMacOS) {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("showAboutDialog", (Class<?>[]) null));
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("exit", (Class<?>[]) null));
            }
        } catch (NoSuchMethodException e) {
            System.exit(1);
        }

        setTitle(Main.APPLICATION_NAME);
        setIconImage(Toolkit.getDefaultToolkit().getImage(AppWindow.class.getResource(Main.APPLICATION_ICON)));
        setBounds(100, 100, 450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

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
        btnDoSomething.setPreferredSize(new Dimension(40, btnDoSomething.getPreferredSize().height));
        btnDoSomething.setEnabled(true);
        btnDoSomething.addActionListener(event -> {handleDoSomething();});

        getContentPane().add(btnDoSomething, BorderLayout.SOUTH);

//        frame.pack();

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
        JOptionPane.showMessageDialog(this, Main.APPLICATION_NAME, "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
