import java.util.*;
import java.util.Calendar;
import javax.swing.SwingUtilities;

public class MarketEventTimer extends TimerTask {
    private StockMarketApplication app;
    private Timer timer;
    private boolean running = false;
    private long nextEventTime = 0;
    private String[] events = {
        "Federal Reserve announcement",
        "Major earnings report", 
        "Geopolitical tensions",
        "Oil price surge",
        "Tech sector news"
    };
    
    private String[] eventDescriptions = {
        "Interest rate changes affect company borrowing costs and investor sentiment",
        "Quarterly results directly impact individual stock valuations and market confidence",
        "Political instability creates market uncertainty, leading to increased volatility",
        "Energy cost fluctuations affect production expenses across all sectors",
        "Technology developments influence innovation expectations and growth projections"
    };
    
    public MarketEventTimer(StockMarketApplication app) {
        this.app = app;
        this.timer = new Timer("MarketEventTimer");
    }
    
    public void start() {
        if (running) {
            System.out.println("MarketEventTimer already running - stop it first");
            return;
        }
        if (timer != null) {
            timer.cancel();
            timer = new Timer("MarketEventTimer");
        }
        running = true;
        nextEventTime = System.currentTimeMillis() + 20000;
        scheduleSpecificTimeEvents();
        SwingUtilities.invokeLater(() -> app.updateNextEventStatus("First event in 20 seconds"));
        System.out.println("MarketEventTimer started - events scheduled at specific times");
    }
    
    private void scheduleSpecificTimeEvents() {
        Calendar now = Calendar.getInstance();
        
        for (int i = 1; i <= 5; i++) {
            Calendar eventTime = (Calendar) now.clone();
            eventTime.add(Calendar.SECOND, i * 20);
            
            timer.schedule(new EventTask(i), eventTime.getTime());
            
            System.out.println("Event " + i + " scheduled for " + eventTime.getTime());
        }
    }
    
    @Override
    public void run() {
    }
    
    private class EventTask extends TimerTask {
        private int eventNumber;
        
        public EventTask(int eventNumber) {
            this.eventNumber = eventNumber;
        }
        
        @Override
        public void run() {
            System.out.println("Event " + eventNumber + " fired!");
            triggerMarketEvent();
            
            if (eventNumber < 5) {
                nextEventTime = System.currentTimeMillis() + (20000);
            } else {
                nextEventTime = 0;
                running = false;
                SwingUtilities.invokeLater(() -> app.updateNextEventStatus("All events completed"));
            }
        }
    }
    
    private void triggerMarketEvent() {
        Random random = new Random();
        int eventIndex = random.nextInt(events.length);
        String event = events[eventIndex];
        
        // Impact trading volumes for all stocks
        Map<String, StockData> stocks = app.getStocks();
        boolean positiveEvent = random.nextBoolean();
        int volumeChange = positiveEvent ? 20000 : -15000;
        
        System.out.println("=== MARKET EVENT ===");
        System.out.println("Event: " + event);
        System.out.println("Description: " + eventDescriptions[eventIndex]);
        System.out.println("Volume Impact: " + (positiveEvent ? "+" : "") + volumeChange + " shares");
        System.out.println("Market Response: " + (positiveEvent ? "Investors increase trading activity" : "Investors reduce trading activity"));
        
        for (String symbol : stocks.keySet()) {
            StockData stock = stocks.get(symbol);
            int oldVolume = stock.getVolume();
            int newVolume = Math.max(10000, oldVolume + volumeChange);
            newVolume = Math.min(500000, newVolume);
            stock.setVolume(newVolume);
            System.out.println("  " + symbol + " volume: " + String.format("%,d", oldVolume) + " → " + String.format("%,d", newVolume));
        }
        
        app.updateStockDisplay();
        System.out.println("===================");
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            running = false;
            nextEventTime = 0;
            SwingUtilities.invokeLater(() -> app.updateNextEventStatus("Events stopped"));
        }
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public int getSecondsToNextEvent() {
        if (!running || nextEventTime == 0) return 0;
        long secondsRemaining = (nextEventTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, (int)secondsRemaining);
    }
}