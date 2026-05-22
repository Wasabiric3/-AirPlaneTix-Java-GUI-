import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class AirplaneTicketingSystem extends JFrame {
    private static final long serialVersionUID = 1L;

    // ========================= COLOR SCHEME =========================
    private static final Color PRIMARY = new Color(0x12, 0x22, 0x45);
    private static final Color PRIMARY_LIGHT = new Color(0x2B, 0x3E, 0x65);
    private static final Color ACCENT = new Color(0x1D, 0x32, 0x58);
    private static final Color ACCENT_LIGHT = new Color(0xF1, 0xF3, 0xF7);
    private static final Color BG_MAIN = ACCENT_LIGHT;
    private static final Color BG_CARD = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(0x1E, 0x2A, 0x3F);
    private static final Color TEXT_SECONDARY = new Color(0x6B, 0x78, 0x90);
    private static final Color SUCCESS_GREEN = new Color(48, 209, 88);
    private static final Color DANGER_RED = new Color(255, 69, 58);
    private static final Color BORDER = new Color(0xC8, 0xD0, 0xDB);
    private static final Color BORDER_LIGHT = new Color(0xE0, 0xE5, 0xEC);
    private static final Color STEP_ACTIVE = new Color(0xF6, 0xA6, 0x00);
    private static final Color STEP_DONE = ACCENT;
    private static final Color STEP_PENDING = PRIMARY_LIGHT;
    private static final Color BG_MUTED = new Color(0xF5, 0xF6, 0xF8);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 15);
    private static final Color FOCUS_RING = new Color(0x12, 0x22, 0x45, 45);

    // ========================= FONTS =========================
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 14);

    // ========================= SPACING SCALE =========================
    private static final int SP_6 = 6;
    private static final int SP_10 = 10;
    private static final int SP_12 = 12;
    private static final int SP_16 = 16;
    private static final int SP_20 = 20;
    private static final int SP_24 = 24;
    private static final int SP_30 = 30;

    // ========================= FLIGHT DATA =========================
    private static final String[] FLIGHT_COLUMNS = {
            "Flight No.", "Origin", "Destination", "Base Price (PHP)"
    };
    private static final String[] FLIGHT_COLUMNS_WITH_EST = {
            "Flight No.", "Departure", "Arrival", "Realtime EST", "Base Fare (PHP)", "Status"
    };

    // 5 Domestic Flights (Philippines)
    private static final Object[][] DOMESTIC_FLIGHTS = {
            { "PR-101", "Manila (MNL)", "Cebu (CEB)", 3500.00 },
            { "PR-102", "Manila (MNL)", "Davao (DVO)", 4200.00 },
            { "PR-103", "Manila (MNL)", "Boracay (MPH)", 3800.00 },
            { "PR-104", "Manila (MNL)", "Palawan (PPS)", 4500.00 },
            { "PR-105", "Manila (MNL)", "Bohol (TAG)", 3200.00 }
    };

    // 5 International Flights (Southeast Asia)
    private static final Object[][] INTERNATIONAL_FLIGHTS = {
            { "PR-201", "Manila (MNL)", "Singapore (SIN)", 8500.00 },
            { "PR-202", "Manila (MNL)", "Bangkok (BKK)", 7800.00 },
            { "PR-203", "Manila (MNL)", "Kuala Lumpur (KUL)", 7200.00 },
            { "PR-204", "Manila (MNL)", "Ho Chi Minh (SGN)", 6800.00 },
            { "PR-205", "Manila (MNL)", "Jakarta (CGK)", 9200.00 }
    };

    // 5 International Flights (Europe)
    private static final Object[][] EUROPE_FLIGHTS = {
            { "PR-301", "Manila (MNL)", "London (LHR)", 45000.00 },
            { "PR-302", "Manila (MNL)", "Paris (CDG)", 43000.00 },
            { "PR-303", "Manila (MNL)", "Rome (FCO)", 41000.00 },
            { "PR-304", "Manila (MNL)", "Amsterdam (AMS)", 42000.00 },
            { "PR-305", "Manila (MNL)", "Frankfurt (FRA)", 44000.00 }
    };

    // 5 International Flights (North America)
    private static final Object[][] NORTH_AMERICA_FLIGHTS = {
            { "PR-401", "Manila (MNL)", "Los Angeles (LAX)", 52000.00 },
            { "PR-402", "Manila (MNL)", "San Francisco (SFO)", 54000.00 },
            { "PR-403", "Manila (MNL)", "New York (JFK)", 58000.00 },
            { "PR-404", "Manila (MNL)", "Vancouver (YVR)", 49000.00 },
            { "PR-405", "Manila (MNL)", "Toronto (YYZ)", 51000.00 }
    };

    // ========================= CLASS & PRICING =========================
    private static final String[] CLASS_NAMES = { "Economy", "Premium Economy", "Business", "First Class" };
    private static final double[] CLASS_MULTIPLIERS = { 1.0, 1.5, 2.5, 4.0 };

    // ========================= ADD-ON DATA =========================
    private static final String[] ADDON_NAMES = { "Extra Baggage", "Pet", "Meal", "Insurance", "Tax" };
    private static final double[] ADDON_PRICES = { 1500.00, 3000.00, 500.00, 800.00, 600.00 };
    private static final String ADDON_TAX = "Tax";

    // ========================= DISCOUNT =========================
    private static final double DISCOUNT_RATE = 0.20; // 20% for Senior/PWD
    private static final double TAX_RATE = 0.12; // 12% VAT (exempt for Senior/PWD)
    private static final double OVERBOOKING_BUFFER_RATE = 0.10; // 10% allowable overbooking
    private static final int DEFAULT_DOMESTIC_CAPACITY = 180;
    private static final int DEFAULT_INTERNATIONAL_CAPACITY = 220;
    private static final int DEFAULT_EUROPE_CAPACITY = 300;
    private static final int DEFAULT_NORTH_AMERICA_CAPACITY = 320;
    private static final String POLICY_OVERBOOKING = "Overbooking limits";
    private static final String POLICY_CHECKIN = "Check-in window (e.g., 24hrs before departure)";
    private static final String POLICY_REFUND = "Refund/cancellation policies";
    private static Map<String, Integer> FLIGHT_CAPACITY = createFlightCapacityMap();
    private static final Map<String, Integer> FLIGHT_BOOKED_SEATS = createBookedSeatMap();
    private static final Map<String, Integer> FLIGHT_EST_DEPARTURE_OFFSET_MIN = createDepartureOffsetMap();
    private static final Map<String, Integer> FLIGHT_EST_DURATION_MIN = createDurationMap();
    private static final Map<String, java.time.LocalTime> FLIGHT_SCHEDULED_TIMES = createScheduledTimesMap();
    private static final int BOOKING_CUTOFF_HOURS = 2;
    private static final DateTimeFormatter EST_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mma");
    private static final DateTimeFormatter EST_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
    private static final DateTimeFormatter FLIGHT_DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_CANCELED = "CANCELED";
    private static final char[] SEAT_LETTERS = { 'A', 'B', 'C', 'D' };
    // Per-class cabin configurations
    private static final char[][] CLASS_SEAT_LETTERS = {
            { 'A', 'B', 'C', 'D', 'E', 'F' }, // Economy — 3+3
            { 'A', 'B', 'C', 'D' }, // Premium Eco — 2+2
            { 'A', 'B', 'C' }, // Business — 2+1
            { 'A', 'B' } // First Class — 1+1
    };
    private static final int[] CLASS_AISLE_AFTER = { 2, 1, 1, 0 };
    private static final double[] CLASS_SEAT_RATIOS = { 0.60, 0.25, 0.10, 0.05 };
    private static final int[] CLASS_BTN_WIDTH = { 44, 52, 64, 80 };
    private static final int[] CLASS_BTN_HEIGHT = { 40, 44, 52, 60 };
    private static final int SEAT_HOLD_MINUTES = 10; // temporary hold duration in minutes
    private static final BookingStore BOOKING_STORE = new BookingStore();

    // ========================= DATA MODEL =========================
    private Object[] selectedFlight = null;
    // Unique session identifier for this app instance (used as seat-hold owner)
    private final String sessionId = UUID.randomUUID().toString();
    // Dynamic flight arrays (loaded from DB, fall back to static defaults)
    private Object[][] activeDomesticFlights = DOMESTIC_FLIGHTS;
    private Object[][] activeInternationalFlights = INTERNATIONAL_FLIGHTS;
    private Object[][] activeEuropeFlights = EUROPE_FLIGHTS;
    private Object[][] activeNorthAmericaFlights = NORTH_AMERICA_FLIGHTS;
    private Map<String, java.time.LocalTime> flightDepartureTimes = new HashMap<>(FLIGHT_SCHEDULED_TIMES);
    // UI helpers for seat map visualizer
    private JPanel seatMapPanel;
    private Map<String, JButton> seatButtonMap = new HashMap<>();
    private LocalDate selectedFlightDate = null;
    private boolean isDomestic = true;
    private int selectedClassIndex = 0;
    private List<Map<String, Object>> passengers = new ArrayList<>();
    private String selectedPaymentMethod = "";
    private String paymentReference = "";
    private double grandTotal = 0;
    private int editingPassengerIndex = -1;
    private BookingRecord latestConfirmedBooking = null;
    private boolean isRefreshingSeatPanel;
    private int activeSeatPassengerIndex = 0;
    private JLabel[] classAvailLabels;
    private JLabel flightInfoLabel;

    // ========================= STEP NAVIGATION =========================
    private static final String[] STEP_IDS = { "HOME", "FLIGHTS", "PASSENGERS", "CLASS", "ADDONS", "BILLING",
            "SEATS", "PAYMENT", "RECEIPT" };
    private static final String[] STEP_TITLES = { "Home", "Flights", "Passengers", "Class", "Add-ons", "Seats",
            "Billing", "Payment", "Receipt" };
    private static final int STEP_HOME = 0;
    private static final int STEP_FLIGHTS = 1;
    private static final int STEP_PASSENGERS = 2;
    private static final int STEP_CLASS = 3;
    private static final int STEP_ADDONS = 4;
    private static final int STEP_SEATS = 5;
    private static final int STEP_BILLING = 6;
    private static final int STEP_PAYMENT = 7;
    private static final int STEP_RECEIPT = 8;
    private int currentStep = 0;

    // ========================= PASSENGER MAP KEYS =========================
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_AGE = "age";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_ID = "id";
    private static final String KEY_SENIOR = "senior";
    private static final String KEY_PWD = "pwd";
    private static final String KEY_ADDONS = "addons";
    private static final String KEY_SEAT = "seat";

    // ========================= UI COMPONENTS =========================
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel[] stepLabels;
    private JLabel[] stepArrows;
    private JProgressBar stepProgressBar;
    private JLabel stepHelperLabel;
    private JLabel clockLabel;
    private JButton btnBack, btnNext;
    private javax.swing.Timer stepTransitionTimer;
    private javax.swing.Timer helperMessageTimer;
    private javax.swing.Timer progressAnimationTimer;
    private boolean isStepTransitioning;
    // Flight panel components
    private JTable domesticTable, internationalTable, europeTable, northAmericaTable;
    private JTabbedPane flightTabs;
    private LocalDate calendarViewDate;
    private LocalDate calendarSelectedDate;
    private JPanel calendarDaysGrid;
    private JLabel calDateDisplayLabel;
    private JLabel calMonthYearLabel;

    // Passenger panel components
    private JTextField tfName, tfAddress, tfContact, tfAge, tfBday, tfId;
    private JCheckBox cbSenior, cbPWD;
    private DefaultListModel<String> passengerListModel;
    private JList<String> passengerJList;
    private JLabel passengerCountLabel;
    private JButton btnPassengerAction;
    private JButton btnCancelPassengerEdit;

    // Class panel components
    private JRadioButton[] classRadios;
    private JLabel classPriceLabel;

    // Add-on panel components
    private JPanel addonListPanel;
    private Map<Integer, JCheckBox[]> addonCheckboxMap = new HashMap<>();

    // Seat panel components
    private JPanel seatListPanel;
    private Map<Integer, JComboBox<String>> seatComboBoxMap = new LinkedHashMap<>();

    // Billing & Receipt
    private JTextArea billingTextArea;
    private JTextArea receiptTextArea;

    // Payment panel components
    private JRadioButton rbCredit, rbEwallet, rbDebit;
    private JTextField tfPaymentNumber, tfPaymentName, tfPaymentExpiry;
    private JCheckBox cbPolicyAcknowledged;
    private JLabel paymentDetailLabel;
    private JLabel paymentNumberLabel;
    private JLabel paymentNameLabel;
    private JLabel paymentExpiryLabel;
    private JComboBox<String> cbEwalletProvider;
    private JPanel ewalletProviderRow;
    private JPanel expiryRow;

    // ================================================================
    // CONSTRUCTOR
    // ================================================================
    public AirplaneTicketingSystem() {
        if (ADDON_NAMES.length != ADDON_PRICES.length) {
            throw new IllegalStateException("ADDON_NAMES and ADDON_PRICES must have the same length");
        }
        setTitle("SkyWings Airline - Airplane Ticketing System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(860, 640));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Unable to apply system look and feel: " + ex.getMessage());
        }

        try {
            BOOKING_STORE.seedFlightsIfEmpty(DOMESTIC_FLIGHTS, INTERNATIONAL_FLIGHTS,
                    DEFAULT_DOMESTIC_CAPACITY, DEFAULT_INTERNATIONAL_CAPACITY);
            BOOKING_STORE.seedFlightsIfEmpty(new Object[0][], EUROPE_FLIGHTS,
                    DEFAULT_EUROPE_CAPACITY, DEFAULT_EUROPE_CAPACITY);
            BOOKING_STORE.seedFlightsIfEmpty(new Object[0][], NORTH_AMERICA_FLIGHTS,
                    DEFAULT_NORTH_AMERICA_CAPACITY, DEFAULT_NORTH_AMERICA_CAPACITY);
        } catch (IOException ex) {
            System.err.println("Could not seed flights: " + ex.getMessage());
        }
        BOOKING_STORE.seedMockCompletedBookings();
        BOOKING_STORE.seedMockCancelledBookings();
        refreshActiveFlights();

        buildUI();
    }

    private static Map<String, Integer> createFlightCapacityMap() {
        Map<String, Integer> map = new HashMap<>();
        addFlightCapacityRows(map, DOMESTIC_FLIGHTS, DEFAULT_DOMESTIC_CAPACITY);
        addFlightCapacityRows(map, INTERNATIONAL_FLIGHTS, DEFAULT_INTERNATIONAL_CAPACITY);
        addFlightCapacityRows(map, EUROPE_FLIGHTS, DEFAULT_EUROPE_CAPACITY);
        addFlightCapacityRows(map, NORTH_AMERICA_FLIGHTS, DEFAULT_NORTH_AMERICA_CAPACITY);
        return map;
    }

    private static void addFlightCapacityRows(Map<String, Integer> target, Object[][] rows, int capacity) {
        for (Object[] row : rows) {
            target.put(String.valueOf(row[0]), capacity);
        }
    }

    private static Map<String, Integer> createBookedSeatMap() {
        // Seat counts are now derived entirely from BookingStore at runtime;
        // no hardcoded baseline is needed.
        return new HashMap<>();
    }

    private static Map<String, Integer> createDepartureOffsetMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("PR-101", 50); // early-morning — CLOSED for today (demo)
        map.put("PR-102", 150); // mid-morning — OPEN
        map.put("PR-103", 240); // noon — OPEN
        map.put("PR-104", 360); // afternoon — OPEN
        map.put("PR-105", 480); // evening — OPEN

        map.put("PR-201", 75); // morning — CLOSED for today (demo)
        map.put("PR-202", 180); // mid-morning — OPEN
        map.put("PR-203", 300); // afternoon — OPEN
        map.put("PR-204", 420); // late-afternoon— OPEN
        map.put("PR-205", 540); // evening — OPEN
        // Europe — well beyond 2-hr cutoff
        map.put("PR-301", 180);
        map.put("PR-302", 210);
        map.put("PR-303", 240);
        map.put("PR-304", 270);
        map.put("PR-305", 300);
        // North America — very long haul, far ahead
        map.put("PR-401", 360);
        map.put("PR-402", 420);
        map.put("PR-403", 480);
        map.put("PR-404", 540);
        map.put("PR-405", 600);
        return map;
    }

    private static Map<String, Integer> createDurationMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("PR-101", 90);
        map.put("PR-102", 115);
        map.put("PR-103", 75);
        map.put("PR-104", 95);
        map.put("PR-105", 80);

        map.put("PR-201", 220);
        map.put("PR-202", 205);
        map.put("PR-203", 235);
        map.put("PR-204", 165);
        map.put("PR-205", 250);
        // Europe (~14 hrs)
        map.put("PR-301", 840);
        map.put("PR-302", 855);
        map.put("PR-303", 870);
        map.put("PR-304", 830);
        map.put("PR-305", 845);
        // North America (~16-17 hrs)
        map.put("PR-401", 980);
        map.put("PR-402", 990);
        map.put("PR-403", 1020);
        map.put("PR-404", 960);
        map.put("PR-405", 970);
        return map;
    }

    private static Map<String, java.time.LocalTime> createScheduledTimesMap() {
        Map<String, java.time.LocalTime> m = new HashMap<>();
        m.put("PR-101", java.time.LocalTime.of(6, 0));
        m.put("PR-102", java.time.LocalTime.of(10, 0));
        m.put("PR-103", java.time.LocalTime.of(13, 0));
        m.put("PR-104", java.time.LocalTime.of(15, 0));
        m.put("PR-105", java.time.LocalTime.of(18, 0));
        m.put("PR-201", java.time.LocalTime.of(7, 0));
        m.put("PR-202", java.time.LocalTime.of(10, 30));
        m.put("PR-203", java.time.LocalTime.of(13, 30));
        m.put("PR-204", java.time.LocalTime.of(16, 0));
        m.put("PR-205", java.time.LocalTime.of(20, 0));
        // Europe (evening departures)
        m.put("PR-301", java.time.LocalTime.of(18, 0));
        m.put("PR-302", java.time.LocalTime.of(19, 0));
        m.put("PR-303", java.time.LocalTime.of(20, 0));
        m.put("PR-304", java.time.LocalTime.of(21, 0));
        m.put("PR-305", java.time.LocalTime.of(22, 0));
        // North America (late-night departures)
        m.put("PR-401", java.time.LocalTime.of(23, 0));
        m.put("PR-402", java.time.LocalTime.of(0, 30));
        m.put("PR-403", java.time.LocalTime.of(1, 0));
        m.put("PR-404", java.time.LocalTime.of(2, 0));
        m.put("PR-405", java.time.LocalTime.of(3, 0));
        return java.util.Collections.unmodifiableMap(m);
    }

    // ================================================================
    // BUILD MAIN UI
    // ================================================================
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // --- Top Header ---
        add(buildHeaderPanel(), BorderLayout.NORTH);

        // --- Center Content (Card Layout for wizard steps) ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_MAIN);

        contentPanel.add(buildHomePanel(), STEP_IDS[STEP_HOME]);
        contentPanel.add(buildFlightPanel(), STEP_IDS[STEP_FLIGHTS]);
        contentPanel.add(buildPassengerPanel(), STEP_IDS[STEP_PASSENGERS]);
        contentPanel.add(buildClassPanel(), STEP_IDS[STEP_CLASS]);
        contentPanel.add(buildAddonPanel(), STEP_IDS[STEP_ADDONS]);
        contentPanel.add(buildSeatPanel(), STEP_IDS[STEP_SEATS]);
        contentPanel.add(buildBillingPanel(), STEP_IDS[STEP_BILLING]);
        contentPanel.add(buildPaymentPanel(), STEP_IDS[STEP_PAYMENT]);
        contentPanel.add(buildReceiptPanel(), STEP_IDS[STEP_RECEIPT]);

        add(contentPanel, BorderLayout.CENTER);

        // --- Bottom Footer (Back / Next buttons) ---
        add(buildFooterPanel(), BorderLayout.SOUTH);

        cardLayout.show(contentPanel, STEP_IDS[STEP_HOME]);
        refreshNavigation();
    }

    // ================================================================
    // HEADER PANEL
    // ================================================================
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(SP_16, SP_24, SP_16, SP_24));

        JPanel titleRow = new JPanel(new BorderLayout(0, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("\u2708  SkyWings Airline");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titleRow.add(titleLabel, BorderLayout.WEST);

        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        updateClock();
        titleRow.add(clockLabel, BorderLayout.EAST);

        javax.swing.Timer clockTimer = new javax.swing.Timer(1000, e -> updateClock());
        clockTimer.start();

        header.add(titleRow);
        header.add(Box.createVerticalStrut(SP_10));

        JPanel stepsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SP_6, 0));
        stepsPanel.setOpaque(false);
        stepsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        stepLabels = new JLabel[STEP_TITLES.length];
        stepArrows = new JLabel[Math.max(0, STEP_TITLES.length - 1)];

        for (int i = 0; i < STEP_TITLES.length; i++) {
            stepLabels[i] = new JLabel((i + 1) + ". " + STEP_TITLES[i]);
            stepLabels[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            stepLabels[i].setOpaque(true);
            stepLabels[i].setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            stepsPanel.add(stepLabels[i]);

            if (i < STEP_TITLES.length - 1) {
                JLabel arrow = new JLabel("\u203a");
                arrow.setForeground(new Color(220, 226, 235, 165));
                arrow.setFont(new Font("Segoe UI", Font.BOLD, 16));
                stepArrows[i] = arrow;
                stepsPanel.add(arrow);
            }
        }

        JScrollPane stepScrollPane = new JScrollPane(
                stepsPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        stepScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        stepScrollPane.setOpaque(false);
        stepScrollPane.getViewport().setOpaque(false);
        stepScrollPane.setBorder(null);
        stepScrollPane.setPreferredSize(new Dimension(0, 42));
        stepScrollPane.getHorizontalScrollBar().setUnitIncrement(24);
        header.add(stepScrollPane);
        header.add(Box.createVerticalStrut(SP_10));

        stepProgressBar = new JProgressBar(1, STEP_TITLES.length);
        stepProgressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        stepProgressBar.setValue(1);
        stepProgressBar.setForeground(STEP_ACTIVE);
        stepProgressBar.setBackground(STEP_PENDING);
        stepProgressBar.setBorder(BorderFactory.createEmptyBorder());
        stepProgressBar.setStringPainted(true);
        stepProgressBar.setFont(FONT_SMALL);
        stepProgressBar.setPreferredSize(new Dimension(0, 22));
        stepProgressBar.setString("Step 1 of " + STEP_TITLES.length + " - " + STEP_TITLES[0]);
        header.add(stepProgressBar);

        return header;
    }

    // ================================================================
    // FOOTER PANEL
    // ================================================================
    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_CARD);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(SP_10, SP_20, SP_10, SP_20)));

        btnBack = createStyledButton("\u2190 Back", Color.WHITE, TEXT_PRIMARY);
        btnNext = createStyledButton("Next \u2192", PRIMARY, Color.WHITE);
        Dimension navBtnSize = new Dimension(140, 42);
        btnBack.setPreferredSize(navBtnSize);
        btnNext.setPreferredSize(navBtnSize);

        btnBack.addActionListener(e -> navigateBack());
        btnNext.addActionListener(e -> navigateNext());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(btnBack);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(btnNext);

        stepHelperLabel = new JLabel("Select a flight to begin your booking.");
        stepHelperLabel.setFont(FONT_SMALL);
        stepHelperLabel.setForeground(TEXT_SECONDARY);
        stepHelperLabel.setBorder(BorderFactory.createEmptyBorder(0, SP_12, 0, SP_12));

        footer.add(leftPanel, BorderLayout.WEST);
        footer.add(stepHelperLabel, BorderLayout.CENTER);
        footer.add(rightPanel, BorderLayout.EAST);

        return footer;
    }

    // ================================================================
    // STEP 1: HOME PANEL
    // ================================================================
    private JPanel buildHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Step 1: Home Dashboard");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JPanel heroCard = new JPanel();
        heroCard.setLayout(new BoxLayout(heroCard, BoxLayout.Y_AXIS));
        heroCard.setBackground(BG_CARD);
        heroCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        heroCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dashboardLabel = new JLabel("SKYWINGS OVERVIEW");
        dashboardLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dashboardLabel.setForeground(PRIMARY_LIGHT);
        dashboardLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        heroCard.add(dashboardLabel);
        heroCard.add(Box.createVerticalStrut(SP_6));

        JLabel welcome = new JLabel("Welcome to SkyWings");
        welcome.setFont(FONT_TITLE);
        welcome.setForeground(TEXT_PRIMARY);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        heroCard.add(welcome);
        heroCard.add(Box.createVerticalStrut(SP_6));

        JLabel intro = new JLabel(
                "<html>Check the available domestic and international routes first, then continue to select your flight.</html>");
        intro.setFont(FONT_BODY);
        intro.setForeground(TEXT_SECONDARY);
        intro.setAlignmentX(Component.LEFT_ALIGNMENT);
        heroCard.add(intro);
        heroCard.add(Box.createVerticalStrut(SP_12));

        int totalIntl = activeInternationalFlights.length + activeEuropeFlights.length
                + activeNorthAmericaFlights.length;
        int totalAll = activeDomesticFlights.length + totalIntl;
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.add(createOverviewStatCard("Domestic Routes", String.valueOf(activeDomesticFlights.length),
                "Philippines departures", ACCENT));
        statsRow.add(createOverviewStatCard("International Routes", String.valueOf(totalIntl),
                "SE Asia, Europe & North America", STEP_ACTIVE));
        statsRow.add(createOverviewStatCard("Total Available", String.valueOf(totalAll),
                "Current routes listed", SUCCESS_GREEN));
        heroCard.add(statsRow);
        heroCard.add(Box.createVerticalStrut(SP_12));

        JButton browseFlights = createStyledButton("Browse Flights \u2192", PRIMARY, Color.WHITE);
        browseFlights.addActionListener(e -> navigateNext());
        JButton manageBooking = createStyledButton("Manage Booking", Color.WHITE, TEXT_PRIMARY);
        manageBooking.addActionListener(e -> openManageBookingDialog(paymentReference));
        JButton viewReports = createStyledButton("View Reports", Color.WHITE, TEXT_PRIMARY);
        viewReports.addActionListener(e -> openReportsDialog());
        JButton manageFlights = createStyledButton("Manage Flights", Color.WHITE, TEXT_PRIMARY);
        manageFlights.addActionListener(e -> openFlightManagementDialog());

        JPanel ctaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ctaRow.setOpaque(false);
        ctaRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        ctaRow.add(browseFlights);
        ctaRow.add(manageBooking);
        ctaRow.add(viewReports);
        ctaRow.add(manageFlights);
        heroCard.add(ctaRow);
        heroCard.add(Box.createVerticalStrut(SP_12));
        heroCard.add(createFlightPolicyCard());
        content.add(heroCard);
        content.add(Box.createVerticalStrut(SP_12));

        JPanel routesRow = new JPanel(new GridLayout(2, 2, 12, 12));
        routesRow.setOpaque(false);
        routesRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        routesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 700));
        routesRow.add(createRouteOverviewCard("Domestic Flights", "Philippines departures and arrivals",
                activeDomesticFlights, ACCENT, true));
        routesRow.add(createRouteOverviewCard("International — SE Asia", "Southeast Asia departures and arrivals",
                activeInternationalFlights, new Color(121, 87, 255), false));
        routesRow.add(createRouteOverviewCard("International — Europe", "European destinations from Manila",
                activeEuropeFlights, new Color(0, 140, 160), false));
        routesRow
                .add(createRouteOverviewCard("International — North America", "North American destinations from Manila",
                        activeNorthAmericaFlights, new Color(180, 90, 0), false));
        content.add(routesRow);
        content.add(Box.createVerticalStrut(SP_10));

        JLabel hint = new JLabel(
                "  \u2139  Review route availability, then continue to flight selection.");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_SECONDARY);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(hint);

        JScrollPane contentScroll = new JScrollPane(content);
        contentScroll.setBorder(null);
        contentScroll.getViewport().setBackground(BG_MAIN);
        contentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(contentScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFlightPolicyCard() {
        JPanel policyCard = new JPanel();
        policyCard.setLayout(new BoxLayout(policyCard, BoxLayout.Y_AXIS));
        policyCard.setBackground(new Color(250, 251, 253));
        policyCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        policyCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Flight Policies");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        policyCard.add(title);
        policyCard.add(Box.createVerticalStrut(4));

        JLabel p1 = new JLabel("- " + POLICY_OVERBOOKING);
        p1.setFont(FONT_SMALL);
        p1.setForeground(TEXT_SECONDARY);
        p1.setAlignmentX(Component.LEFT_ALIGNMENT);
        policyCard.add(p1);

        JLabel p2 = new JLabel("- " + POLICY_CHECKIN);
        p2.setFont(FONT_SMALL);
        p2.setForeground(TEXT_SECONDARY);
        p2.setAlignmentX(Component.LEFT_ALIGNMENT);
        policyCard.add(p2);

        JLabel p3 = new JLabel("- " + POLICY_REFUND);
        p3.setFont(FONT_SMALL);
        p3.setForeground(TEXT_SECONDARY);
        p3.setAlignmentX(Component.LEFT_ALIGNMENT);
        policyCard.add(p3);

        return policyCard;
    }

    private JPanel createOverviewStatCard(String labelText, String valueText, String detailText, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        accentBar.setPreferredSize(new Dimension(0, 4));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(accentBar);
        card.add(Box.createVerticalStrut(SP_10));

        JLabel label = new JLabel(labelText.toUpperCase(Locale.ENGLISH));
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(label);
        card.add(Box.createVerticalStrut(2));

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 22));
        value.setForeground(PRIMARY);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(value);

        JLabel caption = new JLabel(labelText);
        caption.setFont(FONT_BODY);
        caption.setForeground(TEXT_PRIMARY);
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(caption);

        JLabel detail = new JLabel(detailText);
        detail.setFont(FONT_SMALL);
        detail.setForeground(TEXT_SECONDARY);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(detail);
        return card;
    }

    private JPanel createRouteOverviewCard(String titleText, String description, Object[][] sourceFlights,
            Color accentColor, boolean isDomesticType) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JPanel topAccent = new JPanel();
        topAccent.setBackground(accentColor);
        topAccent.setPreferredSize(new Dimension(0, 4));
        card.add(topAccent, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG_CARD);
        body.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JPanel headingBlock = new JPanel();
        headingBlock.setLayout(new BoxLayout(headingBlock, BoxLayout.Y_AXIS));
        headingBlock.setOpaque(false);

        JLabel heading = new JLabel(titleText);
        heading.setFont(FONT_SUBTITLE);
        heading.setForeground(TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        headingBlock.add(heading);
        headingBlock.add(Box.createVerticalStrut(2));

        JLabel desc = new JLabel(description);
        desc.setFont(FONT_SMALL);
        desc.setForeground(TEXT_SECONDARY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        headingBlock.add(desc);

        JButton viewMoreBtn = createStyledButton("View Full Flights", PRIMARY, Color.WHITE);
        viewMoreBtn.setFont(FONT_SMALL);
        viewMoreBtn.setPreferredSize(new Dimension(150, 32));
        viewMoreBtn.addActionListener(e -> {
            // Always rebuild from the exact source array so each card shows its own flights
            Object[][] freshRows = buildFlightRowsWithRealtimeEst(sourceFlights);
            showFlightCatalogDialog(titleText, freshRows, isDomesticType);
        });

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRow.setOpaque(false);
        actionRow.add(viewMoreBtn);

        JPanel headerRow = new JPanel(new BorderLayout(10, 0));
        headerRow.setOpaque(false);
        headerRow.add(headingBlock, BorderLayout.CENTER);
        headerRow.add(actionRow, BorderLayout.EAST);
        body.add(headerRow, BorderLayout.NORTH);

        // Build EST rows from the source array for the compact summary
        JPanel summaryWrap = createRouteSummaryPanel(buildFlightRowsWithRealtimeEst(sourceFlights));
        body.add(summaryWrap, BorderLayout.CENTER);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRouteSummaryPanel(Object[][] rows) {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(new Color(250, 251, 253));
        wrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JLabel title = new JLabel("Compact Summary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(title);
        wrap.add(Box.createVerticalStrut(4));

        JLabel count = new JLabel("Available Flights: " + rows.length);
        count.setFont(FONT_SMALL);
        count.setForeground(TEXT_SECONDARY);
        count.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(count);

        if (rows.length > 0) {
            Object[] nextFlight = rows[0];
            String nextText = "Next: " + nextFlight[0] + "  " + nextFlight[1] + " -> " + nextFlight[2]
                    + "  (EST " + nextFlight[3] + ")";
            JLabel next = new JLabel(nextText);
            next.setFont(FONT_SMALL);
            next.setForeground(TEXT_SECONDARY);
            next.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrap.add(next);
        }

        double lowestFare = Double.MAX_VALUE;
        for (Object[] row : rows) {
            if (row[4] instanceof Number) {
                lowestFare = Math.min(lowestFare, ((Number) row[4]).doubleValue());
            }
        }
        if (lowestFare < Double.MAX_VALUE) {
            JLabel fare = new JLabel(String.format("Lowest Fare: PHP %,.2f", lowestFare));
            fare.setFont(FONT_SMALL);
            fare.setForeground(TEXT_SECONDARY);
            fare.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrap.add(fare);
        }

        wrap.add(Box.createVerticalStrut(6));
        JLabel note = new JLabel("Tap 'View Full Flights' for complete list and detailed info.");
        note.setFont(FONT_SMALL);
        note.setForeground(TEXT_SECONDARY);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(note);

        wrap.setPreferredSize(new Dimension(0, 120));
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        return wrap;
    }

    private void showFlightCatalogDialog(String titleText, Object[][] rows, boolean isDomesticType) {
        JDialog dialog = new JDialog(this, titleText + " - Full Details", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(860, 560);
        dialog.setMinimumSize(new Dimension(760, 500));
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(BG_MAIN);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel(titleText);
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        root.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(rows, FLIGHT_COLUMNS_WITH_EST) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = createStyledTable(model);
        applyStatusColumnRenderer(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setToolTipText("Select a row to view complete flight details");
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        tableScroll.getViewport().setBackground(BG_CARD);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(FONT_MONO);
        detailsArea.setBackground(BG_CARD);
        detailsArea.setForeground(TEXT_PRIMARY);
        detailsArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        detailsScroll.setPreferredSize(new Dimension(0, 190));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                return;
            }

            int modelRow = table.convertRowIndexToModel(selectedRow);
            Object[] selected = {
                    model.getValueAt(modelRow, 0),
                    model.getValueAt(modelRow, 1),
                    model.getValueAt(modelRow, 2),
                    model.getValueAt(modelRow, 4)
            };
            updateFlightDetailPreview(detailsArea, selected, isDomesticType);
        });

        if (rows.length > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, detailsScroll);
        split.setResizeWeight(0.64);
        split.setDividerSize(8);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        JButton closeBtn = createStyledButton("Close", Color.WHITE, TEXT_PRIMARY);
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(closeBtn);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void showModifyFlightDetailsDialog(JDialog ownerDialog, JComboBox<String> bookingFlightCombo,
            Map<String, String> flightOptionMap) {
        if (flightOptionMap.isEmpty()) {
            JOptionPane.showMessageDialog(ownerDialog,
                    "No flights are currently available for this booking plan.",
                    "Flight Details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String initialOption = (String) bookingFlightCombo.getSelectedItem();
        if (initialOption == null || !flightOptionMap.containsKey(initialOption)) {
            initialOption = flightOptionMap.keySet().iterator().next();
        }

        JDialog detailsDialog = new JDialog(ownerDialog, "Flight Details", false);
        detailsDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        detailsDialog.setSize(860, 560);
        detailsDialog.setMinimumSize(new Dimension(760, 500));
        detailsDialog.setLocationRelativeTo(ownerDialog);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(BG_MAIN);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        JLabel title = new JLabel("Flight Full Details");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel(" ");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        root.add(header, BorderLayout.NORTH);

        JComboBox<String> detailFlightCombo = new JComboBox<>(flightOptionMap.keySet().toArray(new String[0]));
        styleComboBox(detailFlightCombo);
        detailFlightCombo.setSelectedItem(initialOption);

        JPanel selectorRow = createFormRow("Flight:", detailFlightCombo);
        selectorRow.setBackground(BG_MAIN);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(FONT_MONO);
        detailsArea.setBackground(BG_CARD);
        detailsArea.setForeground(TEXT_PRIMARY);
        detailsArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        Runnable refreshDetails = () -> {
            String selectedOption = (String) detailFlightCombo.getSelectedItem();
            String selectedFlightNo = selectedOption != null ? flightOptionMap.get(selectedOption) : null;
            Object[] selectedFlightRow = selectedFlightNo != null ? findFlightRowByNumber(selectedFlightNo) : null;

            if (selectedFlightNo == null || selectedFlightRow == null) {
                subtitle.setText("Flight details are currently unavailable.");
                detailsArea.setText("The selected flight could not be loaded.");
                detailsArea.setCaretPosition(0);
                return;
            }

            boolean isDomesticType = isDomesticFlight(selectedFlightNo);
            subtitle.setText(selectedFlightNo + " | " + selectedFlightRow[1] + " -> " + selectedFlightRow[2]);
            updateFlightDetailPreview(detailsArea, selectedFlightRow, isDomesticType);
        };
        detailFlightCombo.addActionListener(e -> refreshDetails.run());
        refreshDetails.run();

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(selectorRow, BorderLayout.NORTH);
        center.add(detailsScroll, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JButton useSelectedBtn = createStyledButton("Use Selected Flight", PRIMARY, Color.WHITE);
        useSelectedBtn.addActionListener(e -> {
            Object selected = detailFlightCombo.getSelectedItem();
            if (selected != null) {
                bookingFlightCombo.setSelectedItem(selected.toString());
            }
            detailsDialog.dispose();
        });

        JButton closeBtn = createStyledButton("Close", Color.WHITE, TEXT_PRIMARY);
        closeBtn.addActionListener(e -> detailsDialog.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.add(useSelectedBtn);
        footer.add(closeBtn);
        root.add(footer, BorderLayout.SOUTH);

        detailsDialog.setContentPane(root);
        detailsDialog.setVisible(true);
    }

    private void updateFlightDetailPreview(JTextArea detailsArea, Object[] selectedFlightRow, boolean isDomesticType) {
        String flightNo = String.valueOf(selectedFlightRow[0]);
        double baseFare = ((Number) selectedFlightRow[3]).doubleValue();
        int capacity = getFlightCapacity(flightNo);
        int overbookingLimit = getFlightOverbookingLimit(flightNo);
        int bookedSeats = FLIGHT_BOOKED_SEATS.getOrDefault(flightNo, 0);
        int remainingSeats = getRemainingSlotsForFlight(flightNo);
        StringBuilder sb = new StringBuilder();
        sb.append("FLIGHT DETAILS").append("\n");
        sb.append("------------------------------------------------------------").append("\n");
        sb.append("Flight No.    : ").append(flightNo).append("\n");
        sb.append("Departure     : ").append(selectedFlightRow[1]).append("\n");
        sb.append("Arrival       : ").append(selectedFlightRow[2]).append("\n");
        sb.append("Route Type    : ")
                .append(isDomesticType ? "Domestic (Philippines)" : "International (Southeast Asia)")
                .append("\n");
        sb.append("Flight Date   : ").append(getFormattedFlightDate()).append("\n");
        sb.append("Realtime EST  : ").append(getRealtimeEstSummary(flightNo)).append("\n");
        sb.append(String.format("Base Fare     : PHP %,.2f%n", baseFare));
        sb.append("\n");

        sb.append("ESTIMATED FARE BY CLASS").append("\n");
        sb.append("------------------------------------------------------------").append("\n");
        for (int i = 0; i < CLASS_NAMES.length; i++) {
            sb.append(String.format("%-18s PHP %,.2f%n", CLASS_NAMES[i] + ":", baseFare * CLASS_MULTIPLIERS[i]));
        }
        sb.append("\n");

        sb.append("ADD-ONS (PER PASSENGER)").append("\n");
        sb.append("------------------------------------------------------------").append("\n");
        for (int i = 0; i < ADDON_NAMES.length; i++) {
            if (ADDON_NAMES[i].equalsIgnoreCase(ADDON_TAX)) {
                sb.append(String.format("%-18s 12%% VAT (required)%n", ADDON_NAMES[i] + ":"));
            } else {
                sb.append(String.format("%-18s PHP %,.2f%n", ADDON_NAMES[i] + ":", ADDON_PRICES[i]));
            }
        }
        sb.append("\nOVERBOOKING POLICY STATUS").append("\n");
        sb.append("------------------------------------------------------------").append("\n");
        sb.append("Seat Capacity : ").append(capacity).append("\n");
        sb.append("Policy Limit  : ").append(overbookingLimit)
                .append(" bookings (").append((int) (OVERBOOKING_BUFFER_RATE * 100)).append("% buffer)\n");
        sb.append("Booked Seats  : ").append(bookedSeats).append("\n");
        sb.append("Slots Left    : ").append(remainingSeats).append("\n");
        sb.append("\nFLIGHT POLICIES").append("\n");
        sb.append("------------------------------------------------------------").append("\n");
        sb.append("- ").append(POLICY_OVERBOOKING).append("\n");
        sb.append("- ").append(POLICY_CHECKIN).append("\n");
        sb.append("- ").append(POLICY_REFUND).append("\n");
        sb.append("\nNote: Senior/PWD passengers get 20% discount on subtotal.");

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    // STEP 2: FLIGHT SELECTION PANEL
    // ================================================================
    private JPanel buildFlightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Step 2: Select Your Flight");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(14, 0));
        centerPanel.setOpaque(false);

        JPanel flightDateCard = new JPanel();
        flightDateCard.setLayout(new BoxLayout(flightDateCard, BoxLayout.Y_AXIS));
        flightDateCard.setBackground(BG_CARD);
        flightDateCard.setBorder(createCardBorder(12, 16, 12, 16));

        JLabel flightDateTitle = new JLabel("Travel Date");
        flightDateTitle.setFont(FONT_SUBTITLE);
        flightDateTitle.setForeground(TEXT_PRIMARY);
        flightDateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        LocalDate today = LocalDate.now();
        calendarViewDate = today;
        calendarSelectedDate = today;
        JPanel calendarWidget = buildInlineCalendar();
        calendarWidget.setAlignmentX(Component.LEFT_ALIGNMENT);

        flightDateCard.add(flightDateTitle);
        flightDateCard.add(Box.createVerticalStrut(SP_10));
        flightDateCard.add(calendarWidget);

        flightTabs = new JTabbedPane();
        flightTabs.setFont(FONT_BODY);
        flightTabs.setBackground(BG_CARD);
        flightTabs.setForeground(TEXT_PRIMARY);
        flightTabs.setFocusable(false);

        // Domestic flights table
        Object[][] domesticRows = buildFlightRowsWithRealtimeEst(activeDomesticFlights);
        DefaultTableModel domesticModel = new DefaultTableModel(domesticRows, FLIGHT_COLUMNS_WITH_EST) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        domesticTable = createStyledTable(domesticModel);
        domesticTable.setToolTipText("Select one domestic flight to continue");
        applyStatusColumnRenderer(domesticTable);
        JScrollPane domesticScroll = new JScrollPane(domesticTable);
        domesticScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        domesticScroll.getViewport().setBackground(BG_CARD);
        flightTabs.addTab("  Domestic (Philippines)  ", domesticScroll);

        // International flights table
        Object[][] intlRows = buildFlightRowsWithRealtimeEst(activeInternationalFlights);
        DefaultTableModel intlModel = new DefaultTableModel(intlRows, FLIGHT_COLUMNS_WITH_EST) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        internationalTable = createStyledTable(intlModel);
        internationalTable.setToolTipText("Select one international flight to continue");
        applyStatusColumnRenderer(internationalTable);
        JScrollPane intlScroll = new JScrollPane(internationalTable);
        intlScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        intlScroll.getViewport().setBackground(BG_CARD);
        flightTabs.addTab("  International (SE Asia)  ", intlScroll);

        // Europe flights table
        Object[][] europeRows = buildFlightRowsWithRealtimeEst(activeEuropeFlights);
        DefaultTableModel europeModel = new DefaultTableModel(europeRows, FLIGHT_COLUMNS_WITH_EST) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        europeTable = createStyledTable(europeModel);
        europeTable.setToolTipText("Select one European flight to continue");
        applyStatusColumnRenderer(europeTable);
        JScrollPane europeScroll = new JScrollPane(europeTable);
        europeScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        europeScroll.getViewport().setBackground(BG_CARD);
        flightTabs.addTab("  International (Europe)  ", europeScroll);

        // North America flights table
        Object[][] naRows = buildFlightRowsWithRealtimeEst(activeNorthAmericaFlights);
        DefaultTableModel naModel = new DefaultTableModel(naRows, FLIGHT_COLUMNS_WITH_EST) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        northAmericaTable = createStyledTable(naModel);
        northAmericaTable.setToolTipText("Select one North American flight to continue");
        applyStatusColumnRenderer(northAmericaTable);
        JScrollPane naScroll = new JScrollPane(northAmericaTable);
        naScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        naScroll.getViewport().setBackground(BG_CARD);
        flightTabs.addTab("  International (North America)  ", naScroll);

        // Clear other tables' selection when switching tabs
        flightTabs.addChangeListener(e -> {
            int idx = flightTabs.getSelectedIndex();
            if (idx != 0)
                domesticTable.clearSelection();
            if (idx != 1)
                internationalTable.clearSelection();
            if (idx != 2 && europeTable != null)
                europeTable.clearSelection();
            if (idx != 3 && northAmericaTable != null)
                northAmericaTable.clearSelection();
            updateFlightInfoLabel();
        });

        // Flight capacity info label — shown below the table when a flight is selected
        flightInfoLabel = new JLabel("  Select a flight row to see seat capacity details.");
        flightInfoLabel.setFont(FONT_SMALL);
        flightInfoLabel.setForeground(TEXT_SECONDARY);
        flightInfoLabel.setBackground(BG_CARD);
        flightInfoLabel.setOpaque(true);
        flightInfoLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        ListSelectionListener flightSelectionListener = e -> {
            if (!e.getValueIsAdjusting())
                updateFlightInfoLabel();
        };
        domesticTable.getSelectionModel().addListSelectionListener(flightSelectionListener);
        internationalTable.getSelectionModel().addListSelectionListener(flightSelectionListener);
        europeTable.getSelectionModel().addListSelectionListener(flightSelectionListener);
        northAmericaTable.getSelectionModel().addListSelectionListener(flightSelectionListener);

        JPanel tabsWithInfo = new JPanel(new BorderLayout());
        tabsWithInfo.setOpaque(false);
        tabsWithInfo.add(flightTabs, BorderLayout.CENTER);
        tabsWithInfo.add(flightInfoLabel, BorderLayout.SOUTH);

        flightDateCard.setPreferredSize(new Dimension(272, 10));
        centerPanel.add(flightDateCard, BorderLayout.WEST);
        centerPanel.add(tabsWithInfo, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        JLabel hint = new JLabel("  \u2139  Select a flight and travel date above, then click Next.");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_SECONDARY);
        panel.add(hint, BorderLayout.SOUTH);

        return panel;
    }

    // ================================================================
    // STEP 2: PASSENGER DETAILS PANEL
    // ================================================================
    private JPanel buildPassengerPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Step 3: Passenger Details");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        // Split: input form on the left, passenger list on the right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(460);
        splitPane.setResizeWeight(0.55);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);

        // ----- LEFT: Input Form Card -----
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(BG_CARD);
        formCard.setBorder(createCardBorder(15, 20, 15, 20));

        JLabel formTitle = new JLabel("Enter Passenger Information");
        formTitle.setFont(FONT_SUBTITLE);
        formTitle.setForeground(TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(SP_12));

        // Text fields with inline suggestions
        tfName = new PlaceholderTextField(20, "e.g., Juan Dela Cruz");
        tfAddress = new PlaceholderTextField(20, "House no., Street, City");
        tfContact = new PlaceholderTextField(20, "e.g., +63 917 123 4567");
        tfAge = new PlaceholderTextField(20, "1 - 120");
        tfBday = new PlaceholderTextField(20, "YYYY-MM-DD");
        tfId = new PlaceholderTextField(20, "Gov't ID no. (5-25 chars)");
        installPassengerInputFilters();
        tfName.setToolTipText("Passenger full legal name");
        tfAddress.setToolTipText("Current residential address");
        tfContact.setToolTipText("Contact number (e.g., +63...)");
        tfAge.setEditable(false);
        tfAge.setBackground(BG_MUTED);
        tfAge.setToolTipText("Auto-calculated from Birthday");
        tfBday.setToolTipText("Birthday (e.g., 2000-12-31) — Age is filled automatically");
        tfId.setToolTipText("Valid government ID number");

        // Auto-calculate age whenever birthday changes
        tfBday.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void recalcAge() {
                String text = tfBday.getText().trim();
                if (text.length() == 10) {
                    try {
                        LocalDate birth = LocalDate.parse(text, BIRTHDAY_FORMATTER);
                        LocalDate today = LocalDate.now();
                        if (!birth.isAfter(today)) {
                            tfAge.setText(String.valueOf(Period.between(birth, today).getYears()));
                            return;
                        }
                    } catch (Exception ignored) {
                    }
                }
                tfAge.setText("");
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                recalcAge();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                recalcAge();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                recalcAge();
            }
        });
        tfBday.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showBirthdayPicker();
            }
        });
        tfBday.setEditable(false);
        tfBday.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        formCard.add(createFormRow("Full Name:", tfName));
        formCard.add(Box.createVerticalStrut(SP_6));
        formCard.add(createFormRow("Address:", tfAddress));
        formCard.add(Box.createVerticalStrut(SP_6));
        formCard.add(createFormRow("Contact #:", tfContact));
        formCard.add(Box.createVerticalStrut(SP_6));
        formCard.add(createFormRow("Birthday:", tfBday));
        formCard.add(Box.createVerticalStrut(SP_6));
        formCard.add(createFormRow("Age:", tfAge));
        formCard.add(Box.createVerticalStrut(SP_6));
        formCard.add(createFormRow("ID Number:", tfId));
        formCard.add(Box.createVerticalStrut(SP_10));

        // Senior / PWD checkboxes
        JPanel discountRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        discountRow.setBackground(BG_CARD);
        discountRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        discountRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        cbSenior = new JCheckBox("Senior Citizen (20% off)");
        cbPWD = new JCheckBox("PWD (20% off)");
        cbSenior.setFont(FONT_BODY);
        cbSenior.setBackground(BG_CARD);
        cbPWD.setFont(FONT_BODY);
        cbPWD.setBackground(BG_CARD);
        cbSenior.setFocusPainted(false);
        cbPWD.setFocusPainted(false);
        discountRow.add(cbSenior);
        discountRow.add(cbPWD);
        JLabel lblDiscountNote = new JLabel("(Max 20% — applies once even if both are checked)");
        lblDiscountNote.setFont(FONT_SMALL);
        lblDiscountNote.setForeground(TEXT_SECONDARY);
        discountRow.add(lblDiscountNote);
        formCard.add(discountRow);
        formCard.add(Box.createVerticalStrut(SP_12));

        // Add Passenger button
        btnPassengerAction = createStyledButton("+ Add Passenger", PRIMARY, Color.WHITE);
        btnPassengerAction.addActionListener(e -> handlePassengerAction());
        btnCancelPassengerEdit = createStyledButton("Cancel Edit", Color.WHITE, TEXT_PRIMARY);
        btnCancelPassengerEdit.addActionListener(e -> cancelPassengerEdit());
        btnCancelPassengerEdit.setVisible(false);
        JPanel addBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addBtnRow.setBackground(BG_CARD);
        addBtnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addBtnRow.add(btnPassengerAction);
        addBtnRow.add(Box.createHorizontalStrut(10));
        addBtnRow.add(btnCancelPassengerEdit);
        formCard.add(addBtnRow);

        splitPane.setLeftComponent(formCard);

        // ----- RIGHT: Passenger List Card -----
        JPanel listCard = new JPanel(new BorderLayout(0, 10));
        listCard.setBackground(BG_CARD);
        listCard.setBorder(createCardBorder(15, 15, 15, 15));

        JLabel listTitle = new JLabel("Added Passengers");
        listTitle.setFont(FONT_SUBTITLE);
        listTitle.setForeground(TEXT_PRIMARY);
        passengerCountLabel = new JLabel("0 passenger(s)");
        passengerCountLabel.setFont(FONT_SMALL);
        passengerCountLabel.setForeground(TEXT_SECONDARY);
        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setBackground(BG_CARD);
        listHeader.add(listTitle, BorderLayout.WEST);
        listHeader.add(passengerCountLabel, BorderLayout.EAST);
        listCard.add(listHeader, BorderLayout.NORTH);

        passengerListModel = new DefaultListModel<>();
        passengerJList = new JList<>(passengerListModel);
        passengerJList.setFont(FONT_BODY);
        passengerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        passengerJList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        passengerJList.setFixedCellHeight(32);
        passengerJList.setSelectionBackground(ACCENT);
        passengerJList.setSelectionForeground(Color.WHITE);
        passengerJList.setBackground(BG_CARD);
        passengerJList.setForeground(TEXT_PRIMARY);
        JScrollPane listScroll = new JScrollPane(passengerJList);
        listScroll.getViewport().setBackground(BG_CARD);
        listScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        listCard.add(listScroll, BorderLayout.CENTER);

        JButton btnEdit = createStyledButton("Edit Selected", Color.WHITE, TEXT_PRIMARY);
        btnEdit.addActionListener(e -> beginPassengerEdit());
        JButton btnRemove = createStyledButton("Remove Selected", DANGER_RED, Color.WHITE);
        btnRemove.addActionListener(e -> removePassenger());
        JPanel removeBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        removeBtnRow.setBackground(BG_CARD);
        removeBtnRow.add(btnEdit);
        removeBtnRow.add(btnRemove);
        listCard.add(removeBtnRow, BorderLayout.SOUTH);

        splitPane.setRightComponent(listCard);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    // STEP 3: CLASS SELECTION PANEL
    // ================================================================
    private JPanel buildClassPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Step 4: Select Flight Class");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(createCardBorder(SP_30, 35, SP_30, 35));

        JLabel subtitle = new JLabel("Choose a class for all passengers:");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_PRIMARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(SP_16));

        ButtonGroup classGroup = new ButtonGroup();
        classRadios = new JRadioButton[CLASS_NAMES.length];
        classAvailLabels = new JLabel[CLASS_NAMES.length];

        String[] descriptions = {
                "Standard seating with complimentary snacks",
                "Extra legroom and priority boarding",
                "Lie-flat seats and premium dining",
                "Private suite with exclusive lounge access"
        };
        String[] configs = { "3+3 layout", "2+2 layout", "2+1 layout", "1+1 layout" };

        for (int i = 0; i < CLASS_NAMES.length; i++) {
            classRadios[i] = new JRadioButton(
                    CLASS_NAMES[i] + "  (" + CLASS_MULTIPLIERS[i] + "x base fare)  -  " + descriptions[i]);
            classRadios[i].setFont(new Font("Segoe UI", Font.PLAIN, 15));
            classRadios[i].setBackground(BG_CARD);
            classRadios[i].setBorder(BorderFactory.createEmptyBorder(8, 5, 4, 5));
            classRadios[i].setFocusPainted(false);
            final int idx = i;
            classRadios[i].addActionListener(e -> {
                selectedClassIndex = idx;
                updateClassPriceLabel();
            });
            classGroup.add(classRadios[i]);
            card.add(classRadios[i]);

            classAvailLabels[i] = new JLabel("   " + configs[i] + "  —  checking availability...");
            classAvailLabels[i].setFont(new Font("Segoe UI", Font.ITALIC, 12));
            classAvailLabels[i].setForeground(TEXT_SECONDARY);
            classAvailLabels[i].setBorder(BorderFactory.createEmptyBorder(0, 26, 8, 5));
            classAvailLabels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(classAvailLabels[i]);
        }
        classRadios[0].setSelected(true);

        card.add(Box.createVerticalStrut(SP_20));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(15));

        classPriceLabel = new JLabel("Calculating...");
        classPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        classPriceLabel.setForeground(PRIMARY);
        classPriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(classPriceLabel);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    // STEP 4: ADD-ONS PANEL
    // ================================================================
    private JPanel buildAddonPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Step 5: Select Add-Ons (Per Passenger)");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        // Dynamic content panel (rebuilt when navigating here)
        addonListPanel = new JPanel();
        addonListPanel.setLayout(new BoxLayout(addonListPanel, BoxLayout.Y_AXIS));
        addonListPanel.setBackground(BG_MAIN);

        JScrollPane scrollPane = new JScrollPane(addonListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        JLabel hint = new JLabel(
                "  \u2139  Check add-ons per passenger. Note: 'Tax' is a mandatory fixed fee and is pre-selected.");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_SECONDARY);
        panel.add(hint, BorderLayout.SOUTH);

        return panel;
    }

    // ================================================================
    // STEP 5: SEAT PANEL
    // ================================================================
    private JPanel buildSeatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Step 6: Seat Assignment");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);

        JLabel hint = new JLabel(
                "  \u2139  Select a passenger on the left, then click a seat on the map to assign it.");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_SECONDARY);

        JPanel northPanel = new JPanel(new BorderLayout(0, 4));
        northPanel.setOpaque(false);
        northPanel.add(title, BorderLayout.NORTH);
        northPanel.add(hint, BorderLayout.SOUTH);
        panel.add(northPanel, BorderLayout.NORTH);

        // Left: passenger list
        seatListPanel = new JPanel();
        seatListPanel.setLayout(new BoxLayout(seatListPanel, BoxLayout.Y_AXIS));
        seatListPanel.setBackground(BG_MAIN);
        JScrollPane leftScroll = new JScrollPane(seatListPanel);
        leftScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        leftScroll.getViewport().setBackground(BG_MAIN);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Right: embedded seat map
        seatMapPanel = new JPanel(new BorderLayout());
        seatMapPanel.setBackground(BG_MAIN);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, seatMapPanel);
        split.setDividerLocation(230);
        split.setDividerSize(6);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    // ================================================================
    // STEP 6: BILLING PANEL
    // ================================================================
    private JPanel buildBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Step 7: Billing Summary");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        billingTextArea = new JTextArea();
        billingTextArea.setFont(FONT_MONO);
        billingTextArea.setEditable(false);
        billingTextArea.setBackground(BG_CARD);
        billingTextArea.setForeground(TEXT_PRIMARY);
        billingTextArea.setBorder(BorderFactory.createEmptyBorder(SP_20, SP_20, SP_20, SP_20));
        billingTextArea.setLineWrap(false);
        billingTextArea.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(billingTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ================================================================
    // STEP 7: PAYMENT PANEL
    // ================================================================
    private JPanel buildPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Step 8: Payment Method");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(createCardBorder(25, SP_30, 25, SP_30));

        // Payment method radio buttons
        JLabel methodLabel = new JLabel("Choose Payment Method:");
        methodLabel.setFont(FONT_SUBTITLE);
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(methodLabel);
        card.add(Box.createVerticalStrut(SP_12));

        ButtonGroup paymentGroup = new ButtonGroup();
        rbCredit = new JRadioButton("Credit Card");
        rbEwallet = new JRadioButton("E-Wallet (GCash, Maya, etc.)");
        rbDebit = new JRadioButton("Debit Card");
        rbCredit.setSelected(true);

        for (JRadioButton rb : new JRadioButton[] { rbCredit, rbEwallet, rbDebit }) {
            rb.setFont(FONT_BODY);
            rb.setBackground(BG_CARD);
            rb.setFocusPainted(false);
            paymentGroup.add(rb);
        }

        ActionListener paymentSwitch = e -> updatePaymentFields();
        rbCredit.addActionListener(paymentSwitch);
        rbEwallet.addActionListener(paymentSwitch);
        rbDebit.addActionListener(paymentSwitch);

        JPanel radioRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        radioRow.setBackground(BG_CARD);
        radioRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        radioRow.add(rbCredit);
        radioRow.add(rbEwallet);
        radioRow.add(rbDebit);
        card.add(radioRow);
        card.add(Box.createVerticalStrut(SP_20));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(15));

        // Payment details fields
        paymentDetailLabel = new JLabel("Credit Card Details:");
        paymentDetailLabel.setFont(FONT_SUBTITLE);
        paymentDetailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(paymentDetailLabel);
        card.add(Box.createVerticalStrut(SP_12));

        tfPaymentNumber = new JTextField(25);
        tfPaymentName = new JTextField(25);
        tfPaymentExpiry = new JTextField(25);
        tfPaymentNumber.setToolTipText("Enter card/account number");
        tfPaymentName.setToolTipText("Enter account holder name");
        tfPaymentExpiry.setToolTipText("Use MM/YY for cards or transaction reference for e-wallet");

        // Build the number row manually so we can update its label when the method
        // changes
        paymentNumberLabel = new JLabel("Card/Account #:");
        paymentNumberLabel.setFont(FONT_BODY);
        paymentNumberLabel.setPreferredSize(new Dimension(140, 25));
        paymentNumberLabel.setForeground(TEXT_PRIMARY);
        JPanel numberRow = new JPanel(new BorderLayout(10, 0));
        numberRow.setBackground(BG_CARD);
        numberRow.setMaximumSize(new Dimension(460, 38));
        numberRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfPaymentNumber.setFont(FONT_BODY);
        tfPaymentNumber.setPreferredSize(new Dimension(tfPaymentNumber.getPreferredSize().width, 34));
        tfPaymentNumber.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        numberRow.add(paymentNumberLabel, BorderLayout.WEST);
        numberRow.add(tfPaymentNumber, BorderLayout.CENTER);
        card.add(numberRow);
        card.add(Box.createVerticalStrut(SP_6));

        // Account Name row — built manually so label can be swapped
        paymentNameLabel = new JLabel("Account Name:");
        paymentNameLabel.setFont(FONT_BODY);
        paymentNameLabel.setPreferredSize(new Dimension(140, 25));
        paymentNameLabel.setForeground(TEXT_PRIMARY);
        tfPaymentName.setFont(FONT_BODY);
        tfPaymentName.setPreferredSize(new Dimension(tfPaymentName.getPreferredSize().width, 34));
        tfPaymentName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        JPanel nameRow = new JPanel(new BorderLayout(10, 0));
        nameRow.setBackground(BG_CARD);
        nameRow.setMaximumSize(new Dimension(460, 38));
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameRow.add(paymentNameLabel, BorderLayout.WEST);
        nameRow.add(tfPaymentName, BorderLayout.CENTER);
        card.add(nameRow);
        card.add(Box.createVerticalStrut(SP_6));

        // Expiry / Reference row — built manually so label can be swapped
        paymentExpiryLabel = new JLabel("Expiry Date:");
        paymentExpiryLabel.setFont(FONT_BODY);
        paymentExpiryLabel.setPreferredSize(new Dimension(140, 25));
        paymentExpiryLabel.setForeground(TEXT_PRIMARY);
        tfPaymentExpiry.setFont(FONT_BODY);
        tfPaymentExpiry.setPreferredSize(new Dimension(tfPaymentExpiry.getPreferredSize().width, 34));
        tfPaymentExpiry.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        expiryRow = new JPanel(new BorderLayout(10, 0));
        expiryRow.setBackground(BG_CARD);
        expiryRow.setMaximumSize(new Dimension(460, 38));
        expiryRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        expiryRow.add(paymentExpiryLabel, BorderLayout.WEST);
        expiryRow.add(tfPaymentExpiry, BorderLayout.CENTER);
        card.add(expiryRow);

        // E-Wallet provider row — shown instead of expiry row when E-Wallet is selected
        cbEwalletProvider = new JComboBox<>(
                new String[] { "-- Select Provider --", "GCash", "Maya", "ShopeePay", "GrabPay" });
        cbEwalletProvider.setFont(FONT_BODY);
        JLabel ewalletProviderLabel = new JLabel("Provider:");
        ewalletProviderLabel.setFont(FONT_BODY);
        ewalletProviderLabel.setPreferredSize(new Dimension(140, 25));
        ewalletProviderLabel.setForeground(TEXT_PRIMARY);
        ewalletProviderRow = new JPanel(new BorderLayout(10, 0));
        ewalletProviderRow.setBackground(BG_CARD);
        ewalletProviderRow.setMaximumSize(new Dimension(460, 38));
        ewalletProviderRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        ewalletProviderRow.add(ewalletProviderLabel, BorderLayout.WEST);
        ewalletProviderRow.add(cbEwalletProvider, BorderLayout.CENTER);
        ewalletProviderRow.setVisible(false);
        card.add(ewalletProviderRow);

        card.add(Box.createVerticalStrut(SP_20));

        // Show grand total here too
        JLabel totalReminder = new JLabel(
                "Review the billing step for totals; confirmation is saved offline after payment.");
        totalReminder.setFont(FONT_SMALL);
        totalReminder.setForeground(TEXT_SECONDARY);
        totalReminder.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totalReminder);
        card.add(Box.createVerticalStrut(SP_12));

        cbPolicyAcknowledged = new JCheckBox(
                "<html>I acknowledge the flight policies (overbooking limits, check-in window, refund/cancellation).</html>");
        cbPolicyAcknowledged.setFont(FONT_SMALL);
        cbPolicyAcknowledged.setBackground(BG_CARD);
        cbPolicyAcknowledged.setForeground(TEXT_PRIMARY);
        cbPolicyAcknowledged.setFocusPainted(false);
        cbPolicyAcknowledged.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(cbPolicyAcknowledged);
        card.add(Box.createVerticalStrut(SP_6));

        JButton viewPolicyBtn = createStyledButton("View Policy Details", Color.WHITE, TEXT_PRIMARY);
        viewPolicyBtn.setFont(FONT_SMALL);
        viewPolicyBtn.setPreferredSize(new Dimension(170, 34));
        viewPolicyBtn.addActionListener(e -> showPolicyDetailsDialog());
        JPanel policyBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        policyBtnRow.setOpaque(false);
        policyBtnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        policyBtnRow.add(viewPolicyBtn);
        card.add(policyBtnRow);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    // STEP 8: RECEIPT PANEL
    // ================================================================
    private JPanel buildReceiptPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("\u2714  Booking Confirmed!");
        title.setFont(FONT_HEADER);
        title.setForeground(SUCCESS_GREEN);
        panel.add(title, BorderLayout.NORTH);

        receiptTextArea = new JTextArea();
        receiptTextArea.setFont(FONT_MONO);
        receiptTextArea.setEditable(false);
        receiptTextArea.setBackground(new Color(255, 255, 248));
        receiptTextArea.setForeground(TEXT_PRIMARY);
        receiptTextArea.setBorder(BorderFactory.createEmptyBorder(SP_20, SP_20, SP_20, SP_20));
        receiptTextArea.setLineWrap(false);
        receiptTextArea.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(receiptTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        bottomPanel.setBackground(BG_MAIN);

        JButton btnManageBooking = createStyledButton("Open Manage Booking", Color.WHITE, TEXT_PRIMARY);
        btnManageBooking.addActionListener(e -> openManageBookingDialog(paymentReference));
        bottomPanel.add(btnManageBooking);

        JButton btnSave = createStyledButton("Save Confirmation", Color.WHITE, TEXT_PRIMARY);
        btnSave.addActionListener(e -> saveConfirmationToFile());
        bottomPanel.add(btnSave);

        JButton btnPrint = createStyledButton("Print Confirmation", Color.WHITE, TEXT_PRIMARY);
        btnPrint.addActionListener(e -> printConfirmation());
        bottomPanel.add(btnPrint);

        JButton btnNewBooking = createStyledButton("New Booking", PRIMARY, Color.WHITE);
        btnNewBooking.addActionListener(e -> resetBooking());
        bottomPanel.add(btnNewBooking);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ================================================================
    // NAVIGATION LOGIC
    // ================================================================
    private void navigateBack() {
        if (isStepTransitioning || currentStep <= 0) {
            return;
        }
        // Save add-on state before leaving add-on panel
        if (currentStep == STEP_ADDONS) {
            saveAddonSelections();
        }
        transitionToStep(currentStep - 1, false);
    }

    private void navigateNext() {
        if (isStepTransitioning) {
            return;
        }
        if (!validateCurrentStep())
            return;

        if (currentStep < STEP_IDS.length - 1) {
            transitionToStep(currentStep + 1, true);
        }
    }

    private void transitionToStep(int targetStep, boolean movingForward) {
        if (targetStep < 0 || targetStep >= STEP_IDS.length) {
            return;
        }
        if (stepTransitionTimer != null && stepTransitionTimer.isRunning()) {
            stepTransitionTimer.stop();
        }

        isStepTransitioning = true;
        setNavigationEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        stepTransitionTimer = new javax.swing.Timer(100, e -> {
            if (movingForward) {
                prepareStep(targetStep);
            }
            currentStep = targetStep;
            cardLayout.show(contentPanel, STEP_IDS[currentStep]);
            refreshNavigation();

            setCursor(Cursor.getDefaultCursor());
            setNavigationEnabled(true);
            isStepTransitioning = false;
        });
        stepTransitionTimer.setRepeats(false);
        stepTransitionTimer.start();
    }

    private void setNavigationEnabled(boolean enabled) {
        if (btnBack != null) {
            btnBack.setEnabled(enabled);
        }
        if (btnNext != null) {
            btnNext.setEnabled(enabled);
        }
    }

    /** Validates the current step. Returns true if OK to proceed. */
    private boolean validateCurrentStep() {
        switch (currentStep) {
            case STEP_HOME:
                return true;

            case STEP_FLIGHTS: // Flight selection
                JTable activeTable = getActiveTable();
                int selRow = activeTable.getSelectedRow();
                if (selRow < 0) {
                    showError("Please select a flight before proceeding.");
                    activeTable.requestFocusInWindow();
                    return false;
                }
                LocalDate flightDate;
                try {
                    flightDate = getFlightDateFromInputs();
                } catch (IllegalArgumentException ex) {
                    showError(ex.getMessage());
                    if (calendarDaysGrid != null)
                        calendarDaysGrid.requestFocusInWindow();
                    return false;
                }
                if (flightDate.isBefore(LocalDate.now())) {
                    showError("Flight date cannot be earlier than today.");
                    if (calendarDaysGrid != null)
                        calendarDaysGrid.requestFocusInWindow();
                    return false;
                }
                if (flightDate.isEqual(LocalDate.now())) {
                    Object[][] src2 = getActiveFlightRows();
                    int selRow2 = getActiveTable().getSelectedRow();
                    String flightNoCheck = selRow2 >= 0 ? String.valueOf(src2[selRow2][0]) : "";
                    // Use the same Realtime EST departure so the check is consistent with the table
                    // display
                    java.time.LocalDateTime realtimeDeparture = getRealtimeEstimatedDeparture(flightNoCheck);
                    java.time.LocalDateTime cutoff = realtimeDeparture.minusHours(BOOKING_CUTOFF_HOURS);
                    if (!java.time.LocalDateTime.now().isBefore(cutoff)) {
                        String depStr = realtimeDeparture.format(
                                java.time.format.DateTimeFormatter.ofPattern("h:mm a"));
                        showError("Booking closed: online booking for this flight closes " +
                                BOOKING_CUTOFF_HOURS + " hours before departure (" + depStr + "). " +
                                "Please visit the airport counter.");
                        if (calendarDaysGrid != null)
                            calendarDaysGrid.requestFocusInWindow();
                        return false;
                    }
                }
                Object[][] src = getActiveFlightRows();
                selectedFlight = src[selRow];
                selectedFlightDate = flightDate;
                isDomestic = (flightTabs.getSelectedIndex() == 0);
                int remainingSlots = getSelectedFlightRemainingSlots();
                if (remainingSlots <= 0) {
                    showError("This flight has reached its overbooking policy limit. Please select another flight.");
                    selectedFlight = null;
                    selectedFlightDate = null;
                    return false;
                }
                return true;

            case STEP_PASSENGERS: // Passengers
                if (passengers.isEmpty()) {
                    showError("Please add at least one passenger.");
                    tfName.requestFocusInWindow();
                    return false;
                }
                if (selectedFlight == null) {
                    showError("Please return to flight selection and choose a valid flight.");
                    return false;
                }
                int availableSlots = getSelectedFlightRemainingSlots();
                if (passengers.size() > availableSlots) {
                    showError("Overbooking policy allows only " + availableSlots
                            + " passenger slot(s) for this flight right now. Please reduce passengers or pick another flight.");
                    tfName.requestFocusInWindow();
                    return false;
                }
                boolean hasMinor = false;
                boolean hasAdult = false;
                for (Map<String, Object> p : passengers) {
                    try {
                        int age = Integer.parseInt(String.valueOf(p.get(KEY_AGE)));
                        if (age < 12)
                            hasMinor = true;
                        if (age >= 18)
                            hasAdult = true;
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (hasMinor && !hasAdult) {
                    showError(
                            "Passengers under 12 cannot travel unaccompanied. Please add an adult passenger (18 or older) to this booking.");
                    tfName.requestFocusInWindow();
                    return false;
                }
                return true;

            case STEP_CLASS: // Class (always valid, radio is pre-selected)
                return true;

            case STEP_ADDONS: // Add-ons - save checkbox states
                saveAddonSelections();
                return true;

            case STEP_SEATS:
                saveSeatSelections();
                return validateSeatAssignments();

            case STEP_BILLING: // Billing - just proceed to payment
                return true;

            case STEP_PAYMENT: // Payment validation
                if (!validateSeatHoldsStillActive()) {
                    return false;
                }
                ValidationResult paymentValidation = validatePaymentInputs();
                if (!paymentValidation.valid) {
                    showError(paymentValidation.message);
                    if (paymentValidation.field != null) {
                        paymentValidation.field.requestFocusInWindow();
                    }
                    return false;
                }
                if (cbPolicyAcknowledged != null && !cbPolicyAcknowledged.isSelected()) {
                    showError("Please acknowledge the flight policies before confirming payment.");
                    cbPolicyAcknowledged.requestFocusInWindow();
                    return false;
                }
                // Save payment info
                selectedPaymentMethod = getSelectedPaymentMethod();
                if ("E-Wallet".equals(selectedPaymentMethod) && cbEwalletProvider.getSelectedIndex() > 0) {
                    selectedPaymentMethod = "E-Wallet (" + cbEwalletProvider.getSelectedItem() + ")";
                }
                if (paymentReference == null || paymentReference.trim().isEmpty()) {
                    paymentReference = generateBookingReference();
                }
                return handleMockPaymentAndConfirm();

            default:
                return true;
        }
    }

    /** Prepares data/UI for the step we're navigating TO. */
    private void prepareStep(int step) {
        switch (step) {
            case STEP_CLASS:
                updateClassPriceLabel();
                refreshClassPanel();
                break;
            case STEP_ADDONS:
                refreshAddonPanel();
                break;
            case STEP_SEATS:
                refreshSeatPanel();
                break;
            case STEP_BILLING:
                generateBilling();
                break;
            case STEP_RECEIPT:
                generateReceipt();
                break;
        }
    }

    /** Updates the header step indicators and button visibility. */
    private void refreshNavigation() {
        // Back button: hidden on first step
        btnBack.setVisible(currentStep > 0 && currentStep < STEP_IDS.length - 1);
        // Next button: hidden on last step (receipt)
        btnNext.setVisible(currentStep < STEP_IDS.length - 1);

        if (currentStep == STEP_HOME) {
            btnNext.setText("Browse Flights \u2192");
        } else if (currentStep == STEP_BILLING) {
            btnNext.setText("Continue to Payment \u2192");
        } else if (currentStep == STEP_PAYMENT) {
            btnNext.setText("Confirm Payment \u2192");
        } else {
            btnNext.setText("Next \u2192");
        }
        if (helperMessageTimer == null || !helperMessageTimer.isRunning()) {
            stepHelperLabel.setForeground(TEXT_SECONDARY);
            stepHelperLabel.setText(getStepHelperText(currentStep));
        }
        animateProgressBarTo(currentStep + 1);
        stepProgressBar.setString(
                "Step " + (currentStep + 1) + " of " + STEP_TITLES.length + " - " + STEP_TITLES[currentStep]);

        // Step indicator styling
        for (int i = 0; i < stepLabels.length; i++) {
            if (i == currentStep) {
                stepLabels[i].setBackground(STEP_ACTIVE);
                stepLabels[i].setForeground(new Color(0x12, 0x22, 0x45));
                stepLabels[i].setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else if (i < currentStep) {
                stepLabels[i].setBackground(STEP_DONE);
                stepLabels[i].setForeground(Color.WHITE);
                stepLabels[i].setFont(FONT_SMALL);
            } else {
                stepLabels[i].setBackground(STEP_PENDING);
                stepLabels[i].setForeground(new Color(221, 227, 238));
                stepLabels[i].setFont(FONT_SMALL);
            }
        }

        for (JLabel arrow : stepArrows) {
            if (arrow != null) {
                arrow.setForeground(new Color(220, 226, 235, 165));
            }
        }

    }

    private void animateProgressBarTo(int targetValue) {
        if (stepProgressBar == null) {
            return;
        }
        int min = stepProgressBar.getMinimum();
        int max = stepProgressBar.getMaximum();
        int boundedTarget = Math.max(min, Math.min(max, targetValue));
        int currentValue = stepProgressBar.getValue();

        if (currentValue == boundedTarget) {
            return;
        }

        if (progressAnimationTimer != null && progressAnimationTimer.isRunning()) {
            progressAnimationTimer.stop();
        }

        final int direction = boundedTarget > currentValue ? 1 : -1;
        progressAnimationTimer = new javax.swing.Timer(35, e -> {
            int next = stepProgressBar.getValue() + direction;
            boolean reachedTarget = (direction > 0 && next >= boundedTarget)
                    || (direction < 0 && next <= boundedTarget);
            if (reachedTarget) {
                stepProgressBar.setValue(boundedTarget);
                progressAnimationTimer.stop();
            } else {
                stepProgressBar.setValue(next);
            }
        });
        progressAnimationTimer.start();
    }

    private void showTransientHelperMessage(String message, Color color) {
        showTransientHelperMessage(message, color, 2600);
    }

    private void showTransientHelperMessage(String message, Color color, int durationMs) {
        if (stepHelperLabel == null) {
            return;
        }
        if (helperMessageTimer != null && helperMessageTimer.isRunning()) {
            helperMessageTimer.stop();
        }
        stepHelperLabel.setText(message);
        stepHelperLabel.setForeground(color);

        helperMessageTimer = new javax.swing.Timer(Math.max(1200, durationMs), e -> {
            stepHelperLabel.setForeground(TEXT_SECONDARY);
            stepHelperLabel.setText(getStepHelperText(currentStep));
        });
        helperMessageTimer.setRepeats(false);
        helperMessageTimer.start();
    }

    // ================================================================
    // PASSENGER MANAGEMENT
    // ================================================================
    private void handlePassengerAction() {
        if (editingPassengerIndex >= 0) {
            updatePassenger();
        } else {
            addPassenger();
        }
    }

    private void addPassenger() {
        String name = tfName.getText().trim();
        String address = tfAddress.getText().trim();
        String contact = tfContact.getText().trim();
        String age = tfAge.getText().trim();
        String bday = tfBday.getText().trim();
        String id = tfId.getText().trim();

        ValidationResult validation = validatePassengerInputs(name, address, contact, age, bday, id);
        if (!validation.valid) {
            showError(validation.message);
            validation.field.requestFocusInWindow();
            return;
        }

        int ageValue = Integer.parseInt(age);

        if (cbSenior.isSelected() && ageValue < 60) {
            showError("Senior Citizen discount is only valid for passengers aged 60 or above.");
            tfAge.requestFocusInWindow();
            return;
        }

        boolean[] addons = new boolean[ADDON_NAMES.length];
        int taxIndex = getAddonIndex(ADDON_TAX);
        if (taxIndex >= 0) {
            addons[taxIndex] = true;
        }

        // Build passenger data map
        Map<String, Object> p = createPassengerMap(name, address, contact, ageValue, bday, id,
                cbSenior.isSelected(), cbPWD.isSelected(), addons, "");

        passengers.add(p);

        // Update the list display
        passengerListModel.addElement(formatPassengerListEntry(passengers.size() - 1, p));
        updatePassengerCountLabel();

        // Clear form fields for next input
        resetPassengerEditor();
        showTransientHelperMessage("Passenger \"" + name + "\" added successfully.", SUCCESS_GREEN);
    }

    private void beginPassengerEdit() {
        int selected = passengerJList.getSelectedIndex();
        if (selected < 0) {
            showError("Please select a passenger to edit.");
            return;
        }

        Map<String, Object> passenger = passengers.get(selected);
        tfName.setText(String.valueOf(passenger.get(KEY_NAME)));
        tfAddress.setText(String.valueOf(passenger.get(KEY_ADDRESS)));
        tfContact.setText(String.valueOf(passenger.get(KEY_CONTACT)));
        tfAge.setText(String.valueOf(passenger.get(KEY_AGE)));
        tfBday.setText(String.valueOf(passenger.get(KEY_BIRTHDAY)));
        tfId.setText(String.valueOf(passenger.get(KEY_ID)));
        cbSenior.setSelected(isSeniorPassenger(passenger));
        cbPWD.setSelected(isPwdPassenger(passenger));
        editingPassengerIndex = selected;
        updatePassengerEditorState();
        tfName.requestFocusInWindow();
        showTransientHelperMessage("Editing passenger \"" + passenger.get(KEY_NAME) + "\".", PRIMARY);
    }

    private void updatePassenger() {
        if (editingPassengerIndex < 0 || editingPassengerIndex >= passengers.size()) {
            cancelPassengerEdit();
            return;
        }

        String name = tfName.getText().trim();
        String address = tfAddress.getText().trim();
        String contact = tfContact.getText().trim();
        String age = tfAge.getText().trim();
        String bday = tfBday.getText().trim();
        String id = tfId.getText().trim();

        ValidationResult validation = validatePassengerInputs(name, address, contact, age, bday, id);
        if (!validation.valid) {
            showError(validation.message);
            validation.field.requestFocusInWindow();
            return;
        }

        int ageValue = Integer.parseInt(age);
        if (cbSenior.isSelected() && ageValue < 60) {
            showError("Senior Citizen discount is only valid for passengers aged 60 or above.");
            tfAge.requestFocusInWindow();
            return;
        }

        Map<String, Object> current = passengers.get(editingPassengerIndex);
        boolean[] existingAddons = Arrays.copyOf(getPassengerAddons(current), ADDON_NAMES.length);
        String seatCode = getPassengerSeat(current);
        Map<String, Object> updated = createPassengerMap(name, address, contact, ageValue, bday, id,
                cbSenior.isSelected(), cbPWD.isSelected(), existingAddons, seatCode);

        passengers.set(editingPassengerIndex, updated);
        passengerListModel.set(editingPassengerIndex, formatPassengerListEntry(editingPassengerIndex, updated));
        resetPassengerEditor();
        showTransientHelperMessage("Passenger \"" + name + "\" updated successfully.", SUCCESS_GREEN);
    }

    private void showBirthdayPicker() {
        java.time.YearMonth current;
        try {
            String existing = tfBday.getText().trim();
            LocalDate d = existing.length() == 10 ? LocalDate.parse(existing, BIRTHDAY_FORMATTER) : LocalDate.now();
            current = java.time.YearMonth.of(d.getYear(), d.getMonthValue());
        } catch (Exception ex) {
            current = java.time.YearMonth.now();
        }
        final java.time.YearMonth[] ym = { current };

        JDialog picker = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(tfBday),
                "Select Birthday", true);
        picker.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        picker.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_CARD);

        // --- Header: Prev | Month | Year | Next ---
        JPanel header = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 8));
        header.setBackground(PRIMARY);

        JButton btnPrev = new JButton("< Prev");
        JButton btnNext = new JButton("Next >");
        for (JButton b : new JButton[] { btnPrev, btnNext }) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setForeground(Color.WHITE);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        String[] monthNames = { "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December" };
        JComboBox<String> cbMonth = new JComboBox<>(monthNames);
        cbMonth.setFont(FONT_BODY);
        cbMonth.setSelectedIndex(ym[0].getMonthValue() - 1);

        int currentYear = LocalDate.now().getYear();
        Integer[] years = new Integer[currentYear - 1899];
        for (int i = 0; i < years.length; i++)
            years[i] = currentYear - i;
        JComboBox<Integer> cbYear = new JComboBox<>(years);
        cbYear.setFont(FONT_BODY);
        cbYear.setSelectedItem(ym[0].getYear());

        header.add(btnPrev);
        header.add(cbMonth);
        header.add(cbYear);
        header.add(btnNext);

        // --- Day-of-week headers ---
        String[] dayNames = { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" };
        Color WEEKEND_COLOR = new Color(210, 60, 60);
        JPanel dayHeader = new JPanel(new java.awt.GridLayout(1, 7, 4, 0));
        dayHeader.setBackground(new Color(235, 238, 245));
        dayHeader.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        for (int di = 0; di < dayNames.length; di++) {
            JLabel dl = new JLabel(dayNames[di], SwingConstants.CENTER);
            dl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dl.setForeground(di == 0 || di == 6 ? WEEKEND_COLOR : TEXT_SECONDARY);
            dayHeader.add(dl);
        }

        // --- Calendar grid ---
        JPanel grid = new JPanel(new java.awt.GridLayout(6, 7, 4, 4));
        grid.setBackground(BG_CARD);
        grid.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        LocalDate today = LocalDate.now();
        Color HOVER_BG = new Color(232, 238, 255);

        Runnable refreshCalendar = () -> {
            cbMonth.setSelectedIndex(ym[0].getMonthValue() - 1);
            cbYear.setSelectedItem(ym[0].getYear());
            btnNext.setEnabled(ym[0].isBefore(java.time.YearMonth.now()));

            grid.removeAll();
            LocalDate first = ym[0].atDay(1);
            int startDow = first.getDayOfWeek().getValue() % 7;
            LocalDate selected = null;
            try {
                String t = tfBday.getText().trim();
                if (t.length() == 10)
                    selected = LocalDate.parse(t, BIRTHDAY_FORMATTER);
            } catch (Exception ignored) {
            }
            final LocalDate sel = selected;
            for (int i = 0; i < 42; i++) {
                LocalDate date = first.plusDays(i - startDow);
                int col = i % 7;
                if (date.getMonthValue() != ym[0].getMonthValue()) {
                    JLabel empty = new JLabel("");
                    empty.setOpaque(true);
                    empty.setBackground(BG_CARD);
                    grid.add(empty);
                    continue;
                }
                JButton btn = new JButton(String.valueOf(date.getDayOfMonth()));
                btn.setFont(FONT_BODY);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                boolean isWeekend = (col == 0 || col == 6);
                if (date.equals(sel)) {
                    btn.setBackground(PRIMARY);
                    btn.setForeground(Color.WHITE);
                } else if (date.equals(today)) {
                    btn.setBackground(STEP_ACTIVE);
                    btn.setForeground(Color.WHITE);
                } else if (date.isAfter(today)) {
                    btn.setBackground(BG_CARD);
                    btn.setForeground(BORDER);
                    btn.setEnabled(false);
                } else {
                    btn.setBackground(BG_CARD);
                    btn.setForeground(isWeekend ? WEEKEND_COLOR : TEXT_PRIMARY);
                    btn.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            btn.setBackground(HOVER_BG);
                        }

                        @Override
                        public void mouseExited(java.awt.event.MouseEvent e) {
                            btn.setBackground(BG_CARD);
                        }
                    });
                }
                if (!date.isAfter(today)) {
                    final LocalDate chosen = date;
                    btn.addActionListener(ev -> {
                        tfBday.setText(chosen.format(BIRTHDAY_FORMATTER));
                        picker.dispose();
                    });
                }
                grid.add(btn);
            }
            grid.revalidate();
            grid.repaint();
        };

        btnPrev.addActionListener(e -> {
            ym[0] = ym[0].minusMonths(1);
            refreshCalendar.run();
        });
        btnNext.addActionListener(e -> {
            if (ym[0].isBefore(java.time.YearMonth.now())) {
                ym[0] = ym[0].plusMonths(1);
                refreshCalendar.run();
            }
        });

        cbMonth.addActionListener(e -> {
            int m = cbMonth.getSelectedIndex() + 1;
            int y = ym[0].getYear();
            java.time.YearMonth candidate = java.time.YearMonth.of(y, m);
            if (!candidate.isAfter(java.time.YearMonth.now())) {
                ym[0] = candidate;
                refreshCalendar.run();
            } else {
                cbMonth.setSelectedIndex(ym[0].getMonthValue() - 1);
            }
        });

        cbYear.addActionListener(e -> {
            if (cbYear.getSelectedItem() == null)
                return;
            int y = (Integer) cbYear.getSelectedItem();
            int m = ym[0].getMonthValue();
            java.time.YearMonth candidate = java.time.YearMonth.of(y, m);
            if (candidate.isAfter(java.time.YearMonth.now()))
                candidate = java.time.YearMonth.now();
            ym[0] = candidate;
            refreshCalendar.run();
        });

        refreshCalendar.run();

        // --- Footer: "Go to Today" link ---
        JPanel footer = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 4));
        footer.setBackground(BG_CARD);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JButton btnToday = new JButton("Go to Today");
        btnToday.setFont(FONT_SMALL);
        btnToday.setForeground(PRIMARY);
        btnToday.setContentAreaFilled(false);
        btnToday.setBorderPainted(false);
        btnToday.setFocusPainted(false);
        btnToday.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btnToday.addActionListener(e -> {
            ym[0] = java.time.YearMonth.now();
            refreshCalendar.run();
        });
        footer.add(btnToday);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG_CARD);
        center.add(dayHeader, BorderLayout.NORTH);
        center.add(grid, BorderLayout.CENTER);
        center.add(footer, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        picker.setContentPane(root);
        picker.pack();
        picker.setMinimumSize(new Dimension(370, 340));
        picker.setLocationRelativeTo(tfBday);
        picker.setVisible(true);
    }

    private void cancelPassengerEdit() {
        if (editingPassengerIndex >= 0) {
            showTransientHelperMessage("Passenger editing canceled.", TEXT_SECONDARY);
        }
        resetPassengerEditor();
    }

    private void removePassenger() {
        int selected = passengerJList.getSelectedIndex();
        if (selected < 0) {
            showError("Please select a passenger to remove.");
            return;
        }

        Map<String, Object> removedPassenger = passengers.get(selected);
        String removedName = (String) removedPassenger.get(KEY_NAME);
        String heldSeat = getPassengerSeat(removedPassenger);
        if (!heldSeat.isEmpty() && selectedFlight != null && selectedFlightDate != null) {
            try {
                BOOKING_STORE.removeSeatHoldBySeat(
                        String.valueOf(selectedFlight[0]), selectedFlightDate, heldSeat, sessionId);
            } catch (IOException ex) {
                // non-fatal
            }
        }
        passengers.remove(selected);
        passengerListModel.remove(selected);
        updatePassengerCountLabel();
        addonCheckboxMap.remove(selected);
        seatComboBoxMap.remove(selected);

        if (editingPassengerIndex == selected) {
            resetPassengerEditor();
        } else if (editingPassengerIndex > selected) {
            editingPassengerIndex--;
            updatePassengerEditorState();
        }

        // Re-number the remaining entries
        for (int i = 0; i < passengerListModel.size(); i++) {
            Map<String, Object> p = passengers.get(i);
            passengerListModel.set(i, formatPassengerListEntry(i, p));
        }
        showTransientHelperMessage("Passenger \"" + removedName + "\" removed.", TEXT_SECONDARY);
    }

    // ================================================================
    // CLASS HELPER
    // ================================================================
    private void updateClassPriceLabel() {
        if (selectedFlight != null) {
            double base = ((Number) selectedFlight[3]).doubleValue();
            double price = base * CLASS_MULTIPLIERS[selectedClassIndex];
            classPriceLabel.setText(String.format(
                    "Price per passenger:  PHP %,.2f    (Base: PHP %,.2f  x  %.1f)",
                    price, base, CLASS_MULTIPLIERS[selectedClassIndex]));
        }
    }

    // ================================================================
    // ADD-ON HELPERS
    // ================================================================

    /** Rebuilds the add-on panel with a section for each passenger. */
    private void refreshAddonPanel() {
        addonListPanel.removeAll();
        addonCheckboxMap.clear();

        for (int i = 0; i < passengers.size(); i++) {
            Map<String, Object> p = passengers.get(i);
            boolean[] currentAddons = getPassengerAddons(p);

            // Card for this passenger
            JPanel pCard = new JPanel();
            pCard.setLayout(new BoxLayout(pCard, BoxLayout.Y_AXIS));
            pCard.setBackground(BG_CARD);
            pCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    BorderFactory.createEmptyBorder(14, 18, 14, 18)));
            pCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Passenger label
            String pLabel = "Passenger " + (i + 1) + ": " + p.get(KEY_NAME);
            if (isSeniorPassenger(p))
                pLabel += "  [Senior]";
            if (isPwdPassenger(p))
                pLabel += "  [PWD]";
            JLabel nameLabel = new JLabel(pLabel);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLabel.setForeground(PRIMARY);
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            pCard.add(nameLabel);
            pCard.add(Box.createVerticalStrut(10));

            // Add-on checkboxes
            JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 2));
            checkRow.setBackground(BG_CARD);
            checkRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JCheckBox[] cbs = new JCheckBox[ADDON_NAMES.length];
            for (int j = 0; j < ADDON_NAMES.length; j++) {
                String addonLabel = ADDON_NAMES[j].equalsIgnoreCase(ADDON_TAX)
                        ? "Tax (12% VAT)"
                        : ADDON_NAMES[j] + " (PHP " + String.format("%,.0f", ADDON_PRICES[j]) + ")";
                cbs[j] = new JCheckBox(addonLabel);
                cbs[j].setFont(FONT_BODY);
                cbs[j].setBackground(BG_CARD);

                if (ADDON_NAMES[j].equalsIgnoreCase(ADDON_TAX)) {
                    cbs[j].setSelected(true);
                    cbs[j].setEnabled(false); // Cannot be unchecked
                    currentAddons[j] = true;
                } else {
                    cbs[j].setSelected(currentAddons[j]);
                }

                checkRow.add(cbs[j]);
            }
            addonCheckboxMap.put(i, cbs);

            pCard.add(checkRow);
            addonListPanel.add(pCard);
            addonListPanel.add(Box.createVerticalStrut(10));
        }

        addonListPanel.revalidate();
        addonListPanel.repaint();
    }

    /** Saves all current checkbox states back into passenger data. */
    private void saveAddonSelections() {
        for (Map.Entry<Integer, JCheckBox[]> entry : addonCheckboxMap.entrySet()) {
            int idx = entry.getKey();
            JCheckBox[] cbs = entry.getValue();
            if (idx < passengers.size()) {
                boolean[] addons = getPassengerAddons(passengers.get(idx));
                for (int j = 0; j < cbs.length; j++) {
                    addons[j] = cbs[j].isSelected();
                }
            }
        }
    }

    // ================================================================
    // FLIGHT INFO LABEL
    // ================================================================
    private void updateFlightInfoLabel() {
        if (flightInfoLabel == null)
            return;
        JTable activeTable = (flightTabs != null) ? getActiveTable() : domesticTable;
        if (activeTable == null)
            return;
        int row = activeTable.getSelectedRow();
        if (row < 0) {
            flightInfoLabel.setText("  Select a flight row to see seat capacity details.");
            flightInfoLabel.setForeground(TEXT_SECONDARY);
            return;
        }
        Object[][] src = getActiveFlightRows();
        if (row >= src.length)
            return;
        String flightNo = String.valueOf(src[row][0]);
        String origin = String.valueOf(src[row][1]);
        String dest = String.valueOf(src[row][2]);
        int totalCapacity = getFlightCapacity(flightNo);
        LocalDate date = calendarSelectedDate;
        Set<String> occupied = getPersistedOccupiedSeats(flightNo, date);
        int booked = occupied.size();
        int available = Math.max(0, totalCapacity - booked);
        double pctFull = totalCapacity > 0 ? (booked * 100.0 / totalCapacity) : 0;
        String dateStr = (date != null) ? date.format(FLIGHT_DATE_DISPLAY_FORMAT) : "—";
        String badge = getFlightStatusBadge(flightNo);
        Color labelColor;
        if ("CLOSED".equals(badge))
            labelColor = new Color(180, 40, 40);
        else if ("BOARDING".equals(badge))
            labelColor = new Color(140, 60, 180);
        else if ("FULL".equals(badge))
            labelColor = new Color(200, 60, 60);
        else if ("FILLING UP".equals(badge))
            labelColor = new Color(180, 120, 0);
        else
            labelColor = new Color(30, 130, 70);
        flightInfoLabel.setText(String.format(
                "  %s  (%s → %s)      Date: %s      Total Seats: %d      Occupied: %d      Available: %d      Fill Rate: %.0f%%      Status: %s",
                flightNo, origin, dest, dateStr, totalCapacity, booked, available, pctFull, badge));
        flightInfoLabel.setForeground(labelColor);
    }

    // ================================================================
    // CLASS HELPERS
    // ================================================================
    private void refreshClassPanel() {
        if (classAvailLabels == null)
            return;
        String[] configs = { "3+3 layout", "2+2 layout", "2+1 layout", "1+1 layout" };
        for (int i = 0; i < CLASS_NAMES.length; i++) {
            int total = getClassSeatCount(i);
            int avail = getClassAvailableSeats(i);
            String config = configs[i];
            String availText;
            Color labelColor;
            if (total < 0) {
                availText = config + "  —  select a flight first";
                labelColor = TEXT_SECONDARY;
            } else if (avail <= 0) {
                availText = config + "  —  FULL (0 seats available)";
                labelColor = new Color(200, 60, 60);
            } else if (avail <= total * 0.15) {
                availText = config + "  —  Only " + avail + " of " + total + " seats left!";
                labelColor = new Color(200, 140, 0);
            } else {
                availText = config + "  —  " + avail + " of " + total + " seats available";
                labelColor = new Color(40, 155, 85);
            }
            classAvailLabels[i].setText("   " + availText);
            classAvailLabels[i].setForeground(labelColor);
        }
    }

    // ================================================================
    // SEAT HELPERS
    // ================================================================
    private void refreshSeatPanel() {
        if (seatListPanel == null || seatMapPanel == null)
            return;

        seatListPanel.removeAll();
        seatComboBoxMap.clear();

        // ── Empty / not-ready state ────────────────────────────────────────
        if (selectedFlight == null || selectedFlightDate == null || passengers.isEmpty()) {
            JPanel msg = new JPanel(new BorderLayout());
            msg.setBackground(BG_CARD);
            msg.setBorder(createCardBorder(18, 18, 18, 18));
            JLabel lbl = new JLabel("Add passengers and complete the earlier steps before assigning seats.");
            lbl.setFont(FONT_BODY);
            lbl.setForeground(TEXT_SECONDARY);
            msg.add(lbl, BorderLayout.CENTER);
            seatListPanel.add(msg);
            seatMapPanel.removeAll();
            seatMapPanel.revalidate();
            seatMapPanel.repaint();
            seatListPanel.revalidate();
            seatListPanel.repaint();
            return;
        }

        List<String> allSeats = getSeatOptionsForCurrentSelection();
        String flightNo = String.valueOf(selectedFlight[0]);
        Set<String> externallyOccupied = getPersistedOccupiedSeats(flightNo, selectedFlightDate);
        sanitizeSeatAssignments(allSeats, externallyOccupied);

        if (activeSeatPassengerIndex >= passengers.size())
            activeSeatPassengerIndex = 0;

        // ── LEFT: passenger cards ──────────────────────────────────────────
        for (int i = 0; i < passengers.size(); i++) {
            Map<String, Object> p = passengers.get(i);
            String seat = getPassengerSeat(p);
            boolean isActive = (i == activeSeatPassengerIndex);

            JPanel card = new JPanel(new BorderLayout(8, 0));
            card.setBackground(isActive ? PRIMARY : BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(isActive ? PRIMARY : BORDER, isActive ? 2 : 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel nameLbl = new JLabel("P" + (i + 1) + "  " + p.get(KEY_NAME));
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLbl.setForeground(isActive ? Color.WHITE : TEXT_PRIMARY);

            JLabel seatLbl = new JLabel(seat.isEmpty() ? "No seat assigned" : seat);
            seatLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            seatLbl.setForeground(seat.isEmpty()
                    ? (isActive ? new Color(190, 200, 230) : TEXT_SECONDARY)
                    : (isActive ? new Color(130, 255, 160) : SUCCESS_GREEN));

            JPanel textCol = new JPanel();
            textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
            textCol.setOpaque(false);
            textCol.add(nameLbl);
            textCol.add(Box.createVerticalStrut(2));
            textCol.add(seatLbl);
            card.add(textCol, BorderLayout.CENTER);

            if (!seat.isEmpty()) {
                JLabel badge = new JLabel("✔");
                badge.setFont(new Font("Segoe UI", Font.BOLD, 16));
                badge.setForeground(isActive ? new Color(130, 255, 160) : SUCCESS_GREEN);
                card.add(badge, BorderLayout.EAST);
            }

            final int idx = i;
            MouseAdapter clickAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    activeSeatPassengerIndex = idx;
                    refreshSeatPanel();
                }
            };
            card.addMouseListener(clickAdapter);
            nameLbl.addMouseListener(clickAdapter);
            seatLbl.addMouseListener(clickAdapter);

            seatListPanel.add(card);
            seatListPanel.add(Box.createVerticalStrut(6));
        }
        seatListPanel.add(Box.createVerticalGlue());

        // ── RIGHT: embedded airplane seat map ──────────────────────────────
        seatMapPanel.removeAll();
        seatMapPanel.setLayout(new BorderLayout());

        purgeExpiredSeatHoldsAsync();
        Map<String, BookingStore.SeatHoldInfo> holds = BOOKING_STORE.getActiveHoldsForFlightDate(flightNo,
                selectedFlightDate);

        Map<String, String> heldOwner = new HashMap<>();
        Map<String, LocalDateTime> heldExpiry = new HashMap<>();
        for (Map.Entry<String, BookingStore.SeatHoldInfo> e : holds.entrySet()) {
            heldOwner.put(e.getKey(), e.getValue().owner);
            heldExpiry.put(e.getKey(), e.getValue().expiresAt);
        }

        Map<String, Object> activePax = passengers.get(activeSeatPassengerIndex);
        String activeCurrentSeat = getPassengerSeat(activePax);

        // Colors (same as original dialog)
        final Color PLANE_BG = new Color(16, 26, 54);
        final Color FUSELAGE = new Color(38, 54, 100);
        final Color COL_HDR = new Color(190, 205, 255);
        final Color ROW_LBL = new Color(150, 165, 210);
        final Color AISLE_CLR = new Color(80, 100, 160);
        final Color SEAT_FREE = new Color(40, 155, 85);
        final Color SEAT_OCC = new Color(82, 88, 105);
        final Color SEAT_HOLD = new Color(165, 115, 35);
        final Color SEAT_MINE = STEP_ACTIVE;
        final Color SEAT_OTHER = new Color(70, 110, 190);

        // "Assigning for" header bar
        JPanel assignHdr = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        assignHdr.setBackground(PRIMARY);
        JLabel assignLbl = new JLabel("Assigning for: " + activePax.get(KEY_NAME)
                + getPassengerBadgeText(activePax));
        assignLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        assignLbl.setForeground(Color.WHITE);
        assignHdr.add(assignLbl);
        if (!activeCurrentSeat.isEmpty()) {
            JLabel curLbl = new JLabel("  →  " + activeCurrentSeat);
            curLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            curLbl.setForeground(new Color(130, 255, 160));
            assignHdr.add(curLbl);
        }
        seatMapPanel.add(assignHdr, BorderLayout.NORTH);

        // Plane background
        JPanel planeRoot = new JPanel(new BorderLayout(0, 0));
        planeRoot.setBackground(PLANE_BG);

        // Nose
        JPanel nose = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(FUSELAGE);
                g2.fillRoundRect(w / 2 - 158, -24, 316, h + 24, 200, 200);
                g2.setColor(new Color(85, 145, 205, 170));
                g2.fillRoundRect(w / 2 - 30, 16, 22, 16, 7, 7);
                g2.fillRoundRect(w / 2 + 8, 16, 22, 16, 7, 7);
                g2.setColor(new Color(55, 115, 175));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(w / 2 - 30, 16, 22, 16, 7, 7);
                g2.drawRoundRect(w / 2 + 8, 16, 22, 16, 7, 7);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String txt = CLASS_NAMES[selectedClassIndex].toUpperCase() + " CABIN";
                g2.drawString(txt, (w - fm.stringWidth(txt)) / 2, h - 8);
            }
        };
        nose.setPreferredSize(new Dimension(0, 68));
        nose.setOpaque(false);
        planeRoot.add(nose, BorderLayout.NORTH);

        // Cabin
        JPanel cabin = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(FUSELAGE);
                g2.fillRect(w / 2 - 158, 0, 316, h);
            }
        };
        cabin.setLayout(new BorderLayout());
        cabin.setOpaque(false);

        // Per-class layout variables
        final char[] classLetters = CLASS_SEAT_LETTERS[selectedClassIndex];
        final int aisleAfter = CLASS_AISLE_AFTER[selectedClassIndex];
        final int aisleGridX = aisleAfter + 2;
        final int btnW = CLASS_BTN_WIDTH[selectedClassIndex];
        final int btnH = CLASS_BTN_HEIGHT[selectedClassIndex];
        final int[] gridCols = new int[classLetters.length];
        for (int ci = 0; ci < classLetters.length; ci++)
            gridCols[ci] = (ci <= aisleAfter) ? ci + 1 : ci + 2;

        // Column header
        JPanel colHeader = new JPanel(new GridBagLayout());
        colHeader.setOpaque(false);
        colHeader.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.insets = new Insets(0, 4, 0, 4);
        // Build colLabels/colWidths dynamically based on class layout
        int totalHdrCols = classLetters.length + 2; // +row-label +aisle
        String[] colLabels = new String[totalHdrCols];
        int[] colWidths = new int[totalHdrCols];
        colLabels[0] = "";
        colWidths[0] = 32;
        colLabels[aisleGridX] = "  ";
        colWidths[aisleGridX] = 18;
        for (int ci = 0; ci < classLetters.length; ci++) {
            colLabels[gridCols[ci]] = String.valueOf(classLetters[ci]);
            colWidths[gridCols[ci]] = btnW;
        }
        for (int c = 0; c < colLabels.length; c++) {
            hgbc.gridx = c;
            JLabel lbl = new JLabel(colLabels[c], SwingConstants.CENTER);
            boolean isAisle = (c == aisleGridX);
            lbl.setFont(new Font("Segoe UI", isAisle ? Font.PLAIN : Font.BOLD, 12));
            lbl.setForeground(isAisle ? AISLE_CLR : COL_HDR);
            lbl.setPreferredSize(new Dimension(colWidths[c], 20));
            colHeader.add(lbl, hgbc);
        }

        // Group seats by row
        TreeMap<Integer, List<String>> rowMap = new TreeMap<>();
        for (String sc : allSeats) {
            String part = sc.contains("-") ? sc.substring(sc.indexOf('-') + 1) : sc;
            int row = Integer.parseInt(part.substring(0, part.length() - 1));
            rowMap.computeIfAbsent(row, k -> new ArrayList<>()).add(sc);
        }

        // Seat grid
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4);
        int gridRow = 0;

        // Build set of seats held/chosen by other passengers in this booking
        Set<String> otherPassengerSeats = getChosenSeatSetExcludingIndex(activeSeatPassengerIndex);

        for (Map.Entry<Integer, List<String>> entry : rowMap.entrySet()) {
            int rowNum = entry.getKey();
            List<String> rowSeats = entry.getValue();

            gbc.gridx = 0;
            gbc.gridy = gridRow;
            JLabel rowLbl = new JLabel(String.valueOf(rowNum), SwingConstants.CENTER);
            rowLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            rowLbl.setForeground(ROW_LBL);
            rowLbl.setPreferredSize(new Dimension(32, btnH));
            gridPanel.add(rowLbl, gbc);

            gbc.gridx = aisleGridX;
            gbc.gridy = gridRow;
            JLabel aisle = new JLabel("", SwingConstants.CENTER);
            aisle.setPreferredSize(new Dimension(18, btnH));
            gridPanel.add(aisle, gbc);

            for (int li = 0; li < rowSeats.size() && li < classLetters.length; li++) {
                String seatCode = rowSeats.get(li);
                String shortCode = seatCode.contains("-")
                        ? seatCode.substring(seatCode.indexOf('-') + 1)
                        : seatCode;

                boolean isBooked = externallyOccupied.contains(seatCode);
                String owner = heldOwner.get(seatCode);
                LocalDateTime exp = heldExpiry.get(seatCode);
                boolean heldByMe = sessionId.equals(owner);
                boolean heldByOther = owner != null && !heldByMe;
                boolean isActiveSpot = seatCode.equals(activeCurrentSeat);
                boolean takenByOther = otherPassengerSeats.contains(seatCode);

                final Color bg;
                final String tip;
                final boolean enabled;
                if (isBooked) {
                    bg = SEAT_OCC;
                    tip = "Occupied";
                    enabled = false;
                } else if (takenByOther) {
                    bg = SEAT_OTHER;
                    tip = "Assigned to another passenger";
                    enabled = false;
                } else if (heldByOther) {
                    bg = SEAT_HOLD;
                    tip = "Held until " + (exp != null ? exp.toLocalTime().toString() : "?");
                    enabled = false;
                } else if (isActiveSpot) {
                    bg = SEAT_MINE;
                    tip = "Your seat — click to deselect";
                    enabled = true;
                } else {
                    bg = SEAT_FREE;
                    tip = "Available — click to select";
                    enabled = true;
                }

                final String thisSeat = seatCode;
                JButton btn = new JButton(shortCode) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int w = getWidth(), h = getHeight();
                        Color draw = (isEnabled() && getModel().isPressed()) ? bg.darker() : bg;
                        g2.setColor(draw.brighter());
                        g2.fillRoundRect(w / 2 - 8, 1, 16, 6, 4, 4);
                        g2.setColor(draw);
                        g2.fillRoundRect(2, 6, w - 4, h - 12, 9, 9);
                        g2.setColor(draw.darker());
                        g2.fillRoundRect(4, h - 9, w - 8, 7, 4, 4);
                        g2.setColor(Color.WHITE);
                        g2.setFont(getFont());
                        FontMetrics fm = g2.getFontMetrics();
                        int tx = (w - fm.stringWidth(getText())) / 2;
                        int ty = 6 + (h - 12 - fm.getHeight()) / 2 + fm.getAscent();
                        g2.drawString(getText(), tx, ty);
                        g2.dispose();
                    }

                    @Override
                    protected void paintBorder(Graphics g) {
                    }
                };
                btn.setPreferredSize(new Dimension(btnW, btnH));
                btn.setFont(new Font("Segoe UI", Font.BOLD, btnW >= 64 ? 11 : 10));
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setOpaque(false);
                btn.setEnabled(enabled);
                btn.setToolTipText(tip);

                btn.addActionListener(e -> {
                    String prevSeat = getPassengerSeat(passengers.get(activeSeatPassengerIndex));
                    if (thisSeat.equals(prevSeat)) {
                        // Deselect
                        passengers.get(activeSeatPassengerIndex).put(KEY_SEAT, "");
                        try {
                            BOOKING_STORE.removeSeatHoldBySeat(flightNo, selectedFlightDate, thisSeat, sessionId);
                        } catch (IOException ex) {
                            /* ignore */ }
                        refreshSeatPanel();
                        return;
                    }
                    if (externallyOccupied.contains(thisSeat)) {
                        showError("Seat " + thisSeat + " is already booked.");
                        return;
                    }
                    BookingStore.SeatHoldInfo existing = holds.get(thisSeat);
                    if (existing != null && !sessionId.equals(existing.owner)) {
                        showError("Seat " + thisSeat + " is currently held by another user.");
                        return;
                    }
                    try {
                        boolean ok = BOOKING_STORE.addSeatHold(flightNo, selectedFlightDate, thisSeat, sessionId);
                        if (!ok) {
                            showError("Unable to hold seat. It may have been taken.");
                            return;
                        }
                        if (prevSeat != null && !prevSeat.isEmpty() && !prevSeat.equals(thisSeat)) {
                            try {
                                BOOKING_STORE.removeSeatHoldBySeat(flightNo, selectedFlightDate, prevSeat, sessionId);
                            } catch (IOException ex) {
                                /* ignore */ }
                        }
                        passengers.get(activeSeatPassengerIndex).put(KEY_SEAT, thisSeat);
                        // Auto-advance to next unassigned passenger
                        for (int ni = 0; ni < passengers.size(); ni++) {
                            if (getPassengerSeat(passengers.get(ni)).isEmpty()) {
                                activeSeatPassengerIndex = ni;
                                break;
                            }
                        }
                        refreshSeatPanel();
                    } catch (IOException ex) {
                        showError("Unable to place seat hold: " + ex.getMessage());
                    }
                });

                gbc.gridx = gridCols[li];
                gbc.gridy = gridRow;
                gridPanel.add(btn, gbc);
            }
            gridRow++;
        }

        JPanel gridWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        gridWrap.setOpaque(false);
        gridWrap.add(colHeader);

        JPanel gridWithHdr = new JPanel(new BorderLayout());
        gridWithHdr.setOpaque(false);

        JPanel colHdrWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        colHdrWrap.setOpaque(false);
        colHdrWrap.add(colHeader);

        JPanel gridCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gridCenter.setOpaque(false);
        gridCenter.add(gridPanel);

        gridWithHdr.add(colHdrWrap, BorderLayout.NORTH);
        gridWithHdr.add(gridCenter, BorderLayout.CENTER);

        JScrollPane mapScroll = new JScrollPane(gridWithHdr);
        mapScroll.setBorder(BorderFactory.createEmptyBorder());
        mapScroll.setOpaque(false);
        mapScroll.getViewport().setOpaque(false);
        mapScroll.getViewport().setBackground(PLANE_BG);
        cabin.add(mapScroll, BorderLayout.CENTER);
        planeRoot.add(cabin, BorderLayout.CENTER);

        // Legend footer
        JPanel tail = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(FUSELAGE);
                g2.fillRoundRect(w / 2 - 158, 0, 316, h + 12, 60, 60);
            }
        };
        tail.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 6));
        tail.setOpaque(false);

        Object[][] legend = {
                { "Available", SEAT_FREE },
                { "My Seat", SEAT_MINE },
                { "Other Pax", SEAT_OTHER },
                { "Occupied", SEAT_OCC },
                { "On Hold", SEAT_HOLD },
        };
        for (Object[] item : legend) {
            Color ic = (Color) item[1];
            JPanel chip = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ic);
                    g2.fillRoundRect(0, 3, 13, 11, 4, 4);
                }
            };
            chip.setPreferredSize(new Dimension(13, 17));
            chip.setOpaque(false);
            JLabel lbl = new JLabel((String) item[0]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lbl.setForeground(COL_HDR);
            tail.add(chip);
            tail.add(lbl);
            tail.add(Box.createHorizontalStrut(4));
        }
        planeRoot.add(tail, BorderLayout.SOUTH);

        seatMapPanel.add(planeRoot, BorderLayout.CENTER);

        seatListPanel.revalidate();
        seatListPanel.repaint();
        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    private void showSeatMapDialog(int passengerIndex) {
        if (selectedFlight == null || selectedFlightDate == null) {
            showError("Please select a flight and date before using the seat map.");
            return;
        }
        if (passengerIndex < 0 || passengerIndex >= passengers.size()) {
            showError("Invalid passenger selected for seat map.");
            return;
        }

        String flightNo = String.valueOf(selectedFlight[0]);
        LocalDate flightDate = selectedFlightDate;

        java.util.List<String> allSeats = getSeatOptionsForCurrentSelection();
        Set<String> booked = getPersistedOccupiedSeats(flightNo, flightDate);

        purgeExpiredSeatHoldsAsync();
        Map<String, BookingStore.SeatHoldInfo> holds = BOOKING_STORE.getActiveHoldsForFlightDate(flightNo, flightDate);

        Map<String, String> heldOwner = new HashMap<>();
        Map<String, LocalDateTime> heldExpiry = new HashMap<>();
        for (Map.Entry<String, BookingStore.SeatHoldInfo> e : holds.entrySet()) {
            heldOwner.put(e.getKey(), e.getValue().owner);
            heldExpiry.put(e.getKey(), e.getValue().expiresAt);
        }

        Set<String> chosenSeats = new HashSet<>();
        for (Map<String, Object> p : passengers) {
            String s = getPassengerSeat(p);
            if (s != null && !s.isEmpty())
                chosenSeats.add(s);
        }

        // Group seats by row number, preserving order
        TreeMap<Integer, List<String>> rowMap = new TreeMap<>();
        for (String seatCode : allSeats) {
            String sc = seatCode.contains("-") ? seatCode.substring(seatCode.indexOf('-') + 1) : seatCode;
            int row = Integer.parseInt(sc.substring(0, sc.length() - 1));
            rowMap.computeIfAbsent(row, k -> new ArrayList<>()).add(seatCode);
        }

        // ── Plane color palette ──
        final Color PLANE_BG = new Color(16, 26, 54);
        final Color FUSELAGE = new Color(38, 54, 100);
        final Color COL_HDR = new Color(190, 205, 255);
        final Color ROW_LBL = new Color(150, 165, 210);
        final Color AISLE_CLR = new Color(80, 100, 160);
        final Color SEAT_FREE = new Color(40, 155, 85);
        final Color SEAT_OCC = new Color(82, 88, 105);
        final Color SEAT_HOLD = new Color(165, 115, 35);
        final Color SEAT_MINE = STEP_ACTIVE;

        JDialog dialog = new JDialog(this, "Seat Map — " + passengers.get(passengerIndex).get(KEY_NAME), true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(510, 700);
        dialog.setMinimumSize(new Dimension(470, 520));
        dialog.setLocationRelativeTo(this);

        // ── Root ──
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(PLANE_BG);

        // ── Nose cone ──
        JPanel nosePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // fuselage arc
                g2.setColor(FUSELAGE);
                g2.fillRoundRect(w / 2 - 158, -24, 316, h + 24, 200, 200);
                // cockpit windows
                g2.setColor(new Color(85, 145, 205, 170));
                g2.fillRoundRect(w / 2 - 30, 16, 22, 16, 7, 7);
                g2.fillRoundRect(w / 2 + 8, 16, 22, 16, 7, 7);
                g2.setColor(new Color(55, 115, 175));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(w / 2 - 30, 16, 22, 16, 7, 7);
                g2.drawRoundRect(w / 2 + 8, 16, 22, 16, 7, 7);
                // title
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "SELECT YOUR SEAT";
                g2.drawString(txt, (w - fm.stringWidth(txt)) / 2, h - 9);
            }
        };
        nosePanel.setPreferredSize(new Dimension(510, 74));
        nosePanel.setOpaque(false);
        root.add(nosePanel, BorderLayout.NORTH);

        // ── Cabin body ──
        JPanel cabin = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(FUSELAGE);
                g2.fillRect(w / 2 - 158, 0, 316, h);
            }
        };
        cabin.setLayout(new BorderLayout());
        cabin.setOpaque(false);

        // Column header row: [row#] [A] [B] [aisle] [C] [D]
        JPanel colHeader = new JPanel(new GridBagLayout());
        colHeader.setOpaque(false);
        colHeader.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.insets = new Insets(0, 4, 0, 4);
        String[] colLabels = { "", "A", "B", "  ", "C", "D" };
        int[] colWidths = { 32, 56, 56, 20, 56, 56 };
        for (int c = 0; c < colLabels.length; c++) {
            hgbc.gridx = c;
            JLabel lbl = new JLabel(colLabels[c], SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", c == 3 ? Font.PLAIN : Font.BOLD, 12));
            lbl.setForeground(c == 3 ? AISLE_CLR : COL_HDR);
            lbl.setPreferredSize(new Dimension(colWidths[c], 20));
            colHeader.add(lbl, hgbc);
        }

        // Seat grid
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        int[] gridCols = { 1, 2, 4, 5 }; // maps seat letter index -> grid column
        int gridRow = 0;
        for (Map.Entry<Integer, List<String>> entry : rowMap.entrySet()) {
            int rowNum = entry.getKey();
            List<String> rowSeats = entry.getValue();

            // Row number label
            gbc.gridx = 0;
            gbc.gridy = gridRow;
            JLabel rowLbl = new JLabel(String.valueOf(rowNum), SwingConstants.CENTER);
            rowLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            rowLbl.setForeground(ROW_LBL);
            rowLbl.setPreferredSize(new Dimension(32, 48));
            gridPanel.add(rowLbl, gbc);

            // Aisle spacer
            gbc.gridx = 3;
            gbc.gridy = gridRow;
            JLabel aisleSpacer = new JLabel("", SwingConstants.CENTER);
            aisleSpacer.setPreferredSize(new Dimension(20, 48));
            gridPanel.add(aisleSpacer, gbc);

            for (int li = 0; li < rowSeats.size() && li < 4; li++) {
                String seatCode = rowSeats.get(li);
                String shortCode = seatCode.contains("-")
                        ? seatCode.substring(seatCode.indexOf('-') + 1)
                        : seatCode;

                boolean isBooked = booked.contains(seatCode);
                String owner = heldOwner.get(seatCode);
                LocalDateTime exp = heldExpiry.get(seatCode);
                boolean heldByMe = sessionId.equals(owner);
                boolean heldByOth = owner != null && !heldByMe;
                boolean takenByOth = chosenSeats.contains(seatCode)
                        && !seatCode.equals(getPassengerSeat(passengers.get(passengerIndex)));

                final Color bg;
                final String tip;
                final boolean enabled;
                if (isBooked) {
                    bg = SEAT_OCC;
                    tip = "Occupied — not available";
                    enabled = false;
                } else if (heldByOth || takenByOth) {
                    bg = SEAT_HOLD;
                    tip = heldByOth
                            ? "Held until " + (exp != null ? exp.toLocalTime().toString() : "?")
                            : "Selected for another passenger";
                    enabled = false;
                } else if (heldByMe) {
                    bg = SEAT_MINE;
                    tip = "Your hold — click to release";
                    enabled = true;
                } else {
                    bg = SEAT_FREE;
                    tip = "Available — click to select";
                    enabled = true;
                }

                JButton btn = new JButton(shortCode) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int w = getWidth(), h = getHeight();
                        Color draw = (isEnabled() && getModel().isPressed()) ? bg.darker() : bg;
                        // headrest bump
                        g2.setColor(draw.brighter());
                        g2.fillRoundRect(w / 2 - 9, 1, 18, 7, 5, 5);
                        // seat back
                        g2.setColor(draw);
                        g2.fillRoundRect(2, 6, w - 4, h - 14, 10, 10);
                        // seat cushion
                        g2.setColor(draw.darker());
                        g2.fillRoundRect(4, h - 11, w - 8, 9, 5, 5);
                        // label
                        g2.setColor(Color.WHITE);
                        g2.setFont(getFont());
                        FontMetrics fm = g2.getFontMetrics();
                        int tx = (w - fm.stringWidth(getText())) / 2;
                        int ty = 6 + (h - 14 - fm.getHeight()) / 2 + fm.getAscent();
                        g2.drawString(getText(), tx, ty);
                        g2.dispose();
                    }

                    @Override
                    protected void paintBorder(Graphics g) {
                        /* no border */ }
                };
                btn.setPreferredSize(new Dimension(56, 48));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setOpaque(false);
                btn.setEnabled(enabled);
                btn.setToolTipText(tip);

                final String thisSeat = seatCode;
                btn.addActionListener(e -> {
                    String current = getPassengerSeat(passengers.get(passengerIndex));
                    if (thisSeat.equals(current)) {
                        passengers.get(passengerIndex).put(KEY_SEAT, "");
                        try {
                            BOOKING_STORE.removeSeatHoldBySeat(flightNo, flightDate, thisSeat, sessionId);
                        } catch (IOException ex) {
                            /* ignore */ }
                        refreshSeatPanel();
                        dialog.dispose();
                        return;
                    }
                    if (booked.contains(thisSeat)) {
                        showError("Seat " + thisSeat + " is already booked.");
                        return;
                    }
                    BookingStore.SeatHoldInfo existing = holds.get(thisSeat);
                    if (existing != null && !sessionId.equals(existing.owner)) {
                        showError("Seat " + thisSeat + " is currently held by another user.");
                        return;
                    }
                    try {
                        boolean ok = BOOKING_STORE.addSeatHold(flightNo, flightDate, thisSeat, sessionId);
                        if (!ok) {
                            showError("Unable to hold seat. It may have been taken.");
                            return;
                        }
                        String prev = getPassengerSeat(passengers.get(passengerIndex));
                        if (prev != null && !prev.isEmpty() && !prev.equals(thisSeat)) {
                            try {
                                BOOKING_STORE.removeSeatHoldBySeat(flightNo, flightDate, prev, sessionId);
                            } catch (IOException ex) {
                                /* ignore */ }
                        }
                        passengers.get(passengerIndex).put(KEY_SEAT, thisSeat);
                        refreshSeatPanel();
                        dialog.dispose();
                    } catch (IOException ex) {
                        showError("Unable to place seat hold: " + ex.getMessage());
                    }
                });

                gbc.gridx = gridCols[li];
                gbc.gridy = gridRow;
                gridPanel.add(btn, gbc);
            }
            gridRow++;
        }

        JPanel gridWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        gridWrap.setOpaque(false);
        gridWrap.add(gridPanel);

        JPanel gridWithHdr = new JPanel(new BorderLayout());
        gridWithHdr.setOpaque(false);
        gridWithHdr.add(colHeader, BorderLayout.NORTH);
        gridWithHdr.add(gridWrap, BorderLayout.CENTER);

        JPanel centred = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centred.setOpaque(false);
        centred.add(gridWithHdr);

        JScrollPane scroll = new JScrollPane(centred);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        cabin.add(scroll, BorderLayout.CENTER);
        root.add(cabin, BorderLayout.CENTER);

        // ── Tail / footer ──
        JPanel tail = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(FUSELAGE);
                g2.fillRoundRect(w / 2 - 158, 0, 316, h + 12, 60, 60);
            }
        };
        tail.setLayout(new BorderLayout(8, 0));
        tail.setOpaque(false);
        tail.setBorder(BorderFactory.createEmptyBorder(6, 14, 8, 14));

        // Legend — center
        Object[][] legend = {
                { "Available", SEAT_FREE },
                { "Selected", SEAT_MINE },
                { "Occupied", SEAT_OCC },
                { "On Hold", SEAT_HOLD },
        };
        JPanel legendRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        legendRow.setOpaque(false);
        for (Object[] item : legend) {
            Color ic = (Color) item[1];
            JPanel chip = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ic);
                    g2.fillRoundRect(0, 3, 14, 12, 5, 5);
                }
            };
            chip.setPreferredSize(new Dimension(14, 18));
            chip.setOpaque(false);
            JLabel lbl = new JLabel((String) item[0]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lbl.setForeground(COL_HDR);
            legendRow.add(chip);
            legendRow.add(lbl);
        }
        tail.add(legendRow, BorderLayout.CENTER);

        // Close button — right
        JButton btnClose = createStyledButton("Close", Color.WHITE, TEXT_PRIMARY);
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeWrap.setOpaque(false);
        closeWrap.add(btnClose);
        tail.add(closeWrap, BorderLayout.EAST);

        root.add(tail, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void saveSeatSelections() {
        // Seat assignments are written directly to the passengers map when a
        // seat button is clicked; nothing extra to sync here.
        refreshPassengerListEntries();
    }

    private boolean validateSeatAssignments() {
        if (selectedFlight == null || selectedFlightDate == null) {
            showError("Please return to the flight step and select a valid flight first.");
            return false;
        }

        Set<String> externallyOccupied = getPersistedOccupiedSeats(String.valueOf(selectedFlight[0]),
                selectedFlightDate);
        Set<String> chosenSeats = new HashSet<>();
        for (int i = 0; i < passengers.size(); i++) {
            Map<String, Object> passenger = passengers.get(i);
            String seatCode = getPassengerSeat(passenger);
            if (seatCode.isEmpty()) {
                showError("Please assign a seat for passenger " + (i + 1) + ".");
                JComboBox<String> combo = seatComboBoxMap.get(i);
                if (combo != null) {
                    combo.requestFocusInWindow();
                }
                return false;
            }
            if (externallyOccupied.contains(seatCode)) {
                showError("Seat " + seatCode + " is no longer available. Please choose another seat.");
                refreshSeatPanel();
                return false;
            }
            if (!chosenSeats.add(seatCode)) {
                showError("Each passenger must have a unique seat assignment.");
                refreshSeatPanel();
                return false;
            }
        }
        return true;
    }

    private void purgeExpiredSeatHoldsAsync() {
        new javax.swing.SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                BOOKING_STORE.purgeExpiredSeatHolds();
                return null;
            }
        }.execute();
    }

    private boolean validateSeatHoldsStillActive() {
        if (selectedFlight == null || selectedFlightDate == null) {
            return true;
        }
        String flightNo = String.valueOf(selectedFlight[0]);
        Map<String, BookingStore.SeatHoldInfo> holds = BOOKING_STORE.getActiveHoldsForFlightDate(flightNo,
                selectedFlightDate);
        for (int i = 0; i < passengers.size(); i++) {
            String seat = getPassengerSeat(passengers.get(i));
            if (seat.isEmpty()) {
                continue;
            }
            BookingStore.SeatHoldInfo info = holds.get(seat);
            if (info == null || !sessionId.equals(info.owner)) {
                showError("Your seat hold for seat " + seat
                        + " has expired. Please go back to the Seats step and re-assign your seats.");
                return false;
            }
        }
        return true;
    }

    // ================================================================
    // BILLING GENERATION
    // ================================================================
    private void generateBilling() {
        StringBuilder sb = new StringBuilder();
        String flightNo = String.valueOf(selectedFlight[0]);
        double basePrice = ((Number) selectedFlight[3]).doubleValue();
        double classFare = basePrice * CLASS_MULTIPLIERS[selectedClassIndex];
        String billedAt = new java.text.SimpleDateFormat("MMMM dd, yyyy  hh:mm a").format(new java.util.Date());
        double totalBeforeDiscount = 0;
        double totalDiscount = 0;
        grandTotal = 0;

        sb.append("====================================================================\n");
        sb.append("                    SKYWINGS AIRLINE BILLING SUMMARY\n");
        sb.append("====================================================================\n");
        sb.append("Billed At     : ").append(billedAt).append("\n");
        sb.append("Flight No.    : ").append(flightNo).append("\n");
        sb.append("Route         : ").append(selectedFlight[1]).append(" -> ").append(selectedFlight[2]).append("\n");
        sb.append("Flight Type   : ")
                .append(isDomestic ? "Domestic (Philippines)" : "International (Southeast Asia)")
                .append("\n");
        sb.append("Flight Date   : ").append(getFormattedFlightDate()).append("\n");
        sb.append("Realtime EST  : ").append(getRealtimeEstSummary(flightNo)).append("\n");
        sb.append("Travel Class  : ").append(CLASS_NAMES[selectedClassIndex]).append("\n");
        sb.append("Passengers    : ").append(passengers.size()).append("\n");
        sb.append("Currency      : PHP\n");
        sb.append("--------------------------------------------------------------------\n\n");

        for (int i = 0; i < passengers.size(); i++) {
            Map<String, Object> p = passengers.get(i);
            boolean[] addons = getPassengerAddons(p);
            boolean isSenior = isSeniorPassenger(p);
            boolean isPWD = isPwdPassenger(p);

            StringBuilder passengerLabel = new StringBuilder();
            passengerLabel.append("Passenger ").append(i + 1).append(" - ").append(p.get(KEY_NAME));
            if (isSenior) {
                passengerLabel.append(" [Senior]");
            }
            if (isPWD) {
                passengerLabel.append(" [PWD]");
            }

            sb.append(passengerLabel).append("\n");
            sb.append("Age ").append(p.get(KEY_AGE)).append(" | Contact ").append(p.get(KEY_CONTACT)).append("\n");
            sb.append("Seat ").append(getPassengerSeat(p)).append("\n");
            sb.append(String.format("  %-46s PHP %,12.2f\n",
                    "Base Fare (" + CLASS_NAMES[selectedClassIndex] + ")", classFare));

            double discountableAddons = 0;
            boolean hasTax = false;
            for (int j = 0; j < ADDON_NAMES.length; j++) {
                if (addons[j]) {
                    if (ADDON_TAX.equals(ADDON_NAMES[j])) {
                        hasTax = true;
                    } else {
                        sb.append(String.format("  %-46s PHP %,12.2f\n",
                                "Add-on: " + ADDON_NAMES[j], ADDON_PRICES[j]));
                        discountableAddons += ADDON_PRICES[j];
                    }
                }
            }

            double taxBase = classFare + discountableAddons;
            double taxAmount = (hasTax && !(isSenior || isPWD)) ? taxBase * TAX_RATE : 0.0;
            if (hasTax) {
                if (isSenior || isPWD) {
                    sb.append(String.format("  %-46s PHP %,12.2f\n", "Add-on: Tax (Exempt)", 0.00));
                } else {
                    sb.append(String.format("  %-46s PHP %,12.2f\n", "Add-on: Tax (12% VAT)", taxAmount));
                }
            }

            double subtotal = taxBase + taxAmount;
            sb.append(String.format("  %-46s PHP %,12.2f\n", "Subtotal", subtotal));

            double discount = 0;
            if (isSenior || isPWD) {
                discount = taxBase * DISCOUNT_RATE;
                String dType = getDiscountTypeLabel(isSenior, isPWD);
                sb.append(String.format("  %-46s -PHP %,12.2f\n",
                        dType + " Discount (20%)", discount));
            }

            double total = subtotal - discount;
            sb.append(String.format("  %-46s PHP %,12.2f\n", "Passenger Total", total));
            sb.append("--------------------------------------------------------------------\n");

            totalBeforeDiscount += subtotal;
            totalDiscount += discount;
            grandTotal += total;
        }

        sb.append("\nOVERALL TOTALS\n");
        sb.append("--------------------------------------------------------------------\n");
        sb.append(String.format("%-50s PHP %,12.2f\n", "Fare + Add-ons (Before Discount)", totalBeforeDiscount));
        sb.append(String.format("%-50s -PHP %,12.2f\n", "Total Discount", totalDiscount));
        sb.append(String.format("%-50s PHP %,12.2f\n", "AMOUNT DUE", grandTotal));
        sb.append("====================================================================\n");

        billingTextArea.setText(sb.toString());
        billingTextArea.setCaretPosition(0);
    }

    // ================================================================
    // RECEIPT GENERATION
    // ================================================================
    private void generateReceipt() {
        if (latestConfirmedBooking == null) {
            receiptTextArea.setText("No confirmed booking is available yet.");
            receiptTextArea.setCaretPosition(0);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================\n");
        sb.append("                     SKYWINGS BOOKING CONFIRMATION\n");
        sb.append("====================================================================\n");
        sb.append("Status        : ").append(latestConfirmedBooking.status).append("\n");
        sb.append("Reference #   : ").append(latestConfirmedBooking.reference).append("\n");
        sb.append("Booked At     : ").append(latestConfirmedBooking.bookedAt).append("\n");
        sb.append("Storage       : Offline confirmation saved inside this application.\n");
        sb.append("--------------------------------------------------------------------\n");
        sb.append("Thank you for booking with SkyWings Airline.\n");
        sb.append("This receipt serves as your email-style confirmation for offline use.\n\n");

        sb.append("FLIGHT SUMMARY\n");
        sb.append("--------------------------------------------------------------------\n");
        sb.append("Flight No.    : ").append(latestConfirmedBooking.flightNo).append("\n");
        sb.append("Route         : ").append(latestConfirmedBooking.origin).append(" -> ")
                .append(latestConfirmedBooking.destination).append("\n");
        sb.append("Flight Type   : ").append(latestConfirmedBooking.domestic ? "Domestic" : "International")
                .append("\n");
        sb.append("Flight Date   : ").append(latestConfirmedBooking.flightDate).append("\n");
        sb.append("Realtime EST  : ").append(latestConfirmedBooking.realtimeEst).append("\n");
        sb.append("Travel Class  : ").append(latestConfirmedBooking.className).append("\n");
        sb.append("Passengers    : ").append(latestConfirmedBooking.passengers.size()).append("\n\n");

        sb.append("PASSENGER SUMMARY\n");
        sb.append("--------------------------------------------------------------------\n");
        for (int i = 0; i < latestConfirmedBooking.passengers.size(); i++) {
            PassengerRecord passenger = latestConfirmedBooking.passengers.get(i);
            sb.append("Passenger ").append(i + 1).append(" : ").append(passenger.name).append("\n");
            sb.append("Seat         : ").append(passenger.seatCode).append("\n");
            sb.append("Contact      : ").append(passenger.contact).append("\n");
            sb.append("Discount     : ").append(passenger.getDiscountLabel()).append("\n");
            sb.append("Add-Ons      : ").append(passenger.getAddonSummary()).append("\n");
            sb.append(String.format("Amount       : PHP %,.2f\n", passenger.totalAmount));
            sb.append("--------------------------------------------------------------------\n");
        }

        sb.append("PAYMENT SUMMARY\n");
        sb.append("--------------------------------------------------------------------\n");
        sb.append("Method        : ").append(latestConfirmedBooking.paymentMethod).append("\n");
        sb.append("Account       : ").append(latestConfirmedBooking.maskedAccount).append("\n");
        sb.append("Status        : ").append(latestConfirmedBooking.paymentStatus).append("\n");
        sb.append("Transaction # : ")
                .append(latestConfirmedBooking.paymentTransactionRef.isEmpty() ? "N/A"
                        : latestConfirmedBooking.paymentTransactionRef)
                .append("\n");
        sb.append("Paid At       : ").append(latestConfirmedBooking.paymentConfirmedAt).append("\n");
        sb.append(String.format("Amount Paid   : PHP %,.2f\n", latestConfirmedBooking.totalAmount));
        sb.append("Policy Status : Acknowledged before confirmation\n\n");

        sb.append("IMPORTANT REMINDERS\n");
        sb.append("--------------------------------------------------------------------\n");
        sb.append("- Keep this booking reference for offline lookup and cancellation.\n");
        sb.append("- Check-in normally opens about 24 hours before departure.\n");
        sb.append("- Save or print this confirmation for your records.\n");
        sb.append("====================================================================\n");

        receiptTextArea.setText(sb.toString());
        receiptTextArea.setCaretPosition(0);
    }

    // ================================================================
    // PAYMENT HELPER
    // ================================================================
    private void updatePaymentFields() {
        String method = getSelectedPaymentMethod();
        paymentDetailLabel.setText(method + " Details:");
        if ("E-Wallet".equals(method)) {
            paymentNumberLabel.setText("Mobile Number:");
            tfPaymentNumber.setToolTipText("Registered Philippine mobile number (e.g. 09XXXXXXXXX)");
            paymentNameLabel.setText("Registered Name:");
            tfPaymentName.setToolTipText("Full name registered on your e-wallet account");
            expiryRow.setVisible(false);
            ewalletProviderRow.setVisible(true);
        } else {
            paymentNumberLabel.setText("Card Number:");
            tfPaymentNumber.setToolTipText("16-digit card number (spaces/dashes allowed)");
            paymentNameLabel.setText("Cardholder Name:");
            tfPaymentName.setToolTipText("Name printed on the card");
            paymentExpiryLabel.setText("Expiry Date:");
            tfPaymentExpiry.setToolTipText("Card expiry in MM/YY format");
            expiryRow.setVisible(true);
            ewalletProviderRow.setVisible(false);
        }
    }

    // ================================================================
    // RESET BOOKING
    // ================================================================
    private void resetBooking() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Start a new booking? Current data will be cleared.",
                "New Booking", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        // Release any lingering seat holds from the previous booking session
        if (selectedFlight != null && selectedFlightDate != null) {
            try {
                BOOKING_STORE.removeHoldsForOwnerAndFlight(
                        sessionId, String.valueOf(selectedFlight[0]), selectedFlightDate);
            } catch (IOException ignored) {
            }
        }

        selectedFlight = null;
        selectedFlightDate = null;
        isDomestic = true;
        selectedClassIndex = 0;
        passengers.clear();
        passengerListModel.clear();
        activeSeatPassengerIndex = 0;
        updatePassengerCountLabel();
        addonCheckboxMap.clear();
        seatComboBoxMap.clear();
        grandTotal = 0;
        paymentReference = "";
        selectedPaymentMethod = "";
        latestConfirmedBooking = null;

        resetPassengerEditor();
        clearPaymentFields();
        classRadios[0].setSelected(true);
        rbCredit.setSelected(true);
        if (cbPolicyAcknowledged != null) {
            cbPolicyAcknowledged.setSelected(false);
        }
        if (calendarDaysGrid != null) {
            setFlightDateSelectors(LocalDate.now());
        }

        flightTabs.setSelectedIndex(0);
        domesticTable.clearSelection();
        internationalTable.clearSelection();

        billingTextArea.setText("");
        receiptTextArea.setText("");
        updateClassPriceLabel();
        updatePaymentFields();
        currentStep = STEP_HOME;
        if (stepTransitionTimer != null && stepTransitionTimer.isRunning()) {
            stepTransitionTimer.stop();
        }
        if (progressAnimationTimer != null && progressAnimationTimer.isRunning()) {
            progressAnimationTimer.stop();
        }
        if (helperMessageTimer != null && helperMessageTimer.isRunning()) {
            helperMessageTimer.stop();
        }
        isStepTransitioning = false;
        setCursor(Cursor.getDefaultCursor());
        setNavigationEnabled(true);
        cardLayout.show(contentPanel, STEP_IDS[STEP_HOME]);
        refreshNavigation();
    }

    // ================================================================
    // MANAGE BOOKING DIALOG (ENHANCED)
    // Now features:
    // LEFT PANEL — scrollable list of ALL saved bookings, each showing
    // the 4-digit ref # and the names of every passenger.
    // Clicking a row auto-fills the ref field + loads details.
    // NAME SEARCH — filters the left panel list by passenger name.
    // REF SEARCH — original behaviour: look up by 4-digit code.
    // ================================================================
    private boolean isFlightPast(BookingRecord b) {
        return b.flightDateValue != null && b.flightDateValue.isBefore(LocalDate.now());
    }

    private boolean isBookingLocked(BookingRecord b) {
        if (isFlightPast(b))
            return true;
        if (b.bookedAtValue != null) {
            long minutesSinceBooking = java.time.Duration.between(b.bookedAtValue, java.time.LocalDateTime.now())
                    .toMinutes();
            if (minutesSinceBooking >= 60)
                return true;
        }
        return false;
    }

    private void openManageBookingDialog(String presetReference) {
        JDialog dialog = new JDialog(this, "Manage Booking", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(1050, 640);
        dialog.setMinimumSize(new Dimension(920, 560));
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(BG_MAIN);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // ── TOP: title + dual search row ───────────────────────────────────
        JLabel titleLbl = new JLabel("Manage Booking");
        titleLbl.setFont(FONT_HEADER);
        titleLbl.setForeground(TEXT_PRIMARY);

        JTextField referenceField = new JTextField(10);
        referenceField.setToolTipText("Enter the 4-digit booking reference number");
        if (presetReference != null && !presetReference.trim().isEmpty()) {
            referenceField.setText(presetReference.trim());
        }
        JButton searchRefBtn = createStyledButton("Search Ref #", PRIMARY, Color.WHITE);

        JTextField nameSearchField = new JTextField(18);
        nameSearchField.setToolTipText("Type part of a passenger name to filter the list on the left");
        JButton nameSearchBtn = createStyledButton("Filter by Name", PRIMARY, Color.WHITE);
        JButton clearFilterBtn = createStyledButton("Clear Filter", Color.WHITE, TEXT_PRIMARY);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);
        JLabel refLbl = new JLabel("4-Digit Ref #:");
        refLbl.setFont(FONT_BODY);
        JLabel nameLbl = new JLabel("Passenger Name:");
        nameLbl.setFont(FONT_BODY);
        searchRow.add(refLbl);
        searchRow.add(referenceField);
        searchRow.add(searchRefBtn);
        searchRow.add(Box.createHorizontalStrut(20));
        searchRow.add(nameLbl);
        searchRow.add(nameSearchField);
        searchRow.add(nameSearchBtn);
        searchRow.add(clearFilterBtn);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleLbl);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(searchRow);
        root.add(topPanel, BorderLayout.NORTH);

        // ── DETAILS AREA (right side) ──────────────────────────────────────
        JTextArea detailsArea = new JTextArea();
        detailsArea.setFont(FONT_MONO);
        detailsArea.setEditable(false);
        detailsArea.setBackground(BG_CARD);
        detailsArea.setForeground(TEXT_PRIMARY);
        detailsArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JButton cancelButton = createStyledButton("Cancel Booking", DANGER_RED, Color.WHITE);
        cancelButton.setEnabled(false);
        JButton modifyButton = createStyledButton("Modify Booking", STEP_ACTIVE, Color.WHITE);
        modifyButton.setEnabled(false);

        // ── LEFT PANEL: Tabbed confirmed / cancelled lists ─────────────────
        DefaultListModel<String> confirmedListModel = new DefaultListModel<>();
        DefaultListModel<String> cancelledListModel = new DefaultListModel<>();
        List<String> confirmedIndexToRef = new ArrayList<>();
        List<String> cancelledIndexToRef = new ArrayList<>();

        java.util.function.Function<DefaultListModel<String>, JList<String>> makeBookingList = (model) -> {
            JList<String> lst = new JList<>(model);
            lst.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            lst.setFixedCellHeight(40);
            lst.setSelectionBackground(ACCENT);
            lst.setSelectionForeground(Color.WHITE);
            lst.setBackground(BG_CARD);
            lst.setForeground(TEXT_PRIMARY);
            lst.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return lst;
        };
        JList<String> confirmedJList = makeBookingList.apply(confirmedListModel);
        JList<String> cancelledJList = makeBookingList.apply(cancelledListModel);

        // Build a display string for a single booking record
        java.util.function.Function<BookingRecord, String> buildEntry = (bk) -> {
            String icon = STATUS_CONFIRMED.equals(bk.status) ? "\u2714" : "\u2718";
            String names = bk.passengers.stream()
                    .map(pr -> pr.name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("(no passengers)");
            // Truncate names if very long so the list stays readable
            if (names.length() > 42) {
                names = names.substring(0, 39) + "...";
            }
            return icon + "  #" + bk.reference + "   " + names;
        };

        // Populate (or re-populate) both tab lists, optionally filtered by name
        Runnable populateList = () -> {
            String nameFilter = nameSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
            confirmedListModel.clear();
            confirmedIndexToRef.clear();
            cancelledListModel.clear();
            cancelledIndexToRef.clear();

            List<BookingRecord> fresh = BOOKING_STORE.loadBookings();

            for (BookingRecord bk : fresh) {
                if (!nameFilter.isEmpty()) {
                    boolean hit = bk.passengers.stream()
                            .anyMatch(pr -> pr.name.toLowerCase(Locale.ENGLISH).contains(nameFilter));
                    if (!hit)
                        continue;
                }
                if (STATUS_CONFIRMED.equals(bk.status)) {
                    confirmedListModel.addElement(buildEntry.apply(bk));
                    confirmedIndexToRef.add(bk.reference);
                } else {
                    cancelledListModel.addElement(buildEntry.apply(bk));
                    cancelledIndexToRef.add(bk.reference);
                }
            }
            if (confirmedListModel.isEmpty()) {
                confirmedListModel.addElement("  (no confirmed bookings)");
                confirmedIndexToRef.add(null);
            }
            if (cancelledListModel.isEmpty()) {
                cancelledListModel.addElement("  (no cancelled bookings)");
                cancelledIndexToRef.add(null);
            }
        };
        populateList.run();

        JTabbedPane bookingTabs = new JTabbedPane();
        bookingTabs.setFont(FONT_BODY);

        JScrollPane confirmedScroll = new JScrollPane(confirmedJList);
        confirmedScroll.setBorder(null);
        confirmedScroll.getViewport().setBackground(BG_CARD);

        JScrollPane cancelledScroll = new JScrollPane(cancelledJList);
        cancelledScroll.setBorder(null);
        cancelledScroll.getViewport().setBackground(BG_CARD);

        bookingTabs.addTab("Confirmed", confirmedScroll);
        bookingTabs.addTab("Cancelled", cancelledScroll);

        JLabel listCountLbl = new JLabel();
        listCountLbl.setFont(FONT_SMALL);
        listCountLbl.setForeground(TEXT_SECONDARY);
        listCountLbl.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 0));

        Runnable updateCountLabel = () -> {
            long conf = confirmedIndexToRef.stream().filter(Objects::nonNull).count();
            long canc = cancelledIndexToRef.stream().filter(Objects::nonNull).count();
            bookingTabs.setTitleAt(0, "Confirmed (" + conf + ")");
            bookingTabs.setTitleAt(1, "Cancelled (" + canc + ")");
            long shown = bookingTabs.getSelectedIndex() == 1 ? canc : conf;
            listCountLbl.setText(shown + " booking(s) shown");
        };
        bookingTabs.addChangeListener(e -> updateCountLabel.run());

        Runnable refreshList = () -> {
            populateList.run();
            updateCountLabel.run();
        };
        refreshList.run();

        // Find and highlight a ref in whichever tab it belongs to
        java.util.function.Consumer<String> selectRefInTabs = (ref) -> {
            for (int i = 0; i < confirmedIndexToRef.size(); i++) {
                if (ref.equalsIgnoreCase(confirmedIndexToRef.get(i))) {
                    bookingTabs.setSelectedIndex(0);
                    confirmedJList.setSelectedIndex(i);
                    confirmedJList.ensureIndexIsVisible(i);
                    return;
                }
            }
            for (int i = 0; i < cancelledIndexToRef.size(); i++) {
                if (ref.equalsIgnoreCase(cancelledIndexToRef.get(i))) {
                    bookingTabs.setSelectedIndex(1);
                    cancelledJList.setSelectedIndex(i);
                    cancelledJList.ensureIndexIsVisible(i);
                    return;
                }
            }
        };

        JPanel leftPane = new JPanel(new BorderLayout(0, 0));
        leftPane.setBackground(BG_MAIN);
        leftPane.add(bookingTabs, BorderLayout.CENTER);
        leftPane.add(listCountLbl, BorderLayout.SOUTH);
        leftPane.setPreferredSize(new Dimension(330, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, detailsScroll);
        splitPane.setDividerLocation(335);
        splitPane.setResizeWeight(0.33);
        splitPane.setDividerSize(7);
        splitPane.setBorder(null);
        root.add(splitPane, BorderLayout.CENTER);

        // ── SHARED: load + display a booking by reference ──────────────────
        Runnable loadByRef = () -> {
            String ref = referenceField.getText().trim();
            if (ref.isEmpty()) {
                showError("Please enter a booking reference number.");
                referenceField.requestFocusInWindow();
                return;
            }
            BookingRecord booking = BOOKING_STORE.findByReference(ref);
            if (booking == null) {
                detailsArea.setText("No booking found for reference: " + ref);
                cancelButton.setEnabled(false);
                modifyButton.setEnabled(false);
                return;
            }
            detailsArea.setText(formatBookingDetails(booking));
            detailsArea.setCaretPosition(0);
            boolean canEdit = STATUS_CONFIRMED.equals(booking.status) && !isBookingLocked(booking);
            cancelButton.setEnabled(canEdit);
            modifyButton.setEnabled(canEdit);
            if (STATUS_CONFIRMED.equals(booking.status) && isBookingLocked(booking)) {
                String tip = isFlightPast(booking) ? "Flight has already departed — booking is locked."
                        : "Booking can only be modified/cancelled within 1 hour of booking.";
                modifyButton.setToolTipText(tip);
                cancelButton.setToolTipText(tip);
            } else {
                modifyButton.setToolTipText(null);
                cancelButton.setToolTipText(null);
            }
            selectRefInTabs.accept(ref);
        };

        // Clicking a row in either tab fills the ref field and loads details
        javax.swing.event.ListSelectionListener rowClickListener = e -> {
            if (e.getValueIsAdjusting())
                return;
            JList<String> src = bookingTabs.getSelectedIndex() == 1 ? cancelledJList : confirmedJList;
            List<String> refs = bookingTabs.getSelectedIndex() == 1 ? cancelledIndexToRef : confirmedIndexToRef;
            int idx = src.getSelectedIndex();
            if (idx < 0 || idx >= refs.size() || refs.get(idx) == null)
                return;
            String ref = refs.get(idx);
            referenceField.setText(ref);
            BookingRecord booking = BOOKING_STORE.findByReference(ref);
            if (booking != null) {
                detailsArea.setText(formatBookingDetails(booking));
                detailsArea.setCaretPosition(0);
                boolean canEdit2 = STATUS_CONFIRMED.equals(booking.status) && !isBookingLocked(booking);
                cancelButton.setEnabled(canEdit2);
                modifyButton.setEnabled(canEdit2);
                if (STATUS_CONFIRMED.equals(booking.status) && isBookingLocked(booking)) {
                    String tip = isFlightPast(booking) ? "Flight has already departed — booking is locked."
                            : "Booking can only be modified/cancelled within 1 hour of booking.";
                    modifyButton.setToolTipText(tip);
                    cancelButton.setToolTipText(tip);
                } else {
                    modifyButton.setToolTipText(null);
                    cancelButton.setToolTipText(null);
                }
            }
        };
        confirmedJList.addListSelectionListener(rowClickListener);
        cancelledJList.addListSelectionListener(rowClickListener);

        // Ref # search
        searchRefBtn.addActionListener(e -> loadByRef.run());
        referenceField.addActionListener(e -> loadByRef.run());

        // Name filter: updates both tab lists
        Runnable doNameFilter = () -> {
            refreshList.run();
            // If exactly one confirmed result, auto-select it
            if (confirmedIndexToRef.size() == 1 && confirmedIndexToRef.get(0) != null) {
                bookingTabs.setSelectedIndex(0);
                confirmedJList.setSelectedIndex(0);
            } else if (confirmedIndexToRef.stream().filter(Objects::nonNull).count() == 0
                    && cancelledIndexToRef.size() == 1 && cancelledIndexToRef.get(0) != null) {
                bookingTabs.setSelectedIndex(1);
                cancelledJList.setSelectedIndex(0);
            }
        };
        nameSearchBtn.addActionListener(e -> doNameFilter.run());
        nameSearchField.addActionListener(e -> doNameFilter.run());

        // Clear filter restores the full list
        clearFilterBtn.addActionListener(e -> {
            nameSearchField.setText("");
            refreshList.run();
        });

        // Cancel booking action
        cancelButton.addActionListener(e -> {
            String ref = referenceField.getText().trim();
            if (ref.isEmpty())
                return;
            BookingRecord toCancel = BOOKING_STORE.findByReference(ref);
            if (toCancel == null) {
                showError("Booking reference was not found.");
                return;
            }
            if (isBookingLocked(toCancel)) {
                String msg = isFlightPast(toCancel)
                        ? "This booking cannot be cancelled — the flight has already departed."
                        : "This booking cannot be cancelled — 1 hour has passed since booking.";
                showError(msg);
                return;
            }
            double refund = computeRefundAmount(toCancel.totalAmount, toCancel.flightDateValue);
            long daysUntil = LocalDate.now().until(toCancel.flightDateValue, java.time.temporal.ChronoUnit.DAYS);
            String rateDesc = daysUntil >= 7 ? "80% (7+ days before flight)"
                    : daysUntil >= 1 ? "50% (1-6 days before flight)"
                            : "0% (same-day — no refund)";
            String msg = "Cancel booking " + ref + "?\n\n"
                    + "Refund Calculation:\n"
                    + "  Flight Date  : " + toCancel.flightDate + "\n"
                    + "  Days Until   : " + daysUntil + " day(s)\n"
                    + "  Refund Rate  : " + rateDesc + "\n"
                    + String.format("  Refund Amt   : PHP %,.2f\n\n", refund)
                    + "This will cancel the booking and record the refund.";
            int confirm = JOptionPane.showConfirmDialog(dialog, msg,
                    "Cancel Booking", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION)
                return;
            try {
                BookingRecord updated = BOOKING_STORE.updateStatus(ref, STATUS_CANCELED, refund);
                if (updated == null) {
                    showError("Booking reference was not found.");
                    return;
                }
                detailsArea.setText(formatBookingDetails(updated));
                detailsArea.setCaretPosition(0);
                cancelButton.setEnabled(false);
                modifyButton.setEnabled(false);
                if (latestConfirmedBooking != null && latestConfirmedBooking.reference.equals(updated.reference)) {
                    latestConfirmedBooking = updated;
                    if (currentStep == STEP_RECEIPT)
                        generateReceipt();
                }
                refreshList.run();
                selectRefInTabs.accept(ref);
                showTransientHelperMessage(
                        "Booking " + ref + " canceled. Refund: PHP " + String.format("%,.2f", refund), SUCCESS_GREEN);
            } catch (IOException ex) {
                showError("Unable to update the booking status: " + ex.getMessage());
            }
        });

        // Modify booking action
        modifyButton.addActionListener(e -> {
            String ref = referenceField.getText().trim();
            if (ref.isEmpty()) {
                showError("Please enter a booking reference number.");
                referenceField.requestFocusInWindow();
                return;
            }

            BookingRecord booking = BOOKING_STORE.findByReference(ref);
            if (booking == null) {
                showError("Booking reference was not found.");
                return;
            }
            if (!STATUS_CONFIRMED.equals(booking.status)) {
                showError("Only confirmed bookings can be modified.");
                return;
            }
            if (isFlightPast(booking)) {
                showError("This booking cannot be modified — the flight has already departed.");
                return;
            }

            BookingRecord updated = openModifyBookingDialog(dialog, booking);
            if (updated == null) {
                return;
            }

            detailsArea.setText(formatBookingDetails(updated));
            detailsArea.setCaretPosition(0);
            boolean canEditUpdated = STATUS_CONFIRMED.equals(updated.status) && !isFlightPast(updated);
            cancelButton.setEnabled(canEditUpdated);
            modifyButton.setEnabled(canEditUpdated);

            if (latestConfirmedBooking != null && latestConfirmedBooking.reference.equals(updated.reference)) {
                latestConfirmedBooking = updated;
                if (currentStep == STEP_RECEIPT) {
                    generateReceipt();
                }
            }

            refreshList.run();
            selectRefInTabs.accept(updated.reference);
            showTransientHelperMessage("Booking " + updated.reference + " modified successfully.", SUCCESS_GREEN);
        });

        // ── BOTTOM: action row ─────────────────────────────────────────────
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);
        JButton closeButton = createStyledButton("Close", Color.WHITE, TEXT_PRIMARY);
        closeButton.addActionListener(e -> dialog.dispose());
        actionRow.add(modifyButton);
        actionRow.add(cancelButton);
        actionRow.add(closeButton);
        root.add(actionRow, BorderLayout.SOUTH);

        dialog.setContentPane(root);

        // Auto-search if a preset reference was passed in
        if (presetReference != null && !presetReference.trim().isEmpty()) {
            SwingUtilities.invokeLater(loadByRef::run);
        }

        dialog.setVisible(true);
    }

    private String formatBookingDetails(BookingRecord booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("SKYWINGS MANAGE BOOKING\n");
        sb.append("====================================================================\n");
        String displayStatus = (STATUS_CONFIRMED.equals(booking.status) && isFlightPast(booking))
                ? "COMPLETED (Flight Departed — Read Only)"
                : booking.status;
        sb.append("Status        : ").append(displayStatus).append("\n");
        sb.append("Reference #   : ").append(booking.reference).append("\n");
        sb.append("Booked At     : ").append(booking.bookedAt).append("\n");
        sb.append("Flight No.    : ").append(booking.flightNo).append("\n");
        sb.append("Route         : ").append(booking.origin).append(" -> ").append(booking.destination).append("\n");
        sb.append("Flight Date   : ").append(booking.flightDate).append("\n");
        sb.append("Realtime EST  : ").append(booking.realtimeEst).append("\n");
        sb.append("Travel Class  : ").append(booking.className).append("\n");
        sb.append("Payment       : ").append(booking.paymentMethod).append(" / ").append(booking.maskedAccount)
                .append("\n");
        sb.append("Payment Stat  : ").append(booking.paymentStatus).append("\n");
        sb.append("Transaction # : ")
                .append(booking.paymentTransactionRef.isEmpty() ? "N/A" : booking.paymentTransactionRef)
                .append("\n");
        sb.append(String.format("Total Amount  : PHP %,.2f\n", booking.totalAmount));
        sb.append("--------------------------------------------------------------------\n");
        for (int i = 0; i < booking.passengers.size(); i++) {
            PassengerRecord passenger = booking.passengers.get(i);
            sb.append("Passenger ").append(i + 1).append(" : ").append(passenger.name).append("\n");
            sb.append("Seat         : ").append(passenger.seatCode).append("\n");
            sb.append("Contact      : ").append(passenger.contact).append("\n");
            sb.append("Discount     : ").append(passenger.getDiscountLabel()).append("\n");
            sb.append("Add-Ons      : ").append(passenger.getAddonSummary()).append("\n");
            sb.append(String.format("Amount       : PHP %,.2f\n", passenger.totalAmount));
            sb.append("--------------------------------------------------------------------\n");
        }
        if (STATUS_CANCELED.equals(booking.status)) {
            sb.append(String.format("Refund Amount : PHP %,.2f\n", booking.refundAmount));
            sb.append("This booking is canceled. Its seats are available again for future bookings.\n");
        } else if (isFlightPast(booking)) {
            sb.append("This flight has already departed. This booking is now read-only.\n");
        } else {
            sb.append("This booking remains active and can still be canceled offline.\n");
        }
        return sb.toString();
    }

    private void openReportsDialog() {
        JDialog dialog = new JDialog(this, "Booking Reports", true);
        dialog.setSize(1080, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_MAIN);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Booking Reports");
        title.setFont(FONT_SUBTITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // ── Tab 1: Summary (existing text report) ────────────────────────
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(FONT_MONO);
        area.setBackground(BG_CARD);
        area.setForeground(TEXT_PRIMARY);
        area.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JScrollPane summaryScroll = new JScrollPane(area);
        summaryScroll.setBorder(BorderFactory.createEmptyBorder());

        // ── Tab 2: Completed Flights ──────────────────────────────────────
        String[] completedCols = { "Reference", "Flight", "Route", "Flight Date", "Class", "Pax", "Total Paid",
                "Payment" };
        DefaultTableModel completedModel = new DefaultTableModel(completedCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable completedTable = createStyledTable(completedModel);
        completedTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        int[] completedWidths = { 95, 70, 290, 110, 140, 50, 130, 85 };
        for (int i = 0; i < completedWidths.length; i++)
            completedTable.getColumnModel().getColumn(i).setPreferredWidth(completedWidths[i]);

        TableRowSorter<DefaultTableModel> completedSorter = new TableRowSorter<>(completedModel);
        completedTable.setRowSorter(completedSorter);

        // ── Filter bar ────────────────────────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filterBar.setBackground(new Color(245, 246, 250));
        filterBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 212, 220)));

        JLabel lRef = new JLabel("Reference:");
        JLabel lFlight = new JLabel("Flight:");
        JLabel lClass = new JLabel("Class:");
        JLabel lDate = new JLabel("Date:");
        for (JLabel lbl : new JLabel[] { lRef, lFlight, lClass, lDate })
            lbl.setFont(FONT_SMALL);

        JTextField filterRef = new JTextField(9);
        filterRef.setFont(FONT_SMALL);
        filterRef.setToolTipText("Filter by reference (e.g. M0043)");

        // Date field — read-only; calendar popup fills it
        JTextField filterDate = new JTextField("All Dates", 10);
        filterDate.setFont(FONT_SMALL);
        filterDate.setEditable(false);
        filterDate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterDate.setToolTipText("Click to pick a date");

        JButton btnCal = createStyledButton("Pick Date", PRIMARY, Color.WHITE);

        JComboBox<String> filterFlight = new JComboBox<>(new String[] {
                "All Flights", "PR-101", "PR-102", "PR-103", "PR-201", "PR-202" });
        filterFlight.setFont(FONT_SMALL);

        JComboBox<String> filterClass = new JComboBox<>(new String[] {
                "All Classes", "Economy", "Premium Economy", "Business", "First Class" });
        filterClass.setFont(FONT_SMALL);

        JButton btnClearFilter = createStyledButton("Clear Filters", new Color(200, 60, 40), Color.WHITE);

        filterBar.add(lRef);
        filterBar.add(filterRef);
        filterBar.add(Box.createHorizontalStrut(4));
        filterBar.add(lFlight);
        filterBar.add(filterFlight);
        filterBar.add(Box.createHorizontalStrut(4));
        filterBar.add(lClass);
        filterBar.add(filterClass);
        filterBar.add(Box.createHorizontalStrut(4));
        filterBar.add(lDate);
        filterBar.add(filterDate);
        filterBar.add(btnCal);
        filterBar.add(Box.createHorizontalStrut(8));
        filterBar.add(btnClearFilter);

        JLabel completedCountLabel = new JLabel("Loading…");
        completedCountLabel.setFont(FONT_SMALL);
        completedCountLabel.setForeground(TEXT_SECONDARY);
        completedCountLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        Runnable applyCompletedFilter = () -> {
            String refTxt = filterRef.getText().trim().toLowerCase();
            String flight = (String) filterFlight.getSelectedItem();
            String cls = (String) filterClass.getSelectedItem();
            String dateTxt = filterDate.getText().trim().equals("All Dates") ? "" : filterDate.getText().trim();
            boolean none = refTxt.isEmpty() && "All Flights".equals(flight)
                    && "All Classes".equals(cls) && dateTxt.isEmpty();
            if (none) {
                completedSorter.setRowFilter(null);
            } else {
                completedSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                        if (!refTxt.isEmpty() && !e.getStringValue(0).toLowerCase().contains(refTxt))
                            return false;
                        if (!"All Flights".equals(flight) && !e.getStringValue(1).equals(flight))
                            return false;
                        if (!"All Classes".equals(cls) && !e.getStringValue(4).contains(cls))
                            return false;
                        if (!dateTxt.isEmpty() && !e.getStringValue(3).contains(dateTxt))
                            return false;
                        return true;
                    }
                });
            }
            int visible = completedTable.getRowCount();
            int total = completedModel.getRowCount();
            if (total == 0) {
                completedCountLabel.setText("  No completed flights yet.");
            } else if (visible == total) {
                completedCountLabel.setText("  " + total + " completed flight(s)");
            } else {
                completedCountLabel.setText("  Showing " + visible + " of " + total + " completed flight(s)");
            }
        };

        javax.swing.event.DocumentListener completedDocL = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent ev) {
                applyCompletedFilter.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent ev) {
                applyCompletedFilter.run();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent ev) {
                applyCompletedFilter.run();
            }
        };
        filterRef.getDocument().addDocumentListener(completedDocL);
        filterDate.getDocument().addDocumentListener(completedDocL);
        filterFlight.addActionListener(ev -> applyCompletedFilter.run());
        filterClass.addActionListener(ev -> applyCompletedFilter.run());
        btnCal.addActionListener(ev -> showCalendarPopup(btnCal, filterDate, applyCompletedFilter));
        btnClearFilter.addActionListener(ev -> {
            filterRef.setText("");
            filterFlight.setSelectedIndex(0);
            filterClass.setSelectedIndex(0);
            filterDate.setText("All Dates");
            applyCompletedFilter.run();
        });

        JPanel completedTopBar = new JPanel(new BorderLayout());
        completedTopBar.setBackground(new Color(245, 246, 250));
        completedTopBar.add(filterBar, BorderLayout.NORTH);
        completedTopBar.add(completedCountLabel, BorderLayout.SOUTH);

        JPanel completedPanel = new JPanel(new BorderLayout());
        completedPanel.setBackground(BG_MAIN);
        completedPanel.add(completedTopBar, BorderLayout.NORTH);
        JScrollPane completedScroll = new JScrollPane(completedTable);
        completedScroll.setBorder(BorderFactory.createEmptyBorder());
        completedScroll.getViewport().setBackground(BG_CARD);
        completedPanel.add(completedScroll, BorderLayout.CENTER);

        // ── Tab 3: Cancelled Bookings ─────────────────────────────────────
        String[] cancelledCols = { "Reference", "Flight", "Route", "Flight Date", "Class", "Pax", "Total Paid",
                "Refund Status", "Refunded Amt" };
        DefaultTableModel cancelledModel = new DefaultTableModel(cancelledCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable cancelledTable = createStyledTable(cancelledModel);
        cancelledTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        int[] cancelledWidths = { 95, 70, 270, 110, 140, 50, 130, 110, 130 };
        for (int i = 0; i < cancelledWidths.length; i++)
            cancelledTable.getColumnModel().getColumn(i).setPreferredWidth(cancelledWidths[i]);

        // Color-code "Refund Status" column: green = REFUNDED, orange = PAID (no
        // refund)
        cancelledTable.getColumnModel().getColumn(7).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public java.awt.Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                        super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        if (!sel) {
                            String v = val == null ? "" : val.toString();
                            setForeground("REFUNDED".equals(v) ? new Color(34, 139, 34) : new Color(200, 80, 0));
                        }
                        setFont(getFont().deriveFont(java.awt.Font.BOLD));
                        return this;
                    }
                });

        JLabel cancelledCountLabel = new JLabel("Loading…");
        cancelledCountLabel.setFont(FONT_SMALL);
        cancelledCountLabel.setForeground(new Color(180, 30, 30));
        cancelledCountLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JPanel cancelledPanel = new JPanel(new BorderLayout());
        cancelledPanel.setBackground(BG_MAIN);
        cancelledPanel.add(cancelledCountLabel, BorderLayout.NORTH);
        JScrollPane cancelledScroll = new JScrollPane(cancelledTable);
        cancelledScroll.setBorder(BorderFactory.createEmptyBorder());
        cancelledScroll.getViewport().setBackground(BG_CARD);
        cancelledPanel.add(cancelledScroll, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BODY);
        tabs.addTab("  Summary  ", summaryScroll);
        tabs.addTab("  Completed Flights  ", completedPanel);
        tabs.addTab("  Cancelled Bookings  ", cancelledPanel);
        tabs.setForegroundAt(2, new Color(180, 30, 30));
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));
        root.add(tabs, BorderLayout.CENTER);

        // ── Bottom button row ─────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(BG_MAIN);
        JButton btnDetails = createStyledButton("Details", new Color(0, 120, 180), Color.WHITE);
        btnDetails.setEnabled(false);
        JButton btnRefresh = createStyledButton("Refresh", PRIMARY, Color.WHITE);
        JButton btnClose = createStyledButton("Close", Color.WHITE, TEXT_PRIMARY);
        btnClose.addActionListener(e -> dialog.dispose());
        btnRow.add(btnDetails);
        btnRow.add(btnRefresh);
        btnRow.add(btnClose);
        root.add(btnRow, BorderLayout.SOUTH);

        // ── Details button wiring ─────────────────────────────────────────
        completedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                btnDetails.setEnabled(tabs.getSelectedIndex() == 1 && completedTable.getSelectedRow() >= 0);
        });
        cancelledTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                btnDetails.setEnabled(tabs.getSelectedIndex() == 2 && cancelledTable.getSelectedRow() >= 0);
        });
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 1) btnDetails.setEnabled(completedTable.getSelectedRow() >= 0);
            else if (idx == 2) btnDetails.setEnabled(cancelledTable.getSelectedRow() >= 0);
            else btnDetails.setEnabled(false);
        });
        btnDetails.addActionListener(e -> {
            int tabIdx = tabs.getSelectedIndex();
            String ref = null;
            if (tabIdx == 1 && completedTable.getSelectedRow() >= 0) {
                int modelRow = completedTable.convertRowIndexToModel(completedTable.getSelectedRow());
                ref = (String) completedModel.getValueAt(modelRow, 0);
            } else if (tabIdx == 2 && cancelledTable.getSelectedRow() >= 0) {
                int modelRow = cancelledTable.convertRowIndexToModel(cancelledTable.getSelectedRow());
                ref = (String) cancelledModel.getValueAt(modelRow, 0);
            }
            if (ref == null) return;
            BookingRecord bk = BOOKING_STORE.findByReference(ref);
            if (bk == null) {
                JOptionPane.showMessageDialog(dialog, "Booking not found: " + ref, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JDialog det = new JDialog(dialog, "Booking Details — " + ref, true);
            det.setSize(560, 520);
            det.setLocationRelativeTo(dialog);
            det.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            JTextArea ta = new JTextArea(formatBookingDetails(bk));
            ta.setEditable(false);
            ta.setFont(FONT_MONO);
            ta.setBackground(BG_CARD);
            ta.setForeground(TEXT_PRIMARY);
            ta.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            ta.setCaretPosition(0);
            JScrollPane sp = new JScrollPane(ta);
            sp.setBorder(BorderFactory.createEmptyBorder());
            JButton btnOk = createStyledButton("Close", PRIMARY, Color.WHITE);
            btnOk.addActionListener(ev -> det.dispose());
            JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
            bot.setBackground(BG_MAIN);
            bot.add(btnOk);
            det.add(sp, BorderLayout.CENTER);
            det.add(bot, BorderLayout.SOUTH);
            det.setVisible(true);
        });

        // ── Load data ─────────────────────────────────────────────────────
        Runnable loadStats = () -> {
            // Summary tab
            Map<String, Object> stats = BOOKING_STORE.loadReportStats();
            if (stats == null) {
                area.setText("Could not load report data. Check database connection.");
            } else {
                long total = (long) stats.getOrDefault("total_bookings", 0L);
                long confirmed = (long) stats.getOrDefault("confirmed_count", 0L);
                long cancelled = (long) stats.getOrDefault("cancelled_count", 0L);
                double revenue = (double) stats.getOrDefault("total_revenue", 0.0);
                double refunded = (double) stats.getOrDefault("total_refunded", 0.0);
                @SuppressWarnings("unchecked")
                List<Object[]> routes = (List<Object[]>) stats.getOrDefault("top_routes", new ArrayList<>());
                @SuppressWarnings("unchecked")
                List<Object[]> classes = (List<Object[]>) stats.getOrDefault("by_class", new ArrayList<>());
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
                StringBuilder sb = new StringBuilder();
                sb.append("SKYWINGS BOOKING REPORTS\n");
                sb.append("Generated: ").append(now).append("\n");
                sb.append("======================================================================\n");
                sb.append("SUMMARY\n");
                sb.append(String.format("  Total Bookings    : %d\n", total));
                sb.append(String.format("  Confirmed         : %d\n", confirmed));
                sb.append(String.format("  Cancelled         : %d\n", cancelled));
                sb.append(String.format("  Total Revenue     : PHP %,.2f\n", revenue));
                sb.append(String.format("  Total Refunded    : PHP %,.2f\n", refunded));
                sb.append("\n");
                sb.append("TOP ROUTES (Confirmed Bookings)\n");
                if (routes.isEmpty()) {
                    sb.append("  No confirmed bookings yet.\n");
                } else {
                    for (Object[] r : routes) {
                        sb.append(String.format("  %-8s %-22s -> %-22s  %d booking(s)\n",
                                r[0], r[1], r[2], ((Number) r[3]).longValue()));
                    }
                }
                sb.append("\n");
                sb.append("BOOKINGS BY CLASS (Confirmed)\n");
                if (classes.isEmpty()) {
                    sb.append("  No confirmed bookings yet.\n");
                } else {
                    for (Object[] c : classes) {
                        sb.append(String.format("  %-18s : %d booking(s)   PHP %,.2f\n",
                                c[0], ((Number) c[1]).longValue(), (double) c[2]));
                    }
                }
                sb.append("\n======================================================================\n");
                area.setText(sb.toString());
                area.setCaretPosition(0);
            }
            // Completed Flights tab
            List<Object[]> completed = BOOKING_STORE.loadCompletedFlightBookings();
            completedModel.setRowCount(0);
            for (Object[] row : completed) {
                completedModel.addRow(new Object[] {
                        row[0], row[1], row[2], row[3], row[4],
                        row[5],
                        String.format("PHP %,.2f", (double) row[6]),
                        row[7]
                });
            }
            applyCompletedFilter.run();
            // Cancelled Bookings tab
            List<Object[]> cancelledList = BOOKING_STORE.loadCancelledBookings();
            cancelledModel.setRowCount(0);
            if (cancelledList.isEmpty()) {
                cancelledCountLabel.setText("  No cancelled bookings.");
            } else {
                cancelledCountLabel.setText("  " + cancelledList.size() + " cancelled booking(s)");
                for (Object[] row : cancelledList) {
                    cancelledModel.addRow(new Object[] {
                            row[0], row[1], row[2], row[3], row[4],
                            row[5],
                            String.format("PHP %,.2f", (double) row[6]),
                            row[7],
                            (double) row[8] > 0 ? String.format("PHP %,.2f", (double) row[8]) : "—"
                    });
                }
            }
        };

        btnRefresh.addActionListener(e -> loadStats.run());
        loadStats.run();

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void showCalendarPopup(Component anchor, JTextField dateField, Runnable onSelect) {
        java.awt.Window owner = SwingUtilities.getWindowAncestor(anchor);
        java.awt.Window popupOwner = (owner != null) ? owner : this;
        JDialog popup = new JDialog(popupOwner, java.awt.Dialog.ModalityType.MODELESS);
        popup.setUndecorated(true);

        java.time.LocalDate[] nav = { null };
        try {
            nav[0] = java.time.LocalDate.parse(dateField.getText().trim()).withDayOfMonth(1);
        } catch (Exception ex) {
            nav[0] = java.time.LocalDate.now().withDayOfMonth(1);
        }

        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(FONT_BODY.deriveFont(java.awt.Font.BOLD));
        monthLabel.setForeground(Color.WHITE);

        JPanel gridHolder = new JPanel(new BorderLayout());
        gridHolder.setBackground(Color.WHITE);

        Runnable buildGrid = () -> {
            gridHolder.removeAll();
            java.time.LocalDate first = nav[0];
            monthLabel.setText(
                    first.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.ENGLISH)));

            JPanel grid = new JPanel(new java.awt.GridLayout(0, 7, 2, 2));
            grid.setBackground(Color.WHITE);
            grid.setBorder(BorderFactory.createEmptyBorder(6, 10, 10, 10));

            for (String d : new String[] { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" }) {
                JLabel h = new JLabel(d, SwingConstants.CENTER);
                h.setFont(FONT_SMALL.deriveFont(java.awt.Font.BOLD));
                h.setForeground(TEXT_SECONDARY);
                grid.add(h);
            }

            int startCol = first.getDayOfWeek().getValue() % 7;
            for (int i = 0; i < startCol; i++)
                grid.add(new JLabel(""));

            int days = first.lengthOfMonth();
            String selected = dateField.getText().trim();
            for (int day = 1; day <= days; day++) {
                java.time.LocalDate date = first.withDayOfMonth(day);
                String dateStr = date.toString();
                JButton btn = new JButton(String.valueOf(day));
                btn.setFont(FONT_SMALL);
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                if (dateStr.equals(selected)) {
                    btn.setBackground(PRIMARY);
                    btn.setForeground(Color.WHITE);
                    btn.setOpaque(true);
                    btn.setBorderPainted(false);
                } else {
                    btn.setContentAreaFilled(false);
                    btn.setBorderPainted(false);
                }
                btn.addActionListener(ev -> {
                    dateField.setText(dateStr);
                    onSelect.run();
                    popup.dispose();
                });
                grid.add(btn);
            }

            gridHolder.add(grid, BorderLayout.CENTER);
            gridHolder.revalidate();
            gridHolder.repaint();
            popup.pack();
        };

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        for (JButton b : new JButton[] { btnPrev, btnNext }) {
            b.setFont(FONT_SMALL);
            b.setForeground(Color.WHITE);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        btnPrev.addActionListener(e -> {
            nav[0] = nav[0].minusMonths(1);
            buildGrid.run();
        });
        btnNext.addActionListener(e -> {
            nav[0] = nav[0].plusMonths(1);
            buildGrid.run();
        });

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(PRIMARY);
        navBar.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        navBar.add(btnPrev, BorderLayout.WEST);
        navBar.add(monthLabel, BorderLayout.CENTER);
        navBar.add(btnNext, BorderLayout.EAST);

        JButton btnClearDate = new JButton("Clear Date");
        btnClearDate.setFont(FONT_SMALL);
        btnClearDate.setForeground(TEXT_SECONDARY);
        btnClearDate.setContentAreaFilled(false);
        btnClearDate.setBorderPainted(false);
        btnClearDate.setFocusPainted(false);
        btnClearDate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClearDate.addActionListener(e -> {
            dateField.setText("All Dates");
            onSelect.run();
            popup.dispose();
        });
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.add(btnClearDate);

        JPanel calPanel = new JPanel(new BorderLayout());
        calPanel.setBorder(BorderFactory.createLineBorder(new Color(180, 185, 200), 1));
        calPanel.add(navBar, BorderLayout.NORTH);
        calPanel.add(gridHolder, BorderLayout.CENTER);
        calPanel.add(bottomBar, BorderLayout.SOUTH);

        buildGrid.run();
        popup.setContentPane(calPanel);
        popup.pack();

        try {
            java.awt.Point p = anchor.getLocationOnScreen();
            popup.setLocation(p.x, p.y + anchor.getHeight());
        } catch (Exception ignored) {
        }

        popup.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
            }

            public void windowLostFocus(java.awt.event.WindowEvent e) {
                popup.dispose();
            }
        });
        popup.setVisible(true);
    }

    private void openFlightManagementDialog() {
        JDialog dialog = new JDialog(this, "Flight Management", true);
        dialog.setSize(980, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_MAIN);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Flight Management");
        title.setFont(FONT_SUBTITLE);
        title.setForeground(Color.WHITE);
        JLabel hint = new JLabel("Add, edit, or deactivate flights. Changes apply to new bookings.");
        hint.setFont(FONT_SMALL);
        hint.setForeground(new Color(180, 195, 220));
        header.add(title, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── 4 partitioned tabs ──────────────────────────────────────────────
        String[] cols = { "Flight No.", "Origin", "Destination", "Base Price (PHP)", "Type", "Capacity", "Status",
                "Departure" };
        String[] tabLabels = { "  Domestic (PH)  ", "  International — SE Asia  ", "  International — Europe  ",
                "  International — North America  " };

        DefaultTableModel[] models = new DefaultTableModel[4];
        JTable[] tables = new JTable[4];
        JTabbedPane mgmtTabs = new JTabbedPane();
        mgmtTabs.setFont(FONT_BODY);

        for (int t = 0; t < 4; t++) {
            final int ti = t;
            models[ti] = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            tables[ti] = createStyledTable(models[ti]);
            JScrollPane sp = new JScrollPane(tables[ti]);
            sp.setBorder(BorderFactory.createEmptyBorder());
            sp.getViewport().setBackground(BG_CARD);
            mgmtTabs.addTab(tabLabels[ti], sp);
        }
        mgmtTabs.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));
        root.add(mgmtTabs, BorderLayout.CENTER);

        Runnable loadTable = () -> {
            for (DefaultTableModel m : models)
                m.setRowCount(0);
            for (Object[] row : BOOKING_STORE.loadAllFlights()) {
                String fn = String.valueOf(row[0]);
                if (fn.startsWith("PR-1") || fn.startsWith("PR-10") || (!fn.startsWith("PR-2") && !fn.startsWith("PR-3")
                        && !fn.startsWith("PR-4") && String.valueOf(row[4]).equals("Domestic")))
                    models[0].addRow(row);
                else if (fn.startsWith("PR-2"))
                    models[1].addRow(row);
                else if (fn.startsWith("PR-3"))
                    models[2].addRow(row);
                else if (fn.startsWith("PR-4"))
                    models[3].addRow(row);
                else {
                    // Fallback: domestic flag decides
                    if ("Domestic".equals(String.valueOf(row[4])))
                        models[0].addRow(row);
                    else
                        models[1].addRow(row);
                }
            }
        };
        loadTable.run();

        // Helpers: resolve active tab's table and model at click-time
        java.util.function.Supplier<JTable> activeTable = () -> tables[Math.max(0, mgmtTabs.getSelectedIndex())];
        java.util.function.Supplier<DefaultTableModel> activeModel = () -> models[Math.max(0,
                mgmtTabs.getSelectedIndex())];

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(BG_MAIN);
        JButton btnAdd = createStyledButton("Add Flight", PRIMARY, Color.WHITE);
        JButton btnEdit = createStyledButton("Edit Selected", STEP_ACTIVE, TEXT_PRIMARY);
        JButton btnDeact = createStyledButton("Deactivate Selected", DANGER_RED, Color.WHITE);
        JButton btnClose = createStyledButton("Close", Color.WHITE, TEXT_PRIMARY);
        btnRow.add(btnAdd);
        btnRow.add(btnEdit);
        btnRow.add(btnDeact);
        btnRow.add(btnClose);
        root.add(btnRow, BorderLayout.SOUTH);

        btnClose.addActionListener(e -> dialog.dispose());

        // --- Add / Edit sub-dialog ---
        java.util.function.BiConsumer<String[], Boolean> openEditDialog = (prefill, isEdit) -> {
            JDialog sub = new JDialog(dialog, isEdit ? "Edit Flight" : "Add New Flight", true);
            sub.setSize(480, 580);
            sub.setMinimumSize(new Dimension(440, 560));
            sub.setLocationRelativeTo(dialog);
            sub.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            JPanel sp = new JPanel();
            sp.setLayout(new BoxLayout(sp, BoxLayout.Y_AXIS));
            sp.setBackground(BG_MAIN);
            sp.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

            JTextField tfNo = new JTextField(prefill != null ? prefill[0] : "");
            JTextField tfOrig = new JTextField(prefill != null ? prefill[1] : "");
            JTextField tfDest = new JTextField(prefill != null ? prefill[2] : "");
            JTextField tfPrice = new JTextField(prefill != null ? prefill[3] : "");
            JTextField tfDep = new JTextField(prefill != null && prefill.length > 7 ? prefill[7] : "08:00 AM");
            JTextField tfCap = new JTextField(prefill != null ? prefill[5] : "180");
            JComboBox<String> cbType = new JComboBox<>(new String[] { "Domestic", "International" });
            if (prefill != null && "International".equals(prefill[4]))
                cbType.setSelectedIndex(1);

            if (isEdit)
                tfNo.setEditable(false);

            for (Font f : new Font[] { FONT_BODY }) {
                for (JTextField tf : new JTextField[] { tfNo, tfOrig, tfDest, tfPrice, tfDep, tfCap }) {
                    tf.setFont(f);
                    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                    tf.setAlignmentX(Component.LEFT_ALIGNMENT);
                }
            }
            cbType.setFont(FONT_BODY);
            cbType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            cbType.setAlignmentX(Component.LEFT_ALIGNMENT);

            String[][] rows = { { "Flight No. (e.g. PR-301)", null }, { "Origin", null }, { "Destination", null },
                    { "Base Price (PHP)", null }, { "Departure Time (e.g. 08:00 AM)", null }, { "Type", null },
                    { "Capacity (seats)", null } };
            JTextField[] fields = { tfNo, tfOrig, tfDest, tfPrice, tfDep, null, tfCap };
            for (int i = 0; i < rows.length; i++) {
                JLabel lbl = new JLabel(rows[i][0]);
                lbl.setFont(FONT_SMALL);
                lbl.setForeground(TEXT_SECONDARY);
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                sp.add(lbl);
                sp.add(Box.createVerticalStrut(3));
                if (i == 5) {
                    sp.add(cbType);
                } else {
                    sp.add(fields[i]);
                }
                sp.add(Box.createVerticalStrut(8));
            }

            JLabel errLbl = new JLabel(" ");
            errLbl.setFont(FONT_SMALL);
            errLbl.setForeground(DANGER_RED);
            errLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.add(errLbl);

            JPanel subBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            subBtns.setBackground(BG_MAIN);
            subBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
            JButton btnSave = createStyledButton("Save", PRIMARY, Color.WHITE);
            JButton btnCancel = createStyledButton("Cancel", Color.WHITE, TEXT_PRIMARY);
            subBtns.add(btnSave);
            subBtns.add(btnCancel);
            sp.add(subBtns);

            btnCancel.addActionListener(ev -> sub.dispose());
            btnSave.addActionListener(ev -> {
                String no = tfNo.getText().trim().toUpperCase();
                String orig = tfOrig.getText().trim();
                String dest = tfDest.getText().trim();
                String priceS = tfPrice.getText().trim();
                String depS = tfDep.getText().trim();
                String capS = tfCap.getText().trim();
                boolean dom = cbType.getSelectedIndex() == 0;
                if (no.isEmpty() || orig.isEmpty() || dest.isEmpty() || priceS.isEmpty() || depS.isEmpty()
                        || capS.isEmpty()) {
                    errLbl.setText("All fields are required.");
                    return;
                }
                double price;
                int cap;
                try {
                    price = Double.parseDouble(priceS);
                } catch (NumberFormatException ex2) {
                    errLbl.setText("Base price must be a number.");
                    return;
                }
                try {
                    cap = Integer.parseInt(capS);
                } catch (NumberFormatException ex2) {
                    errLbl.setText("Capacity must be a whole number.");
                    return;
                }
                if (price <= 0 || cap <= 0) {
                    errLbl.setText("Price and capacity must be positive.");
                    return;
                }
                java.time.LocalTime depTime;
                try {
                    depTime = java.time.LocalTime.parse(depS.toUpperCase(),
                            java.time.format.DateTimeFormatter.ofPattern("h:mm a"));
                } catch (Exception ex2) {
                    try {
                        depTime = java.time.LocalTime.parse(depS,
                                java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                    } catch (Exception ex3) {
                        errLbl.setText("Departure time must be e.g. 08:00 AM or 14:30.");
                        return;
                    }
                }
                if (!isEdit && BOOKING_STORE.flightExists(no)) {
                    int confirm = JOptionPane.showConfirmDialog(sub,
                            "Flight " + no + " already exists.\nOverwrite its details?",
                            "Duplicate Flight Number", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm != JOptionPane.YES_OPTION)
                        return;
                }
                try {
                    BOOKING_STORE.saveFlight(no, orig, dest, price, dom, cap, true, depTime);
                    sub.dispose();
                    loadTable.run();
                    refreshFlightTables();
                } catch (IOException ex2) {
                    errLbl.setText("Save failed: " + ex2.getMessage());
                }
            });

            sub.setContentPane(sp);
            sub.setVisible(true);
        };

        btnAdd.addActionListener(e -> openEditDialog.accept(null, false));

        btnEdit.addActionListener(e -> {
            JTable tbl = activeTable.get();
            DefaultTableModel mdl = activeModel.get();
            int row = tbl.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Select a flight to edit.");
                return;
            }
            String[] data = new String[8];
            for (int i = 0; i < 8; i++)
                data[i] = String.valueOf(mdl.getValueAt(row, i));
            openEditDialog.accept(data, true);
        });

        btnDeact.addActionListener(e -> {
            JTable tbl = activeTable.get();
            DefaultTableModel mdl = activeModel.get();
            int row = tbl.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Select a flight to deactivate.");
                return;
            }
            String flightNo = String.valueOf(mdl.getValueAt(row, 0));
            String status = String.valueOf(mdl.getValueAt(row, 6));
            if ("Inactive".equals(status)) {
                try {
                    String depStr = String.valueOf(mdl.getValueAt(row, 7));
                    java.time.LocalTime depTime;
                    try {
                        depTime = java.time.LocalTime.parse(depStr,
                                java.time.format.DateTimeFormatter.ofPattern("h:mm a"));
                    } catch (Exception ex2) {
                        depTime = java.time.LocalTime.of(8, 0);
                    }
                    BOOKING_STORE.saveFlight(flightNo,
                            String.valueOf(mdl.getValueAt(row, 1)),
                            String.valueOf(mdl.getValueAt(row, 2)),
                            Double.parseDouble(String.valueOf(mdl.getValueAt(row, 3))),
                            "Domestic".equals(mdl.getValueAt(row, 4)),
                            Integer.parseInt(String.valueOf(mdl.getValueAt(row, 5))),
                            true, depTime);
                    loadTable.run();
                    refreshFlightTables();
                } catch (IOException ex) {
                    showError("Failed to reactivate: " + ex.getMessage());
                }
                return;
            }
            int activeBookings = BOOKING_STORE.countActiveBookingsForFlight(flightNo);
            String warningMsg = "Deactivate flight " + flightNo + "?\nIt will no longer appear in new bookings.";
            if (activeBookings > 0) {
                warningMsg = "WARNING: Flight " + flightNo + " has " + activeBookings
                        + " active confirmed booking(s).\n\n"
                        + "Deactivating will hide it from new bookings but will NOT cancel existing ones.\n"
                        + "Are you sure you want to deactivate this flight?";
            }
            int confirm = JOptionPane.showConfirmDialog(dialog, warningMsg,
                    "Confirm Deactivate", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION)
                return;
            try {
                BOOKING_STORE.deactivateFlight(flightNo);
                loadTable.run();
                refreshFlightTables();
            } catch (IOException ex) {
                showError("Failed to deactivate: " + ex.getMessage());
            }
        });

        // Update Deactivate button label when selection changes in any tab
        javax.swing.event.ListSelectionListener deactLabelUpdater = ev -> {
            if (ev.getValueIsAdjusting())
                return;
            JTable tbl = activeTable.get();
            DefaultTableModel mdl = activeModel.get();
            int row = tbl.getSelectedRow();
            if (row >= 0) {
                String status = String.valueOf(mdl.getValueAt(row, 6));
                btnDeact.setText("Inactive".equals(status) ? "Reactivate Selected" : "Deactivate Selected");
            }
        };
        for (JTable t : tables)
            t.getSelectionModel().addListSelectionListener(deactLabelUpdater);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private BookingRecord openModifyBookingDialog(Window owner, BookingRecord originalBooking) {
        if (isFlightPast(originalBooking)) {
            showError("This booking cannot be modified — the flight has already departed.");
            return null;
        }
        JDialog dialog = new JDialog(owner,
                "Modify Booking #" + originalBooking.reference,
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(900, 440);
        dialog.setMinimumSize(new Dimension(780, 380));
        dialog.setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(BG_MAIN);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Modify Booking Plan");
        title.setFont(FONT_SUBTITLE);
        title.setForeground(TEXT_PRIMARY);
        root.add(title, BorderLayout.NORTH);

        // ── LEFT: Current Booking Summary ──────────────────────────────────
        JPanel summaryCard = new JPanel();
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setBackground(BG_MUTED);
        summaryCard.setBorder(createCardBorder(12, 14, 12, 14));

        JLabel summaryTitle = new JLabel("Current Booking");
        summaryTitle.setFont(FONT_BODY.deriveFont(Font.BOLD));
        summaryTitle.setForeground(TEXT_SECONDARY);
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryCard.add(summaryTitle);
        summaryCard.add(Box.createVerticalStrut(8));

        String[] summaryLines = {
                "Flight    : " + originalBooking.flightNo,
                "Route     : " + originalBooking.origin + " → " + originalBooking.destination,
                "Date      : " + originalBooking.flightDate,
                "Class     : " + originalBooking.className,
                String.format("Total     : PHP %,.2f", originalBooking.totalAmount),
                "────────────────────────"
        };
        for (String line : summaryLines) {
            JLabel lbl = new JLabel(line);
            lbl.setFont(FONT_SMALL);
            lbl.setForeground(TEXT_PRIMARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryCard.add(lbl);
            summaryCard.add(Box.createVerticalStrut(4));
        }
        JLabel paxHeader = new JLabel("Passengers:");
        paxHeader.setFont(FONT_SMALL.deriveFont(Font.BOLD));
        paxHeader.setForeground(TEXT_PRIMARY);
        paxHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryCard.add(paxHeader);
        summaryCard.add(Box.createVerticalStrut(3));
        for (int i = 0; i < originalBooking.passengers.size(); i++) {
            JLabel pl = new JLabel("  " + (i + 1) + ". " + originalBooking.passengers.get(i).name);
            pl.setFont(FONT_SMALL);
            pl.setForeground(TEXT_SECONDARY);
            pl.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryCard.add(pl);
        }

        // ── RIGHT: Editable Form ───────────────────────────────────────────
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(BG_CARD);
        formCard.setBorder(createCardBorder(14, 16, 14, 16));

        Map<String, String> flightOptionMap = new LinkedHashMap<>();
        JComboBox<String> cbFlight = new JComboBox<>();
        styleComboBox(cbFlight);
        for (Object[] row : activeDomesticFlights) {
            String flightNo = String.valueOf(row[0]);
            String option = flightNo + " | " + row[1] + " -> " + row[2] + " (Domestic)";
            cbFlight.addItem(option);
            flightOptionMap.put(option, flightNo);
        }
        for (Object[] row : activeInternationalFlights) {
            String flightNo = String.valueOf(row[0]);
            String option = flightNo + " | " + row[1] + " -> " + row[2] + " (International)";
            cbFlight.addItem(option);
            flightOptionMap.put(option, flightNo);
        }
        for (Map.Entry<String, String> entry : flightOptionMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(originalBooking.flightNo)) {
                cbFlight.setSelectedItem(entry.getKey());
                break;
            }
        }
        JButton btnFlightDetails = createStyledButton("View Full Details", PRIMARY, Color.WHITE);
        btnFlightDetails.setFont(FONT_SMALL.deriveFont(Font.BOLD));
        btnFlightDetails.addActionListener(e -> showModifyFlightDetailsDialog(dialog, cbFlight, flightOptionMap));
        JPanel flightSelectorField = new JPanel(new BorderLayout(8, 0));
        flightSelectorField.setOpaque(false);
        flightSelectorField.add(cbFlight, BorderLayout.CENTER);
        flightSelectorField.add(btnFlightDetails, BorderLayout.EAST);

        JTextField tfFlightDate = new JTextField(originalBooking.flightDateValue.toString(), 16);
        tfFlightDate.setEditable(false);
        tfFlightDate.setToolTipText("Click to select flight date");
        tfFlightDate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JComboBox<String> cbClass = new JComboBox<>(CLASS_NAMES);
        styleComboBox(cbClass);
        cbClass.setSelectedItem(originalBooking.className);

        formCard.add(createFormRow("Flight:", flightSelectorField));
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(createFormRow("Flight Date:", tfFlightDate));
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(createFormRow("Travel Class:", cbClass));
        formCard.add(Box.createVerticalStrut(12));

        JLabel pricePreview = new JLabel(" ");
        pricePreview.setFont(FONT_SMALL);
        pricePreview.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(pricePreview);
        formCard.add(Box.createVerticalStrut(6));

        JLabel notes = new JLabel(
                "<html>Seats are retained when possible. If there are conflicts, seats are auto-reassigned.</html>");
        notes.setFont(FONT_SMALL);
        notes.setForeground(TEXT_SECONDARY);
        notes.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(notes);

        // Live price preview logic
        Runnable updatePreview = () -> {
            String sel = (String) cbFlight.getSelectedItem();
            String flightNo = sel != null ? flightOptionMap.get(sel) : null;
            Object[] flightRow = flightNo != null ? findFlightRowByNumber(flightNo) : null;
            String cls = (String) cbClass.getSelectedItem();
            if (flightRow != null && cls != null) {
                double est = estimateModifiedTotal(flightRow, cls, originalBooking.passengers);
                pricePreview.setText(String.format(
                        "<html><b>Estimated New Total:</b> <font color='#F6A600'>PHP %,.2f</font>"
                                + "&nbsp;&nbsp;<font color='#6B7890'>(current: PHP %,.2f)</font></html>",
                        est, originalBooking.totalAmount));
            }
        };
        cbFlight.addActionListener(ev -> updatePreview.run());
        cbClass.addActionListener(ev -> updatePreview.run());
        tfFlightDate.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showCalendarPopup(tfFlightDate, tfFlightDate, updatePreview);
            }
        });
        updatePreview.run();

        // ── Split center ───────────────────────────────────────────────────
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.setOpaque(false);
        center.add(summaryCard);
        center.add(formCard);
        root.add(center, BorderLayout.CENTER);

        final BookingRecord[] result = new BookingRecord[1];
        JButton btnSave = createStyledButton("Save Changes", PRIMARY, Color.WHITE);
        JButton btnCancel = createStyledButton("Close", Color.WHITE, TEXT_PRIMARY);

        btnSave.addActionListener(e -> {
            String selectedOption = (String) cbFlight.getSelectedItem();
            if (selectedOption == null || !flightOptionMap.containsKey(selectedOption)) {
                JOptionPane.showMessageDialog(dialog, "Please select a valid flight.",
                        "Modify Booking", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate newFlightDate;
            try {
                newFlightDate = LocalDate.parse(tfFlightDate.getText().trim(), BIRTHDAY_FORMATTER);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Flight date must be in YYYY-MM-DD format (example: 2026-12-31).",
                        "Modify Booking", JOptionPane.WARNING_MESSAGE);
                tfFlightDate.requestFocusInWindow();
                return;
            }
            if (newFlightDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(dialog, "Flight date cannot be in the past.",
                        "Modify Booking", JOptionPane.WARNING_MESSAGE);
                tfFlightDate.requestFocusInWindow();
                return;
            }

            String newClassName = (String) cbClass.getSelectedItem();
            if (newClassName == null || newClassName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please select a travel class.",
                        "Modify Booking", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String newFlightNo = flightOptionMap.get(selectedOption);
            Object[] newFlightRow = findFlightRowByNumber(newFlightNo);
            double estimatedTotal = newFlightRow != null
                    ? estimateModifiedTotal(newFlightRow, newClassName, originalBooking.passengers)
                    : 0;

            // Confirmation summary — only show changed fields
            StringBuilder confirmMsg = new StringBuilder();
            confirmMsg.append("Changes to Booking #").append(originalBooking.reference).append("\n");
            confirmMsg.append("=============================================\n");
            if (!newFlightNo.equalsIgnoreCase(originalBooking.flightNo)) {
                confirmMsg.append(String.format("Flight    : %s  →  %s\n",
                        originalBooking.flightNo, newFlightNo));
            }
            if (!newFlightDate.equals(originalBooking.flightDateValue)) {
                confirmMsg.append(String.format("Date      : %s  →  %s\n",
                        originalBooking.flightDate,
                        newFlightDate.format(FLIGHT_DATE_DISPLAY_FORMAT)));
            }
            if (!newClassName.equals(originalBooking.className)) {
                confirmMsg.append(String.format("Class     : %s  →  %s\n",
                        originalBooking.className, newClassName));
            }
            confirmMsg.append(String.format("Est. Total: PHP %,.2f  →  PHP %,.2f\n",
                    originalBooking.totalAmount, estimatedTotal));
            if (estimatedTotal < originalBooking.totalAmount) {
                double fareDiff = Math.round((originalBooking.totalAmount - estimatedTotal) * 100.0) / 100.0;
                String pm = originalBooking.paymentMethod;
                String refundDest;
                if ("E-Wallet".equalsIgnoreCase(pm)) {
                    refundDest = "E-Wallet (" + originalBooking.maskedAccount + ")";
                } else if ("Credit Card".equalsIgnoreCase(pm)) {
                    refundDest = "Credit Card (" + originalBooking.maskedAccount + ")";
                } else {
                    refundDest = "Debit Card (" + originalBooking.maskedAccount + ")";
                }
                confirmMsg.append(String.format("Refund Amt: PHP %,.2f\n", fareDiff));
                confirmMsg.append("Refund To : ").append(refundDest).append("\n");
                confirmMsg.append("Timeline  : 7–14 business days after confirmation\n");
            }
            confirmMsg.append("\nSeat reassignment will happen automatically.\n");
            confirmMsg.append("Proceed with these changes?");

            int confirm = JOptionPane.showConfirmDialog(dialog, confirmMsg.toString(),
                    "Confirm Changes", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION)
                return;

            try {
                BookingRecord updated = buildModifiedBookingRecord(
                        originalBooking, newFlightNo, newFlightDate, newClassName);
                BOOKING_STORE.upsertBooking(updated);
                result[0] = updated;
                dialog.dispose();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Modify Booking", JOptionPane.WARNING_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Unable to save booking changes: " + ex.getMessage(),
                        "Modify Booking", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.add(btnSave);
        footer.add(btnCancel);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
        return result[0];
    }

    private BookingRecord buildModifiedBookingRecord(BookingRecord originalBooking,
            String newFlightNo,
            LocalDate newFlightDate,
            String newClassName) {
        Object[] flightRow = findFlightRowByNumber(newFlightNo);
        if (flightRow == null) {
            throw new IllegalArgumentException("Selected flight was not found in the catalog.");
        }

        Set<String> occupiedSeats = getPersistedOccupiedSeatsExcludingReference(
                newFlightNo,
                newFlightDate,
                originalBooking.reference);
        List<PassengerRecord> updatedPassengers = rebuildPassengersForModifiedPlan(
                originalBooking,
                newFlightNo,
                newClassName,
                occupiedSeats);

        double updatedTotal = 0;
        for (PassengerRecord passenger : updatedPassengers) {
            updatedTotal += passenger.totalAmount;
        }

        return new BookingRecord(
                originalBooking.reference,
                originalBooking.bookedAtValue,
                newFlightNo,
                String.valueOf(flightRow[1]),
                String.valueOf(flightRow[2]),
                newFlightDate,
                isDomesticFlight(newFlightNo),
                newClassName,
                originalBooking.paymentMethod,
                originalBooking.maskedAccount,
                updatedTotal,
                getRealtimeEstSummary(newFlightNo),
                updatedPassengers,
                originalBooking.status,
                originalBooking.paymentStatus,
                originalBooking.paymentTransactionRef,
                originalBooking.paymentConfirmedAtValue,
                originalBooking.refundAmount);
    }

    private List<PassengerRecord> rebuildPassengersForModifiedPlan(BookingRecord originalBooking,
            String flightNo,
            String className,
            Set<String> occupiedSeats) {
        Object[] flightRow = findFlightRowByNumber(flightNo);
        if (flightRow == null) {
            throw new IllegalArgumentException("Selected flight was not found in the catalog.");
        }

        double baseFare = ((Number) flightRow[3]).doubleValue();
        int classIndex = getClassIndexByName(className);
        double classFare = baseFare * CLASS_MULTIPLIERS[classIndex];

        List<String> seatOptions = getSeatOptionsForBookingPlan(flightNo, className);
        Set<String> assignedSeats = new HashSet<>();
        List<PassengerRecord> rebuilt = new ArrayList<>();

        for (PassengerRecord passenger : originalBooking.passengers) {
            String preferredSeat = passenger.seatCode == null ? "" : passenger.seatCode;
            String assignedSeat = "";

            boolean preferredSeatAvailable = !preferredSeat.isEmpty()
                    && seatOptions.contains(preferredSeat)
                    && !occupiedSeats.contains(preferredSeat)
                    && assignedSeats.add(preferredSeat);
            if (preferredSeatAvailable) {
                assignedSeat = preferredSeat;
            } else {
                assignedSeat = findNextAvailableSeat(seatOptions, occupiedSeats, assignedSeats);
                if (assignedSeat.isEmpty()) {
                    throw new IllegalStateException(
                            "Unable to reassign seats for all passengers on the selected flight/date.");
                }
            }

            double discountableAddons = 0;
            boolean hasTax = false;
            for (String addonName : passenger.addons) {
                int addOnIndex = getAddonIndex(addonName);
                if (addOnIndex >= 0) {
                    if (ADDON_TAX.equalsIgnoreCase(addonName)) {
                        hasTax = true;
                    } else {
                        discountableAddons += ADDON_PRICES[addOnIndex];
                    }
                }
            }
            double taxBase = classFare + discountableAddons;
            double taxAmount = (hasTax && !(passenger.senior || passenger.pwd)) ? taxBase * TAX_RATE : 0.0;
            double subtotal = taxBase + taxAmount;
            double discount = (passenger.senior || passenger.pwd) ? taxBase * DISCOUNT_RATE : 0;
            double passengerTotal = subtotal - discount;

            rebuilt.add(new PassengerRecord(
                    passenger.name,
                    passenger.address,
                    passenger.contact,
                    passenger.age,
                    passenger.birthday,
                    passenger.idNumber,
                    passenger.senior,
                    passenger.pwd,
                    new SeatAssignment(passenger.name, assignedSeat),
                    passenger.addons,
                    passengerTotal));
        }

        return rebuilt;
    }

    private String findNextAvailableSeat(List<String> seatOptions, Set<String> occupiedSeats,
            Set<String> assignedSeats) {
        for (String seat : seatOptions) {
            if (!occupiedSeats.contains(seat) && assignedSeats.add(seat)) {
                return seat;
            }
        }
        return "";
    }

    private Object[] findFlightRowByNumber(String flightNo) {
        if (flightNo == null || flightNo.trim().isEmpty()) {
            return null;
        }
        for (Object[] row : activeDomesticFlights) {
            if (flightNo.equalsIgnoreCase(String.valueOf(row[0]))) {
                return row;
            }
        }
        for (Object[] row : activeInternationalFlights) {
            if (flightNo.equalsIgnoreCase(String.valueOf(row[0]))) {
                return row;
            }
        }
        return null;
    }

    private boolean isDomesticFlight(String flightNo) {
        for (Object[] row : activeDomesticFlights) {
            if (flightNo.equalsIgnoreCase(String.valueOf(row[0]))) {
                return true;
            }
        }
        return false;
    }

    private int getClassIndexByName(String className) {
        if (className == null) {
            return 0;
        }
        for (int i = 0; i < CLASS_NAMES.length; i++) {
            if (CLASS_NAMES[i].equalsIgnoreCase(className)) {
                return i;
            }
        }
        return 0;
    }

    private List<String> getSeatOptionsForBookingPlan(String flightNo, String className) {
        int classIndex = getClassIndexByName(className);
        int capacity = getFlightCapacity(flightNo);
        char[] letters = CLASS_SEAT_LETTERS[classIndex];
        int classCapacity = Math.max(letters.length * 2,
                (int) Math.ceil(capacity * CLASS_SEAT_RATIOS[classIndex]));
        int totalRows = (int) Math.ceil(classCapacity / (double) letters.length);
        String prefix = getSeatPrefix(classIndex);
        List<String> seats = new ArrayList<>();
        for (int row = 1; row <= totalRows; row++) {
            for (char letter : letters) {
                seats.add(String.format(Locale.ENGLISH, "%s-%02d%c", prefix, row, letter));
            }
        }
        return seats;
    }

    private Set<String> getPersistedOccupiedSeatsExcludingReference(String flightNo,
            LocalDate flightDate,
            String excludedReference) {
        Set<String> occupied = new HashSet<>();
        if (flightNo == null || flightDate == null) {
            return occupied;
        }

        for (BookingRecord booking : BOOKING_STORE.loadBookings()) {
            if (!STATUS_CONFIRMED.equals(booking.status)
                    || !flightNo.equals(booking.flightNo)
                    || !flightDate.equals(booking.flightDateValue)
                    || (excludedReference != null && excludedReference.equalsIgnoreCase(booking.reference))) {
                continue;
            }
            for (PassengerRecord passenger : booking.passengers) {
                if (passenger.seatCode != null && !passenger.seatCode.isEmpty()) {
                    occupied.add(passenger.seatCode);
                }
            }
        }
        return occupied;
    }

    private void showError(String message) {
        showTransientHelperMessage(message, DANGER_RED, 3600);
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void showPolicyDetailsDialog() {
        String details = "Flight Policy Details\n\n"
                + "1. " + POLICY_OVERBOOKING + "\n"
                + "   - Booking is capped at seat capacity plus 10% buffer.\n"
                + "   - Current passenger count must fit available slots for the selected flight.\n\n"
                + "2. " + POLICY_CHECKIN + "\n"
                + "   - Online check-in opens approximately 24 hours before departure.\n\n"
                + "3. " + POLICY_REFUND + "\n"
                + "   - Refund/cancellation handling depends on airline policy and timing.";
        JOptionPane.showMessageDialog(this, details, "Flight Policy Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private ValidationResult validatePassengerInputs(String name, String address, String contact,
            String age, String bday, String id) {
        if (name.isEmpty()) {
            return ValidationResult.error("Full name is required.", tfName);
        }
        if (!name.matches("^[A-Za-z][A-Za-z .'-]{1,58}[A-Za-z.]$")) {
            return ValidationResult.error(
                    "Full name must use letters and basic punctuation only (space, apostrophe, hyphen, period).",
                    tfName);
        }
        if (name.split("\\s+").length < 2) {
            return ValidationResult.error("Please enter full name with at least first and last name.", tfName);
        }

        if (address.isEmpty()) {
            return ValidationResult.error("Address is required.", tfAddress);
        }
        if (address.length() < 8 || address.length() > 120) {
            return ValidationResult.error("Address must be between 8 and 120 characters.", tfAddress);
        }

        if (contact.isEmpty()) {
            return ValidationResult.error("Contact number is required.", tfContact);
        }
        String digitsOnly = contact.replaceAll("[^0-9]", "");
        if (!contact.matches("^[0-9+\\-() ]+$") || digitsOnly.length() < 7 || digitsOnly.length() > 15) {
            return ValidationResult.error(
                    "Contact number must contain 7 to 15 digits and may include +, spaces, hyphens, and parentheses.",
                    tfContact);
        }

        if (age.isEmpty()) {
            return ValidationResult.error("Age is required.", tfAge);
        }
        int ageValue;
        try {
            ageValue = Integer.parseInt(age);
        } catch (NumberFormatException ex) {
            return ValidationResult.error("Age must be a whole number.", tfAge);
        }
        if (ageValue < 1 || ageValue > 120) {
            return ValidationResult.error("Age must be between 1 and 120.", tfAge);
        }

        if (bday.isEmpty()) {
            return ValidationResult.error("Birthday is required.", tfBday);
        }
        LocalDate birthday;
        try {
            birthday = LocalDate.parse(bday, BIRTHDAY_FORMATTER);
        } catch (DateTimeParseException ex) {
            return ValidationResult.error("Birthday must be in YYYY-MM-DD format (example: 2000-12-31).", tfBday);
        }
        if (birthday.isAfter(LocalDate.now())) {
            return ValidationResult.error("Birthday cannot be in the future.", tfBday);
        }

        int computedAge = Period.between(birthday, LocalDate.now()).getYears();
        if (Math.abs(computedAge - ageValue) > 1) {
            return ValidationResult.error("Age does not match birthday. Please check both fields.", tfAge);
        }

        if (id.isEmpty()) {
            return ValidationResult.error("ID number is required.", tfId);
        }
        if (!id.matches("^[A-Za-z0-9][A-Za-z0-9\\- ]{3,23}[A-Za-z0-9]$")) {
            return ValidationResult.error(
                    "ID number must be 5 to 25 characters and use letters, numbers, spaces, or hyphens.", tfId);
        }

        return ValidationResult.ok();
    }

    // ================================================================
    // UTILITY METHODS
    // ================================================================

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : BG_MUTED);
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(ACCENT);
                    c.setForeground(Color.WHITE);
                }
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                }
                return c;
            }
        };
        table.setFont(FONT_BODY);
        table.setRowHeight(42);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setFocusable(false);
        table.setBackground(BG_CARD);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 46));
        header.setReorderingAllowed(false);
        header.setBackground(new Color(245, 247, 250));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(245, 247, 250));
                c.setForeground(TEXT_PRIMARY);
                return c;
            }
        };
        headerRenderer.setBackground(new Color(245, 247, 250));
        headerRenderer.setForeground(TEXT_PRIMARY);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        headerRenderer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        for (int i = 0; i < table.getColumnCount(); i++) {
            String headerText = table.getColumnName(i).toLowerCase(Locale.ENGLISH);
            int preferredWidth = 150;
            int minWidth = 100;

            if (headerText.contains("flight")) {
                preferredWidth = 110;
                minWidth = 90;
            } else if (headerText.contains("origin") || headerText.contains("departure")) {
                preferredWidth = 180;
                minWidth = 130;
            } else if (headerText.equals("arrival") || headerText.contains("destination")) {
                preferredWidth = 180;
                minWidth = 130;
            } else if (headerText.contains("realtime est") || headerText.equals("est")) {
                preferredWidth = 170;
                minWidth = 150;
            } else if (headerText.contains("price") || headerText.contains("fare")) {
                preferredWidth = 170;
                minWidth = 130;
            } else if (headerText.contains("status")) {
                preferredWidth = 100;
                minWidth = 80;
            }

            table.getColumnModel().getColumn(i).setMinWidth(minWidth);
            table.getColumnModel().getColumn(i).setPreferredWidth(preferredWidth);
        }

        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    setText(String.format("PHP %,.2f", ((Number) value).doubleValue()));
                } else {
                    super.setValue(value);
                }
            }
        };
        priceRenderer.setHorizontalAlignment(JLabel.RIGHT);
        int priceColumnIndex = -1;
        for (int i = 0; i < table.getColumnCount(); i++) {
            String headerText = table.getColumnName(i).toLowerCase(Locale.ENGLISH);
            if (headerText.contains("price") || headerText.contains("fare")) {
                priceColumnIndex = i;
                break;
            }
        }
        if (priceColumnIndex >= 0) {
            table.getColumnModel().getColumn(priceColumnIndex).setCellRenderer(priceRenderer);
        }

        return table;
    }

    private void applyStatusColumnRenderer(JTable table) {
        int statusCol = -1;
        for (int i = 0; i < table.getColumnCount(); i++) {
            if ("Status".equalsIgnoreCase(table.getColumnName(i))) {
                statusCol = i;
                break;
            }
        }
        if (statusCol < 0)
            return;
        final int col = statusCol;
        table.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                String s = value == null ? "" : value.toString();
                if (!isSelected) {
                    if ("CLOSED".equals(s))
                        c.setForeground(new Color(180, 40, 40));
                    else if ("BOARDING".equals(s))
                        c.setForeground(new Color(140, 60, 180));
                    else if ("FULL".equals(s))
                        c.setForeground(new Color(200, 60, 60));
                    else if ("FILLING UP".equals(s))
                        c.setForeground(new Color(200, 140, 0));
                    else
                        c.setForeground(new Color(40, 155, 85));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        });
    }

    private int getClassSeatCount(int classIndex) {
        if (selectedFlight == null)
            return -1;
        String flightNo = String.valueOf(selectedFlight[0]);
        int capacity = getFlightCapacity(flightNo);
        return Math.max(CLASS_SEAT_LETTERS[classIndex].length * 2,
                (int) Math.ceil(capacity * CLASS_SEAT_RATIOS[classIndex]));
    }

    private int getClassAvailableSeats(int classIndex) {
        if (selectedFlight == null || selectedFlightDate == null)
            return -1;
        String flightNo = String.valueOf(selectedFlight[0]);
        String prefix = getSeatPrefix(classIndex) + "-";
        Set<String> occ = getPersistedOccupiedSeats(flightNo, selectedFlightDate);
        long taken = occ.stream().filter(s -> s.startsWith(prefix)).count();
        return (int) Math.max(0, getClassSeatCount(classIndex) - taken);
    }

    private JButton 
    createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                if (isHovered && isEnabled() && !bg.equals(Color.WHITE)) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 3, 16, 16);
                }

                Color buttonColor = isEnabled() ? getBackground() : new Color(220, 220, 220);
                g2.setColor(buttonColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                FontMetrics fm = g2.getFontMetrics();
                java.awt.geom.Rectangle2D r = fm.getStringBounds(getText(), g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(isEnabled() ? getForeground() : new Color(150, 150, 150));
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                if (bg.equals(Color.WHITE)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.dispose();
                }
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                if (bg.equals(Color.WHITE)) {
                    button.setBackground(new Color(248, 248, 248));
                } else {
                    button.setBackground(new Color(
                            Math.min(255, bg.getRed() + 15),
                            Math.min(255, bg.getGreen() + 15),
                            Math.min(255, bg.getBlue() + 15)));
                }
                ((JButton) e.getSource()).repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).setBackground(bg);
                ((JButton) e.getSource()).repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(new Color(
                        Math.max(0, bg.getRed() - 20),
                        Math.max(0, bg.getGreen() - 20),
                        Math.max(0, bg.getBlue() - 20)));
                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(bg);
                button.repaint();
            }
        });

        return btn;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_BODY);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        comboBox.setFocusable(true);
    }

    private JPanel createFormRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BODY);
        label.setPreferredSize(new Dimension(140, 25));
        label.setForeground(TEXT_PRIMARY);

        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setFont(FONT_BODY);
            tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 38));

            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));

            tf.setBackground(new Color(255, 255, 255));
            tf.setForeground(TEXT_PRIMARY);
            tf.setCaretColor(ACCENT);

            tf.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    tf.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ACCENT, 2),
                            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    tf.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER, 1),
                            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                }
            });
        } else if (field instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) field;
            styleComboBox(comboBox);
        }

        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(SHADOW_COLOR);
                g2.fillRoundRect(0, 2, getWidth(), getHeight() - 2, 12, 12);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 2, 12, 12);

                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 12, 12);
                g2.dispose();
            }
        };
        card.setBackground(BG_CARD);
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        return card;
    }

    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        private PlaceholderTextField(int columns, String placeholder) {
            super(columns);
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!getText().isEmpty() || placeholder == null || placeholder.isEmpty()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(TEXT_SECONDARY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, insets.left + 1, y);
            g2.dispose();
        }
    }

    private Border createCardBorder(int top, int left, int bottom, int right) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    private void installPassengerInputFilters() {
        applyTextFilter(tfName, 60, "[A-Za-z .'-]*");
        applyTextFilter(tfAddress, 120, "[A-Za-z0-9 .,#'/-]*");
        applyTextFilter(tfContact, 20, "[0-9+\\-() ]*");
        applyTextFilter(tfBday, 10, "[0-9-]*");
        applyTextFilter(tfId, 25, "[A-Za-z0-9\\- ]*");
    }

    private void applyTextFilter(JTextField field, int maxLength, String allowedPattern) {
        Pattern pattern = Pattern.compile(allowedPattern);
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                replace(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String incoming = (text == null) ? "" : text;
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String candidate = current.substring(0, offset)
                        + incoming
                        + current.substring(offset + length);

                if (candidate.length() <= maxLength && pattern.matcher(candidate).matches()) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
    }

    private String formatPassengerListEntry(int index, Map<String, Object> passenger) {
        String text = (index + 1) + ". " + passenger.get(KEY_NAME);
        if (isSeniorPassenger(passenger))
            text += " [Senior]";
        if (isPwdPassenger(passenger))
            text += " [PWD]";
        String seat = getPassengerSeat(passenger);
        if (!seat.isEmpty())
            text += " - Seat " + seat;
        return text;
    }

    private boolean[] getPassengerAddons(Map<String, Object> passenger) {
        return (boolean[]) passenger.get(KEY_ADDONS);
    }

    private boolean isSeniorPassenger(Map<String, Object> passenger) {
        return (Boolean) passenger.get(KEY_SENIOR);
    }

    private boolean isPwdPassenger(Map<String, Object> passenger) {
        return (Boolean) passenger.get(KEY_PWD);
    }

    private Object[][] buildFlightRowsWithRealtimeEst(Object[][] sourceRows) {
        Object[][] rows = new Object[sourceRows.length][6];
        for (int i = 0; i < sourceRows.length; i++) {
            String flightNo = String.valueOf(sourceRows[i][0]);
            rows[i][0] = sourceRows[i][0];
            rows[i][1] = sourceRows[i][1];
            rows[i][2] = sourceRows[i][2];
            rows[i][3] = getRealtimeEstTableValue(flightNo);
            rows[i][4] = sourceRows[i][3];
            rows[i][5] = getFlightStatusBadge(flightNo);
        }
        return rows;
    }

    private String getFlightStatusBadge(String flightNo) {
        // Time-based status takes priority when booking for today
        if (calendarSelectedDate != null && calendarSelectedDate.isEqual(LocalDate.now())) {
            int offsetMin = FLIGHT_EST_DEPARTURE_OFFSET_MIN.getOrDefault(flightNo, 60);
            if (offsetMin < 30)
                return "BOARDING";
            if (offsetMin < BOOKING_CUTOFF_HOURS * 60)
                return "CLOSED";
        }
        // Seat-availability status
        int remaining = getRemainingSlotsForFlight(flightNo);
        int cap = getFlightCapacity(flightNo);
        if (remaining <= 0)
            return "FULL";
        if (remaining <= cap * 0.15)
            return "FILLING UP";
        return "OPEN";
    }

    private void refreshFlightTableStatuses() {
        refreshTableStatusColumn(domesticTable, activeDomesticFlights);
        refreshTableStatusColumn(internationalTable, activeInternationalFlights);
        refreshTableStatusColumn(europeTable, activeEuropeFlights);
        refreshTableStatusColumn(northAmericaTable, activeNorthAmericaFlights);
    }

    private void refreshTableStatusColumn(JTable table, Object[][] sourceFlights) {
        if (table == null)
            return;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int row = 0; row < model.getRowCount() && row < sourceFlights.length; row++) {
            String flightNo = String.valueOf(model.getValueAt(row, 0));
            model.setValueAt(getFlightStatusBadge(flightNo), row, 5);
        }
    }

    private LocalDateTime getRealtimeEstimatedDeparture(String flightNo) {
        int offsetMinutes = FLIGHT_EST_DEPARTURE_OFFSET_MIN.getOrDefault(flightNo, 60);
        return LocalDateTime.now().plusMinutes(offsetMinutes);
    }

    private LocalDateTime getRealtimeEstimatedArrival(String flightNo) {
        return getRealtimeEstimatedArrival(flightNo, getRealtimeEstimatedDeparture(flightNo));
    }

    private LocalDateTime getRealtimeEstimatedArrival(String flightNo, LocalDateTime departure) {
        int durationMinutes = FLIGHT_EST_DURATION_MIN.getOrDefault(flightNo, 120);
        return departure.plusMinutes(durationMinutes);
    }

    private String getRealtimeEstTableValue(String flightNo) {
        LocalDateTime departure = getRealtimeEstimatedDeparture(flightNo);
        LocalDateTime arrival = getRealtimeEstimatedArrival(flightNo, departure);
        return departure.format(EST_TIME_FORMAT) + "-" + arrival.format(EST_TIME_FORMAT);
    }

    private String getRealtimeEstSummary(String flightNo) {
        LocalDateTime departure = getRealtimeEstimatedDeparture(flightNo);
        LocalDateTime arrival = getRealtimeEstimatedArrival(flightNo, departure);
        return departure.format(EST_DATE_TIME_FORMAT) + " -> " + arrival.format(EST_DATE_TIME_FORMAT);
    }

    private String centerDocumentBlock(String text, int targetWidth) {
        StringBuilder centered = new StringBuilder();
        String[] lines = text.split("\\r?\\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                centered.append(line);
            } else {
                int pad = Math.max(0, (targetWidth - line.length()) / 2);
                centered.append(repeatSpaces(pad)).append(line);
            }
            if (i < lines.length - 1) {
                centered.append("\n");
            }
        }
        return centered.toString();
    }

    private String repeatSpaces(int count) {
        if (count <= 0) {
            return "";
        }
        char[] chars = new char[count];
        Arrays.fill(chars, ' ');
        return new String(chars);
    }

    private JTable getActiveTable() {
        int idx = flightTabs.getSelectedIndex();
        if (idx == 2)
            return europeTable;
        if (idx == 3)
            return northAmericaTable;
        if (idx == 1)
            return internationalTable;
        return domesticTable;
    }

    private Object[][] getActiveFlightRows() {
        int idx = flightTabs.getSelectedIndex();
        if (idx == 2)
            return activeEuropeFlights;
        if (idx == 3)
            return activeNorthAmericaFlights;
        if (idx == 1)
            return activeInternationalFlights;
        return activeDomesticFlights;
    }

    private int getFlightCapacity(String flightNo) {
        if (flightNo == null || flightNo.isEmpty()) {
            return DEFAULT_DOMESTIC_CAPACITY;
        }
        int defaultCapacity = flightNo.startsWith("PR-2") ? DEFAULT_INTERNATIONAL_CAPACITY : DEFAULT_DOMESTIC_CAPACITY;
        return FLIGHT_CAPACITY.getOrDefault(flightNo, defaultCapacity);
    }

    private int getFlightOverbookingLimit(String flightNo) {
        int capacity = getFlightCapacity(flightNo);
        return (int) Math.floor(capacity * (1.0 + OVERBOOKING_BUFFER_RATE));
    }

    private int getRemainingSlotsForFlight(String flightNo, LocalDate flightDate) {
        int bookedSeats = FLIGHT_BOOKED_SEATS.getOrDefault(flightNo, 0)
                + getActiveStoredBookingCount(flightNo, flightDate);
        int overbookingLimit = getFlightOverbookingLimit(flightNo);
        return Math.max(0, overbookingLimit - bookedSeats);
    }

    private int getRemainingSlotsForFlight(String flightNo) {
        return getRemainingSlotsForFlight(flightNo, null);
    }

    private int getSelectedFlightRemainingSlots() {
        if (selectedFlight == null) {
            return Integer.MAX_VALUE;
        }
        return getRemainingSlotsForFlight(String.valueOf(selectedFlight[0]), selectedFlightDate);
    }

    private String getSelectedPaymentMethod() {
        if (rbCredit.isSelected())
            return "Credit Card";
        if (rbEwallet.isSelected())
            return "E-Wallet";
        return "Debit Card";
    }

    private void setFlightDateSelectors(LocalDate date) {
        calendarSelectedDate = date;
        calendarViewDate = date;
        if (calDateDisplayLabel != null) {
            calDateDisplayLabel.setText("Selected:  " + date.format(FLIGHT_DATE_DISPLAY_FORMAT));
        }
        refreshCalendarGrid();
    }

    private void refreshCalendarGrid() {
        if (calendarDaysGrid == null || calendarViewDate == null)
            return;
        calendarDaysGrid.removeAll();

        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = calendarViewDate.withDayOfMonth(1);
        int startDow = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = calendarViewDate.lengthOfMonth();
        int prevMonthDays = firstOfMonth.minusDays(1).getDayOfMonth();

        if (calMonthYearLabel != null) {
            calMonthYearLabel.setText(calendarViewDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        }

        for (int i = 0; i < startDow; i++) {
            JLabel lbl = new JLabel(String.valueOf(prevMonthDays - startDow + 1 + i), SwingConstants.CENTER);
            lbl.setFont(FONT_SMALL);
            lbl.setForeground(BORDER);
            calendarDaysGrid.add(lbl);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate thisDate = calendarViewDate.withDayOfMonth(day);
            boolean isPast = thisDate.isBefore(today);
            boolean isToday = thisDate.isEqual(today);
            boolean isSelected = calendarSelectedDate != null && thisDate.isEqual(calendarSelectedDate);

            JButton btn = new JButton(String.valueOf(day)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int pad = 3;
                    if (isSelected) {
                        g2.setColor(PRIMARY);
                        g2.fillOval(pad, pad, getWidth() - pad * 2, getHeight() - pad * 2);
                    } else if (isToday) {
                        g2.setColor(STEP_ACTIVE);
                        g2.fillOval(pad, pad, getWidth() - pad * 2, getHeight() - pad * 2);
                    } else if (getModel().isRollover() && !isPast) {
                        g2.setColor(ACCENT_LIGHT);
                        g2.fillOval(pad, pad, getWidth() - pad * 2, getHeight() - pad * 2);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setRolloverEnabled(true);
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setFont(FONT_SMALL.deriveFont(isSelected || isToday ? Font.BOLD : Font.PLAIN));
            btn.setForeground(isSelected || isToday ? Color.WHITE : (isPast ? BORDER : TEXT_PRIMARY));
            btn.setPreferredSize(new Dimension(36, 32));
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            if (!isPast) {
                LocalDate clickDate = thisDate;
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> {
                    calendarSelectedDate = clickDate;
                    if (calDateDisplayLabel != null) {
                        calDateDisplayLabel
                                .setText("Selected:  " + calendarSelectedDate.format(FLIGHT_DATE_DISPLAY_FORMAT));
                    }
                    refreshCalendarGrid();
                    refreshFlightTableStatuses();
                    updateFlightInfoLabel();
                });
            }
            calendarDaysGrid.add(btn);
        }

        int usedCells = startDow + daysInMonth;
        for (int i = 1; i <= 42 - usedCells; i++) {
            JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            lbl.setFont(FONT_SMALL);
            lbl.setForeground(BORDER);
            calendarDaysGrid.add(lbl);
        }

        calendarDaysGrid.revalidate();
        calendarDaysGrid.repaint();
    }

    private LocalDate getFlightDateFromInputs() {
        if (calendarSelectedDate == null) {
            throw new IllegalArgumentException("Please select a flight date.");
        }
        return calendarSelectedDate;
    }

    private String getFormattedFlightDate() {
        LocalDate date = selectedFlightDate != null ? selectedFlightDate
                : (calendarSelectedDate != null ? calendarSelectedDate : LocalDate.now());
        return date.format(FLIGHT_DATE_DISPLAY_FORMAT);
    }

    private JPanel buildInlineCalendar() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);

        // Selected date display strip
        String initDate = calendarSelectedDate != null
                ? calendarSelectedDate.format(FLIGHT_DATE_DISPLAY_FORMAT)
                : "—";
        calDateDisplayLabel = new JLabel("Selected:  " + initDate);
        calDateDisplayLabel.setFont(FONT_BODY.deriveFont(Font.BOLD));
        calDateDisplayLabel.setForeground(PRIMARY);
        calDateDisplayLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JPanel displayRow = new JPanel(new BorderLayout());
        displayRow.setOpaque(false);
        displayRow.add(calDateDisplayLabel, BorderLayout.CENTER);

        // Month/year navigation
        JPanel navRow = new JPanel(new BorderLayout(6, 0));
        navRow.setOpaque(false);
        navRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JButton btnPrev = createCalNavButton("‹");
        JButton btnNext = createCalNavButton("›");

        calMonthYearLabel = new JLabel("", SwingConstants.CENTER);
        calMonthYearLabel.setFont(FONT_BODY.deriveFont(Font.BOLD));
        calMonthYearLabel.setForeground(TEXT_PRIMARY);

        navRow.add(btnPrev, BorderLayout.WEST);
        navRow.add(calMonthYearLabel, BorderLayout.CENTER);
        navRow.add(btnNext, BorderLayout.EAST);

        btnPrev.addActionListener(e -> {
            calendarViewDate = calendarViewDate.minusMonths(1);
            refreshCalendarGrid();
        });
        btnNext.addActionListener(e -> {
            calendarViewDate = calendarViewDate.plusMonths(1);
            refreshCalendarGrid();
        });

        // Day-of-week headers
        String[] dayHeaders = { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" };
        JPanel headerRow = new JPanel(new GridLayout(1, 7, 2, 0));
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT));
        for (String d : dayHeaders) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(FONT_SMALL.deriveFont(Font.BOLD));
            lbl.setForeground(TEXT_SECONDARY);
            lbl.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
            headerRow.add(lbl);
        }

        // Days grid
        calendarDaysGrid = new JPanel(new GridLayout(6, 7, 2, 2));
        calendarDaysGrid.setOpaque(false);

        JPanel gridWrapper = new JPanel(new BorderLayout(0, 4));
        gridWrapper.setOpaque(false);
        gridWrapper.add(navRow, BorderLayout.NORTH);
        gridWrapper.add(headerRow, BorderLayout.CENTER);
        gridWrapper.add(calendarDaysGrid, BorderLayout.SOUTH);

        wrapper.add(displayRow, BorderLayout.NORTH);
        wrapper.add(gridWrapper, BorderLayout.CENTER);

        refreshCalendarGrid();
        return wrapper;
    }

    private JButton createCalNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_MUTED);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);
        return btn;
    }

    private void clearPassengerForm() {
        tfName.setText("");
        tfAddress.setText("");
        tfContact.setText("");
        tfAge.setText("");
        tfBday.setText("");
        tfId.setText("");
        cbSenior.setSelected(false);
        cbPWD.setSelected(false);
        tfName.requestFocus();
    }

    private void updateClock() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String date = now.format(java.time.format.DateTimeFormatter.ofPattern("EEE, MMM d yyyy"));
        String time = now.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a"));
        clockLabel.setText("<html><div style='text-align:right'>" + time + "<br><span style='font-size:80%'>" + date
                + "</span></div></html>");
    }

    private void clearPaymentFields() {
        tfPaymentNumber.setText("");
        tfPaymentName.setText("");
        tfPaymentExpiry.setText("");
        cbEwalletProvider.setSelectedIndex(0);
    }

    private void resetPassengerEditor() {
        editingPassengerIndex = -1;
        clearPassengerForm();
        updatePassengerEditorState();
    }

    private void updatePassengerEditorState() {
        if (btnPassengerAction != null) {
            btnPassengerAction.setText(editingPassengerIndex >= 0 ? "Save Passenger Update" : "+ Add Passenger");
        }
        if (btnCancelPassengerEdit != null) {
            btnCancelPassengerEdit.setVisible(editingPassengerIndex >= 0);
        }
    }

    private Map<String, Object> createPassengerMap(String name, String address, String contact, int ageValue,
            String birthday, String id, boolean senior, boolean pwd, boolean[] addons, String seatCode) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put(KEY_NAME, name);
        p.put(KEY_ADDRESS, address);
        p.put(KEY_CONTACT, contact);
        p.put(KEY_AGE, ageValue);
        p.put(KEY_BIRTHDAY, birthday);
        p.put(KEY_ID, id);
        p.put(KEY_SENIOR, senior);
        p.put(KEY_PWD, pwd);
        p.put(KEY_ADDONS, Arrays.copyOf(addons, addons.length));
        p.put(KEY_SEAT, seatCode == null ? "" : seatCode);
        return p;
    }

    private String getPassengerSeat(Map<String, Object> passenger) {
        Object value = passenger.get(KEY_SEAT);
        return value == null ? "" : value.toString();
    }

    private String getPassengerBadgeText(Map<String, Object> passenger) {
        StringBuilder badges = new StringBuilder();
        if (isSeniorPassenger(passenger)) {
            badges.append(" [Senior]");
        }
        if (isPwdPassenger(passenger)) {
            badges.append(" [PWD]");
        }
        return badges.toString();
    }

    private List<String> getSeatOptionsForCurrentSelection() {
        String flightNo = selectedFlight != null ? String.valueOf(selectedFlight[0]) : "";
        int capacity = getFlightCapacity(flightNo);
        char[] letters = CLASS_SEAT_LETTERS[selectedClassIndex];
        int classCapacity = Math.max(letters.length * 2,
                (int) Math.ceil(capacity * CLASS_SEAT_RATIOS[selectedClassIndex]));
        int totalRows = (int) Math.ceil(classCapacity / (double) letters.length);
        String prefix = getSeatPrefix();
        List<String> seats = new ArrayList<>();
        for (int row = 1; row <= totalRows; row++) {
            for (char letter : letters) {
                seats.add(String.format(Locale.ENGLISH, "%s-%02d%c", prefix, row, letter));
            }
        }
        return seats;
    }

    private String getSeatPrefix() {
        return getSeatPrefix(selectedClassIndex);
    }

    private String getSeatPrefix(int classIndex) {
        switch (classIndex) {
            case 1:
                return "PRE";
            case 2:
                return "BUS";
            case 3:
                return "FST";
            default:
                return "ECO";
        }
    }

    private void sanitizeSeatAssignments(List<String> validSeats, Set<String> externallyOccupied) {
        Set<String> seenSeats = new HashSet<>();
        for (Map<String, Object> passenger : passengers) {
            String seat = getPassengerSeat(passenger);
            boolean valid = !seat.isEmpty() && validSeats.contains(seat) && !externallyOccupied.contains(seat)
                    && seenSeats.add(seat);
            if (!valid) {
                passenger.put(KEY_SEAT, "");
            }
        }
    }

    private Set<String> getChosenSeatSetExcludingIndex(int index) {
        Set<String> chosen = new HashSet<>();
        for (int i = 0; i < passengers.size(); i++) {
            if (i == index) {
                continue;
            }
            String seat = getPassengerSeat(passengers.get(i));
            if (!seat.isEmpty()) {
                chosen.add(seat);
            }
        }
        return chosen;
    }

    private int getDeterministicMockOccupancy(String flightNo, LocalDate flightDate) {
        if (flightNo == null)
            return 0;
        LocalDate dateToUse = (flightDate != null) ? flightDate
                : (calendarSelectedDate != null ? calendarSelectedDate : LocalDate.now());

        int capacity = getFlightCapacity(flightNo);
        java.util.Random r = new java.util.Random(flightNo.hashCode() + dateToUse.hashCode());

        boolean isClosed = false;
        if (dateToUse.isEqual(LocalDate.now())) {
            int offsetMin = FLIGHT_EST_DEPARTURE_OFFSET_MIN.getOrDefault(flightNo, 60);
            if (offsetMin < BOOKING_CUTOFF_HOURS * 60) {
                isClosed = true;
            }
        }

        double occupancyRate;
        if (isClosed) {
            occupancyRate = 0.80 + (r.nextDouble() * 0.15); // 80% to 95%
        } else {
            occupancyRate = 0.20 + (r.nextDouble() * 0.70); // 20% to 90%
        }

        return (int) (capacity * occupancyRate);
    }

    private int getActiveStoredBookingCount(String flightNo, LocalDate flightDate) {
        if (flightNo == null)
            return 0;

        int count = getDeterministicMockOccupancy(flightNo, flightDate);

        if (flightDate != null) {
            for (BookingRecord booking : BOOKING_STORE.loadBookings()) {
                if (STATUS_CONFIRMED.equals(booking.status)
                        && flightNo.equals(booking.flightNo)
                        && flightDate.equals(booking.flightDateValue)) {
                    count += booking.passengers.size();
                }
            }
        }
        return count;
    }

    private Set<String> getPersistedOccupiedSeats(String flightNo, LocalDate flightDate) {
        Set<String> occupied = new HashSet<>();
        if (flightNo == null)
            return occupied;

        int targetOccupancy = getDeterministicMockOccupancy(flightNo, flightDate);

        java.util.List<String> premiumSeats = new java.util.ArrayList<>();
        java.util.List<String> economySeats = new java.util.ArrayList<>();
        for (int i = 0; i < CLASS_NAMES.length; i++) {
            if (CLASS_NAMES[i].equals("Business") || CLASS_NAMES[i].equals("First Class")) {
                premiumSeats.addAll(getSeatOptionsForBookingPlan(flightNo, CLASS_NAMES[i]));
            } else {
                economySeats.addAll(getSeatOptionsForBookingPlan(flightNo, CLASS_NAMES[i]));
            }
        }

        LocalDate dateToUse = (flightDate != null) ? flightDate
                : (calendarSelectedDate != null ? calendarSelectedDate : LocalDate.now());
        java.util.Random seededRandom = new java.util.Random(flightNo.hashCode() + dateToUse.hashCode());
        java.util.Collections.shuffle(premiumSeats, seededRandom);
        java.util.Collections.shuffle(economySeats, seededRandom);

        int capPremium = (int) Math.ceil(premiumSeats.size() * (0.03 + seededRandom.nextDouble() * 0.02));
        int currentPremiumCount = 0;
        java.util.List<String> remainingPremium = new java.util.ArrayList<>();

        for (String seat : premiumSeats) {
            if (currentPremiumCount < capPremium && occupied.size() < targetOccupancy) {
                occupied.add(seat);
                currentPremiumCount++;
            } else {
                remainingPremium.add(seat);
            }
        }

        for (String seat : economySeats) {
            if (occupied.size() >= targetOccupancy)
                break;
            occupied.add(seat);
        }

        // Fill remaining with premium if economy is full and we still need to reach
        // targetOccupancy
        for (String seat : remainingPremium) {
            if (occupied.size() >= targetOccupancy)
                break;
            occupied.add(seat);
        }

        if (flightDate != null) {
            for (BookingRecord booking : BOOKING_STORE.loadBookings()) {
                if (STATUS_CONFIRMED.equals(booking.status)
                        && flightNo.equals(booking.flightNo)
                        && flightDate.equals(booking.flightDateValue)) {
                    for (PassengerRecord passenger : booking.passengers) {
                        if (passenger.seatCode != null && !passenger.seatCode.isEmpty()) {
                            occupied.add(passenger.seatCode);
                        }
                    }
                }
            }
        }

        return occupied;
    }

    private ValidationResult validatePaymentInputs() {
        String number = tfPaymentNumber.getText().trim();
        String name = tfPaymentName.getText().trim();
        String expiry = tfPaymentExpiry.getText().trim();
        String method = getSelectedPaymentMethod();

        if ("E-Wallet".equals(method)) {
            if (number.isEmpty()) {
                return ValidationResult.error("Please enter your registered mobile number.", tfPaymentNumber);
            }
            String normalized = number.replaceAll("[\\s\\-]", "");
            boolean validPH = normalized.matches("^09\\d{9}$")
                    || normalized.matches("^\\+639\\d{9}$")
                    || normalized.matches("^639\\d{9}$");
            if (!validPH) {
                return ValidationResult.error(
                        "Mobile number must be a valid Philippine number (e.g. 09XXXXXXXXX or +639XXXXXXXXX).",
                        tfPaymentNumber);
            }
            if (name.isEmpty()) {
                return ValidationResult.error("Please enter the name registered on your e-wallet.", tfPaymentName);
            }
            if (!name.matches("^[A-Za-z][A-Za-z .'-]{1,58}[A-Za-z.]$")) {
                return ValidationResult.error("Registered name must use letters and basic punctuation only.",
                        tfPaymentName);
            }
            if (cbEwalletProvider.getSelectedIndex() == 0) {
                return ValidationResult.error("Please select your e-wallet provider.", cbEwalletProvider);
            }
        } else {
            if (number.isEmpty()) {
                return ValidationResult.error("Please enter your card number.", tfPaymentNumber);
            }
            String digitsOnly = number.replaceAll("[^0-9]", "");
            if (digitsOnly.length() < 12 || digitsOnly.length() > 19) {
                return ValidationResult.error("Card number must contain 12 to 19 digits.", tfPaymentNumber);
            }
            if (name.isEmpty()) {
                return ValidationResult.error("Please enter the cardholder name.", tfPaymentName);
            }
            if (!name.matches("^[A-Za-z][A-Za-z .'-]{1,58}[A-Za-z.]$")) {
                return ValidationResult.error("Cardholder name must use letters and basic punctuation only.",
                        tfPaymentName);
            }
            if (expiry.isEmpty()) {
                return ValidationResult.error("Please enter the card expiry date.", tfPaymentExpiry);
            }
            if (!expiry.matches("^(0[1-9]|1[0-2])/[0-9]{2}$")) {
                return ValidationResult.error("Card expiry must be in MM/YY format (e.g. 12/27).", tfPaymentExpiry);
            }
            // Check the card has not expired
            try {
                String[] parts = expiry.split("/");
                int expMonth = Integer.parseInt(parts[0]);
                int expYear = 2000 + Integer.parseInt(parts[1]);
                java.time.YearMonth cardExpiry = java.time.YearMonth.of(expYear, expMonth);
                java.time.YearMonth currentMonth = java.time.YearMonth.now();
                if (cardExpiry.isBefore(currentMonth)) {
                    return ValidationResult.error("This card has already expired. Please use a valid card.",
                            tfPaymentExpiry);
                }
            } catch (Exception ignored) {
            }

        }

        return ValidationResult.ok();
    }

    private boolean handleMockPaymentAndConfirm() {
        String maskedAccount = maskPaymentNumber(tfPaymentNumber.getText().trim());
        MockPaymentResult paymentResult = showMockPaymentAuthorizationDialog(
                paymentReference, selectedPaymentMethod, maskedAccount, grandTotal);
        if (paymentResult == null) {
            showTransientHelperMessage("Payment simulation canceled.", TEXT_SECONDARY);
            return false;
        }

        try {
            BOOKING_STORE.recordPaymentAttempt(
                    paymentReference,
                    grandTotal,
                    selectedPaymentMethod,
                    maskedAccount,
                    paymentResult.status,
                    paymentResult.transactionReference,
                    paymentResult.processedAt,
                    paymentResult.detailMessage);
        } catch (IOException ex) {
            showError("Unable to save payment transaction: " + ex.getMessage());
            return false;
        }

        if (!paymentResult.approved) {
            showError("Mock payment was declined. Seats remain on hold; retry payment to confirm booking.");
            return false;
        }

        return confirmCurrentBooking(paymentResult);
    }

    private MockPaymentResult showMockPaymentAuthorizationDialog(String bookingRef, String method,
            String maskedAccount, double amount) {
        String txnRef = generateMockTransactionReference();
        String summary = "<html><b>Sandbox Payment Authorization</b><br><br>"
                + "Booking Ref: " + bookingRef + "<br>"
                + "Method: " + method + "<br>"
                + "Account: " + maskedAccount + "<br>"
                + String.format(Locale.ENGLISH, "Amount: PHP %,.2f", amount) + "<br>"
                + "Transaction: " + txnRef + "<br><br>"
                + "Choose an outcome for this offline mock payment.</html>";
        Object[] options = { "Approve Payment", "Decline Payment", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this,
                summary,
                "Mock Payment Gateway",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        LocalDateTime processedAt = LocalDateTime.now();
        if (choice == 0) {
            return MockPaymentResult.approved(txnRef, processedAt, "Approved in offline sandbox.");
        }
        if (choice == 1) {
            return MockPaymentResult.declined(txnRef, processedAt, "Declined in offline sandbox.");
        }
        return null;
    }

    private String generateMockTransactionReference() {
        Random rng = new Random();
        int suffix = 100 + rng.nextInt(900);
        return "MOCK-" + System.currentTimeMillis() + "-" + suffix;
    }

    private String generateBookingReference() {
        Set<String> existingCodes = new HashSet<>();
        for (BookingRecord booking : BOOKING_STORE.loadBookings()) {
            existingCodes.add(booking.reference);
        }

        Random rng = new Random();
        String code;
        int attempts = 0;

        do {
            int num = 1000 + rng.nextInt(9000);
            code = String.valueOf(num);
            attempts++;

            if (attempts > 8900) {
                for (int n = 1000; n <= 9999; n++) {
                    String candidate = String.valueOf(n);
                    if (!existingCodes.contains(candidate)) {
                        return candidate;
                    }
                }
                throw new IllegalStateException("All 9000 booking codes (1000–9999) are in use.");
            }
        } while (existingCodes.contains(code));

        return code;
    }

    private boolean confirmCurrentBooking(MockPaymentResult paymentResult) {
        saveAddonSelections();
        saveSeatSelections();
        BookingRecord booking = buildCurrentBookingRecord(paymentResult);
        try {
            BOOKING_STORE.upsertBooking(booking);
            // Release any temporary holds owned by this session for this flight/date
            try {
                BOOKING_STORE.removeHoldsForOwnerAndFlight(sessionId, booking.flightNo, booking.flightDateValue);
            } catch (IOException ex) {
                // non-fatal: booking already saved, but log/show transient
                showTransientHelperMessage("Saved booking but could not remove seat holds: " + ex.getMessage(),
                        TEXT_SECONDARY);
            }
        } catch (IOException ex) {
            showError("Unable to save the booking locally: " + ex.getMessage());
            return false;
        }
        latestConfirmedBooking = booking;
        paymentReference = booking.reference;
        return true;
    }

    private BookingRecord buildCurrentBookingRecord(MockPaymentResult paymentResult) {
        String flightNo = String.valueOf(selectedFlight[0]);
        String maskedAccount = maskPaymentNumber(tfPaymentNumber.getText().trim());
        List<PassengerRecord> savedPassengers = new ArrayList<>();

        double basePrice = ((Number) selectedFlight[3]).doubleValue();
        double classFare = basePrice * CLASS_MULTIPLIERS[selectedClassIndex];
        for (Map<String, Object> passenger : passengers) {
            boolean[] addons = getPassengerAddons(passenger);
            double discountableAddons = 0;
            boolean hasTax = false;
            List<String> addonNames = new ArrayList<>();
            for (int j = 0; j < ADDON_NAMES.length; j++) {
                if (addons[j]) {
                    addonNames.add(ADDON_NAMES[j]);
                    if (ADDON_TAX.equals(ADDON_NAMES[j])) {
                        hasTax = true;
                    } else {
                        discountableAddons += ADDON_PRICES[j];
                    }
                }
            }
            boolean senior = isSeniorPassenger(passenger);
            boolean pwd = isPwdPassenger(passenger);
            double taxBase = classFare + discountableAddons;
            double taxAmount = (hasTax && !(senior || pwd)) ? taxBase * TAX_RATE : 0.0;
            double subtotal = taxBase + taxAmount;
            double discount = (senior || pwd) ? taxBase * DISCOUNT_RATE : 0;
            double total = subtotal - discount;

            savedPassengers.add(new PassengerRecord(
                    String.valueOf(passenger.get(KEY_NAME)),
                    String.valueOf(passenger.get(KEY_ADDRESS)),
                    String.valueOf(passenger.get(KEY_CONTACT)),
                    (Integer) passenger.get(KEY_AGE),
                    String.valueOf(passenger.get(KEY_BIRTHDAY)),
                    String.valueOf(passenger.get(KEY_ID)),
                    senior,
                    pwd,
                    new SeatAssignment(String.valueOf(passenger.get(KEY_NAME)), getPassengerSeat(passenger)),
                    addonNames,
                    total));
        }

        return new BookingRecord(
                paymentReference,
                LocalDateTime.now(),
                flightNo,
                String.valueOf(selectedFlight[1]),
                String.valueOf(selectedFlight[2]),
                selectedFlightDate,
                isDomestic,
                CLASS_NAMES[selectedClassIndex],
                selectedPaymentMethod,
                maskedAccount,
                grandTotal,
                getRealtimeEstSummary(flightNo),
                savedPassengers,
                STATUS_CONFIRMED,
                "PAID",
                paymentResult.transactionReference,
                paymentResult.processedAt,
                0.0);
    }

    private String maskPaymentNumber(String value) {
        String digitsOnly = value.replaceAll("[^0-9]", "");
        if (digitsOnly.length() >= 4) {
            return "****-****-" + digitsOnly.substring(digitsOnly.length() - 4);
        }
        return value;
    }

    private void saveConfirmationToFile() {
        if (receiptTextArea == null || receiptTextArea.getText().trim().isEmpty()) {
            showError("There is no confirmation to save yet.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        String reference = latestConfirmedBooking != null ? latestConfirmedBooking.reference : paymentReference;
        chooser.setSelectedFile(new File("confirmation-" + reference + ".txt"));
        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.print(receiptTextArea.getText());
        } catch (IOException ex) {
            showError("Unable to save the confirmation: " + ex.getMessage());
            return;
        }
        showTransientHelperMessage("Confirmation saved to " + file.getName() + ".", SUCCESS_GREEN);
    }

    private void printConfirmation() {
        if (receiptTextArea == null || receiptTextArea.getText().trim().isEmpty()) {
            showError("There is no confirmation to print yet.");
            return;
        }

        try {
            boolean printed = receiptTextArea.print();
            if (printed) {
                showTransientHelperMessage("Print job sent successfully.", SUCCESS_GREEN);
            }
        } catch (PrinterException ex) {
            showError("Unable to print the confirmation: " + ex.getMessage());
        }
    }

    private static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final JComponent field;

        private ValidationResult(boolean valid, String message, JComponent field) {
            this.valid = valid;
            this.message = message;
            this.field = field;
        }

        private static ValidationResult ok() {
            return new ValidationResult(true, "", null);
        }

        private static ValidationResult error(String message, JComponent field) {
            return new ValidationResult(false, message, field);
        }
    }

    private static class MockPaymentResult {
        private final boolean approved;
        private final String status;
        private final String transactionReference;
        private final LocalDateTime processedAt;
        private final String detailMessage;

        private MockPaymentResult(boolean approved, String status, String transactionReference,
                LocalDateTime processedAt, String detailMessage) {
            this.approved = approved;
            this.status = status;
            this.transactionReference = transactionReference;
            this.processedAt = processedAt;
            this.detailMessage = detailMessage;
        }

        private static MockPaymentResult approved(String transactionReference, LocalDateTime processedAt,
                String detailMessage) {
            return new MockPaymentResult(true, "SUCCESS", transactionReference, processedAt, detailMessage);
        }

        private static MockPaymentResult declined(String transactionReference, LocalDateTime processedAt,
                String detailMessage) {
            return new MockPaymentResult(false, "FAILED", transactionReference, processedAt, detailMessage);
        }
    }

    private static class SeatAssignment {
        private final String passengerName;
        private final String seatCode;

        private SeatAssignment(String passengerName, String seatCode) {
            this.passengerName = passengerName;
            this.seatCode = seatCode == null ? "" : seatCode;
        }
    }

    private static class PassengerRecord {
        private final String name;
        private final String address;
        private final String contact;
        private final int age;
        private final String birthday;
        private final String idNumber;
        private final boolean senior;
        private final boolean pwd;
        private final SeatAssignment seatAssignment;
        private final List<String> addons;
        private final double totalAmount;
        private final String seatCode;

        private PassengerRecord(String name, String address, String contact, int age, String birthday,
                String idNumber, boolean senior, boolean pwd, SeatAssignment seatAssignment,
                List<String> addons, double totalAmount) {
            this.name = name;
            this.address = address;
            this.contact = contact;
            this.age = age;
            this.birthday = birthday;
            this.idNumber = idNumber;
            this.senior = senior;
            this.pwd = pwd;
            this.seatAssignment = seatAssignment;
            this.addons = new ArrayList<>(addons);
            this.totalAmount = totalAmount;
            this.seatCode = seatAssignment != null ? seatAssignment.seatCode : "";
        }

        private String getDiscountLabel() {
            if (senior && pwd) {
                return "Senior/PWD";
            }
            if (senior) {
                return "Senior";
            }
            if (pwd) {
                return "PWD";
            }
            return "None";
        }

        private String getAddonSummary() {
            return addons.isEmpty() ? "None" : String.join(", ", addons);
        }
    }

    private static class BookingRecord {
        private final String reference;
        private final LocalDateTime bookedAtValue;
        private final String bookedAt;
        private final String flightNo;
        private final String origin;
        private final String destination;
        private final LocalDate flightDateValue;
        private final String flightDate;
        private final boolean domestic;
        private final String className;
        private final String paymentMethod;
        private final String maskedAccount;
        private final double totalAmount;
        private final String realtimeEst;
        private final List<PassengerRecord> passengers;
        private final String status;
        private final String paymentStatus;
        private final String paymentTransactionRef;
        private final LocalDateTime paymentConfirmedAtValue;
        private final String paymentConfirmedAt;
        private final double refundAmount;

        private BookingRecord(String reference, LocalDateTime bookedAtValue, String flightNo, String origin,
                String destination, LocalDate flightDateValue, boolean domestic, String className,
                String paymentMethod, String maskedAccount, double totalAmount, String realtimeEst,
                List<PassengerRecord> passengers, String status, String paymentStatus,
                String paymentTransactionRef, LocalDateTime paymentConfirmedAtValue, double refundAmount) {
            this.reference = reference;
            this.bookedAtValue = bookedAtValue;
            this.bookedAt = bookedAtValue.format(EST_DATE_TIME_FORMAT);
            this.flightNo = flightNo;
            this.origin = origin;
            this.destination = destination;
            this.flightDateValue = flightDateValue;
            this.flightDate = flightDateValue.format(FLIGHT_DATE_DISPLAY_FORMAT);
            this.domestic = domestic;
            this.className = className;
            this.paymentMethod = paymentMethod;
            this.maskedAccount = maskedAccount;
            this.totalAmount = totalAmount;
            this.realtimeEst = realtimeEst;
            this.passengers = new ArrayList<>(passengers);
            this.status = status;
            this.paymentStatus = paymentStatus == null || paymentStatus.trim().isEmpty() ? "UNPAID" : paymentStatus;
            this.paymentTransactionRef = paymentTransactionRef == null ? "" : paymentTransactionRef;
            this.paymentConfirmedAtValue = paymentConfirmedAtValue;
            this.paymentConfirmedAt = paymentConfirmedAtValue == null
                    ? "N/A"
                    : paymentConfirmedAtValue.format(EST_DATE_TIME_FORMAT);
            this.refundAmount = refundAmount;
        }

        private BookingRecord withStatus(String newStatus) {
            return new BookingRecord(reference, bookedAtValue, flightNo, origin, destination, flightDateValue,
                    domestic, className, paymentMethod, maskedAccount, totalAmount, realtimeEst, passengers, newStatus,
                    paymentStatus, paymentTransactionRef, paymentConfirmedAtValue, refundAmount);
        }
    }

    private static class BookingStore {
        private static final String PROPS_FILE = "db.properties";
        private final String dbUrl;
        private final String dbUser;
        private final String dbPass;

        private BookingStore() {
            Properties props = new Properties();
            File propsFile = new File(PROPS_FILE);
            if (propsFile.exists()) {
                try (FileInputStream fis = new FileInputStream(propsFile)) {
                    props.load(fis);
                } catch (IOException ignored) {
                }
            }
            dbUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/skywings");
            dbUser = props.getProperty("db.user", "root");
            dbPass = props.getProperty("db.password", "");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(
                        "MySQL JDBC driver not found. Add mysql-connector-j JAR to the Eclipse build path.", e);
            }
            initSchema();
        }

        private Connection getConnection() throws SQLException {
            return DriverManager.getConnection(dbUrl, dbUser, dbPass);
        }

        private void initSchema() {
            String[] ddl = {
                    "CREATE TABLE IF NOT EXISTS bookings (" +
                            "  reference      VARCHAR(20)   PRIMARY KEY," +
                            "  booked_at      DATETIME      NOT NULL," +
                            "  flight_no      VARCHAR(10)   NOT NULL," +
                            "  origin         VARCHAR(60)   NOT NULL," +
                            "  destination    VARCHAR(60)   NOT NULL," +
                            "  flight_date    DATE          NOT NULL," +
                            "  domestic       TINYINT(1)    NOT NULL," +
                            "  class_name     VARCHAR(30)   NOT NULL," +
                            "  payment_method VARCHAR(40)   NOT NULL," +
                            "  masked_account VARCHAR(30)   NOT NULL," +
                            "  total_amount   DECIMAL(10,2) NOT NULL," +
                            "  realtime_est   VARCHAR(80)   NOT NULL," +
                            "  status         VARCHAR(15)   NOT NULL," +
                            "  payment_status VARCHAR(20)   NOT NULL DEFAULT 'UNPAID'," +
                            "  payment_txn_ref VARCHAR(40)  NULL," +
                            "  payment_confirmed_at DATETIME NULL" +
                            ")",
                    "CREATE TABLE IF NOT EXISTS passengers (" +
                            "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                            "  booking_ref  VARCHAR(20)   NOT NULL," +
                            "  name         VARCHAR(120)  NOT NULL," +
                            "  address      VARCHAR(200)  NOT NULL," +
                            "  contact      VARCHAR(20)   NOT NULL," +
                            "  age          INT           NOT NULL," +
                            "  birthday     VARCHAR(12)   NOT NULL," +
                            "  id_number    VARCHAR(40)   NOT NULL," +
                            "  senior       TINYINT(1)    NOT NULL," +
                            "  pwd          TINYINT(1)    NOT NULL," +
                            "  seat_code    VARCHAR(8)    NOT NULL," +
                            "  addons       VARCHAR(300)  NOT NULL," +
                            "  total_amount DECIMAL(10,2) NOT NULL," +
                            "  FOREIGN KEY (booking_ref) REFERENCES bookings(reference) ON DELETE CASCADE" +
                            ")",
                    "CREATE TABLE IF NOT EXISTS seat_holds (" +
                            "  id          VARCHAR(36)  PRIMARY KEY," +
                            "  flight_no   VARCHAR(10)  NOT NULL," +
                            "  flight_date DATE         NOT NULL," +
                            "  seat_code   VARCHAR(8)   NOT NULL," +
                            "  owner       VARCHAR(36)  NOT NULL," +
                            "  expires_at  DATETIME     NOT NULL," +
                            "  status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'" +
                            ")",
                    "CREATE TABLE IF NOT EXISTS booking_payments (" +
                            "  id              BIGINT AUTO_INCREMENT PRIMARY KEY," +
                            "  booking_ref     VARCHAR(20)   NOT NULL," +
                            "  amount          DECIMAL(10,2) NOT NULL," +
                            "  method          VARCHAR(60)   NOT NULL," +
                            "  account_masked  VARCHAR(40)   NOT NULL," +
                            "  status          VARCHAR(20)   NOT NULL," +
                            "  transaction_ref VARCHAR(40)   NULL," +
                            "  detail_message  VARCHAR(255)  NULL," +
                            "  created_at      DATETIME      NOT NULL" +
                            ")"
            };
            try (Connection con = getConnection(); Statement st = con.createStatement()) {
                for (String sql : ddl) {
                    st.execute(sql);
                }
                ensureBookingColumns(con);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Cannot connect to MySQL.\n" + e.getMessage() +
                                "\n\nCheck db.properties and make sure MySQL Server is running.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void ensureBookingColumns(Connection con) throws SQLException {
            ensureColumn(con,
                    "ALTER TABLE bookings ADD COLUMN payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID'");
            ensureColumn(con,
                    "ALTER TABLE bookings ADD COLUMN payment_txn_ref VARCHAR(40) NULL");
            ensureColumn(con,
                    "ALTER TABLE bookings ADD COLUMN payment_confirmed_at DATETIME NULL");
            ensureColumn(con,
                    "ALTER TABLE bookings ADD COLUMN refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0");
            ensureColumn(con,
                    "ALTER TABLE flights ADD COLUMN departure_time TIME NOT NULL DEFAULT '08:00:00'");
        }

        private void ensureColumn(Connection con, String alterSql) throws SQLException {
            try (Statement st = con.createStatement()) {
                st.execute(alterSql);
            } catch (SQLException e) {
                if (e.getErrorCode() != 1060) { // duplicate column
                    throw e;
                }
            }
        }

        private synchronized List<BookingRecord> loadBookings() {
            List<BookingRecord> list = new ArrayList<>();
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM bookings ORDER BY booked_at DESC");
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildBookingRecord(con, rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return list;
        }

        private BookingRecord buildBookingRecord(Connection con, ResultSet rs) throws SQLException {
            String ref = rs.getString("reference");
            Timestamp paymentConfirmedAt = rs.getTimestamp("payment_confirmed_at");
            double refundAmt = 0.0;
            try {
                refundAmt = rs.getDouble("refund_amount");
            } catch (SQLException ignored) {
            }
            return new BookingRecord(
                    ref,
                    rs.getTimestamp("booked_at").toLocalDateTime(),
                    rs.getString("flight_no"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getDate("flight_date").toLocalDate(),
                    rs.getInt("domestic") == 1,
                    rs.getString("class_name"),
                    rs.getString("payment_method"),
                    rs.getString("masked_account"),
                    rs.getDouble("total_amount"),
                    rs.getString("realtime_est"),
                    loadPassengers(con, ref),
                    rs.getString("status"),
                    rs.getString("payment_status"),
                    rs.getString("payment_txn_ref"),
                    paymentConfirmedAt == null ? null : paymentConfirmedAt.toLocalDateTime(),
                    refundAmt);
        }

        private List<PassengerRecord> loadPassengers(Connection con, String bookingRef) throws SQLException {
            List<PassengerRecord> list = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM passengers WHERE booking_ref = ?")) {
                ps.setString(1, bookingRef);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String addonsStr = rs.getString("addons");
                        List<String> addons = addonsStr.isEmpty() ? new ArrayList<>()
                                : new ArrayList<>(Arrays.asList(addonsStr.split(",")));
                        String seat = rs.getString("seat_code");
                        SeatAssignment sa = seat.isEmpty() ? null : new SeatAssignment("", seat);
                        list.add(new PassengerRecord(
                                rs.getString("name"),
                                rs.getString("address"),
                                rs.getString("contact"),
                                rs.getInt("age"),
                                rs.getString("birthday"),
                                rs.getString("id_number"),
                                rs.getInt("senior") == 1,
                                rs.getInt("pwd") == 1,
                                sa,
                                addons,
                                rs.getDouble("total_amount")));
                    }
                }
            }
            return list;
        }

        private synchronized void upsertBooking(BookingRecord booking) throws IOException {
            String upsertSql = "INSERT INTO bookings (reference,booked_at,flight_no,origin,destination,flight_date," +
                    "domestic,class_name,payment_method,masked_account,total_amount,realtime_est,status," +
                    "payment_status,payment_txn_ref,payment_confirmed_at,refund_amount) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "booked_at=VALUES(booked_at),flight_no=VALUES(flight_no)," +
                    "origin=VALUES(origin),destination=VALUES(destination)," +
                    "flight_date=VALUES(flight_date),domestic=VALUES(domestic)," +
                    "class_name=VALUES(class_name),payment_method=VALUES(payment_method)," +
                    "masked_account=VALUES(masked_account),total_amount=VALUES(total_amount)," +
                    "realtime_est=VALUES(realtime_est),status=VALUES(status)," +
                    "payment_status=VALUES(payment_status),payment_txn_ref=VALUES(payment_txn_ref)," +
                    "payment_confirmed_at=VALUES(payment_confirmed_at)," +
                    "refund_amount=VALUES(refund_amount)";
            try (Connection con = getConnection()) {
                con.setAutoCommit(false);
                try (PreparedStatement ps = con.prepareStatement(upsertSql)) {
                    ps.setString(1, booking.reference);
                    ps.setTimestamp(2, Timestamp.valueOf(booking.bookedAtValue));
                    ps.setString(3, booking.flightNo);
                    ps.setString(4, booking.origin);
                    ps.setString(5, booking.destination);
                    ps.setDate(6, java.sql.Date.valueOf(booking.flightDateValue));
                    ps.setInt(7, booking.domestic ? 1 : 0);
                    ps.setString(8, booking.className);
                    ps.setString(9, booking.paymentMethod);
                    ps.setString(10, booking.maskedAccount);
                    ps.setDouble(11, booking.totalAmount);
                    ps.setString(12, booking.realtimeEst);
                    ps.setString(13, booking.status);
                    ps.setString(14, booking.paymentStatus);
                    ps.setString(15, booking.paymentTransactionRef.isEmpty() ? null : booking.paymentTransactionRef);
                    if (booking.paymentConfirmedAtValue == null) {
                        ps.setNull(16, Types.TIMESTAMP);
                    } else {
                        ps.setTimestamp(16, Timestamp.valueOf(booking.paymentConfirmedAtValue));
                    }
                    ps.setDouble(17, booking.refundAmount);
                    ps.executeUpdate();
                }
                try (PreparedStatement del = con.prepareStatement(
                        "DELETE FROM passengers WHERE booking_ref = ?")) {
                    del.setString(1, booking.reference);
                    del.executeUpdate();
                }
                try (PreparedStatement pp = con.prepareStatement(
                        "INSERT INTO passengers (booking_ref,name,address,contact,age,birthday," +
                                "id_number,senior,pwd,seat_code,addons,total_amount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)")) {
                    for (PassengerRecord p : booking.passengers) {
                        pp.setString(1, booking.reference);
                        pp.setString(2, p.name);
                        pp.setString(3, p.address);
                        pp.setString(4, p.contact);
                        pp.setInt(5, p.age);
                        pp.setString(6, p.birthday);
                        pp.setString(7, p.idNumber);
                        pp.setInt(8, p.senior ? 1 : 0);
                        pp.setInt(9, p.pwd ? 1 : 0);
                        pp.setString(10, p.seatCode);
                        pp.setString(11, String.join(",", p.addons));
                        pp.setDouble(12, p.totalAmount);
                        pp.addBatch();
                    }
                    pp.executeBatch();
                }
                con.commit();
            } catch (SQLException e) {
                throw new IOException("Database error saving booking: " + e.getMessage(), e);
            }
        }

        private synchronized void recordPaymentAttempt(String bookingReference,
                double amount,
                String method,
                String maskedAccount,
                String status,
                String transactionRef,
                LocalDateTime createdAt,
                String detailMessage) throws IOException {
            String insertSql = "INSERT INTO booking_payments " +
                    "(booking_ref,amount,method,account_masked,status,transaction_ref,detail_message,created_at) " +
                    "VALUES (?,?,?,?,?,?,?,?)";
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, bookingReference);
                ps.setDouble(2, amount);
                ps.setString(3, method);
                ps.setString(4, maskedAccount);
                ps.setString(5, status);
                ps.setString(6, transactionRef);
                ps.setString(7, detailMessage);
                ps.setTimestamp(8, Timestamp.valueOf(createdAt));
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new IOException("Database error saving payment attempt: " + e.getMessage(), e);
            }
        }

        private synchronized BookingRecord findByReference(String reference) {
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT * FROM bookings WHERE reference = ?")) {
                ps.setString(1, reference);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return buildBookingRecord(con, rs);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        private synchronized BookingRecord updateStatus(String reference, String status) throws IOException {
            return updateStatus(reference, status, 0.0);
        }

        private synchronized BookingRecord updateStatus(String reference, String status, double refundAmount)
                throws IOException {
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE bookings SET status = ?, refund_amount = ? WHERE reference = ?")) {
                ps.setString(1, status);
                ps.setDouble(2, refundAmount);
                ps.setString(3, reference);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new IOException("Database error updating status: " + e.getMessage(), e);
            }
            return findByReference(reference);
        }

        public static class SeatHoldInfo {
            public final String owner;
            public final LocalDateTime expiresAt;

            private SeatHoldInfo(String owner, LocalDateTime expiresAt) {
                this.owner = owner;
                this.expiresAt = expiresAt;
            }
        }

        synchronized void seedFlightsIfEmpty(Object[][] domestic, Object[][] international,
                int domCap, int intlCap) throws IOException {
            // Add flights table if not present
            String ddl = "CREATE TABLE IF NOT EXISTS flights (" +
                    "  flight_no      VARCHAR(10)   PRIMARY KEY," +
                    "  origin         VARCHAR(60)   NOT NULL," +
                    "  destination    VARCHAR(60)   NOT NULL," +
                    "  base_price     DECIMAL(10,2) NOT NULL," +
                    "  domestic       TINYINT(1)    NOT NULL," +
                    "  capacity       INT           NOT NULL," +
                    "  active         TINYINT(1)    NOT NULL DEFAULT 1," +
                    "  departure_time TIME          NOT NULL DEFAULT '08:00:00'" +
                    ")";
            // INSERT IGNORE skips rows whose flight_no already exists (PRIMARY KEY), so
            // this is safe to run on every startup — new flights get added, existing
            // untouched.
            String ins = "INSERT IGNORE INTO flights (flight_no,origin,destination,base_price,domestic,capacity,active,departure_time) VALUES (?,?,?,?,?,?,1,?)";
            try (Connection con = getConnection(); Statement st = con.createStatement()) {
                st.execute(ddl);
                try (PreparedStatement ps = con.prepareStatement(ins)) {
                    for (Object[] row : domestic) {
                        String fn = String.valueOf(row[0]);
                        ps.setString(1, fn);
                        ps.setString(2, String.valueOf(row[1]));
                        ps.setString(3, String.valueOf(row[2]));
                        ps.setDouble(4, (double) row[3]);
                        ps.setInt(5, 1);
                        ps.setInt(6, domCap);
                        ps.setString(7,
                                FLIGHT_SCHEDULED_TIMES.getOrDefault(fn, java.time.LocalTime.of(8, 0)).toString());
                        ps.addBatch();
                    }
                    for (Object[] row : international) {
                        String fn = String.valueOf(row[0]);
                        ps.setString(1, fn);
                        ps.setString(2, String.valueOf(row[1]));
                        ps.setString(3, String.valueOf(row[2]));
                        ps.setDouble(4, (double) row[3]);
                        ps.setInt(5, 0);
                        ps.setInt(6, intlCap);
                        ps.setString(7,
                                FLIGHT_SCHEDULED_TIMES.getOrDefault(fn, java.time.LocalTime.of(8, 0)).toString());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            } catch (SQLException e) {
                throw new IOException("Error seeding flights: " + e.getMessage(), e);
            }
        }

        synchronized List<Object[]> loadActiveFlights(boolean domestic) {
            List<Object[]> list = new ArrayList<>();
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT flight_no, origin, destination, base_price FROM flights" +
                                    " WHERE domestic = ? AND active = 1 ORDER BY flight_no")) {
                ps.setInt(1, domestic ? 1 : 0);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Object[] {
                                rs.getString("flight_no"), rs.getString("origin"),
                                rs.getString("destination"), rs.getDouble("base_price")
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return list;
        }

        synchronized List<Object[]> loadAllFlights() {
            List<Object[]> list = new ArrayList<>();
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT flight_no, origin, destination, base_price, domestic, capacity, active, departure_time"
                                    +
                                    " FROM flights ORDER BY domestic DESC, flight_no")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Time rawTime = null;
                        try {
                            rawTime = rs.getTime("departure_time");
                        } catch (SQLException ignored) {
                        }
                        String depDisplay = rawTime != null
                                ? rawTime.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
                                : "08:00 AM";
                        list.add(new Object[] {
                                rs.getString("flight_no"), rs.getString("origin"),
                                rs.getString("destination"), rs.getDouble("base_price"),
                                rs.getInt("domestic") == 1 ? "Domestic" : "International",
                                rs.getInt("capacity"),
                                rs.getInt("active") == 1 ? "Active" : "Inactive",
                                depDisplay
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return list;
        }

        synchronized Map<String, Integer> loadFlightCapacities() {
            Map<String, Integer> map = new HashMap<>();
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT flight_no, capacity FROM flights WHERE active = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        map.put(rs.getString("flight_no"), rs.getInt("capacity"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        }

        synchronized boolean flightExists(String flightNo) {
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT 1 FROM flights WHERE flight_no = ? LIMIT 1")) {
                ps.setString(1, flightNo);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        synchronized Map<String, java.time.LocalTime> loadFlightDepartureTimes() {
            Map<String, java.time.LocalTime> map = new HashMap<>();
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT flight_no, departure_time FROM flights WHERE active = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Time t = rs.getTime("departure_time");
                        if (t != null)
                            map.put(rs.getString("flight_no"), t.toLocalTime());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        }

        synchronized void saveFlight(String flightNo, String origin, String destination,
                double basePrice, boolean domestic, int capacity, boolean active,
                java.time.LocalTime departureTime) throws IOException {
            String sql = "INSERT INTO flights (flight_no,origin,destination,base_price,domestic,capacity,active,departure_time)"
                    +
                    " VALUES (?,?,?,?,?,?,?,?)" +
                    " ON DUPLICATE KEY UPDATE origin=VALUES(origin), destination=VALUES(destination)," +
                    " base_price=VALUES(base_price), domestic=VALUES(domestic)," +
                    " capacity=VALUES(capacity), active=VALUES(active), departure_time=VALUES(departure_time)";
            try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, flightNo);
                ps.setString(2, origin);
                ps.setString(3, destination);
                ps.setDouble(4, basePrice);
                ps.setInt(5, domestic ? 1 : 0);
                ps.setInt(6, capacity);
                ps.setInt(7, active ? 1 : 0);
                ps.setString(8, departureTime != null ? departureTime.toString() : "08:00");
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new IOException("Error saving flight: " + e.getMessage(), e);
            }
        }

        synchronized int countActiveBookingsForFlight(String flightNo) {
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT COUNT(*) FROM bookings WHERE flight_no = ? AND status = 'CONFIRMED'")) {
                ps.setString(1, flightNo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        synchronized void deactivateFlight(String flightNo) throws IOException {
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE flights SET active = 0 WHERE flight_no = ?")) {
                ps.setString(1, flightNo);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new IOException("Error deactivating flight: " + e.getMessage(), e);
            }
        }

        synchronized Map<String, Object> loadReportStats() {
            Map<String, Object> result = new HashMap<>();
            try (Connection con = getConnection()) {
                // Summary
                String sumSql = "SELECT COUNT(*) AS total," +
                        " SUM(CASE WHEN status='CONFIRMED' THEN 1 ELSE 0 END) AS confirmed," +
                        " SUM(CASE WHEN status='CANCELED'  THEN 1 ELSE 0 END) AS cancelled," +
                        " SUM(CASE WHEN status='CONFIRMED' THEN total_amount ELSE 0 END) AS revenue," +
                        " SUM(CASE WHEN status='CANCELED'  THEN refund_amount ELSE 0 END) AS refunded" +
                        " FROM bookings";
                try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sumSql)) {
                    if (rs.next()) {
                        result.put("total_bookings", rs.getLong("total"));
                        result.put("confirmed_count", rs.getLong("confirmed"));
                        result.put("cancelled_count", rs.getLong("cancelled"));
                        result.put("total_revenue", rs.getDouble("revenue"));
                        result.put("total_refunded", rs.getDouble("refunded"));
                    }
                }
                // Top routes
                String routeSql = "SELECT flight_no, origin, destination, COUNT(*) AS cnt" +
                        " FROM bookings WHERE status='CONFIRMED'" +
                        " GROUP BY flight_no, origin, destination ORDER BY cnt DESC LIMIT 10";
                List<Object[]> routes = new ArrayList<>();
                try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(routeSql)) {
                    while (rs.next()) {
                        routes.add(new Object[] { rs.getString("flight_no"), rs.getString("origin"),
                                rs.getString("destination"), rs.getLong("cnt") });
                    }
                }
                result.put("top_routes", routes);
                // By class
                String classSql = "SELECT class_name, COUNT(*) AS cnt, SUM(total_amount) AS rev" +
                        " FROM bookings WHERE status='CONFIRMED'" +
                        " GROUP BY class_name ORDER BY cnt DESC";
                List<Object[]> byClass = new ArrayList<>();
                try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(classSql)) {
                    while (rs.next()) {
                        byClass.add(new Object[] { rs.getString("class_name"), rs.getLong("cnt"),
                                rs.getDouble("rev") });
                    }
                }
                result.put("by_class", byClass);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }

        synchronized List<Object[]> loadCompletedFlightBookings() {
            List<Object[]> rows = new ArrayList<>();
            String sql = "SELECT b.reference, b.flight_no, b.origin, b.destination," +
                    " b.flight_date, b.class_name, b.total_amount, b.payment_status," +
                    " COUNT(p.id) AS pax_count" +
                    " FROM bookings b" +
                    " LEFT JOIN passengers p ON p.booking_ref = b.reference" +
                    " WHERE b.status = 'CONFIRMED' AND b.flight_date < CURDATE()" +
                    " GROUP BY b.reference, b.flight_no, b.origin, b.destination," +
                    " b.flight_date, b.class_name, b.total_amount, b.payment_status" +
                    " ORDER BY b.flight_date DESC";
            try (Connection con = getConnection();
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    rows.add(new Object[] {
                            rs.getString("reference"),
                            rs.getString("flight_no"),
                            rs.getString("origin") + " → " + rs.getString("destination"),
                            rs.getString("flight_date"),
                            rs.getString("class_name"),
                            rs.getLong("pax_count"),
                            rs.getDouble("total_amount"),
                            rs.getString("payment_status")
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return rows;
        }

        synchronized List<Object[]> loadCancelledBookings() {
            List<Object[]> rows = new ArrayList<>();
            String sql = "SELECT b.reference, b.flight_no, b.origin, b.destination," +
                    " b.flight_date, b.class_name, b.total_amount, b.payment_status," +
                    " b.refund_amount, COUNT(p.id) AS pax_count" +
                    " FROM bookings b" +
                    " LEFT JOIN passengers p ON p.booking_ref = b.reference" +
                    " WHERE b.status = 'CANCELED'" +
                    " GROUP BY b.reference, b.flight_no, b.origin, b.destination," +
                    " b.flight_date, b.class_name, b.total_amount, b.payment_status, b.refund_amount" +
                    " ORDER BY b.flight_date DESC";
            try (Connection con = getConnection();
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    rows.add(new Object[] {
                            rs.getString("reference"),
                            rs.getString("flight_no"),
                            rs.getString("origin") + " → " + rs.getString("destination"),
                            rs.getString("flight_date"),
                            rs.getString("class_name"),
                            rs.getLong("pax_count"),
                            rs.getDouble("total_amount"),
                            rs.getString("payment_status"),
                            rs.getDouble("refund_amount")
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return rows;
        }

        public synchronized void purgeExpiredSeatHolds() throws IOException {
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE seat_holds SET status='EXPIRED' WHERE expires_at < NOW() AND status='ACTIVE'")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new IOException("Database error purging seat holds: " + e.getMessage(), e);
            }
        }

        public synchronized Map<String, SeatHoldInfo> getActiveHoldsForFlightDate(String flightNo,
                LocalDate flightDate) {
            Map<String, SeatHoldInfo> map = new HashMap<>();
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "SELECT seat_code, owner, expires_at FROM seat_holds " +
                                    "WHERE flight_no = ? AND flight_date = ? AND expires_at >= NOW() AND status='ACTIVE'")) {
                ps.setString(1, flightNo);
                ps.setDate(2, java.sql.Date.valueOf(flightDate));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        map.put(rs.getString("seat_code"),
                                new SeatHoldInfo(rs.getString("owner"),
                                        rs.getTimestamp("expires_at").toLocalDateTime()));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        }

        public synchronized boolean addSeatHold(String flightNo, LocalDate flightDate, String seatCode, String owner)
                throws IOException {
            try (Connection con = getConnection()) {
                // Check if seat is already actively held
                try (PreparedStatement chk = con.prepareStatement(
                        "SELECT COUNT(*) FROM seat_holds " +
                                "WHERE flight_no=? AND flight_date=? AND seat_code=? AND expires_at >= NOW() AND status='ACTIVE'")) {
                    chk.setString(1, flightNo);
                    chk.setDate(2, java.sql.Date.valueOf(flightDate));
                    chk.setString(3, seatCode);
                    try (ResultSet rs = chk.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            return false;
                        }
                    }
                }
                // Mark any expired hold for this seat as EXPIRED before inserting
                try (PreparedStatement del = con.prepareStatement(
                        "UPDATE seat_holds SET status='EXPIRED' WHERE flight_no=? AND flight_date=? AND seat_code=? AND status='ACTIVE'")) {
                    del.setString(1, flightNo);
                    del.setDate(2, java.sql.Date.valueOf(flightDate));
                    del.setString(3, seatCode);
                    del.executeUpdate();
                }
                try (PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO seat_holds (id,flight_no,flight_date,seat_code,owner,expires_at,status) " +
                                "VALUES (?,?,?,?,?,?,'ACTIVE')")) {
                    ins.setString(1, UUID.randomUUID().toString());
                    ins.setString(2, flightNo);
                    ins.setDate(3, java.sql.Date.valueOf(flightDate));
                    ins.setString(4, seatCode);
                    ins.setString(5, owner);
                    ins.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now().plusMinutes(SEAT_HOLD_MINUTES)));
                    ins.executeUpdate();
                }
            } catch (SQLException e) {
                throw new IOException("Database error adding seat hold: " + e.getMessage(), e);
            }
            return true;
        }

        public synchronized boolean removeSeatHoldBySeat(String flightNo, LocalDate flightDate, String seatCode,
                String owner) throws IOException {
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE seat_holds SET status='RELEASED' WHERE flight_no=? AND flight_date=? AND seat_code=? AND owner=? AND status='ACTIVE'")) {
                ps.setString(1, flightNo);
                ps.setDate(2, java.sql.Date.valueOf(flightDate));
                ps.setString(3, seatCode);
                ps.setString(4, owner);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new IOException("Database error removing seat hold: " + e.getMessage(), e);
            }
        }

        public synchronized void removeHoldsForOwnerAndFlight(String owner, String flightNo, LocalDate flightDate)
                throws IOException {
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE seat_holds SET status='COMPLETED' WHERE owner=? AND flight_no=? AND flight_date=? AND status='ACTIVE'")) {
                ps.setString(1, owner);
                ps.setString(2, flightNo);
                ps.setDate(3, java.sql.Date.valueOf(flightDate));
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new IOException("Database error removing seat holds: " + e.getMessage(), e);
            }
        }

        synchronized void seedMockCompletedBookings() {
            try (Connection con = getConnection()) {
                try (PreparedStatement chk = con.prepareStatement(
                        "SELECT COUNT(*) FROM bookings WHERE reference LIKE 'M%'")) {
                    try (ResultSet rs = chk.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0)
                            return;
                    }
                }

                Object[][] flightData = {
                        { "PR-101", "Manila (MNL)", "Cebu (CEB)", 3500.0, 1 },
                        { "PR-102", "Manila (MNL)", "Davao (DVO)", 4200.0, 1 },
                        { "PR-103", "Manila (MNL)", "Boracay (MPH)", 3800.0, 1 },
                        { "PR-201", "Manila (MNL)", "Singapore (SIN)", 8500.0, 0 },
                        { "PR-202", "Manila (MNL)", "Bangkok (BKK)", 7800.0, 0 },
                };
                String[] dates = {
                        "2026-05-12", "2026-05-13", "2026-05-14",
                        "2026-05-15", "2026-05-16", "2026-05-17"
                };
                String[][] classDist = {
                        { "Economy", "1.0" }, { "Economy", "1.0" }, { "Economy", "1.0" }, { "Economy", "1.0" },
                        { "Premium Economy", "1.8" }, { "Business", "3.0" }, { "First Class", "5.0" }
                };
                String[] payMethods = { "Credit Card", "E-Wallet", "Debit Card" };
                String[] maskedAccts = { "**** 4521", "**** 7832", "**** 3310", "**** 9087", "**** 6641" };
                String[] names = {
                        "Maria Santos", "Juan dela Cruz", "Ana Reyes", "Pedro Ramos",
                        "Rosa Garcia", "Carlos Mendoza", "Elena Villanueva", "Miguel Torres",
                        "Luz Fernandez", "Roberto Aquino", "Celine Bautista", "Jose Pascual",
                        "Grace Morales", "Emmanuel Cruz", "Patricia Lim", "Antonio Gomez",
                        "Marisol Castillo", "Danilo Navarro", "Teresita Ocampo", "Rafael Dizon"
                };
                String[] addresses = {
                        "123 Rizal St, Makati City",
                        "456 Mabini Ave, Quezon City",
                        "789 Luna Blvd, Cebu City",
                        "321 Bonifacio St, Davao City",
                        "654 Del Pilar St, Manila"
                };
                java.time.format.DateTimeFormatter dtFmt = java.time.format.DateTimeFormatter
                        .ofPattern("MMM dd, yyyy hh:mm a", java.util.Locale.ENGLISH);

                String insBook = "INSERT IGNORE INTO bookings " +
                        "(reference,booked_at,flight_no,origin,destination,flight_date," +
                        "domestic,class_name,payment_method,masked_account,total_amount," +
                        "realtime_est,status,payment_status,payment_txn_ref," +
                        "payment_confirmed_at,refund_amount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                String insPax = "INSERT INTO passengers " +
                        "(booking_ref,name,address,contact,age,birthday,id_number," +
                        "senior,pwd,seat_code,addons,total_amount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

                int refNum = 1;
                java.util.Random rnd = new java.util.Random(42);

                try (PreparedStatement pb = con.prepareStatement(insBook);
                        PreparedStatement pp = con.prepareStatement(insPax)) {

                    for (Object[] fl : flightData) {
                        String fn = (String) fl[0];
                        String org = (String) fl[1];
                        String dst = (String) fl[2];
                        double base = (double) fl[3];
                        int dom = (int) fl[4];
                        int seatRow = 1;

                        for (String dateStr : dates) {
                            java.time.LocalDate flDate = java.time.LocalDate.parse(dateStr);
                            int bPerDay = 6 + rnd.nextInt(5);

                            for (int b = 0; b < bPerDay; b++) {
                                String ref = String.format("M%04d", refNum++);
                                String[] cls = classDist[b % classDist.length];
                                String className = cls[0];
                                double multiplier = Double.parseDouble(cls[1]);
                                String seatPfx = className.equals("Economy") ? "ECO"
                                        : className.equals("Premium Economy") ? "PRE"
                                                : className.equals("Business") ? "BIZ" : "FST";
                                int pax = 1 + rnd.nextInt(3);
                                double totalAmt = base * multiplier * pax;
                                java.time.LocalDate bookedDate = flDate.minusDays(2 + rnd.nextInt(12));
                                java.time.LocalDateTime bookedAt = bookedDate.atTime(9 + rnd.nextInt(10),
                                        rnd.nextInt(60));
                                java.time.LocalDateTime dep = flDate.atTime(6 + (b % 4) * 2, 0);
                                java.time.LocalDateTime arr = dep.plusMinutes(dom == 1 ? 90 : 240);
                                String estStr = dep.format(dtFmt) + " -> " + arr.format(dtFmt);
                                String txnRef = "TXN-" + String.format("%08d", rnd.nextInt(100000000));

                                pb.setString(1, ref);
                                pb.setTimestamp(2, java.sql.Timestamp.valueOf(bookedAt));
                                pb.setString(3, fn);
                                pb.setString(4, org);
                                pb.setString(5, dst);
                                pb.setDate(6, java.sql.Date.valueOf(flDate));
                                pb.setInt(7, dom);
                                pb.setString(8, className);
                                pb.setString(9, payMethods[b % payMethods.length]);
                                pb.setString(10, maskedAccts[b % maskedAccts.length]);
                                pb.setDouble(11, totalAmt);
                                pb.setString(12, estStr);
                                pb.setString(13, "CONFIRMED");
                                pb.setString(14, "PAID");
                                pb.setString(15, txnRef);
                                pb.setTimestamp(16, java.sql.Timestamp.valueOf(bookedAt.plusMinutes(5)));
                                pb.setDouble(17, 0.0);
                                pb.addBatch();

                                for (int p = 0; p < pax; p++) {
                                    String pName = names[(refNum + p) % names.length];
                                    String pAddr = addresses[p % addresses.length];
                                    String contact = "09" + String.format("%09d",
                                            100000000 + rnd.nextInt(900000000));
                                    int age = 22 + rnd.nextInt(36);
                                    String bday = (2026 - age) + "-" +
                                            String.format("%02d-%02d", 1 + rnd.nextInt(12),
                                                    1 + rnd.nextInt(28));
                                    String idNum = "PSP-" + String.format("%07d",
                                            rnd.nextInt(10000000));
                                    int cols = className.equals("Economy") ? 6
                                            : className.equals("Premium Economy") ? 4 : 3;
                                    char col = (char) ('A' + (p % cols));
                                    String seat = seatPfx + "-" + String.format("%02d", seatRow) + col;

                                    pp.setString(1, ref);
                                    pp.setString(2, pName);
                                    pp.setString(3, pAddr);
                                    pp.setString(4, contact);
                                    pp.setInt(5, age);
                                    pp.setString(6, bday);
                                    pp.setString(7, idNum);
                                    pp.setInt(8, 0);
                                    pp.setInt(9, 0);
                                    pp.setString(10, seat);
                                    pp.setString(11, "");
                                    pp.setDouble(12, totalAmt / pax);
                                    pp.addBatch();
                                }
                                seatRow++;
                            }
                        }
                    }
                    // 17 cancelled bookings
                    // columns: ref, flightNo, origin, dest, dateStr, domestic, class, multiplier,
                    // pax, payMethod, maskedAcct, refundRatio
                    Object[][] cancelledData = {
                            { "M0301", "PR-101", "Manila (MNL)", "Cebu (CEB)", "2026-05-12", 1, "Economy", 1.0, 1,
                                    "Credit Card", "**** 4521", 1.0 },
                            { "M0302", "PR-102", "Manila (MNL)", "Davao (DVO)", "2026-05-13", 1, "Economy", 1.0, 2,
                                    "E-Wallet", "**** 7832", 0.2 },
                            { "M0303", "PR-103", "Manila (MNL)", "Boracay (MPH)", "2026-05-14", 1, "Premium Economy",
                                    1.8, 1, "Debit Card", "**** 3310", 0.5 },
                            { "M0304", "PR-201", "Manila (MNL)", "Singapore (SIN)", "2026-05-12", 0, "Economy", 1.0, 3,
                                    "Credit Card", "**** 9087", 1.0 },
                            { "M0305", "PR-202", "Manila (MNL)", "Bangkok (BKK)", "2026-05-13", 0, "Business", 3.0, 1,
                                    "E-Wallet", "**** 6641", 0.5 },
                            { "M0306", "PR-101", "Manila (MNL)", "Cebu (CEB)", "2026-05-14", 1, "Economy", 1.0, 2,
                                    "Debit Card", "**** 4521", 0.2 },
                            { "M0307", "PR-102", "Manila (MNL)", "Davao (DVO)", "2026-05-15", 1, "Premium Economy", 1.8,
                                    1, "Credit Card", "**** 7832", 1.0 },
                            { "M0308", "PR-201", "Manila (MNL)", "Singapore (SIN)", "2026-05-16", 0, "Economy", 1.0, 2,
                                    "E-Wallet", "**** 3310", 0.2 },
                            { "M0309", "PR-202", "Manila (MNL)", "Bangkok (BKK)", "2026-05-17", 0, "Economy", 1.0, 1,
                                    "Credit Card", "**** 9087", 0.5 },
                            { "M0310", "PR-103", "Manila (MNL)", "Boracay (MPH)", "2026-05-15", 1, "Business", 3.0, 1,
                                    "Debit Card", "**** 6641", 1.0 },
                            { "M0311", "PR-101", "Manila (MNL)", "Cebu (CEB)", "2026-05-16", 1, "First Class", 5.0, 1,
                                    "Credit Card", "**** 4521", 0.2 },
                            { "M0312", "PR-202", "Manila (MNL)", "Bangkok (BKK)", "2026-05-14", 0, "Economy", 1.0, 3,
                                    "E-Wallet", "**** 7832", 1.0 },
                            { "M0313", "PR-102", "Manila (MNL)", "Davao (DVO)", "2026-05-12", 1, "Economy", 1.0, 2,
                                    "Debit Card", "**** 3310", 0.2 },
                            { "M0314", "PR-201", "Manila (MNL)", "Singapore (SIN)", "2026-05-17", 0, "Premium Economy",
                                    1.8, 1, "Credit Card", "**** 9087", 0.5 },
                            { "M0315", "PR-103", "Manila (MNL)", "Boracay (MPH)", "2026-05-13", 1, "Economy", 1.0, 1,
                                    "E-Wallet", "**** 6641", 0.2 },
                            { "M0316", "PR-101", "Manila (MNL)", "Cebu (CEB)", "2026-05-17", 1, "Business", 3.0, 2,
                                    "Debit Card", "**** 4521", 1.0 },
                            { "M0317", "PR-202", "Manila (MNL)", "Bangkok (BKK)", "2026-05-15", 0, "Economy", 1.0, 2,
                                    "Credit Card", "**** 7832", 0.5 },
                    };
                    int cIdx = 0;
                    for (Object[] cx : cancelledData) {
                        String cRef = (String) cx[0];
                        String cFn = (String) cx[1];
                        String cOrg = (String) cx[2];
                        String cDst = (String) cx[3];
                        String cDate = (String) cx[4];
                        int cDom = (int) cx[5];
                        String cCls = (String) cx[6];
                        double cMult = (double) cx[7];
                        int cPax = (int) cx[8];
                        String cPay = (String) cx[9];
                        String cAcct = (String) cx[10];
                        double refRatio = (double) cx[11];
                        double cBase = cFn.equals("PR-101") ? 3500.0
                                : cFn.equals("PR-102") ? 4200.0
                                        : cFn.equals("PR-103") ? 3800.0 : cFn.equals("PR-201") ? 8500.0 : 7800.0;
                        double cTotal = cBase * cMult * cPax;
                        double cRefund = cTotal * refRatio;
                        String cPaySt = refRatio > 0 ? "REFUNDED" : "PAID";
                        java.time.LocalDate cFlDate = java.time.LocalDate.parse(cDate);
                        java.time.LocalDateTime cBookedAt = cFlDate.minusDays(5).atTime(10, 30);
                        java.time.LocalDateTime cDep = cFlDate.atTime(8, 0);
                        java.time.LocalDateTime cArr = cDep.plusMinutes(cDom == 1 ? 90 : 240);
                        String cEst = cDep.format(dtFmt) + " -> " + cArr.format(dtFmt);
                        String cTxn = "TXN-CNCL" + String.format("%04d", cIdx + 1);

                        pb.setString(1, cRef);
                        pb.setTimestamp(2, java.sql.Timestamp.valueOf(cBookedAt));
                        pb.setString(3, cFn);
                        pb.setString(4, cOrg);
                        pb.setString(5, cDst);
                        pb.setDate(6, java.sql.Date.valueOf(cFlDate));
                        pb.setInt(7, cDom);
                        pb.setString(8, cCls);
                        pb.setString(9, cPay);
                        pb.setString(10, cAcct);
                        pb.setDouble(11, cTotal);
                        pb.setString(12, cEst);
                        pb.setString(13, "CANCELED");
                        pb.setString(14, cPaySt);
                        pb.setString(15, cTxn);
                        pb.setTimestamp(16, java.sql.Timestamp.valueOf(cBookedAt.plusMinutes(5)));
                        pb.setDouble(17, cRefund);
                        pb.addBatch();

                        for (int p = 0; p < cPax; p++) {
                            String pName = names[(cIdx * 3 + p) % names.length];
                            String pAddr = addresses[p % addresses.length];
                            String contact = "09" + String.format("%09d", 900000000L + cIdx * 10000L + p);
                            int age = 22 + (cIdx + p) % 36;
                            String bday = (2026 - age) + "-" + String.format("%02d-%02d", 1 + p % 12, 1 + cIdx % 28);
                            String idNum = "PSP-CX" + String.format("%05d", cIdx * 10 + p);
                            int cols = cCls.equals("Economy") ? 6 : cCls.equals("Premium Economy") ? 4 : 3;
                            char col = (char) ('A' + (p % cols));
                            String sPfx = cCls.equals("Economy") ? "ECO"
                                    : cCls.equals("Premium Economy") ? "PRE"
                                            : cCls.equals("Business") ? "BIZ" : "FST";
                            String seat = sPfx + "-" + String.format("%02d", cIdx + 1) + col;

                            pp.setString(1, cRef);
                            pp.setString(2, pName);
                            pp.setString(3, pAddr);
                            pp.setString(4, contact);
                            pp.setInt(5, age);
                            pp.setString(6, bday);
                            pp.setString(7, idNum);
                            pp.setInt(8, 0);
                            pp.setInt(9, 0);
                            pp.setString(10, seat);
                            pp.setString(11, "");
                            pp.setDouble(12, cTotal / cPax);
                            pp.addBatch();
                        }
                        cIdx++;
                    }

                    pb.executeBatch();
                    pp.executeBatch();
                }
            } catch (Exception e) {
                System.err.println("Mock completed bookings seed error: " + e.getMessage());
            }
        }

        synchronized void seedMockCancelledBookings() {
            try (Connection con = getConnection()) {
                // Guard: skip if M0301 already exists
                try (PreparedStatement chk = con.prepareStatement(
                        "SELECT COUNT(*) FROM bookings WHERE reference = 'M0301'")) {
                    try (ResultSet rs = chk.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0)
                            return;
                    }
                }

                java.time.format.DateTimeFormatter dtFmt = java.time.format.DateTimeFormatter
                        .ofPattern("MMM dd, yyyy hh:mm a", java.util.Locale.ENGLISH);
                String[] names = {
                        "Maria Santos", "Juan dela Cruz", "Ana Reyes", "Pedro Ramos",
                        "Rosa Garcia", "Carlos Mendoza", "Elena Villanueva", "Miguel Torres",
                        "Luz Fernandez", "Roberto Aquino", "Celine Bautista", "Jose Pascual",
                        "Grace Morales", "Emmanuel Cruz", "Patricia Lim", "Antonio Gomez",
                        "Marisol Castillo", "Danilo Navarro", "Teresita Ocampo", "Rafael Dizon"
                };
                String[] addresses = {
                        "123 Rizal St, Makati City",
                        "456 Mabini Ave, Quezon City",
                        "789 Luna Blvd, Cebu City",
                        "321 Bonifacio St, Davao City",
                        "654 Del Pilar St, Manila"
                };

                // columns: ref, flightNo, origin, dest, dateStr, domestic, class, multiplier,
                // pax, payMethod, maskedAcct, refundRatio
                Object[][] cancelledData = {
                        { "M0301", "PR-101", "Manila (MNL)", "Cebu (CEB)", "2026-05-12", 1, "Economy", 1.0, 1,
                                "Credit Card", "**** 4521", 1.0 },
                        { "M0302", "PR-102", "Manila (MNL)", "Davao (DVO)", "2026-05-13", 1, "Economy", 1.0, 2,
                                "E-Wallet", "**** 7832", 0.2 },
                        { "M0303", "PR-103", "Manila (MNL)", "Boracay (MPH)", "2026-05-14", 1, "Premium Economy", 1.8,
                                1, "Debit Card", "**** 3310", 0.5 },
                        { "M0304", "PR-201", "Manila (MNL)", "Singapore (SIN)", "2026-05-12", 0, "Economy", 1.0, 3,
                                "Credit Card", "**** 9087", 1.0 },
                        { "M0305", "PR-202", "Manila (MNL)", "Bangkok (BKK)", "2026-05-13", 0, "Business", 3.0, 1,
                                "E-Wallet", "**** 6641", 0.5 },
                        { "M0306", "PR-101", "Manila (MNL)", "Cebu (CEB)", "2026-05-14", 1, "Economy", 1.0, 2,
                                "Debit Card", "**** 4521", 0.2 },
                        { "M0307", "PR-102", "Manila (MNL)", "Davao (DVO)", "2026-05-15", 1, "Premium Economy", 1.8, 1,
                                "Credit Card", "**** 7832", 1.0 },
                        { "M0308", "PR-201", "Manila (MNL)", "Singapore (SIN)", "2026-05-16", 0, "Economy", 1.0, 2,
                                "E-Wallet", "**** 3310", 0.2 },
                        { "M0309", "PR-202", "Manila (MNL)", "Bangkok (BKK)", "2026-05-17", 0, "Economy", 1.0, 1,
                                "Credit Card", "**** 9087", 0.5 },
                        { "M0310", "PR-103", "Manila (MNL)", "Boracay (MPH)", "2026-05-15", 1, "Business", 3.0, 1,
                                "Debit Card", "**** 6641", 1.0 },
                        { "M0311", "PR-101", "Manila (MNL)", "Cebu (CEB)", "2026-05-16", 1, "First Class", 5.0, 1,
                                "Credit Card", "**** 4521", 0.2 },
                        { "M0312", "PR-202", "Manila (MNL)", "Bangkok (BKK)", "2026-05-14", 0, "Economy", 1.0, 3,
                                "E-Wallet", "**** 7832", 1.0 },
                        { "M0313", "PR-102", "Manila (MNL)", "Davao (DVO)", "2026-05-12", 1, "Economy", 1.0, 2,
                                "Debit Card", "**** 3310", 0.2 },
                        { "M0314", "PR-201", "Manila (MNL)", "Singapore (SIN)", "2026-05-17", 0, "Premium Economy", 1.8,
                                1, "Credit Card", "**** 9087", 0.5 },
                        { "M0315", "PR-103", "Manila (MNL)", "Boracay (MPH)", "2026-05-13", 1, "Economy", 1.0, 1,
                                "E-Wallet", "**** 6641", 0.2 },
                        { "M0316", "PR-101", "Manila (MNL)", "Cebu (CEB)", "2026-05-17", 1, "Business", 3.0, 2,
                                "Debit Card", "**** 4521", 1.0 },
                        { "M0317", "PR-202", "Manila (MNL)", "Bangkok (BKK)", "2026-05-15", 0, "Economy", 1.0, 2,
                                "Credit Card", "**** 7832", 0.5 },
                };

                String insBook = "INSERT IGNORE INTO bookings " +
                        "(reference,booked_at,flight_no,origin,destination,flight_date," +
                        "domestic,class_name,payment_method,masked_account,total_amount," +
                        "realtime_est,status,payment_status,payment_txn_ref," +
                        "payment_confirmed_at,refund_amount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                String insPax = "INSERT INTO passengers " +
                        "(booking_ref,name,address,contact,age,birthday,id_number," +
                        "senior,pwd,seat_code,addons,total_amount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

                try (PreparedStatement pb = con.prepareStatement(insBook);
                        PreparedStatement pp = con.prepareStatement(insPax)) {
                    int cIdx = 0;
                    for (Object[] cx : cancelledData) {
                        String cRef = (String) cx[0];
                        String cFn = (String) cx[1];
                        String cOrg = (String) cx[2];
                        String cDst = (String) cx[3];
                        String cDate = (String) cx[4];
                        int cDom = (int) cx[5];
                        String cCls = (String) cx[6];
                        double cMult = (double) cx[7];
                        int cPax = (int) cx[8];
                        String cPay = (String) cx[9];
                        String cAcct = (String) cx[10];
                        double refRatio = (double) cx[11];
                        double cBase = cFn.equals("PR-101") ? 3500.0
                                : cFn.equals("PR-102") ? 4200.0
                                        : cFn.equals("PR-103") ? 3800.0 : cFn.equals("PR-201") ? 8500.0 : 7800.0;
                        double cTotal = cBase * cMult * cPax;
                        double cRefund = cTotal * refRatio;
                        String cPaySt = refRatio > 0 ? "REFUNDED" : "PAID";
                        java.time.LocalDate cFlDate = java.time.LocalDate.parse(cDate);
                        java.time.LocalDateTime cBookedAt = cFlDate.minusDays(5).atTime(10, 30);
                        java.time.LocalDateTime cDep = cFlDate.atTime(8, 0);
                        java.time.LocalDateTime cArr = cDep.plusMinutes(cDom == 1 ? 90 : 240);
                        String cEst = cDep.format(dtFmt) + " -> " + cArr.format(dtFmt);
                        String cTxn = "TXN-CNCL" + String.format("%04d", cIdx + 1);

                        pb.setString(1, cRef);
                        pb.setTimestamp(2, java.sql.Timestamp.valueOf(cBookedAt));
                        pb.setString(3, cFn);
                        pb.setString(4, cOrg);
                        pb.setString(5, cDst);
                        pb.setDate(6, java.sql.Date.valueOf(cFlDate));
                        pb.setInt(7, cDom);
                        pb.setString(8, cCls);
                        pb.setString(9, cPay);
                        pb.setString(10, cAcct);
                        pb.setDouble(11, cTotal);
                        pb.setString(12, cEst);
                        pb.setString(13, "CANCELED");
                        pb.setString(14, cPaySt);
                        pb.setString(15, cTxn);
                        pb.setTimestamp(16, java.sql.Timestamp.valueOf(cBookedAt.plusMinutes(5)));
                        pb.setDouble(17, cRefund);
                        pb.addBatch();

                        for (int p = 0; p < cPax; p++) {
                            String pName = names[(cIdx * 3 + p) % names.length];
                            String pAddr = addresses[p % addresses.length];
                            String contact = "09" + String.format("%09d", 900000000L + cIdx * 10000L + p);
                            int age = 22 + (cIdx + p) % 36;
                            String bday = (2026 - age) + "-" + String.format("%02d-%02d", 1 + p % 12, 1 + cIdx % 28);
                            String idNum = "PSP-CX" + String.format("%05d", cIdx * 10 + p);
                            int cols = cCls.equals("Economy") ? 6 : cCls.equals("Premium Economy") ? 4 : 3;
                            char col = (char) ('A' + (p % cols));
                            String sPfx = cCls.equals("Economy") ? "ECO"
                                    : cCls.equals("Premium Economy") ? "PRE"
                                            : cCls.equals("Business") ? "BIZ" : "FST";
                            String seat = sPfx + "-" + String.format("%02d", cIdx + 1) + col;

                            pp.setString(1, cRef);
                            pp.setString(2, pName);
                            pp.setString(3, pAddr);
                            pp.setString(4, contact);
                            pp.setInt(5, age);
                            pp.setString(6, bday);
                            pp.setString(7, idNum);
                            pp.setInt(8, 0);
                            pp.setInt(9, 0);
                            pp.setString(10, seat);
                            pp.setString(11, "");
                            pp.setDouble(12, cTotal / cPax);
                            pp.addBatch();
                        }
                        cIdx++;
                    }
                    pb.executeBatch();
                    pp.executeBatch();
                }
            } catch (Exception e) {
                System.err.println("Mock cancelled bookings seed error: " + e.getMessage());
            }
        }
    }

    private int getAddonIndex(String addonName) {
        for (int i = 0; i < ADDON_NAMES.length; i++) {
            if (ADDON_NAMES[i].equalsIgnoreCase(addonName)) {
                return i;
            }
        }
        return -1;
    }

    private String getDiscountTypeLabel(boolean isSenior, boolean isPWD) {
        if (isSenior && isPWD) {
            return "Senior/PWD";
        }
        return isSenior ? "Senior" : "PWD";
    }

    private void updatePassengerCountLabel() {
        if (passengerCountLabel != null) {
            passengerCountLabel.setText(passengers.size() + " passenger(s)");
        }
    }

    private void refreshPassengerListEntries() {
        if (passengerListModel == null) {
            return;
        }
        for (int i = 0; i < passengers.size() && i < passengerListModel.size(); i++) {
            passengerListModel.set(i, formatPassengerListEntry(i, passengers.get(i)));
        }
    }

    private double estimateModifiedTotal(Object[] flightRow, String className,
            List<PassengerRecord> passengers) {
        double baseFare = ((Number) flightRow[3]).doubleValue();
        double classFare = baseFare * CLASS_MULTIPLIERS[getClassIndexByName(className)];
        double total = 0;
        for (PassengerRecord p : passengers) {
            double discountableAddons = 0;
            boolean hasTax = false;
            for (String addonName : p.addons) {
                int idx = getAddonIndex(addonName);
                if (idx >= 0) {
                    if (ADDON_TAX.equalsIgnoreCase(addonName)) {
                        hasTax = true;
                    } else {
                        discountableAddons += ADDON_PRICES[idx];
                    }
                }
            }
            double taxBase = classFare + discountableAddons;
            double taxAmount = (hasTax && !(p.senior || p.pwd)) ? taxBase * TAX_RATE : 0.0;
            double subtotal = taxBase + taxAmount;
            double discount = (p.senior || p.pwd) ? taxBase * DISCOUNT_RATE : 0;
            total += subtotal - discount;
        }
        return Math.round(total * 100.0) / 100.0;
    }

    private double computeRefundAmount(double totalAmount, LocalDate flightDate) {
        long days = LocalDate.now().until(flightDate, java.time.temporal.ChronoUnit.DAYS);
        if (days >= 7)
            return Math.round(totalAmount * 0.80 * 100.0) / 100.0;
        if (days >= 1)
            return Math.round(totalAmount * 0.50 * 100.0) / 100.0;
        // Even late cancellations get a 20% partial refund now
        return Math.round(totalAmount * 0.20 * 100.0) / 100.0;
    }

    private void refreshActiveFlights() {
        List<Object[]> dom = BOOKING_STORE.loadActiveFlights(true);
        List<Object[]> intlAll = BOOKING_STORE.loadActiveFlights(false);
        if (!dom.isEmpty())
            activeDomesticFlights = dom.toArray(new Object[0][]);
        // Split non-domestic flights by flight-number prefix into their regions
        List<Object[]> seAsia = new ArrayList<>(), europe = new ArrayList<>(), northAm = new ArrayList<>();
        for (Object[] r : intlAll) {
            String no = String.valueOf(r[0]);
            if (no.startsWith("PR-3"))
                europe.add(r);
            else if (no.startsWith("PR-4"))
                northAm.add(r);
            else
                seAsia.add(r);
        }
        if (!seAsia.isEmpty())
            activeInternationalFlights = seAsia.toArray(new Object[0][]);
        if (!europe.isEmpty())
            activeEuropeFlights = europe.toArray(new Object[0][]);
        if (!northAm.isEmpty())
            activeNorthAmericaFlights = northAm.toArray(new Object[0][]);
        // Rebuild capacity map from DB
        Map<String, Integer> newCap = BOOKING_STORE.loadFlightCapacities();
        if (!newCap.isEmpty())
            FLIGHT_CAPACITY = newCap;
        // Load departure times (DB overrides static defaults)
        Map<String, java.time.LocalTime> newTimes = BOOKING_STORE.loadFlightDepartureTimes();
        if (!newTimes.isEmpty())
            flightDepartureTimes = newTimes;
    }

    private void refreshFlightTables() {
        refreshActiveFlights();
        if (domesticTable != null) {
            DefaultTableModel dm = (DefaultTableModel) domesticTable.getModel();
            dm.setRowCount(0);
            for (Object[] row : buildFlightRowsWithRealtimeEst(activeDomesticFlights)) {
                dm.addRow(row);
            }
        }
        if (internationalTable != null) {
            DefaultTableModel im = (DefaultTableModel) internationalTable.getModel();
            im.setRowCount(0);
            for (Object[] row : buildFlightRowsWithRealtimeEst(activeInternationalFlights)) {
                im.addRow(row);
            }
        }
        if (europeTable != null) {
            DefaultTableModel em = (DefaultTableModel) europeTable.getModel();
            em.setRowCount(0);
            for (Object[] row : buildFlightRowsWithRealtimeEst(activeEuropeFlights)) {
                em.addRow(row);
            }
        }
        if (northAmericaTable != null) {
            DefaultTableModel nm = (DefaultTableModel) northAmericaTable.getModel();
            nm.setRowCount(0);
            for (Object[] row : buildFlightRowsWithRealtimeEst(activeNorthAmericaFlights)) {
                nm.addRow(row);
            }
        }
    }

    private String getStepHelperText(int step) {
        switch (step) {
            case STEP_HOME:
                return "Review available routes, then browse flights to start booking.";
            case STEP_FLIGHTS:
                return "Select one flight and choose a travel date before continuing.";
            case STEP_PASSENGERS:
                return "Add at least one passenger before continuing.";
            case STEP_CLASS:
                return "Choose the travel class for all passengers.";
            case STEP_ADDONS:
                return "Configure add-ons per passenger. Tax stays required.";
            case STEP_SEATS:
                return "Assign one unique seat per passenger before billing.";
            case STEP_BILLING:
                return "Review billing details, totals, and assigned seats before payment.";
            case STEP_PAYMENT:
                return "Provide payment details and acknowledge policies before confirming.";
            case STEP_RECEIPT:
                return "Booking complete. You can start a new booking anytime.";
            default:
                return "";
        }
    }

    // ================================================================
    // ADMIN LOGIN
    // ================================================================
    private static boolean showLoginDialog() {
        Properties props = new Properties();
        File propsFile = new File("db.properties");
        if (propsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propsFile)) {
                props.load(fis);
            } catch (IOException ignored) {
            }
        }
        final String adminUser = props.getProperty("admin.username", "admin");
        final String adminPass = props.getProperty("admin.password", "admin123");

        JDialog dialog = new JDialog((Frame) null, "SkyWings Admin Login", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);

        boolean[] success = { false };

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PRIMARY);

        // Header
        JPanel header = new JPanel();
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("SkyWings Airline");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Admin Access");
        sub.setFont(FONT_SMALL);
        sub.setForeground(new Color(180, 195, 220));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setBackground(BG_MAIN);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        JTextField tfUser = new JTextField();
        tfUser.setFont(FONT_BODY);
        tfUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JPasswordField tfPass = new JPasswordField();
        tfPass.setFont(FONT_BODY);
        tfPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(FONT_SMALL);
        lblUser.setForeground(TEXT_SECONDARY);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(FONT_SMALL);
        lblPass.setForeground(TEXT_SECONDARY);
        JLabel lblError = new JLabel(" ");
        lblError.setFont(FONT_SMALL);
        lblError.setForeground(DANGER_RED);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(lblUser);
        form.add(Box.createVerticalStrut(4));
        form.add(tfUser);
        form.add(Box.createVerticalStrut(12));
        form.add(lblPass);
        form.add(Box.createVerticalStrut(4));
        form.add(tfPass);
        form.add(Box.createVerticalStrut(10));
        form.add(lblError);
        form.add(Box.createVerticalStrut(10));

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(FONT_BODY);
        btnLogin.setBackground(PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        form.add(btnLogin);

        root.add(form, BorderLayout.CENTER);
        dialog.setContentPane(root);

        ActionListener loginAction = e -> {
            String user = tfUser.getText().trim();
            String pass = new String(tfPass.getPassword());
            if (adminUser.equals(user) && adminPass.equals(pass)) {
                success[0] = true;
                dialog.dispose();
            } else {
                lblError.setText("Invalid credentials. Please try again.");
                tfPass.setText("");
                tfPass.requestFocusInWindow();
            }
        };

        btnLogin.addActionListener(loginAction);
        tfPass.addActionListener(loginAction);
        tfUser.addActionListener(e -> tfPass.requestFocusInWindow());

        dialog.setVisible(true);
        return success[0];
    }

    // ================================================================
    // MAIN
    // ================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (!showLoginDialog()) {
                System.exit(0);
                return;
            }
            AirplaneTicketingSystem app = new AirplaneTicketingSystem();
            app.setVisible(true);
        });
    }
}
