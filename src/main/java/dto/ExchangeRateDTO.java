package dto;

import model.Currency;

public class ExchangeRateDTO {
    private int id;
    private CurrencyDTO baseCurrencyDto;
    private CurrencyDTO targetCurrencyDto;
    private double rate;

    public ExchangeRateDTO(int id, CurrencyDTO baseCurrencyDto,
                           CurrencyDTO targetCurrencyDto, double rate) {
        this.id = id;
        this.baseCurrencyDto = baseCurrencyDto;
        this.targetCurrencyDto = targetCurrencyDto;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public CurrencyDTO getBaseCurrency() {
        return baseCurrencyDto;
    }

    public CurrencyDTO getTargetCurrency() {
        return targetCurrencyDto;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", baseCurrencyDto=" + baseCurrencyDto +
                ", targetCurrencyDto=" + targetCurrencyDto +
                ", rate=" + rate +
                '}';
    }
}
