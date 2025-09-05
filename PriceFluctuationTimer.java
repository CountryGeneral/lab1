import java.util.*;

public class PriceFluctuationTimer extends TimerTask {
    private StockMarketApplication app;
    private Timer timer;
    
    public PriceFluctuationTimer(StockMarketApplication app) {
        this.app = app;
        this.timer = new Timer("PriceTimer");
    }
    
    public void start() {
        timer.scheduleAtFixedRate(this, 0, 2000);
        System.out.println("PriceFluctuationTimer started - updates every 2 seconds");
    }
    
    @Override
    public void run() {
        updateStockPrices();
    }
    
    private void updateStockPrices() {
        Map<String, StockData> stocks = app.getStocks();
        Random random = new Random();
        
        for (String symbol : stocks.keySet()) {
            StockData stock = stocks.get(symbol);
            double currentPrice = stock.getCurrentPrice();
            
            // Generate price change: -3% to +3%
            double changePercent = (random.nextGaussian() * 0.03);
            double newPrice = currentPrice * (1 + changePercent);
            
            // Round to 2 decimal places
            newPrice = Math.round(newPrice * 100.0) / 100.0;
            
            stock.setCurrentPrice(newPrice);
            
            System.out.printf("Updated %s: $%.2f (%.2f%%)%n", 
                            symbol, newPrice, changePercent * 100);
        }
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}