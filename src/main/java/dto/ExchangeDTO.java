package dto;

public class ExchangeDTO extends ExchangeRateDTO {

    private final double amount;
    private final double convertedAmount;

    public ExchangeDTO(int id, CurrencyDTO baseCurrencyDto,
                       CurrencyDTO targetCurrencyDto, double rate,
                       double amount, double convertedAmount) {
        super(id, baseCurrencyDto, targetCurrencyDto, rate);
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    @Override
    public String toString() {
        return "ExchangeDTO{"
                + "id=" + getId()
                + ", baseCurrencyDto=" + getBaseCurrency()
                + ", targetCurrencyDto=" + getTargetCurrency()
                + ", rate=" + getRate()
                + ", amount=" + amount
                + ", convertedAmount=" + convertedAmount
                + '}';
    }
}
