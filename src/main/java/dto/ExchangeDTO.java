package dto;

import java.math.BigDecimal;

public class ExchangeDTO extends ExchangeRateDTO {

    private final BigDecimal amount;
    private final BigDecimal convertedAmount;

    public ExchangeDTO(int id, CurrencyDTO baseCurrencyDto,
                       CurrencyDTO targetCurrencyDto, BigDecimal rate,
                       BigDecimal amount, BigDecimal convertedAmount) {
        super(id, baseCurrencyDto, targetCurrencyDto, rate);
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
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
