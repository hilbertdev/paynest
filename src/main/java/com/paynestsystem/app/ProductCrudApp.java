package com.paynestsystem.app;

import com.paynestsystem.domain.Product;
import com.paynestsystem.persistence.jdbc.DbTarget;
import com.paynestsystem.persistence.jdbc.JdbcProductRepository;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Classroom Swing demo: CRUD products and switch between H2 and Postgres.
 *
 * <pre>
 *   docker compose up -d   # only needed for the Postgres side of the switch
 *   mvn -q compile exec:java -Dexec.mainClass="com.paynestsystem.app.ProductCrudApp"
 * </pre>
 *
 * Each database has its own data. Keep DBeaver/pgAdmin open and refresh after edits.
 */
public class ProductCrudApp extends JFrame {

    private final JRadioButton h2Radio = new JRadioButton("H2", true);
    private final JRadioButton postgresRadio = new JRadioButton("Postgres");
    private final JButton connectButton = new JButton("Connect");
    private final JButton refreshButton = new JButton("Refresh");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"id", "name", "price"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable productTable = new JTable(tableModel);

    private final JTextField idField = new JTextField(8);
    private final JTextField nameField = new JTextField(16);
    private final JTextField priceField = new JTextField(8);
    private final JButton createButton = new JButton("Create");
    private final JButton updateButton = new JButton("Update");
    private final JButton deleteButton = new JButton("Delete");

    private final JLabel statusLabel = new JLabel("Not connected. Choose H2 or Postgres, then Connect.");

    private final AtomicReference<JdbcProductRepository> repository = new AtomicReference<>();

    public ProductCrudApp() {
        super("PayNest — Product CRUD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(buildTopPanel(), BorderLayout.NORTH);
        root.add(new JScrollPane(productTable), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);
        setContentPane(root);

        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        ButtonGroup dbGroup = new ButtonGroup();
        dbGroup.add(h2Radio);
        dbGroup.add(postgresRadio);

        connectButton.addActionListener(e -> connectSelected());
        refreshButton.addActionListener(e -> reloadTable());
        createButton.addActionListener(e -> createProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        h2Radio.addActionListener(e -> connectSelected());
        postgresRadio.addActionListener(e -> connectSelected());

        setCrudEnabled(false);
        refreshButton.setEnabled(false);

        pack();
        setSize(Math.max(getWidth(), 640), Math.max(getHeight(), 420));
        setLocationRelativeTo(null);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Database"));
        panel.add(h2Radio);
        panel.add(postgresRadio);
        panel.add(connectButton);
        panel.add(refreshButton);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Product"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("id"), gbc);
        gbc.gridx = 1;
        form.add(idField, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("name"), gbc);
        gbc.gridx = 3;
        form.add(nameField, gbc);

        gbc.gridx = 4;
        form.add(new JLabel("price"), gbc);
        gbc.gridx = 5;
        form.add(priceField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(createButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);

        panel.add(form, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        panel.add(statusLabel, BorderLayout.NORTH);
        return panel;
    }

    private DbTarget selectedTarget() {
        return postgresRadio.isSelected() ? DbTarget.POSTGRES : DbTarget.H2;
    }

    private void connectSelected() {
        DbTarget target = selectedTarget();
        setBusy(true);
        setStatus("Connecting to " + target + "...");

        new SwingWorker<JdbcProductRepository, Void>() {
            @Override
            protected JdbcProductRepository doInBackground() throws Exception {
                if (target == DbTarget.H2) {
                    Files.createDirectories(Path.of("data"));
                }
                JdbcProductRepository repo = new JdbcProductRepository(target);
                repo.ensureSchema();
                return repo;
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    JdbcProductRepository next = get();
                    JdbcProductRepository previous = repository.getAndSet(next);
                    closeQuietly(previous);
                    setCrudEnabled(true);
                    refreshButton.setEnabled(true);
                    setStatus("Connected to " + target + " — " + next.getTarget().getJdbcUrl());
                    reloadTable();
                } catch (Exception ex) {
                    closeQuietly(repository.getAndSet(null));
                    setCrudEnabled(false);
                    refreshButton.setEnabled(false);
                    tableModel.setRowCount(0);
                    setStatus(friendlyConnectError(target, unwrap(ex)));
                }
            }
        }.execute();
    }

    private void reloadTable() {
        JdbcProductRepository repo = repository.get();
        if (repo == null) {
            setStatus("Not connected.");
            return;
        }
        setBusy(true);
        new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return repo.findAll();
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    List<Product> products = get();
                    tableModel.setRowCount(0);
                    for (Product product : products) {
                        tableModel.addRow(new Object[]{
                                product.getId(),
                                product.getName(),
                                product.getPrice()
                        });
                    }
                    setStatus("Loaded " + products.size() + " product(s) from " + repo.getTarget() + ".");
                } catch (Exception ex) {
                    setStatus("Refresh failed: " + unwrap(ex).getMessage());
                }
            }
        }.execute();
    }

