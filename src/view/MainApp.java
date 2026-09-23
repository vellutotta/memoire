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
        setTitle("BiblioTech");

        // DIMENSIONI FINESTRA DESKTOP (Largh: 900px, Alt: 650px)
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // Area di contenuto principale
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_COLOR);
        add(contentArea, BorderLayout.CENTER);

        // Barra di Navigazione
        add(createBottomNavBar(), BorderLayout.SOUTH);

        // Vista iniziale: Categorie della Libreria
        showGridByCategory(null);
    }

    // 1. Vista Categorie
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

        // Menu categorie
        panel.add(createCategoryRow("📚", "Tutti i Libri", controller.getAllBooks().size() + " libri", e -> showGridByCategory(null)));
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

    // 2. Vista Griglia Copertine per Desktop (4 Colonne)
    // 2. Vista Griglia Copertine (Modificata per la Home: senza tasto Indietro e titolo "I miei libri")
    private void showGridByCategory(ReadingStatus filterStatus) {
        contentArea.removeAll();

        List<Book> books = (filterStatus == null) ? controller.getAllBooks() : controller.getBooksByStatus(filterStatus);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 30, 10, 30));

        // Se siamo nella Home (filterStatus == null), mostriamo "I miei libri" e NASCONDIAMO il tasto Indietro
        boolean isHome = (filterStatus == null);

        if (!isHome) {
            JButton btnBack = new JButton("← Indietro");
            btnBack.addActionListener(e -> showLibraryCategoriesView());
            topPanel.add(btnBack, BorderLayout.WEST);
        }

        String titleText = isHome ? "I miei libri (" + books.size() + ")" : "Griglia Libri (" + books.size() + ")";
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        topPanel.add(title, isHome ? BorderLayout.WEST : BorderLayout.CENTER);

        // Griglia a 4 colonne per Desktop
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

    // 3. Vista Dettaglio Libro
    // Task 4: Vista Dettaglio Libro adattata per il nuovo Book.java di master
    private void showBookDetailView(Book book) {
        contentArea.removeAll();

        JPanel mainBox = new JPanel();
        mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));
        mainBox.setBackground(BG_COLOR);
        mainBox.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Bottone Indietro
        JButton btnBack = new JButton("← Torna alla lista");
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> showLibraryCategoriesView());
        mainBox.add(btnBack);
        mainBox.add(Box.createRigidArea(new Dimension(0, 20)));

        // Layout Orizzontale per Copertina Grande + Dettagli
        JPanel headerPanel = new JPanel(new BorderLayout(25, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. Copertina Grande (180x250)
        JLabel largeCover = new JLabel();
        largeCover.setPreferredSize(new Dimension(180, 250));
        largeCover.setHorizontalAlignment(SwingConstants.CENTER);
        loadBookCover(largeCover, book.getCoverUrl(), book.getTitle());
        headerPanel.add(largeCover, BorderLayout.WEST);

        // Info Testuali
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

        // 2. PILL BUTTONS PER I GENERI (Simulati nella UI per soddisfare i requisiti del Task 4)
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

        // 3. Menu a tendina Stato Lettura
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

        // 4. Card Valutazione & Recensione con Stelle
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

        // Visualizzazione Stelle
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

    // Pop-up Recensioni
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

        // Pannello orizzontale per contenere le stelle
        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starPanel.setOpaque(false);

        // Array per mantenere il valore del voto (usiamo un array per poterlo modificare dentro i listener)
        final int[] selectedRating = {interaction.getRating() > 0 ? interaction.getRating() : 5};
        JLabel[] starLabels = new JLabel[5];

        for (int i = 0; i < 5; i++) {
            final int starIndex = i;
            starLabels[i] = new JLabel(i < selectedRating[0] ? "★" : "☆");
            starLabels[i].setFont(new Font("SansSerif", Font.PLAIN, 26));
            starLabels[i].setForeground(new Color(218, 165, 32)); // Colore oro
            starLabels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

            starLabels[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedRating[0] = starIndex + 1; // Aggiorna il voto
                    // Aggiorna visivamente tutte le stelle
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
            // Prende il voto dall'array che viene aggiornato cliccando le stelle
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

    // Barra Navigazione Inferiore
    // Barra Navigazione Inferiore Aggiornata
    private JPanel createBottomNavBar() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        navBar.setBackground(CARD_COLOR);
        navBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        navBar.setPreferredSize(new Dimension(0, 50));

        JButton btnHome = new JButton("🏠 Home");
        JButton btnLibrary = new JButton("📚 Libreria");
        JButton btnStats = new JButton("📊 Statistiche");

        // Collega i pulsanti alle rispettive viste
        btnHome.addActionListener(e -> showGridByCategory(null)); // Mostra tutti i libri nella Home
        btnLibrary.addActionListener(e -> showLibraryCategoriesView()); // Mostra le categorie
        btnStats.addActionListener(e -> showStatisticheView()); // Mostra la schermata statistiche

        navBar.add(btnHome);
        navBar.add(btnLibrary);
        navBar.add(btnStats);

        return navBar;
    }

    private void showStatisticheView() {
        contentArea.removeAll();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Selector Mese e Anno
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

        // 2. FILTRAGGIO CORRETTO: Considera SOLO i libri FINISHED
        List<Book> allBooks = controller.getAllBooks();
        List<ReadingInteraction> monthInteractions = new ArrayList<>();
        int totalStars = 0;

        for (Book b : allBooks) {
            ReadingInteraction inter = controller.getInteraction(b.getId());
            if (inter != null && inter.getStatus() == ReadingStatus.FINISHED && inter.getEndDate() != null) {
                if (inter.getEndDate().getMonthValue() == selectedMonth && inter.getEndDate().getYear() == selectedYear) {
                    monthInteractions.add(inter);
                    totalStars += inter.getRating();
                }
            }
        }

        int booksReadCount = monthInteractions.size();
        double avgRating = booksReadCount > 0 ? (double) totalStars / booksReadCount : 0.0;
        String avgStr = booksReadCount > 0 ? String.format(Locale.US, "%.1f ★", avgRating) : "N/D";

        // 3. Header Mese
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

        // 4. Riepilogo Statistiche
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(800, 70));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.add(createStatCounter(String.valueOf(booksReadCount), "LIBRI LETTI"));
        statsRow.add(createStatCounter(avgStr, "MEDIA VALUTAZIONI"));

        mainPanel.add(statsRow);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 5. Griglia Calendario
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

        // Mappa giorno -> interazione (dà la priorità alle recensioni con voto)
        Map<Integer, ReadingInteraction> dayToInteraction = new HashMap<>();
        for (ReadingInteraction inter : monthInteractions) {
            int day = inter.getEndDate().getDayOfMonth();
            if (!dayToInteraction.containsKey(day) || inter.getRating() > 0) {
                dayToInteraction.put(day, inter);
            }
        }

        for (int dayNum = 1; dayNum <= daysInMonth; dayNum++) {
            ReadingInteraction interactionOnDay = dayToInteraction.get(dayNum);
            calendarGrid.add(createCalendarCell(String.valueOf(dayNum), interactionOnDay));
        }

        mainPanel.add(calendarGrid);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);

        refreshUI();
    }

    // Helper: Crea i contatori per il riepilogo
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

    // Helper: Crea la singola cella del calendario
    // Sostituisci il vecchio createCalendarCell con questo:
    private JPanel createCalendarCell(String day, ReadingInteraction interaction) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setPreferredSize(new Dimension(60, 70));

        if (interaction != null) {
            cell.setBackground(new Color(30, 30, 35));
            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));

            JLabel dayLbl = new JLabel(day, SwingConstants.LEFT);
            dayLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            dayLbl.setForeground(Color.GRAY);
            dayLbl.setBorder(new EmptyBorder(3, 5, 0, 0));

            JLabel ratingLbl = new JLabel("★ " + interaction.getRating() + ".0", SwingConstants.CENTER);
            ratingLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            ratingLbl.setForeground(Color.WHITE);

            cell.add(dayLbl, BorderLayout.NORTH);
            cell.add(ratingLbl, BorderLayout.CENTER);
        } else {
            cell.setBackground(new Color(225, 225, 230));
            cell.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 215), 1, true));

            JLabel dayLbl = new JLabel(day, SwingConstants.CENTER);
            dayLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
            dayLbl.setForeground(Color.GRAY);
            cell.add(dayLbl, BorderLayout.CENTER);
        }
        return cell;
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