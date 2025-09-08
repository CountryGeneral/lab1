import javax.swing.*;
import java.awt.*;
import java.util.*;

public class StockMarketApplication extends JFrame {
    private Map<String, StockData> stocks;
    private JLabel statusLabel;
    private JPanel stockDisplayPanel;
    private JPanel controlPanel;
    private JButton startPricesButton, stopPricesButton;
    private JButton startEventsButton, stopEventsButton;
    private JButton tradeButton; 
    private JLabel nextEventLabel;
    private JLabel priceTimerLabel;
    private PriceFluctuationTimer priceFluctuationTimer;
    private MarketEventTimer marketEventTimer;
    private TradingVolumeTimer tradingVolumeTimer;
    private javax.swing.Timer guiUpdateTimer;
    private javax.swing.Timer cooldownTimer;
    private int cooldownSeconds = 0;
    
    private volatile boolean pendingStockUpdate = false;
    private Map<String, JLabel> priceLabels;
    private Map<String, JLabel> volumeLabels;
    
    public StockMarketApplication() {
        initializeData();
        setupGUI();
        initializeTimers();
        setupButtonActions();
    }
    
    private void initializeData() {
        stocks = new HashMap<>();
        priceLabels = new HashMap<>();
        volumeLabels = new HashMap<>();
        
        stocks.put("AAPL", new StockData("Apple", 150.0));
        stocks.put("GOOGL", new StockData("Google", 2800.0));
        stocks.put("MSFT", new StockData("Microsoft", 300.0));
    }
    
    private void setupGUI() {
        setTitle("Stock Market Trading Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        statusLabel = new JLabel("Market Status: READY", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        stockDisplayPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        stockDisplayPanel.setBorder(BorderFactory.createTitledBorder("Stock Data"));
        
        stockDisplayPanel.add(new JLabel("Symbol", JLabel.CENTER));
        stockDisplayPanel.add(new JLabel("Price", JLabel.CENTER));
        stockDisplayPanel.add(new JLabel("Volume", JLabel.CENTER));
        
        for (String symbol : stocks.keySet()) {
            StockData stock = stocks.get(symbol);
            stockDisplayPanel.add(new JLabel(symbol, JLabel.CENTER));
            
            JLabel priceLabel = new JLabel("$" + String.format("%.2f", stock.getCurrentPrice()), JLabel.CENTER);
            JLabel volumeLabel = new JLabel(String.format("%,d", stock.getVolume()), JLabel.CENTER);
            
            priceLabels.put(symbol, priceLabel);
            volumeLabels.put(symbol, volumeLabel);
            
            stockDisplayPanel.add(priceLabel);
            stockDisplayPanel.add(volumeLabel);
        }
        
        controlPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Market Controls"));
        
        startPricesButton = new JButton("Start Price Updates");
        stopPricesButton = new JButton("Stop Price Updates");
        startEventsButton = new JButton("Start Market Events");
        stopEventsButton = new JButton("Stop Market Events");
        tradeButton = new JButton("Trade");
        tradeButton.setFont(new Font("Arial", Font.BOLD, 12));
        tradeButton.setBackground(new Color(50, 150, 50));
        tradeButton.setForeground(Color.WHITE);
        
        priceTimerLabel = new JLabel("Price Timer: STOPPED");
        nextEventLabel = new JLabel("Next Event: Not scheduled");
        
        controlPanel.add(startPricesButton);
        controlPanel.add(stopPricesButton);
        controlPanel.add(startEventsButton);
        controlPanel.add(stopEventsButton);
        controlPanel.add(tradeButton);
        controlPanel.add(new JLabel());
        controlPanel.add(priceTimerLabel);
        controlPanel.add(nextEventLabel);
        
        add(statusLabel, BorderLayout.NORTH);
        add(stockDisplayPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        setSize(660, 350);
        setLocationRelativeTo(null);
    }
    
    private void initializeTimers() {
        priceFluctuationTimer = new PriceFluctuationTimer(this);
        marketEventTimer = new MarketEventTimer(this);
        tradingVolumeTimer = new TradingVolumeTimer(this);
        
        guiUpdateTimer = new javax.swing.Timer(1000, e -> updateTimerDisplays());
        guiUpdateTimer.start();
        
        cooldownTimer = new javax.swing.Timer(1000, e -> updateCooldown());
    }
    
    private void updateTimerDisplays() {
        boolean anyRunning = (priceFluctuationTimer != null && priceFluctuationTimer.isRunning()) ||
                            (marketEventTimer != null && marketEventTimer.isRunning());
        
        if (!anyRunning) return;
        
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
    
    private void updateCooldown() {
        cooldownSeconds--;
        if (cooldownSeconds > 0) {
            tradeButton.setText("Cooldown: " + cooldownSeconds + "s");
            tradeButton.setBackground(new Color(150, 50, 50));
        } else {
            cooldownTimer.stop();
            tradeButton.setText("Trade");
            tradeButton.setEnabled(true);
            tradeButton.setBackground(new Color(50, 150, 50));
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
        
        tradeButton.addActionListener(e -> {
            if (!tradeButton.isEnabled() || cooldownSeconds > 0) {
                return;
            }
            
            String input = JOptionPane.showInputDialog(
                this,
                "Enter trade volume:\n" +
                "+ to buy (e.g.: +5000)\n" +
                "- to sell (e.g.: -3000)",
                "Trade",
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (input == null || input.trim().isEmpty()) {
                return;
            }
            
            try {
                int volumeChange = Integer.parseInt(input.trim());
                

                tradeButton.setEnabled(false);
                tradeButton.setText("Processing...");
                tradeButton.setBackground(new Color(150, 150, 50));
                
                if (tradingVolumeTimer != null) {
                    tradingVolumeTimer.executeTrade(volumeChange);
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Wrong number format. Use integers with a + or -",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
    
    public void startCooldown() {
        cooldownSeconds = 20;
        cooldownTimer.start();
    }
    
    public void updateTradeButtonStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            tradeButton.setText(status);
            if (status.equals("Processing...")) {
                tradeButton.setBackground(new Color(150, 150, 50));
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
        if (!pendingStockUpdate) {
            pendingStockUpdate = true;
            SwingUtilities.invokeLater(() -> {
                performStockDisplayUpdate();
                pendingStockUpdate = false;
            });
        }
    }
    
    private void performStockDisplayUpdate() {
        for (String symbol : stocks.keySet()) {
            StockData stock = stocks.get(symbol);
            
            JLabel priceLabel = priceLabels.get(symbol);
            JLabel volumeLabel = volumeLabels.get(symbol);
            
            if (priceLabel != null) {
                priceLabel.setText("$" + String.format("%.2f", stock.getCurrentPrice()));
            }
            if (volumeLabel != null) {
                volumeLabel.setText(String.format("%,d", stock.getVolume()));
            }
        }
    }
    
    public Map<String, StockData> getStocks() {
        return stocks;
    }
    
    public PriceFluctuationTimer getPriceTimer() {
        return priceFluctuationTimer;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StockMarketApplication app = new StockMarketApplication();
            app.setVisible(true);
        });
    }
}