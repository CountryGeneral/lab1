import java.util.*;
import javax.swing.SwingUtilities;

public class PriceFluctuationTimer extends TimerTask {
    private StockMarketApplication app;
    private Timer timer;
    private boolean running = false;
    private long startTime;
    private PriceFluctuationTimer currentTask;
    public PriceFluctuationTimer(StockMarketApplication app) {
        this.app = app;
    }
    
    public void start() {
        if (running) {
            System.out.println("PriceFluctuationTimer already running - stop it first");
            return;
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer("PriceTimer");
        currentTask = new PriceFluctuationTimer(app);
        currentTask.app = this.app;
        currentTask.timer = timer;
        currentTask.running = true;
        currentTask.startTime = System.currentTimeMillis();
        this.running = true;
        this.startTime = currentTask.startTime;
        timer.scheduleAtFixedRate(currentTask, 0, 2000);
        SwingUtilities.invokeLater(() -> app.updatePriceTimerStatus("RUNNING (every 2s)"));
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
            
            double changePercent = (random.nextGaussian() * 0.03);
            double newPrice = currentPrice * (1 + changePercent);
            newPrice = Math.round(newPrice * 100.0) / 100.0;
            
            stock.setCurrentPrice(newPrice);
        
            System.out.printf("Updated %s: $%.2f (%.2f%%)%n", 
                            symbol, newPrice, changePercent * 100);
        }
        
        app.updateStockDisplay();
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();

            timer = null;
        }
        if (currentTask != null) {
            currentTask.running = false;
        }
        running = false;
        SwingUtilities.invokeLater(() -> app.updatePriceTimerStatus("STOPPED"));
        System.out.println("PriceFluctuationTimer stopped");
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public int getSecondsRunning() {
        if (!running) return 0;
        return (int)((System.currentTimeMillis() - startTime) / 1000);
    }
}