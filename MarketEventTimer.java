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
    
    public MarketEventTimer(StockMarketApplication app) {
        this.app = app;
        this.timer = new Timer("MarketEventTimer");
    }
    
    public void start() {
        running = true;
        nextEventTime = System.currentTimeMillis() + 20000; // First event in 20 seconds
        scheduleSpecificTimeEvents();
        SwingUtilities.invokeLater(() -> app.updateNextEventStatus("First event in 20 seconds"));
        System.out.println("MarketEventTimer started - events scheduled at specific times");
    }
    
    private void scheduleSpecificTimeEvents() {
        Calendar now = Calendar.getInstance();
        Random random = new Random();
        
        // Schedule events at specific times (every 15-30 seconds)
        for (int i = 1; i <= 5; i++) {
            Calendar eventTime = (Calendar) now.clone();
            eventTime.add(Calendar.SECOND, i * 20 + random.nextInt(10));
            
            timer.schedule(new EventTask(i), eventTime.getTime());
        }
    }
    
    @Override
    public void run() {
        // This will be used by EventTask inner class
    }
    
    private class EventTask extends TimerTask {
        private int eventNumber;
        
        public EventTask(int eventNumber) {
            this.eventNumber = eventNumber;
        }
        
        @Override
        public void run() {
            triggerMarketEvent();
            
            // Update next event time
            if (eventNumber < 5) {
                nextEventTime = System.currentTimeMillis() + (20000 + (eventNumber * 2000)); // Next event timing
            } else {
                nextEventTime = 0; // No more events
                SwingUtilities.invokeLater(() -> app.updateNextEventStatus("All events completed"));
            }
        }
    }
    
    private void triggerMarketEvent() {
        Random random = new Random();
        String event = events[random.nextInt(events.length)];
        
        System.out.println("=== MARKET EVENT ===");
        System.out.println("Event: " + event);
        System.out.println("===================");
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            running = false;
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