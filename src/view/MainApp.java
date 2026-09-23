package view;

import controller.LibraryController;
import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainApp extends JFrame {

    private final LibraryController controller = new LibraryController();
    private final JPanel mainPanel;

    public MainApp() {
        setTitle("Libreria Digitale - Frontend Mock");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Vista iniziale: Griglia dei libri
        showBookGrid();
    }

    // 1. Schermata Griglia dei Libri
    private void showBookGrid() {
        mainPanel.removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("La Mia Libreria", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Barra di filtraggio
        JPanel filterBar = new JPanel();
        filterBar.add(new JLabel("Filtra:"));

        JButton btnAll = new JButton("Tutti");
        JButton btnUnread = new JButton("Da Leggere");
        JButton btnReading = new JButton("In Lettura");
        JButton btnFinished = new JButton("Completati");

        btnAll.addActionListener(e -> renderGrid(controller.getAllBooks()));
        btnUnread.addActionListener(e -> renderGrid(controller.getBooksByStatus(ReadingStatus.UNREAD)));
        btnReading.addActionListener(e -> renderGrid(controller.getBooksByStatus(ReadingStatus.READING)));
        btnFinished.addActionListener(e -> renderGrid(controller.getBooksByStatus(ReadingStatus.FINISHED)));

        filterBar.add(btnAll);
        filterBar.add(btnUnread);
        filterBar.add(btnReading);
        filterBar.add(btnFinished);

        topPanel.add(filterBar, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        renderGrid(controller.getAllBooks());

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void renderGrid(List<Book> books) {
        JPanel grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (Book book : books) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            card.setBackground(new Color(248, 248, 248));

            JLabel lblTitle = new JLabel("<html><b>" + book.getTitle() + "</b></html>", SwingConstants.CENTER);
            JLabel lblAuthor = new JLabel(book.getAuthor(), SwingConstants.CENTER);

            JButton btnDetails = new JButton("Dettagli");
            btnDetails.addActionListener(e -> showBookDetail(book));

            JPanel centerBox = new JPanel(new GridLayout(2, 1));
            centerBox.setOpaque(false);
            centerBox.add(lblTitle);
            centerBox.add(lblAuthor);

            card.add(centerBox, BorderLayout.CENTER);
            card.add(btnDetails, BorderLayout.SOUTH);

            grid.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(grid);

        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        Component centerComp = layout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null) {
            mainPanel.remove(centerComp);
        }
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // 2. Pagina Dettaglio Libro
    private void showBookDetail(Book book) {
        mainPanel.removeAll();

        JPanel detailBox = new JPanel();
        detailBox.setLayout(new BoxLayout(detailBox, BoxLayout.Y_AXIS));
        detailBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnBack = new JButton("← Torna alla lista");
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> showBookGrid());

        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel authorLabel = new JLabel("Autore: " + book.getAuthor());
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel isbnLabel = new JLabel("ISBN: " + book.getIsbn());
        isbnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ReadingInteraction interaction = controller.getInteraction(book.getId());
        JLabel statusLabel = new JLabel("Stato Attuale: " + interaction.getStatus());
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ratingLabel = new JLabel("Valutazione: " + (interaction.getRating() > 0 ? interaction.getRating() + " ★" : "Nessuna"));
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bottoni per cambio stato (Collegamento con MockBookDAO)
        JPanel statusButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusButtons.add(new JLabel("Cambia Stato:"));

        JButton btnToRead = new JButton("Segna Da Leggere");
        JButton btnReading = new JButton("In Lettura");
        JButton btnFinished = new JButton("Aggiungi ai letti");

        btnToRead.addActionListener(e -> {
            controller.updateReadingStatus(book.getId(), ReadingStatus.UNREAD);
            showBookDetail(book);
        });

        btnReading.addActionListener(e -> {
            controller.updateReadingStatus(book.getId(), ReadingStatus.READING);
            showBookDetail(book);
        });

        btnFinished.addActionListener(e -> {
            controller.updateReadingStatus(book.getId(), ReadingStatus.FINISHED);
            showBookDetail(book);
        });

        statusButtons.add(btnToRead);
        statusButtons.add(btnReading);
        statusButtons.add(btnFinished);

        // Bottoni Stelle Rating (1 - 5)
        JPanel ratingButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        ratingButtons.add(new JLabel("Valuta:"));

        for (int i = 1; i <= 5; i++) {
            int stars = i;
            JButton starBtn = new JButton(stars + " ★");
            starBtn.addActionListener(e -> {
                controller.updateRating(book.getId(), stars);
                showBookDetail(book);
            });
            ratingButtons.add(starBtn);
        }

        detailBox.add(btnBack);
        detailBox.add(Box.createRigidArea(new Dimension(0, 10)));
        detailBox.add(titleLabel);
        detailBox.add(Box.createRigidArea(new Dimension(0, 5)));
        detailBox.add(authorLabel);
        detailBox.add(isbnLabel);
        detailBox.add(Box.createRigidArea(new Dimension(0, 10)));
        detailBox.add(statusLabel);
        detailBox.add(ratingLabel);
        detailBox.add(Box.createRigidArea(new Dimension(0, 10)));
        detailBox.add(new JSeparator());
        detailBox.add(Box.createRigidArea(new Dimension(0, 10)));
        detailBox.add(statusButtons);
        detailBox.add(ratingButtons);

        mainPanel.add(detailBox, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}