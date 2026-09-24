package view;

import controller.LibraryController;
import model.Book;
import model.ReadingInteraction;
import model.ReadingStatus;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

public class MainApp extends JFrame {

    private final LibraryController controller = new LibraryController();
    private final JPanel contentArea;
    private static final Color BG_COLOR = new Color(245, 245, 247);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(28, 28, 30);
    private int selectedMonth = LocalDate.now().getMonthValue();
    private int selectedYear = LocalDate.now().getYear();

    public MainApp() {
        setTitle("Memoir");

        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_COLOR);
        add(contentArea, BorderLayout.CENTER);

        add(createBottomNavBar(), BorderLayout.SOUTH);

        showHomeView();
    }

    // 1. Vista Home (Cerca + Tutti i libri)
    private void showHomeView() {
        contentArea.removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        JLabel titleLbl = new JLabel("I miei libri");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);
        searchBarPanel.setMaximumSize(new Dimension(1200, 35));
        searchBarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton searchBtn = new JButton("🔍 Cerca");
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchBtn, BorderLayout.EAST);

        topPanel.add(titleLbl);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        topPanel.add(searchBarPanel);

        JPanel resultsContainer = new JPanel(new BorderLayout());
        resultsContainer.setBackground(BG_COLOR);

        java.awt.event.ActionListener performSearch = e -> {
            String query = searchField.getText();
            List<Book> foundBooks = controller.searchBooks(query);
            renderSearchResults(resultsContainer, foundBooks);
        };

        searchBtn.addActionListener(performSearch);
        searchField.addActionListener(performSearch);

        renderSearchResults(resultsContainer, controller.getAllBooks());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(resultsContainer, BorderLayout.CENTER);

        contentArea.add(mainPanel, BorderLayout.CENTER);
        refreshUI();
    }

    private void renderSearchResults(JPanel container, List<Book> books) {
        container.removeAll();

        if (books.isEmpty()) {
            JLabel emptyLbl = new JLabel("Nessun libro trovato per la tua ricerca.", SwingConstants.CENTER);
            emptyLbl.setFont(new Font("SansSerif", Font.ITALIC, 15));
            emptyLbl.setForeground(Color.GRAY);
            container.add(emptyLbl, BorderLayout.CENTER);
        } else {
            JPanel grid = new JPanel(new GridLayout(0, 4, 15, 15));
            grid.setBackground(BG_COLOR);
            grid.setBorder(new EmptyBorder(10, 30, 20, 30));

            for (Book book : books) {
                JPanel bookCard = new JPanel(new BorderLayout(0, 5));
                bookCard.setBackground(CARD_COLOR);
                bookCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
                bookCard.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JLabel coverLabel = new JLabel();
                coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
                coverLabel.setPreferredSize(new Dimension(140, 180));
                loadBookCover(coverLabel, book.getCoverUrl(), book.getTitle());

                JLabel lblTitle = new JLabel("<html><center><b>" + book.getTitle() + "</b></center></html>", SwingConstants.CENTER);
                lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
                lblTitle.setBorder(new EmptyBorder(5, 5, 8, 5));

                bookCard.add(coverLabel, BorderLayout.CENTER);
                bookCard.add(lblTitle, BorderLayout.SOUTH);

                bookCard.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        showBookDetailView(book);
                    }
                });

                grid.add(bookCard);
            }

            JPanel gridWrapper = new JPanel(new BorderLayout());
            gridWrapper.setBackground(BG_COLOR);
            gridWrapper.add(grid, BorderLayout.NORTH);

            JScrollPane scrollPane = new JScrollPane(gridWrapper);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            container.add(scrollPane, BorderLayout.CENTER);
        }

        container.revalidate();
        container.repaint();
    }

    // 2. Vista Categorie
    private void showLibraryCategoriesView() {
        contentArea.removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel titleLabel = new JLabel("Libreria");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(createCategoryRow("📚", "Tutti i Libri", controller.getAllBooks().size() + " libri", e -> showHomeView()));
        panel.add(createCategoryRow("🔖", "Da leggere", controller.getBooksByStatus(ReadingStatus.UNREAD).size() + " elementi", e -> showGridByCategory(ReadingStatus.UNREAD)));
        panel.add(createCategoryRow("📖", "In lettura", controller.getBooksByStatus(ReadingStatus.READING).size() + " libri", e -> showGridByCategory(ReadingStatus.READING)));
        panel.add(createCategoryRow("✅", "Lettura terminata", controller.getBooksByStatus(ReadingStatus.FINISHED).size() + " letture", e -> showGridByCategory(ReadingStatus.FINISHED)));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentArea.add(scrollPane, BorderLayout.CENTER);

        refreshUI();
    }

    private JPanel createCategoryRow(String icon, String title, String subtitle, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(1200, 70));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 22));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);

        textPanel.add(lblTitle);
        textPanel.add(lblSub);

        JLabel arrowLbl = new JLabel("→");
        arrowLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrowLbl.setForeground(Color.GRAY);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(arrowLbl, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    // 3. Vista Griglia Categoria
    private void showGridByCategory(ReadingStatus filterStatus) {
        contentArea.removeAll();

        List<Book> books = controller.getBooksByStatus(filterStatus);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 30, 10, 30));

        JButton btnBack = new JButton("← Indietro");
        btnBack.addActionListener(e -> showLibraryCategoriesView());
        topPanel.add(btnBack, BorderLayout.WEST);

        String titleText = "Categoria (" + books.size() + " libri)";
        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        topPanel.add(title, BorderLayout.CENTER);

        JPanel grid = new JPanel(new GridLayout(0, 4, 15, 15));
        grid.setBackground(BG_COLOR);
        grid.setBorder(new EmptyBorder(10, 30, 20, 30));

        for (Book book : books) {
            JPanel bookCard = new JPanel(new BorderLayout(0, 5));
            bookCard.setBackground(CARD_COLOR);
            bookCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
            bookCard.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel coverLabel = new JLabel();
            coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
            coverLabel.setPreferredSize(new Dimension(140, 180));
            loadBookCover(coverLabel, book.getCoverUrl(), book.getTitle());

            JLabel lblTitle = new JLabel("<html><center><b>" + book.getTitle() + "</b></center></html>", SwingConstants.CENTER);
            lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblTitle.setBorder(new EmptyBorder(5, 5, 8, 5));

            bookCard.add(coverLabel, BorderLayout.CENTER);
            bookCard.add(lblTitle, BorderLayout.SOUTH);

            bookCard.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showBookDetailView(book);
                }
            });

            grid.add(bookCard);
        }

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(BG_COLOR);
        gridWrapper.add(grid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentArea.add(topPanel, BorderLayout.NORTH);
        contentArea.add(scrollPane, BorderLayout.CENTER);

        refreshUI();
    }

    // 4. Vista Dettaglio Libro
    private void showBookDetailView(Book book) {
        contentArea.removeAll();

        JPanel mainBox = new JPanel();
        mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));
        mainBox.setBackground(BG_COLOR);
        mainBox.setBorder(new EmptyBorder(25, 40, 25, 40));

        JButton btnBack = new JButton("← Torna alla lista");
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> showHomeView());
        mainBox.add(btnBack);
        mainBox.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel headerPanel = new JPanel(new BorderLayout(25, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel largeCover = new JLabel();
        largeCover.setPreferredSize(new Dimension(180, 250));
        largeCover.setHorizontalAlignment(SwingConstants.CENTER);
        loadBookCover(largeCover, book.getCoverUrl(), book.getTitle());
        headerPanel.add(largeCover, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(book.getTitle());
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel authorLbl = new JLabel("di " + book.getAuthor());
        authorLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        authorLbl.setForeground(Color.GRAY);

        JLabel isbnLbl = new JLabel("ISBN: " + (book.getIsbn() != null ? book.getIsbn() : "N/D"));
        isbnLbl.setFont(new Font("SansSerif", Font.ITALIC, 12));
        isbnLbl.setForeground(Color.GRAY);

        infoPanel.add(titleLbl);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(authorLbl);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(isbnLbl);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        List<String> mockGenres = List.of("Fiction", "Romanzo", "Letteratura");
        JPanel genresPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        genresPanel.setOpaque(false);
        genresPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String genre : mockGenres) {
            JLabel pill = new JLabel(genre);
            pill.setFont(new Font("SansSerif", Font.BOLD, 12));
            pill.setForeground(new Color(60, 60, 90));
            pill.setOpaque(true);
            pill.setBackground(new Color(225, 230, 245));
            pill.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(190, 200, 230), 1, true),
                    new EmptyBorder(5, 12, 5, 12)
            ));
            genresPanel.add(pill);
        }
        infoPanel.add(genresPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        ReadingInteraction interaction = controller.getInteraction(book.getId());

        JPanel statusBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusBox.setOpaque(false);
        statusBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStato = new JLabel("Stato Lettura: ");
        lblStato.setFont(new Font("SansSerif", Font.BOLD, 13));
        statusBox.add(lblStato);

        JComboBox<ReadingStatus> statusCombo = new JComboBox<>(ReadingStatus.values());
        statusCombo.setSelectedItem(interaction.getStatus());
        statusCombo.addActionListener(e -> {
            controller.updateReadingStatus(book.getId(), (ReadingStatus) statusCombo.getSelectedItem());
        });
        statusBox.add(statusCombo);

        infoPanel.add(statusBox);

        headerPanel.add(infoPanel, BorderLayout.CENTER);
        mainBox.add(headerPanel);

        mainBox.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel reviewCard = new JPanel();
        reviewCard.setLayout(new BoxLayout(reviewCard, BoxLayout.Y_AXIS));
        reviewCard.setBackground(CARD_COLOR);
        reviewCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        reviewCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ratingTitle = new JLabel("Valutazione & Recensione");
        ratingTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        reviewCard.add(ratingTitle);
        reviewCard.add(Box.createRigidArea(new Dimension(0, 10)));

        StringBuilder starsStr = new StringBuilder();
        int currentRating = interaction.getRating();
        for (int i = 1; i <= 5; i++) {
            starsStr.append(i <= currentRating ? "★ " : "☆ ");
        }

        JLabel currentStars = new JLabel("Voto: " + starsStr.toString());
        currentStars.setFont(new Font("SansSerif", Font.PLAIN, 15));
        currentStars.setForeground(new Color(218, 165, 32));
        reviewCard.add(currentStars);

        reviewCard.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel currentComment = new JLabel("<html><i>\"" + (interaction.getReviewText() != null && !interaction.getReviewText().isBlank() ? interaction.getReviewText() : "Nessun commento inserito") + "\"</i></html>");
        currentComment.setFont(new Font("SansSerif", Font.PLAIN, 13));
        currentComment.setForeground(Color.DARK_GRAY);
        reviewCard.add(currentComment);

        reviewCard.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnAddReview = new JButton("✍️ Scrivi / Modifica Recensione");
        btnAddReview.addActionListener(e -> openReviewDialog(book, interaction));
        reviewCard.add(btnAddReview);

        mainBox.add(reviewCard);

        JScrollPane scrollPane = new JScrollPane(mainBox);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);

        refreshUI();
    }

    private void openReviewDialog(Book book, ReadingInteraction interaction) {
        JDialog dialog = new JDialog(this, "Recensione - " + book.getTitle(), true);
        dialog.setSize(400, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_COLOR);

        JLabel titleLbl = new JLabel("La tua recensione");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(titleLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Seleziona stelle (1-5):"));

        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starPanel.setOpaque(false);

        final int[] selectedRating = {interaction.getRating() > 0 ? interaction.getRating() : 5};
        JLabel[] starLabels = new JLabel[5];

        for (int i = 0; i < 5; i++) {
            final int starIndex = i;
            starLabels[i] = new JLabel(i < selectedRating[0] ? "★" : "☆");
            starLabels[i].setFont(new Font("SansSerif", Font.PLAIN, 26));
            starLabels[i].setForeground(new Color(218, 165, 32));
            starLabels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

            starLabels[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedRating[0] = starIndex + 1;
                    for (int j = 0; j < 5; j++) {
                        starLabels[j].setText(j < selectedRating[0] ? "★" : "☆");
                    }
                }
            });
            starPanel.add(starLabels[i]);
        }
        panel.add(starPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(new JLabel("Scrivi un commento/recensione:"));
        JTextArea commentArea = new JTextArea(interaction.getReviewText(), 6, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane textScroll = new JScrollPane(commentArea);
        panel.add(textScroll);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnSave = new JButton("Salva Recensione");
        btnSave.setBackground(ACCENT_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            int finalRating = selectedRating[0];
            String comment = commentArea.getText();
            controller.updateReview(book.getId(), finalRating, comment);
            dialog.dispose();
            showBookDetailView(book);
        });
        panel.add(btnSave);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // 5. Barra Navigazione Inferiore
    private JPanel createBottomNavBar() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        navBar.setBackground(CARD_COLOR);
        navBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        navBar.setPreferredSize(new Dimension(0, 50));

        JButton btnHome = new JButton("🏠 Home");
        JButton btnLibrary = new JButton("📚 Libreria");
        JButton btnStats = new JButton("📊 Statistiche");

        btnHome.addActionListener(e -> showHomeView());
        btnLibrary.addActionListener(e -> showLibraryCategoriesView());
        btnStats.addActionListener(e -> showStatisticheView());

        navBar.add(btnHome);
        navBar.add(btnLibrary);
        navBar.add(btnStats);

        return navBar;
    }

    // 6. Vista Statistiche
    private void showStatisticheView() {
        contentArea.removeAll();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorPanel.setOpaque(false);
        selectorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSelect = new JLabel("Seleziona periodo: ");
        lblSelect.setFont(new Font("SansSerif", Font.BOLD, 14));

        String[] mesi = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
                "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
        JComboBox<String> monthCombo = new JComboBox<>(mesi);
        monthCombo.setSelectedIndex(selectedMonth - 1);

        Integer[] anni = {2024, 2025, 2026, 2027};
        JComboBox<Integer> yearCombo = new JComboBox<>(anni);
        yearCombo.setSelectedItem(selectedYear);

        java.awt.event.ActionListener updateCalendar = e -> {
            selectedMonth = monthCombo.getSelectedIndex() + 1;
            selectedYear = (Integer) yearCombo.getSelectedItem();
            showStatisticheView();
        };

        monthCombo.addActionListener(updateCalendar);
        yearCombo.addActionListener(updateCalendar);

        selectorPanel.add(lblSelect);
        selectorPanel.add(monthCombo);
        selectorPanel.add(yearCombo);

        mainPanel.add(selectorPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        List<Book> allBooks = controller.getAllBooks();
        List<ReadingInteraction> monthInteractions = new ArrayList<>();
        Map<Integer, List<Book>> dayToBooksMap = new HashMap<>();
        int totalStars = 0;

        for (Book b : allBooks) {
            ReadingInteraction inter = controller.getInteraction(b.getId());
            if (inter != null && inter.getStatus() == ReadingStatus.FINISHED && inter.getEndDate() != null) {
                if (inter.getEndDate().getMonthValue() == selectedMonth && inter.getEndDate().getYear() == selectedYear) {
                    monthInteractions.add(inter);
                    totalStars += inter.getRating();

                    int dayNum = inter.getEndDate().getDayOfMonth();
                    dayToBooksMap.computeIfAbsent(dayNum, k -> new ArrayList<>()).add(b);
                }
            }
        }

        int booksReadCount = monthInteractions.size();
        double avgRating = booksReadCount > 0 ? (double) totalStars / booksReadCount : 0.0;
        String avgStr = booksReadCount > 0 ? String.format(Locale.US, "%.1f ★", avgRating) : "N/D";

        JPanel monthCard = new JPanel(new BorderLayout());
        monthCard.setBackground(new Color(20, 20, 25));
        monthCard.setMaximumSize(new Dimension(800, 110));
        monthCard.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        monthCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel monthLbl = new JLabel(mesi[selectedMonth - 1], SwingConstants.RIGHT);
        monthLbl.setFont(new Font("Serif", Font.BOLD, 38));
        monthLbl.setForeground(Color.WHITE);

        JLabel yearLbl = new JLabel(String.valueOf(selectedYear), SwingConstants.RIGHT);
        yearLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        yearLbl.setForeground(Color.LIGHT_GRAY);

        JPanel monthTextPanel = new JPanel(new GridLayout(2, 1));
        monthTextPanel.setOpaque(false);
        monthTextPanel.add(monthLbl);
        monthTextPanel.add(yearLbl);
        monthCard.add(monthTextPanel, BorderLayout.EAST);

        mainPanel.add(monthCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(800, 70));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.add(createStatCounter(String.valueOf(booksReadCount), "LIBRI LETTI"));
        statsRow.add(createStatCounter(avgStr, "MEDIA VALUTAZIONI"));

        mainPanel.add(statsRow);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel calendarGrid = new JPanel(new GridLayout(0, 7, 8, 8));
        calendarGrid.setOpaque(false);
        calendarGrid.setMaximumSize(new Dimension(800, 420));
        calendarGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] days = {"LUN", "MAR", "MER", "GIO", "VEN", "SAB", "DOM"};
        for (String day : days) {
            JLabel dayLbl = new JLabel(day, SwingConstants.CENTER);
            dayLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            dayLbl.setForeground(Color.GRAY);
            calendarGrid.add(dayLbl);
        }

        YearMonth yearMonth = YearMonth.of(selectedYear, selectedMonth);
        int daysInMonth = yearMonth.lengthOfMonth();
        DayOfWeek firstDay = yearMonth.atDay(1).getDayOfWeek();
        int emptyCellsBefore = firstDay.getValue() - 1;

        for (int i = 0; i < emptyCellsBefore; i++) {
            JPanel emptyCell = new JPanel();
            emptyCell.setOpaque(false);
            calendarGrid.add(emptyCell);
        }

        for (int dayNum = 1; dayNum <= daysInMonth; dayNum++) {
            List<Book> booksOnDay = dayToBooksMap.get(dayNum);
            calendarGrid.add(createCalendarCell(dayNum, booksOnDay));
        }

        mainPanel.add(calendarGrid);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);

        refreshUI();
    }

    private JPanel createStatCounter(String number, String label) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel numLbl = new JLabel(number, SwingConstants.CENTER);
        numLbl.setFont(new Font("Serif", Font.BOLD, 28));
        numLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLbl = new JLabel(label, SwingConstants.CENTER);
        textLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textLbl.setForeground(Color.DARK_GRAY);
        textLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(numLbl);
        p.add(textLbl);
        return p;
    }

    private JPanel createCalendarCell(int dayNum, List<Book> booksOnDay) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setPreferredSize(new Dimension(60, 70));

        if (booksOnDay != null && !booksOnDay.isEmpty()) {
            cell.setBackground(new Color(30, 30, 35));
            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
            cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel dayLbl = new JLabel(String.valueOf(dayNum), SwingConstants.LEFT);
            dayLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            dayLbl.setForeground(Color.GRAY);
            dayLbl.setBorder(new EmptyBorder(3, 5, 0, 0));

            double avgRating = booksOnDay.stream()
                    .mapToInt(b -> controller.getInteraction(b.getId()).getRating())
                    .average().orElse(0.0);

            JLabel ratingLbl = new JLabel(String.format(Locale.US, "★ %.1f", avgRating), SwingConstants.CENTER);
            ratingLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            ratingLbl.setForeground(Color.WHITE);

            cell.add(dayLbl, BorderLayout.NORTH);
            cell.add(ratingLbl, BorderLayout.CENTER);

            // Click listener per aprire il dettaglio della giornata
            cell.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showDayDetailDialog(dayNum, booksOnDay);
                }
            });
        } else {
            cell.setBackground(new Color(225, 225, 230));
            cell.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 215), 1, true));

            JLabel dayLbl = new JLabel(String.valueOf(dayNum), SwingConstants.CENTER);
            dayLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
            dayLbl.setForeground(Color.GRAY);
            cell.add(dayLbl, BorderLayout.CENTER);
        }
        return cell;
    }

    // Pop-up con i dettagli dei libri recensiti nel giorno selezionato
    private void showDayDetailDialog(int day, List<Book> books) {
        String[] mesi = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
                "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
        String dateTitle = day + " " + mesi[selectedMonth - 1] + " " + selectedYear;

        JDialog dialog = new JDialog(this, "Letture del " + dateTitle, true);
        dialog.setSize(460, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_COLOR);
        container.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLbl = new JLabel("Libri completati il " + dateTitle);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(titleLbl);
        container.add(Box.createRigidArea(new Dimension(0, 15)));

        for (Book book : books) {
            ReadingInteraction inter = controller.getInteraction(book.getId());

            JPanel card = new JPanel(new BorderLayout(12, 0));
            card.setBackground(CARD_COLOR);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            card.setMaximumSize(new Dimension(1000, 110));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel coverLabel = new JLabel();
            coverLabel.setPreferredSize(new Dimension(50, 75));
            coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
            loadBookCover(coverLabel, book.getCoverUrl(), book.getTitle());
            card.add(coverLabel, BorderLayout.WEST);

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);

            JLabel bTitle = new JLabel(book.getTitle());
            bTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

            JLabel bAuthor = new JLabel("di " + book.getAuthor());
            bAuthor.setFont(new Font("SansSerif", Font.PLAIN, 12));
            bAuthor.setForeground(Color.GRAY);

            StringBuilder stars = new StringBuilder("Voto: ");
            int rating = inter.getRating();
            for (int i = 1; i <= 5; i++) {
                stars.append(i <= rating ? "★" : "☆");
            }
            JLabel bStars = new JLabel(stars.toString());
            bStars.setFont(new Font("SansSerif", Font.PLAIN, 12));
            bStars.setForeground(new Color(218, 165, 32));

            String comment = (inter.getReviewText() != null && !inter.getReviewText().isBlank())
                    ? "\"" + inter.getReviewText() + "\""
                    : "Nessun commento testuale";
            JLabel bComment = new JLabel("<html><i>" + comment + "</i></html>");
            bComment.setFont(new Font("SansSerif", Font.PLAIN, 11));
            bComment.setForeground(Color.DARK_GRAY);

            info.add(bTitle);
            info.add(bAuthor);
            info.add(bStars);
            info.add(Box.createRigidArea(new Dimension(0, 3)));
            info.add(bComment);

            card.add(info, BorderLayout.CENTER);

            // Cliccando sulla scheda del libro si va alla vista di dettaglio
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    dialog.dispose();
                    showBookDetailView(book);
                }
            });

            container.add(card);
            container.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void loadBookCover(JLabel label, String urlStr, String fallbackTitle) {
        try {
            URL url = new URL(urlStr);
            BufferedImage img = ImageIO.read(url);
            if (img != null) {
                Image scaled = img.getScaledInstance(130, 170, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
                return;
            }
        } catch (Exception ignored) {}
        label.setText("<html><center>📖<br>" + fallbackTitle + "</center></html>");
        label.setOpaque(true);
        label.setBackground(new Color(230, 230, 235));
    }

    private void refreshUI() {
        contentArea.revalidate();
        contentArea.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}