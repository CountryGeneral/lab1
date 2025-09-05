public class StockData {
    private String name;
    private double currentPrice;
    
    public StockData(String name, double currentPrice) {
        this.name = name;
        this.currentPrice = currentPrice;
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
}