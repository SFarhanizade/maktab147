package ir.maktabsharif.files.todo.ui;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public final class Theme {
    private Theme() {
    }

    public static final Color BG_TOP = new Color(0x9B7BFF);
    public static final Color BG_BOTTOM = new Color(0xC4A9FF);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color PRIMARY = new Color(0x6C47FF);
    public static final Color PRIMARY_HOVER = new Color(0x5732E5);
    public static final Color DANGER = new Color(0xFF4D4D);
    public static final Color DANGER_SOFT = new Color(0xFFEBEB);
    public static final Color WARNING = new Color(0xFFA726);
    public static final Color SUCCESS = new Color(0x4CAF50);
    public static final Color DRAFT_CLR = new Color(0x9E9E9E);

    public static final Color TEXT_DARK = new Color(0x2D264B);
    public static final Color TEXT_MUTED = new Color(0x8A88A3);
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;

    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_HOVER = new Color(0xF3F0FF);
    public static final Color ZEBRA = new Color(0xFAF9FF);
    public static final Color BORDER_SOFT = new Color(0xEFEBFF);
    public static final Color SCROLL_TRACK = new Color(0xF3F0FF);
    public static final Color SCROLL_THUMB = new Color(0xC9BFFF);
    public static final Color GLOW = new Color(255, 255, 255, 60);

    public static final Color OUTER_FILL = new Color(0xEDE9FF);

    public static final Font FONT_BASE = new Font("Quicksand", Font.PLAIN, 13);
    public static final Font FONT_LABEL = new Font("Quicksand", Font.BOLD, 13);
    public static final Font FONT_H1 = new Font("Quicksand", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("Quicksand", Font.BOLD, 18);
    public static final Font FONT_BUTTON = new Font("Quicksand", Font.BOLD, 13);
    public static final Font FONT_BADGE = new Font("Quicksand", Font.BOLD, 11);
    public static final Font FONT_TABLE = new Font("Quicksand", Font.PLAIN, 14);
    public static final Font FONT_TABLE_HEADER = new Font("Quicksand", Font.BOLD, 12);
    public static final Font FONT_BRAND = new Font("Quicksand", Font.BOLD, 26);
    public static final Font FONT_NAV = new Font("Quicksand", Font.BOLD, 14);
    public static final Font FONT_INPUT = new Font("Quicksand", Font.PLAIN, 14);
    public static final Font FONT_ICON_LG = new Font("SansSerif", Font.PLAIN, 48);
    public static final Font FONT_ICON_SM = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Quicksand", Font.PLAIN, 12);
    public static final Font FONT_TITLE_BAR = new Font("Quicksand", Font.BOLD, 14);

    public static void install() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {
        }

        UIManager.put("Component.arc", 14);
        UIManager.put("Component.focusColor", PRIMARY);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.borderColor", BORDER_SOFT);

        UIManager.put("TextComponent.arc", 14);
        UIManager.put("TextField.arc", 14);
        UIManager.put("TextField.margin", new Insets(8, 12, 8, 12));
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("TextField.focusedBackground", SURFACE);
        UIManager.put("TextArea.margin", new Insets(8, 12, 8, 12));

        UIManager.put("ComboBox.arc", 14);
        UIManager.put("ComboBox.padding", new Insets(4, 10, 4, 10));
        UIManager.put("ComboBox.buttonStyle", "none");
        UIManager.put("ComboBox.background", SURFACE);

        UIManager.put("Button.arc", 14);
        UIManager.put("Button.margin", new Insets(8, 18, 8, 18));

        UIManager.put("ScrollBar.width", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.track", SCROLL_TRACK);
        UIManager.put("ScrollBar.thumb", SCROLL_THUMB);
        UIManager.put("ScrollBar.hoverThumbColor", PRIMARY);

        UIManager.put("Table.arc", 0);
        UIManager.put("Table.selectionBackground", PRIMARY);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", BORDER_SOFT);
        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);

        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("Panel.background", SURFACE);
        UIManager.put("OptionPane.messageFont", FONT_BASE);
        UIManager.put("OptionPane.buttonFont", FONT_LABEL);

        UIManager.put("defaultFont", FONT_BASE);
    }
}