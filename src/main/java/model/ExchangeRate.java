package model;

public class ExchangeRate {

    private final int id;
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private double rate;

    public ExchangeRate(int id, Currency baseCurrency,
                        Currency targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "ExchangeRateEntity{" +
                "id=" + id +
                ", baseCurrencyId=" + baseCurrency +
                ", targetCurrencyId=" + targetCurrency +
                ", rate=" + rate +
                '}';
    }
}
