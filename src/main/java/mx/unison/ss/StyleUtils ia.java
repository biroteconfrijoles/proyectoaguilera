package mx.unison;

import javax.swing.*;
import java.awt.*;

public class StyleUtils {
    // Colores Unison
    public static final Color UNISON_BLUE = new Color(0, 82, 158);
    public static final Color UNISON_BLUE_DARK = new Color(1, 82, 148);
    public static final Color UNISON_GOLD = new Color(248, 187, 0);
    public static final Color UNISON_GOLD_DARK = new Color(217, 158, 48);
    public static final Color DANGER = new Color(192, 57, 43);

    // Fuentes Segoe UI
    public static final Font F_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font F_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font F_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font F_NAV = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font F_SECTION = new Font("Segoe UI", Font.BOLD, 16);

    // --- Fábrica de Botones ---

    public static JButton primaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(UNISON_BLUE);
        b.setForeground(Color.WHITE);
        b.setFont(F_BOLD);
        b.setFocusPainted(false);
        return b;
    }

    public static JButton secondaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(Color.WHITE);
        b.setForeground(UNISON_BLUE);
        b.setFont(F_BOLD);
        b.setFocusPainted(false);
        return b;
    }

    public static JButton dangerBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(DANGER);
        b.setForeground(Color.WHITE);
        b.setFont(F_BOLD);
        b.setFocusPainted(false);
        return b;
    }

    public static JButton navBtn(String text) {
        JButton b = primaryBtn(text);
        b.setPreferredSize(new Dimension(120, 44));
        return b;
    }

    // --- Fábrica de Campos ---

    public static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(F_NORMAL);
        return f;
    }

    public static JPasswordField styledPass() {
        JPasswordField f = new JPasswordField();
        f.setFont(F_NORMAL);
        return f;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_LABEL);
        return l;
    }
}