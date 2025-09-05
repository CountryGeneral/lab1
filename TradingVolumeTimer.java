import java.util.*;
import javax.swing.SwingUtilities;

public class TradingVolumeTimer extends TimerTask {
    private StockMarketApplication app;
    private Timer timer;
    private boolean running = false;
    private Map<String, Integer> stockVolumes;
    
    public TradingVolumeTimer(StockMarketApplication app) {
        this.app = app;
        this.timer = new Timer("TradingVolumeTimer");
        this.stockVolumes = new HashMap<>();
        
        // Initialize volumes
        stockVolumes.put("AAPL", 100000);
        stockVolumes.put("GOOGL", 75000);
        stockVolumes.put("MSFT", 120000);
    }
    
    public void start() {
        // Uses schedule with period - runs every 15 seconds with period
        timer.schedule(this, 5000, 15000); // Start after 5s, repeat every 15s
        running = true;
        System.out.println("TradingVolumeTimer started - updates every 15 seconds (periodic)");
    }
    
    @Override
    public void run() {
        updateTradingVolumes();
    }
    
    private void updateTradingVolumes() {
        Random random = new Random();
        Map<String, StockData> stocks = app.getStocks();
        
        System.out.println("=== VOLUME UPDATE ===");
        
        for (String symbol : stocks.keySet()) {
            StockData stock = stocks.get(symbol);
            int currentVolume = stock.getVolume();
            
            // Volume can change by -20% to +30%
            double changePercent = (random.nextDouble() - 0.3) * 0.5;
            int newVolume = (int)(currentVolume * (1 + changePercent));
            
            // Keep volume reasonable
            newVolume = Math.max(10000, Math.min(500000, newVolume));
            
            stock.setVolume(newVolume);
            
            System.out.printf("Volume %s: %,d shares (%.1f%% change)%n", 
                            symbol, newVolume, changePercent * 100);
        }
        
        // Update GUI
        SwingUtilities.invokeLater(() -> app.updateStockDisplay());
        
        System.out.println("=====================");
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            running = false;
            System.out.println("TradingVolumeTimer stopped");
        }
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public Map<String, Integer> getVolumes() {
        return new HashMap<>(stockVolumes);
    }
}