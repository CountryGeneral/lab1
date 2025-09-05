import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

public class StockMarketApplication extends JFrame {
    private Map<String, StockData> stocks;
    private JLabel statusLabel;
    private JPanel stockDisplayPanel;
    private JPanel controlPanel;
    private JButton startPricesButton, stopPricesButton;
    private JButton startEventsButton, stopEventsButton;
    private JLabel nextEventLabel;
    private JLabel priceTimerLabel;
    private PriceFluctuationTimer priceFluctuationTimer;
    private MarketEventTimer marketEventTimer;
    private javax.swing.Timer guiUpdateTimer;
    
    public StockMarketApplication() {
        initializeData();
        setupGUI();
        initializeTimers();
        setupButtonActions();
    }
    
    private void initializeData() {
        stocks = new HashMap<>();
        
        stocks.put("AAPL", new StockData("Apple", 150.0));
        stocks.put("GOOGL", new StockData("Google", 2800.0));
        stocks.put("MSFT", new StockData("Microsoft", 300.0));
    }
    
    private void setupGUI() {
        setTitle("Stock Market Simulation - Lab 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        statusLabel = new JLabel("Market Status: STARTING...", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        stockDisplayPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        stockDisplayPanel.setBorder(BorderFactory.createTitledBorder("Stock Prices"));
        
        stockDisplayPanel.add(new JLabel("Symbol", JLabel.CENTER));
        stockDisplayPanel.add(new JLabel("Price", JLabel.CENTER));
        
        for (String symbol : stocks.keySet()) {
            stockDisplayPanel.add(new JLabel(symbol, JLabel.CENTER));
            stockDisplayPanel.add(new JLabel("$" + stocks.get(symbol).getCurrentPrice(), JLabel.CENTER));
        }
        
        controlPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Timer Controls"));
        
        startPricesButton = new JButton("Start Price Updates");
        stopPricesButton = new JButton("Stop Price Updates");
        startEventsButton = new JButton("Start Market Events");
        stopEventsButton = new JButton("Stop Market Events");
        
        priceTimerLabel = new JLabel("Price Timer: STOPPED");
        nextEventLabel = new JLabel("Next Event: Not scheduled");
        
        controlPanel.add(startPricesButton);
        controlPanel.add(stopPricesButton);
        controlPanel.add(startEventsButton);
        controlPanel.add(stopEventsButton);
        controlPanel.add(priceTimerLabel);
        controlPanel.add(nextEventLabel);
        
        add(statusLabel, BorderLayout.NORTH);
        add(stockDisplayPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        setSize(660, 300);
        setLocationRelativeTo(null);
    }
    
    private void initializeTimers() {
        priceFluctuationTimer = new PriceFluctuationTimer(this);
        marketEventTimer = new MarketEventTimer(this);
        
        guiUpdateTimer = new javax.swing.Timer(1000, e -> updateTimerDisplays());
        guiUpdateTimer.start();
    }
    
    private void updateTimerDisplays() {
        if (priceFluctuationTimer != null && priceFluctuationTimer.isRunning()) {
            int secondsRunning = priceFluctuationTimer.getSecondsRunning();
            int nextUpdate = 2 - (secondsRunning % 2);
            priceTimerLabel.setText("Price Timer: RUNNING (next update in " + nextUpdate + "s)");
        }
        
        if (marketEventTimer != null && marketEventTimer.isRunning()) {
            int secondsToNext = marketEventTimer.getSecondsToNextEvent();
            if (secondsToNext > 0) {
                nextEventLabel.setText("Next Event: " + secondsToNext + " seconds");
            }
        }
    }
    
    private void setupButtonActions() {
        startPricesButton.addActionListener(e -> {
            if (priceFluctuationTimer != null) {
                priceFluctuationTimer.start();
            }
        });
        
        stopPricesButton.addActionListener(e -> {
            if (priceFluctuationTimer != null) {
                priceFluctuationTimer.stop();
            }
        });
        
        startEventsButton.addActionListener(e -> {
            if (marketEventTimer != null) {
                marketEventTimer.start();
            }
        });
        
        stopEventsButton.addActionListener(e -> {
            if (marketEventTimer != null) {
                marketEventTimer.stop();
            }
        });
    }
    
    public void updatePriceTimerStatus(String status) {
        priceTimerLabel.setText("Price Timer: " + status);
    }
    
    public void updateNextEventStatus(String eventInfo) {
        nextEventLabel.setText("Next Event: " + eventInfo);
    }
    
    public void updateStockDisplay() {
        Component[] components = stockDisplayPanel.getComponents();
        int componentIndex = 2; 
        
        for (String symbol : stocks.keySet()) {
            if (componentIndex < components.length && components[componentIndex] instanceof JLabel) {
                JLabel priceLabel = (JLabel) components[componentIndex + 1];
                priceLabel.setText("$" + String.format("%.2f", stocks.get(symbol).getCurrentPrice()));
            }
            componentIndex += 2;
        }
        
        stockDisplayPanel.repaint();
    }
    
    public Map<String, StockData> getStocks() {
        return stocks;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StockMarketApplication app = new StockMarketApplication();
            app.setVisible(true);
        });
    }
}