    private void createProduct() {
        Product product = readFormProduct();
        if (product == null) {
            return;
        }
        runMutation("Create", () -> {
            repository.get().insert(product);
            return "Created #" + product.getId() + " " + product.getName();
        });
    }

    private void updateProduct() {
        Product product = readFormProduct();
        if (product == null) {
            return;
        }
        runMutation("Update", () -> {
            int updated = repository.get().update(product);
            if (updated == 0) {
                throw new SQLException("No row with id " + product.getId());
            }
            return "Updated #" + product.getId();
        });
    }

    private void deleteProduct() {
        Integer id = readIdOnly();
        if (id == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete product #" + id + "?",
                "Confirm delete",
                JOptionPane.OK_CANCEL_OPTION);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }
        runMutation("Delete", () -> {
            int deleted = repository.get().deleteById(id);
            if (deleted == 0) {
                throw new SQLException("No row with id " + id);
            }
            return "Deleted #" + id;
        });
    }

    private interface Mutation {
        String run() throws Exception;
    }

    private void runMutation(String action, Mutation mutation) {
        JdbcProductRepository repo = repository.get();
        if (repo == null) {
            setStatus("Not connected.");
            return;
        }
        setBusy(true);
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return mutation.run();
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    setStatus(get() + " (" + repo.getTarget() + "). Refresh DBeaver/pgAdmin to see it.");
                    reloadTable();
                } catch (Exception ex) {
                    setStatus(action + " failed: " + unwrap(ex).getMessage());
                }
            }
        }.execute();
    }

    private Product readFormProduct() {
        Integer id = readIdOnly();
        if (id == null) {
            return null;
        }
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            setStatus("Name is required.");
            return null;
        }
        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException ex) {
            setStatus("Price must be a number.");
            return null;
        }
        return new Product(id, name, price);
    }

    private Integer readIdOnly() {
        try {
            return Integer.parseInt(idField.getText().trim());
        } catch (NumberFormatException ex) {
            setStatus("Id must be an integer.");
            return null;
        }
    }

    private void fillFormFromSelection() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        priceField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
    }

    private void setCrudEnabled(boolean enabled) {
        createButton.setEnabled(enabled);
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        idField.setEnabled(enabled);
        nameField.setEnabled(enabled);
        priceField.setEnabled(enabled);
    }

    private void setBusy(boolean busy) {
        connectButton.setEnabled(!busy);
        h2Radio.setEnabled(!busy);
        postgresRadio.setEnabled(!busy);
        refreshButton.setEnabled(!busy && repository.get() != null);
        boolean crud = !busy && repository.get() != null;
        setCrudEnabled(crud);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private static String friendlyConnectError(DbTarget target, Throwable error) {
        String detail = error.getMessage() == null ? error.toString() : error.getMessage();
        if (target == DbTarget.POSTGRES) {
            return "Postgres not reachable — run `docker compose up -d`. (" + detail + ")";
        }
        return "Connect failed: " + detail;
    }

    private static Throwable unwrap(Exception ex) {
        Throwable cause = ex.getCause();
        return cause != null ? cause : ex;
    }

    private static void closeQuietly(JdbcProductRepository repo) {
        if (repo == null) {
            return;
        }
        try {
            repo.close();
        } catch (SQLException ignored) {
            // demo UI — ignore close errors
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductCrudApp app = new ProductCrudApp();
            app.setVisible(true);
            app.connectSelected();
        });
    }
}
