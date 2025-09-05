import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

public class StockMarketApplication extends JFrame {
    private Map<String, StockData> stocks;
    private JLabel statusLabel;
    private JPanel stockDisplayPanel;
    
    public StockMarketApplication() {
        initializeData();
        setupGUI();
        
        System.out.println("Stock Market Application initialized");
        System.out.println("Ready for timer implementation...");
    }
    
    private void initializeData() {
        stocks = new HashMap<>();
        
        // Initialize 3 sample stocks
        stocks.put("AAPL", new StockData("Apple", 150.0));
        stocks.put("GOOGL", new StockData("Google", 2800.0));
        stocks.put("MSFT", new StockData("Microsoft", 300.0));
    }
    
    private void setupGUI() {
        setTitle("Stock Market Simulation - Lab 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Status panel
        statusLabel = new JLabel("Market Status: STARTING...", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Stock display panel
        stockDisplayPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        stockDisplayPanel.setBorder(BorderFactory.createTitledBorder("Stock Prices"));
        
        // Headers
        stockDisplayPanel.add(new JLabel("Symbol", JLabel.CENTER));
        stockDisplayPanel.add(new JLabel("Price", JLabel.CENTER));
        
        // Stock rows
        for (String symbol : stocks.keySet()) {
            stockDisplayPanel.add(new JLabel(symbol, JLabel.CENTER));
            stockDisplayPanel.add(new JLabel("$" + stocks.get(symbol).getCurrentPrice(), JLabel.CENTER));
        }
        
        add(statusLabel, BorderLayout.NORTH);
        add(stockDisplayPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    // Getter methods for timers to access
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