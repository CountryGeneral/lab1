import java.util.*;

public class TradingVolumeTimer extends TimerTask {
    private StockMarketApplication app;
    private Timer timer;
    private boolean running = false;
    private Map<String, Integer> stockVolumes;
    private long nextUpdateTime = 0;
    
    public TradingVolumeTimer(StockMarketApplication app) {
        this.app = app;
        this.timer = new Timer("TradingVolumeTimer");
        this.stockVolumes = new HashMap<>();
        
        stockVolumes.put("AAPL", 100000);
        stockVolumes.put("GOOGL", 75000);
        stockVolumes.put("MSFT", 120000);
    }
    
    public void start() {
        if (running) {
            System.out.println("TradingVolumeTimer already running - stop it first");
            return;
        }
        if (timer != null) {
            timer.cancel();
            timer = new Timer("TradingVolumeTimer");
        }
        running = true;
        nextUpdateTime = System.currentTimeMillis() + 5000;
        timer.schedule(this, 5000, 15000);
        System.out.println("TradingVolumeTimer started - updates every 15 seconds");
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
            double currentPrice = stock.getCurrentPrice();
            
            double changePercent = (random.nextDouble() - 0.3) * 0.5;
            int newVolume = (int)(currentVolume * (1 + changePercent));
            newVolume = Math.max(10000, Math.min(500000, newVolume));
            stock.setVolume(newVolume);
            
            // high volume = more price movement
            double volumeImpact = (newVolume - currentVolume) / 100000.0;
            double priceChange = volumeImpact * random.nextGaussian() * 0.5;
            double newPrice = currentPrice + priceChange;
            newPrice = Math.round(newPrice * 100.0) / 100.0;
            stock.setCurrentPrice(newPrice);
            
            System.out.printf("Volume %s: %,d shares (%.1f%% change)%n", 
                            symbol, newVolume, changePercent * 100);
            System.out.printf("Price %s: $%.2f (volume impact: %+.2f)%n", 
                            symbol, newPrice, priceChange);
        }
        
        app.updateStockDisplay();
        
        nextUpdateTime = System.currentTimeMillis() + 15000;
        
        System.out.println("=====================");
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            running = false;
            nextUpdateTime = 0;
            System.out.println("TradingVolumeTimer stopped");
        }
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public int getSecondsToNextUpdate() {
        if (!running || nextUpdateTime == 0) return 0;
        long secondsRemaining = (nextUpdateTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, (int)secondsRemaining);
    }
    
    public Map<String, Integer> getVolumes() {
        return new HashMap<>(stockVolumes);
    }
}