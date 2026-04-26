//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package mx.unison;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StyleUtils – Colores, fuentes y fábrica de componentes Unison")
class StyleUtilsTest {
    @Test
    @DisplayName("UNISON_BLUE debe ser exactamente #00529E (R=0, G=82, B=158)")
    void color_unisonBlue_rgbCorrecto() {
        Color c = StyleUtils.UNISON_BLUE;
        Assertions.assertNotNull(c);
        Assertions.assertEquals(0, c.getRed(), "R de UNISON_BLUE debe ser 0");
        Assertions.assertEquals(82, c.getGreen(), "G de UNISON_BLUE debe ser 82");
        Assertions.assertEquals(158, c.getBlue(), "B de UNISON_BLUE debe ser 158");
    }

    @Test
    @DisplayName("UNISON_BLUE_DARK debe ser exactamente #015294 (R=1, G=82, B=148)")
    void color_unisonBlueDark_rgbCorrecto() {
        Color c = StyleUtils.UNISON_BLUE_DARK;
        Assertions.assertNotNull(c);
        Assertions.assertEquals(1, c.getRed());
        Assertions.assertEquals(82, c.getGreen());
        Assertions.assertEquals(148, c.getBlue());
    }

    @Test
    @DisplayName("UNISON_GOLD debe ser exactamente #F8BB00 (R=248, G=187, B=0)")
    void color_unisonGold_rgbCorrecto() {
        Color c = StyleUtils.UNISON_GOLD;
        Assertions.assertNotNull(c);
        Assertions.assertEquals(248, c.getRed(), "R de UNISON_GOLD debe ser 248");
        Assertions.assertEquals(187, c.getGreen(), "G de UNISON_GOLD debe ser 187");
        Assertions.assertEquals(0, c.getBlue(), "B de UNISON_GOLD debe ser 0");
    }

    @Test
    @DisplayName("UNISON_GOLD_DARK debe ser exactamente #D99E30 (R=217, G=158, B=48)")
    void color_unisonGoldDark_rgbCorrecto() {
        Color c = StyleUtils.UNISON_GOLD_DARK;
        Assertions.assertNotNull(c);
        Assertions.assertEquals(217, c.getRed());
        Assertions.assertEquals(158, c.getGreen());
        Assertions.assertEquals(48, c.getBlue());
    }

    @Test
    @DisplayName("DANGER debe ser rojo oscuro #C0392B (R=192, G=57, B=43)")
    void color_danger_rgbCorrecto() {
        Color c = StyleUtils.DANGER;
        Assertions.assertNotNull(c);
        Assertions.assertEquals(192, c.getRed());
        Assertions.assertEquals(57, c.getGreen());
        Assertions.assertEquals(43, c.getBlue());
    }

    @Test
    @DisplayName("Los colores Unison azul y dorado son visualmente distintos")
    void colores_unisonBlue_y_unisonGold_sonDistintos() {
        Assertions.assertNotEquals(StyleUtils.UNISON_BLUE, StyleUtils.UNISON_GOLD);
        Assertions.assertNotEquals(StyleUtils.UNISON_BLUE, StyleUtils.DANGER);
    }

    @Test
    @DisplayName("F_NORMAL debe ser Segoe UI, PLAIN, 13pt")
    void fuente_normal_segoeUiPlain13() {
        Font f = StyleUtils.F_NORMAL;
        Assertions.assertNotNull(f);
        Assertions.assertEquals("Segoe UI", f.getName(), "La fuente debe solicitarse como Segoe UI");
        Assertions.assertEquals(0, f.getStyle());
        Assertions.assertEquals(13, f.getSize());
    }

    @Test
    @DisplayName("F_BOLD debe ser Segoe UI, BOLD, 13pt")
    void fuente_bold_segoeUiBold13() {
        Font f = StyleUtils.F_BOLD;
        Assertions.assertNotNull(f);
        Assertions.assertEquals("Segoe UI", f.getName(), "La fuente debe solicitarse como Segoe UI");
        Assertions.assertEquals(1, f.getStyle());
        Assertions.assertEquals(13, f.getSize());
    }

