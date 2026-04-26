package mx.unison;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class MainApp extends JFrame {
    private CardLayout mainLayout;
    private JPanel cardsPanel;
    private Database db;
    private Usuario currentUsuario;

    // Paneles de la aplicación y navegación interna
    private CardLayout appLayout;
    private JPanel appCards;

    // Modelos de tablas y controles CRUD (para aplicar permisos)
    private DefaultTableModel modelProductos;
    private JTable tableProductos;
    private JPanel crudProdPanel;

    private DefaultTableModel modelAlmacenes;
    private JTable tableAlmacenes;
    private JPanel crudAlmPanel;

    public MainApp() {
        db = new Database("jdbc:sqlite:InventarioBD.db");

        setTitle("Sistema Básico de Inventario - Universidad de Sonora");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainLayout = new CardLayout();
        cardsPanel = new JPanel(mainLayout);

        cardsPanel.add(createLoginPanel(), "LOGIN");
        cardsPanel.add(createAppContainer(), "APP");

        add(cardsPanel);
        mainLayout.show(cardsPanel, "LOGIN");
    }

    // ────────────────────────────────────────────────────────────────────────
    // 1. PANEL DE LOGIN
    // ────────────────────────────────────────────────────────────────────────
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblLogo = new JLabel("UNISON");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblLogo.setForeground(StyleUtils.UNISON_GOLD);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblLogo, gbc);

        JLabel lblTitle = new JLabel("Acceso al Sistema");
        lblTitle.setFont(StyleUtils.F_TITLE);
        lblTitle.setForeground(StyleUtils.UNISON_BLUE);
        gbc.gridy = 1;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1; gbc.gridy = 2;
        panel.add(StyleUtils.label("Usuario:"), gbc);
        JTextField txtUser = StyleUtils.styledField();
        txtUser.setPreferredSize(new Dimension(220, 35));
        gbc.gridx = 1;
        panel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(StyleUtils.label("Contraseña:"), gbc);
        JPasswordField txtPass = StyleUtils.styledPass();
        txtPass.setPreferredSize(new Dimension(220, 35));
        gbc.gridx = 1;
        panel.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton btnLogin = StyleUtils.primaryBtn("Iniciar Sesión");
        btnLogin.setPreferredSize(new Dimension(200, 40));
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String passStr = new String(txtPass.getPassword());
            Usuario u = db.authenticate(txtUser.getText(), passStr);
            if (u != null) {
                currentUsuario = u;
                txtUser.setText("");
                txtPass.setText("");
                applyRolePermissions();
                refreshData();
                mainLayout.show(cardsPanel, "APP");
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ────────────────────────────────────────────────────────────────────────
    // 2. CONTENEDOR PRINCIPAL Y NAVBAR
    // ────────────────────────────────────────────────────────────────────────
    private JPanel createAppContainer() {
        JPanel container = new JPanel(new BorderLayout());

        // NavBar Superior
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        navBar.setBackground(StyleUtils.UNISON_BLUE_DARK);

        JButton btnInicio = StyleUtils.navBtn("Inicio");
        JButton btnProd = StyleUtils.navBtn("Productos");
        JButton btnAlm = StyleUtils.navBtn("Almacenes");
        JButton btnExit = StyleUtils.dangerBtn("Cerrar Sesión");

        navBar.add(btnInicio);
        navBar.add(btnProd);
        navBar.add(btnAlm);
        navBar.add(btnExit);

        container.add(navBar, BorderLayout.NORTH);

        // Contenedor de Vistas
        appLayout = new CardLayout();
        appCards = new JPanel(appLayout);

        appCards.add(createInicioPanel(), "INICIO");
        appCards.add(createProductosPanel(), "PRODUCTOS");
        appCards.add(createAlmacenesPanel(), "ALMACENES");

        container.add(appCards, BorderLayout.CENTER);

        // Navegación
        btnInicio.addActionListener(e -> appLayout.show(appCards, "INICIO"));
        btnProd.addActionListener(e -> appLayout.show(appCards, "PRODUCTOS"));
        btnAlm.addActionListener(e -> appLayout.show(appCards, "ALMACENES"));
        btnExit.addActionListener(e -> mainLayout.show(cardsPanel, "LOGIN"));

        return container;
    }

    private JPanel createInicioPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;

        JLabel title = new JLabel("Bienvenido al Sistema de Inventario");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(StyleUtils.UNISON_BLUE);
        p.add(title, gbc);

        return p;
    }

    // ────────────────────────────────────────────────────────────────────────
    // 3. VISTA DE PRODUCTOS (Filtros, Tabla, Precarga y CRUD)
    // ────────────────────────────────────────────────────────────────────────
    private JPanel createProductosPanel() {
        JPanel p = new JPanel(new BorderLayout());

        // Filtro Superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Búsqueda Avanzada (Todas las columnas)"));
        JTextField txtSearch = StyleUtils.styledField();
        txtSearch.setPreferredSize(new Dimension(300, 30));
        topPanel.add(StyleUtils.label("Filtrar:"));
        topPanel.add(txtSearch);
        p.add(topPanel, BorderLayout.NORTH);

        // Tabla
        modelProductos = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción", "Cant", "Precio", "Almacén", "Modificado Por"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableProductos = new JTable(modelProductos);
        tableProductos.setRowHeight(25);
        tableProductos.setFont(StyleUtils.F_NORMAL);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelProductos);
        tableProductos.setRowSorter(sorter);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtSearch.getText()));
            }
        });
        p.add(new JScrollPane(tableProductos), BorderLayout.CENTER);

        // CRUD Inferior
        crudProdPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        crudProdPanel.setBackground(new Color(245, 245, 245));

        JTextField fNom = StyleUtils.styledField(); fNom.setPreferredSize(new Dimension(100, 30));
        JTextField fDesc = StyleUtils.styledField(); fDesc.setPreferredSize(new Dimension(120, 30));
        JTextField fCant = StyleUtils.styledField(); fCant.setPreferredSize(new Dimension(50, 30));
        JTextField fPrec = StyleUtils.styledField(); fPrec.setPreferredSize(new Dimension(60, 30));
        JTextField fAlmId = StyleUtils.styledField(); fAlmId.setPreferredSize(new Dimension(50, 30));

        crudProdPanel.add(StyleUtils.label("Nombre:")); crudProdPanel.add(fNom);
        crudProdPanel.add(StyleUtils.label("Desc:")); crudProdPanel.add(fDesc);
        crudProdPanel.add(StyleUtils.label("Cant:")); crudProdPanel.add(fCant);
        crudProdPanel.add(StyleUtils.label("Precio:")); crudProdPanel.add(fPrec);
        crudProdPanel.add(StyleUtils.label("ID Alm:")); crudProdPanel.add(fAlmId);

        JButton btnAdd = StyleUtils.primaryBtn("Agregar");
        JButton btnDel = StyleUtils.dangerBtn("Eliminar");
        crudProdPanel.add(btnAdd); crudProdPanel.add(btnDel);

        // Lógica de Precarga al seleccionar una fila
        tableProductos.getSelectionModel().addListSelectionListener(e -> {
            int row = tableProductos.getSelectedRow();
            if (row != -1) {
                // Convertir índice de vista a modelo por si hay filtro activo
                int modelRow = tableProductos.convertRowIndexToModel(row);
                fNom.setText(modelProductos.getValueAt(modelRow, 1).toString());
                fDesc.setText(modelProductos.getValueAt(modelRow, 2).toString());
                fCant.setText(modelProductos.getValueAt(modelRow, 3).toString());
                fPrec.setText(modelProductos.getValueAt(modelRow, 4).toString());
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                Producto prod = new Producto();
                prod.nombre = fNom.getText();
                prod.descripcion = fDesc.getText();
                prod.cantidad = Integer.parseInt(fCant.getText());
                prod.precio = Double.parseDouble(fPrec.getText());
                prod.almacenId = fAlmId.getText().isEmpty() ? 0 : Integer.parseInt(fAlmId.getText());

                db.insertProducto(prod, currentUsuario.nombre);
                refreshData();
                fNom.setText(""); fDesc.setText(""); fCant.setText(""); fPrec.setText(""); fAlmId.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos numéricos inválidos.");
            }
        });

        btnDel.addActionListener(e -> {
            int row = tableProductos.getSelectedRow();
            if (row != -1) {
                int modelRow = tableProductos.convertRowIndexToModel(row);
                int id = (int) modelProductos.getValueAt(modelRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar permanentemente el producto ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    db.deleteProducto(id);
                    refreshData();
                }
            }
        });

        p.add(crudProdPanel, BorderLayout.SOUTH);
        return p;
    }

    // ────────────────────────────────────────────────────────────────────────
    // 4. VISTA DE ALMACENES
    // ────────────────────────────────────────────────────────────────────────
    private JPanel createAlmacenesPanel() {
        JPanel p = new JPanel(new BorderLayout());

        modelAlmacenes = new DefaultTableModel(new Object[]{"ID", "Nombre", "Ubicación", "Modificado Por"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableAlmacenes = new JTable(modelAlmacenes);
        tableAlmacenes.setRowHeight(25);
        tableAlmacenes.setFont(StyleUtils.F_NORMAL);
        p.add(new JScrollPane(tableAlmacenes), BorderLayout.CENTER);

        crudAlmPanel = new JPanel(new FlowLayout());
        crudAlmPanel.setBackground(new Color(245, 245, 245));

        JTextField fNom = StyleUtils.styledField(); fNom.setPreferredSize(new Dimension(150, 30));
        JTextField fUbi = StyleUtils.styledField(); fUbi.setPreferredSize(new Dimension(150, 30));
        JButton btnAdd = StyleUtils.primaryBtn("Agregar Almacén");
        JButton btnDel = StyleUtils.dangerBtn("Eliminar");

        crudAlmPanel.add(StyleUtils.label("Nombre:")); crudAlmPanel.add(fNom);
        crudAlmPanel.add(StyleUtils.label("Ubicación:")); crudAlmPanel.add(fUbi);
        crudAlmPanel.add(btnAdd); crudAlmPanel.add(btnDel);

        // Precarga de datos
        tableAlmacenes.getSelectionModel().addListSelectionListener(e -> {
            int row = tableAlmacenes.getSelectedRow();
            if (row != -1) {
                fNom.setText(modelAlmacenes.getValueAt(row, 1).toString());
                fUbi.setText(modelAlmacenes.getValueAt(row, 2).toString());
            }
        });

        btnAdd.addActionListener(e -> {
            db.insertAlmacen(fNom.getText(), fUbi.getText(), currentUsuario.nombre);
            refreshData();
            fNom.setText(""); fUbi.setText("");
        });

        btnDel.addActionListener(e -> {
            int row = tableAlmacenes.getSelectedRow();
            if (row != -1) {
                int id = (int) modelAlmacenes.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "¿Eliminar almacén ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    db.deleteAlmacen(id);
                    refreshData();
                }
            }
        });

        p.add(crudAlmPanel, BorderLayout.SOUTH);
        return p;
    }

    // ────────────────────────────────────────────────────────────────────────
    // 5. LÓGICA DE NEGOCIO (Permisos y Refresco de Tablas)
    // ────────────────────────────────────────────────────────────────────────
    private void applyRolePermissions() {
        String rol = currentUsuario.rol;
        boolean isAdmin = rol.equals("ADMIN");

        crudProdPanel.setVisible(isAdmin || rol.equals("PRODUCTOS"));
        crudAlmPanel.setVisible(isAdmin || rol.equals("ALMACENES"));

        appLayout.show(appCards, "INICIO");
    }

    private void refreshData() {
        // Refrescar Productos
        modelProductos.setRowCount(0);
        List<Producto> prods = db.listProductos();
        for (Producto p : prods) {
            String almName = (p.almacenNombre == null || p.almacenNombre.isEmpty()) ? "Sin Asignar" : p.almacenNombre;
            modelProductos.addRow(new Object[]{p.id, p.nombre, p.descripcion, p.cantidad, p.precio, almName, p.ultimoUsuario});
        }

        // Refrescar Almacenes
        modelAlmacenes.setRowCount(0);
        List<Almacen> alms = db.listAlmacenes();
        for (Almacen a : alms) {
            modelAlmacenes.addRow(new Object[]{a.id, a.nombre, a.ubicacion, a.ultimoUsuario});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}