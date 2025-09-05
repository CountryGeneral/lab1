public class StockData {
    private String name;
    private double currentPrice;
    private int volume;
    
    public StockData(String name, double currentPrice) {
        this.name = name;
        this.currentPrice = currentPrice;
        this.volume = (int)(Math.random() * 100000) + 50000; // Random initial volume
    }
    
    public String getName() {
        return name;
    }
    
    public double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(double price) {
        this.currentPrice = price;
    }
    
    public int getVolume() {
        return volume;
    }
    
    public void setVolume(int volume) {
        this.volume = volume;
    }
}