    @Test
    @DisplayName("F_TITLE debe ser Segoe UI, BOLD, 24pt")
    void fuente_title_segoeUiBold24() {
        Font f = StyleUtils.F_TITLE;
        Assertions.assertNotNull(f);
        Assertions.assertEquals("Segoe UI", f.getName(), "La fuente debe solicitarse como Segoe UI");
        Assertions.assertEquals(1, f.getStyle());
        Assertions.assertEquals(24, f.getSize());
    }

    @Test
    @DisplayName("F_LABEL debe ser Segoe UI, PLAIN, 12pt (la más pequeña)")
    void fuente_label_segoeUiPlain12() {
        Font f = StyleUtils.F_LABEL;
        Assertions.assertNotNull(f);
        Assertions.assertEquals("Segoe UI", f.getName(), "La fuente debe solicitarse como Segoe UI");
        Assertions.assertEquals(0, f.getStyle());
        Assertions.assertEquals(12, f.getSize());
    }

    @Test
    @DisplayName("F_TITLE es más grande que F_NORMAL")
    void fuente_title_mayorQueNormal() {
        Assertions.assertTrue(StyleUtils.F_TITLE.getSize() > StyleUtils.F_NORMAL.getSize());
    }

    @Test
    @DisplayName("Ninguna constante de fuente es null")
    void fuentes_ningunaNula() {
        Assertions.assertNotNull(StyleUtils.F_NORMAL);
        Assertions.assertNotNull(StyleUtils.F_BOLD);
        Assertions.assertNotNull(StyleUtils.F_TITLE);
        Assertions.assertNotNull(StyleUtils.F_SUBTITLE);
        Assertions.assertNotNull(StyleUtils.F_LABEL);
        Assertions.assertNotNull(StyleUtils.F_NAV);
        Assertions.assertNotNull(StyleUtils.F_SECTION);
    }

    @Test
    @DisplayName("primaryBtn() no retorna null y tiene el texto correcto")
    void primaryBtn_noNullYTextoCorrect() {
        JButton b = StyleUtils.primaryBtn("Guardar");
        Assertions.assertNotNull(b);
        Assertions.assertEquals("Guardar", b.getText());
    }

    @Test
    @DisplayName("primaryBtn() tiene fondo azul Unison")
    void primaryBtn_fondoAzulUnison() {
        JButton b = StyleUtils.primaryBtn("Test");
        Assertions.assertEquals(StyleUtils.UNISON_BLUE, b.getBackground(), "El botón primario debe tener fondo UNISON_BLUE");
    }

    @Test
    @DisplayName("primaryBtn() tiene texto blanco (contraste sobre azul)")
    void primaryBtn_textoBlanco() {
        JButton b = StyleUtils.primaryBtn("Test");
        Assertions.assertEquals(Color.WHITE, b.getForeground());
    }

    @Test
    @DisplayName("primaryBtn() usa fuente Segoe UI Bold")
    void primaryBtn_fuenteSegoeUiBold() {
        JButton b = StyleUtils.primaryBtn("Test");
        Assertions.assertEquals("Segoe UI", b.getFont().getName(), "El botón debe solicitar Segoe UI");
        Assertions.assertTrue(b.getFont().isBold());
    }

    @Test
    @DisplayName("secondaryBtn() tiene fondo blanco y texto azul Unison")
    void secondaryBtn_fondoBlancoTextoAzul() {
        JButton b = StyleUtils.secondaryBtn("Cancelar");
        Assertions.assertNotNull(b);
        Assertions.assertEquals(Color.WHITE, b.getBackground());
        Assertions.assertEquals(StyleUtils.UNISON_BLUE, b.getForeground());
    }

