import java.util.*;
import javax.swing.SwingUtilities;

public class TradingVolumeTimer {
    private StockMarketApplication app;
    private Timer timer;
    private int userVolumeChange = 0;
    
    public TradingVolumeTimer(StockMarketApplication app) {
        this.app = app;
    }
    
    public void executeTrade(int volumeChange) {
        this.userVolumeChange = volumeChange;
        
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer("TradingTimer");
        
        System.out.println("=== Trading session start ===");
        System.out.println("User's trade: " + 
                          (volumeChange > 0 ? "+" : "") + volumeChange);
        
        stopMarket();
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                processVolumeChanges();
            }
        }, 2000);
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                calculatePriceImpact();
            }
        }, 5000);
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                resumeMarketAndStartCooldown();
            }
        }, 10000);
    }
    
    private void stopMarket() {
        System.out.println("\nMarket halt to process the trade.");
        
        PriceFluctuationTimer priceTimer = app.getPriceTimer();
        if (priceTimer != null && priceTimer.isRunning()) {
            priceTimer.stop();
            System.out.println("  Price fluctuations halted");
        }
        
        SwingUtilities.invokeLater(() -> {
            app.updatePriceTimerStatus("PAUSED (Trading)");
        });
    }
    
    private void processVolumeChanges() {
        System.out.println("\nPrcoessing new trade volume");
        
        Map<String, StockData> stocks = app.getStocks();
        

        int changePerStock = userVolumeChange / stocks.size();
        
        for (String symbol : stocks.keySet()) {
            StockData stock = stocks.get(symbol);
            int oldVolume = stock.getVolume();
            

            Random random = new Random();
            double randomFactor = 0.8 + random.nextDouble() * 0.4; 
            int actualChange = (int)(changePerStock * randomFactor);
            
            int newVolume = oldVolume + actualChange;
            newVolume = Math.max(10000, Math.min(500000, newVolume)); 
            
            stock.setVolume(newVolume);
            
            System.out.printf("  %s: %,d → %,d (change: %+,d)%n", 
                            symbol, oldVolume, newVolume, (newVolume - oldVolume));
        }
        
        app.updateStockDisplay();
    }
    
    private void calculatePriceImpact() {
        System.out.println("\nCalculating impact");
        
        Map<String, StockData> stocks = app.getStocks();
        Random random = new Random();
        
        for (String symbol : stocks.keySet()) {
            StockData stock = stocks.get(symbol);
            double currentPrice = stock.getCurrentPrice();
            
            double volumeImpact = userVolumeChange / 100000.0;
            double priceChangePercent = volumeImpact * (0.5 + random.nextDouble() * 0.5);
            
            priceChangePercent = Math.max(-0.05, Math.min(0.05, priceChangePercent));
            
            double newPrice = currentPrice * (1 + priceChangePercent);
            newPrice = Math.round(newPrice * 100.0) / 100.0;
            
            stock.setCurrentPrice(newPrice);
            
            System.out.printf("  %s: $%.2f → $%.2f (%.2f%%)%n", 
                            symbol, currentPrice, newPrice, priceChangePercent * 100);
        }
        
        app.updateStockDisplay();
    }
    
    private void resumeMarketAndStartCooldown() {
        System.out.println("\nResuming market");
        
        PriceFluctuationTimer priceTimer = app.getPriceTimer();
        if (priceTimer != null && !priceTimer.isRunning()) {
            priceTimer.start();
            System.out.println("  Price fluctuation resumed");
        }
        
        SwingUtilities.invokeLater(() -> {
            app.startCooldown();
            System.out.println("  20-second trading cooldown");
        });
        
        System.out.println("\n=== Trade session ended ===\n");
    
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            System.out.println("TradingVolumeTimer is stopped");
        }
    }
}
