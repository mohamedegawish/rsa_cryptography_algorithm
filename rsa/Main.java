package rsa;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main extends JFrame {

    private static final Color COL_BACKGROUND = new Color(26, 27, 38);
    private static final Color COL_SECONDARY = new Color(36, 40, 59);
    private static final Color COL_ACCENT = new Color(187, 154, 247);
    private static final Color COL_TEXT = new Color(192, 202, 245);
    private static final Color COL_BTN_TEXT = new Color(26, 27, 38);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 14);

    private JTextArea publicKeyArea;
    private JTextArea privateKeyArea;
    private JButton generateKeysButton;
    private JTextArea encryptionInputArea;
    private JTextArea encryptionOutputArea;
    private JButton encryptButton;
    private JTextArea decryptionInputArea;
    private JTextArea decryptionOutputArea;
    private JButton decryptButton;

    private RSAKeyPairGenerator keyPairGenerator;
    private RSAEncryptor encryptor;
    private RSADecryptor decryptor;
    private RSAPublicKey currentPublicKey;
    private RSAPrivateKey currentPrivateKey;

    public Main() {
        keyPairGenerator = new RSAKeyPairGenerator();
        encryptor = new RSAEncryptor();
        decryptor = new RSADecryptor();

        setTitle("RSA Professional Tool");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COL_BACKGROUND);
        setLayout(new BorderLayout());

        UIManager.put("Label.foreground", COL_TEXT);
        UIManager.put("Panel.background", COL_BACKGROUND);
        UIManager.put("TextArea.background", COL_SECONDARY);
        UIManager.put("TextArea.foreground", COL_TEXT);
        UIManager.put("TextArea.caretForeground", COL_TEXT);

        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        headerPanel.setBackground(COL_BACKGROUND);
        JLabel titleLabel = new JLabel("RSA Encryption & Decryption");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COL_ACCENT);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_NORMAL);
        tabbedPane.setBackground(COL_BACKGROUND);
        tabbedPane.setForeground(COL_BACKGROUND);

        JPanel keyPanel = createTabPanel();

        JPanel keyCtrlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        keyCtrlPanel.setBackground(COL_BACKGROUND);
        generateKeysButton = createStyledButton("Generate New Key Pair (2048-bit)");
        generateKeysButton.addActionListener(e -> generateKeys());
        keyCtrlPanel.add(generateKeysButton);
        keyPanel.add(keyCtrlPanel, BorderLayout.NORTH);

        JPanel keyDisplayPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        keyDisplayPanel.setBackground(COL_BACKGROUND);
        publicKeyArea = createStyledTextArea("Public Key (e, n)", keyDisplayPanel);
        privateKeyArea = createStyledTextArea("Private Key (d, n)", keyDisplayPanel);
        keyPanel.add(keyDisplayPanel, BorderLayout.CENTER);

        tabbedPane.addTab("  Key Generation  ", keyPanel);

        JPanel encryptPanel = createTabPanel();

        encryptionInputArea = createStyledTextArea("Step 1: Message to Encrypt", encryptPanel, BorderLayout.NORTH, 150);

        JPanel encryptCenterPanel = new JPanel(new BorderLayout(10, 10));
        encryptCenterPanel.setBackground(COL_BACKGROUND);

        JPanel encBtnParams = new JPanel(new FlowLayout(FlowLayout.CENTER));
        encBtnParams.setBackground(COL_BACKGROUND);
        encryptButton = createStyledButton("Encrypt with Public Key");
        encryptButton.addActionListener(e -> encryptMessage());
        encBtnParams.add(encryptButton);
        encryptCenterPanel.add(encBtnParams, BorderLayout.NORTH);

        encryptionOutputArea = createStyledTextArea("Step 2: Encrypted Output (Base64)", null);
        encryptionOutputArea.setEditable(false);
        encryptCenterPanel.add(new JScrollPane(encryptionOutputArea), BorderLayout.CENTER);

        encryptPanel.add(encryptCenterPanel, BorderLayout.CENTER);

        tabbedPane.addTab("  Encryption  ", encryptPanel);

        JPanel decryptPanel = createTabPanel();

        decryptionInputArea = createStyledTextArea("Step 1: Encrypted Message (Base64) to Decrypt", decryptPanel,
                BorderLayout.NORTH, 150);

        JPanel decryptCenterPanel = new JPanel(new BorderLayout(10, 10));
        decryptCenterPanel.setBackground(COL_BACKGROUND);

        JPanel decBtnParams = new JPanel(new FlowLayout(FlowLayout.CENTER));
        decBtnParams.setBackground(COL_BACKGROUND);
        decryptButton = createStyledButton("Decrypt with Private Key");
        decryptButton.addActionListener(e -> decryptMessage());
        decBtnParams.add(decryptButton);
        decryptCenterPanel.add(decBtnParams, BorderLayout.NORTH);

        decryptionOutputArea = createStyledTextArea("Step 2: Decrypted Plain Text", null);
        decryptionOutputArea.setEditable(false);
        decryptCenterPanel.add(new JScrollPane(decryptionOutputArea), BorderLayout.CENTER);

        decryptPanel.add(decryptCenterPanel, BorderLayout.CENTER);

        tabbedPane.addTab("  Decryption  ", decryptPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void generateKeys() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                generateKeysButton.setEnabled(false);
                generateKeysButton.setText("Generating... (Please wait)");
                keyPairGenerator.generateKeyPair(2048);
                return null;
            }

            @Override
            protected void done() {
                try {
                    currentPublicKey = keyPairGenerator.getPublicKey();
                    currentPrivateKey = keyPairGenerator.getPrivateKey();
                    publicKeyArea.setText(currentPublicKey.toString());
                    privateKeyArea.setText(currentPrivateKey.toString());
                    JOptionPane.showMessageDialog(Main.this, "Keys generated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                } finally {
                    generateKeysButton.setEnabled(true);
                    generateKeysButton.setText("Generate New Key Pair (2048-bit)");
                }
            }
        };
        worker.execute();
    }

    private void encryptMessage() {
        if (currentPublicKey == null) {
            showError("Generate keys first!");
            return;
        }
        String msg = encryptionInputArea.getText();
        if (msg.isEmpty())
            return;
        try {
            encryptionOutputArea.setText(encryptor.encrypt(msg, currentPublicKey));
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void decryptMessage() {
        if (currentPrivateKey == null) {
            showError("Generate keys first!");
            return;
        }
        String msg = decryptionInputArea.getText();
        if (msg.isEmpty())
            return;
        try {
            decryptionOutputArea.setText(decryptor.decrypt(msg, currentPrivateKey));
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createTabPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COL_BACKGROUND);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        return p;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(COL_ACCENT.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(COL_ACCENT.brighter());
                } else {
                    g2.setColor(COL_ACCENT);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COL_BTN_TEXT);
                g2.setFont(FONT_NORMAL);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(FONT_NORMAL);
        btn.setForeground(COL_BTN_TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(280, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTextArea createStyledTextArea(String title, JComponent parent) {
        return createStyledTextArea(title, parent, BorderLayout.CENTER, 0);
    }

    private JTextArea createStyledTextArea(String title, JComponent parent, String layoutConstraints, int height) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COL_BACKGROUND);

        JLabel label = new JLabel(title);
        label.setFont(FONT_NORMAL);
        label.setForeground(COL_TEXT);
        panel.add(label, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setFont(FONT_MONO);
        area.setBackground(COL_SECONDARY);
        area.setForeground(COL_TEXT);
        area.setCaretColor(COL_TEXT);
        area.setLineWrap(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(COL_SECONDARY.darker()));
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = COL_ACCENT;
                this.trackColor = COL_BACKGROUND;
            }
        });

        if (height > 0) {
            scroll.setPreferredSize(new Dimension(100, height));
        }

        panel.add(scroll, BorderLayout.CENTER);

        if (parent != null) {
            parent.add(panel, layoutConstraints);
        }
        return area;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