    @Test
    @DisplayName("dangerBtn() tiene fondo rojo DANGER")
    void dangerBtn_fondoRojo() {
        JButton b = StyleUtils.dangerBtn("Eliminar");
        Assertions.assertNotNull(b);
        Assertions.assertEquals(StyleUtils.DANGER, b.getBackground());
        Assertions.assertEquals(Color.WHITE, b.getForeground());
    }

    @Test
    @DisplayName("navBtn() tiene fondo azul y dimensión de 120x44")
    void navBtn_fondoAzulYDimension() {
        JButton b = StyleUtils.navBtn("Inicio");
        Assertions.assertNotNull(b);
        Assertions.assertEquals(StyleUtils.UNISON_BLUE, b.getBackground());
        Assertions.assertEquals(120, b.getPreferredSize().width);
        Assertions.assertEquals(44, b.getPreferredSize().height);
    }

    @Test
    @DisplayName("Los botones primario, secundario y peligro tienen textos distintos si se pide")
    void botonesDistintos_seCreanIndependientemente() {
        JButton b1 = StyleUtils.primaryBtn("Uno");
        JButton b2 = StyleUtils.secondaryBtn("Dos");
        JButton b3 = StyleUtils.dangerBtn("Tres");
        Assertions.assertEquals("Uno", b1.getText());
        Assertions.assertEquals("Dos", b2.getText());
        Assertions.assertEquals("Tres", b3.getText());
    }

    @Test
    @DisplayName("primaryBtn() con texto vacío crea el botón sin error")
    void primaryBtn_textoVacio_noFalla() {
        Assertions.assertDoesNotThrow(() -> StyleUtils.primaryBtn(""));
    }

    @Test
    @DisplayName("styledField() retorna un JTextField no nulo")
    void styledField_noNulo() {
        JTextField tf = StyleUtils.styledField();
        Assertions.assertNotNull(tf);
    }

    @Test
    @DisplayName("styledField() usa fuente Segoe UI")
    void styledField_fuenteSegoeUI() {
        JTextField tf = StyleUtils.styledField();
        Assertions.assertEquals("Segoe UI", tf.getFont().getName(), "El campo debe solicitar Segoe UI");
    }

    @Test
    @DisplayName("styledPass() retorna un JPasswordField no nulo")
    void styledPass_noNulo() {
        JPasswordField pf = StyleUtils.styledPass();
        Assertions.assertNotNull(pf);
    }

    @Test
    @DisplayName("styledPass() usa fuente Segoe UI")
    void styledPass_fuenteSegoeUI() {
        JPasswordField pf = StyleUtils.styledPass();
        Assertions.assertEquals("Segoe UI", pf.getFont().getName(), "El campo de contraseña debe solicitar Segoe UI");
    }

    @Test
    @DisplayName("Cada llamada a styledField() crea una instancia nueva")
    void styledField_cadaLlamadaCreaNuevaInstancia() {
        JTextField tf1 = StyleUtils.styledField();
        JTextField tf2 = StyleUtils.styledField();
        Assertions.assertNotSame(tf1, tf2, "Cada llamada debe retornar un objeto nuevo");
    }

    @Test
    @DisplayName("label() retorna un JLabel no nulo con el texto correcto")
    void label_noNuloYTextoCorrect() {
        JLabel l = StyleUtils.label("Mi Etiqueta");
        Assertions.assertNotNull(l);
        Assertions.assertEquals("Mi Etiqueta", l.getText());
    }

    @Test
    @DisplayName("label() usa fuente Segoe UI PLAIN 12pt")
    void label_fuenteSegoeUi12() {
        JLabel l = StyleUtils.label("X");
        Assertions.assertEquals("Segoe UI", l.getFont().getName(), "La etiqueta debe solicitar Segoe UI");
        Assertions.assertEquals(12, l.getFont().getSize());
    }
}
