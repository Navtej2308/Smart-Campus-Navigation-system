package smartcampus.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.swing.BrowserView;

public class CampusTour extends JFrame {

    public CampusTour() {
        setTitle("Campus Tour");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setLayout(new BorderLayout());

        // 1. Setup JxBrowser engine
        EngineOptions options = EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                .licenseKey("3GC4U6A49ZWA20GGGEJJK76GB0XTOLEX3MNNPT013B7A86QWP8071GPQEJYAT3R848GX7INI1BZRT1N59J2198O8FXEUIQBXT46INYCS4WRBG5RMJXG574FOVC2I6ZE5LBPAW9LJZUWWYVQJ")
                .build();

        Engine engine = Engine.newInstance(options);
        Browser browser = engine.newBrowser();

        // 2. Build path to the HTML file
        String projectDir = System.getProperty("user.dir");
        String htmlFilePath = projectDir + "/src/main/resources/public/CampusTour.html";
        File htmlFile = new File(htmlFilePath);

        if (htmlFile.exists()) {
            String url = htmlFile.toURI().toString();
            System.out.println("✅ Loading HTML from: " + url);
            browser.navigation().loadUrl(url);
        } else {
            System.err.println("❌ HTML file not found at: " + htmlFilePath);
            JOptionPane.showMessageDialog(this, "HTML file not found at:\n" + htmlFilePath,
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
        }

        // 3. Show browser
        BrowserView browserView = BrowserView.newInstance(browser);
        add(browserView, BorderLayout.CENTER);

        // 4. Create and add login button at bottom
        JButton btnLogin = new JButton("🔙 Back to Login");
        btnLogin.setBackground(new Color(255, 204, 153)); // light orange
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 245, 220)); // beige
        bottomPanel.add(btnLogin);
        add(bottomPanel, BorderLayout.SOUTH);

        // 5. Button action
        btnLogin.addActionListener(e -> {
            dispose(); // Close campus tour
            new LoginPage(); // Return to login
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CampusTour());
    }
}
