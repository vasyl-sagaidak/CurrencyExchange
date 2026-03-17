package service;

import dao.ExchangeRateDAO;
import dto.ExchangeDTO;
import exception.NegativeExchangeAmountException;
import mapper.ExchangeMapper;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExchangeService {
    private static final int ONE = 1;
    ExchangeRateDAO exchangeRateDAO;

    public ExchangeService(ExchangeRateDAO exchangeRateDAO) {
        this.exchangeRateDAO = exchangeRateDAO;
    }

    public ExchangeDTO exchange(String baseCode, String targetCode, BigDecimal sum) {
        if (sum == null || sum.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeExchangeAmountException(
                    "Сумма для обмена должна быть представлена положительным числом.");
        }

        ExchangeRate direct = getMatch(baseCode, targetCode);
        if (direct != null) {
            BigDecimal convertedAmount = sum.multiply(direct.getRate());
            return ExchangeMapper.toDto(direct, sum, convertedAmount);

        }

        ExchangeRate reverse = getMatch(targetCode, baseCode);
        if (reverse != null) {
            BigDecimal rate = BigDecimal.ONE.divide(reverse.getRate(), 4, RoundingMode.HALF_UP);
            ExchangeRate virtual =
                    new ExchangeRate(0, reverse.getBaseCurrency(), reverse.getTargetCurrency(), rate);
            return ExchangeMapper.toDto(virtual, sum, sum.multiply(rate));

        } else {

            ExchangeRate cross = throughUsdMatch(baseCode, targetCode);
            return ExchangeMapper.toDto(cross, sum, sum.multiply(cross.getRate()));
        }

    }

    private ExchangeRate getMatch(String baseCode, String targetCode) {

        return exchangeRateDAO.getAll().stream()
                .filter(r -> r.getBaseCurrency().getCode().equalsIgnoreCase(baseCode))
                .filter(r -> r.getTargetCurrency().getCode().equalsIgnoreCase(targetCode))
                .findFirst().orElse(null);
    }

    private ExchangeRate throughUsdMatch(String baseCode, String targetCode) {

        List<ExchangeRate> allRates = exchangeRateDAO.getAll();

        ExchangeRate usdToBase = allRates.stream()
                .filter(r -> r.getBaseCurrency().getCode().equalsIgnoreCase("USD")
                        && r.getTargetCurrency().getCode().equalsIgnoreCase(baseCode))
                .findFirst().orElseThrow(() -> new RuntimeException("Нет пары USD/" + baseCode));

        ExchangeRate usdToTarget = allRates.stream()
                .filter(r ->
                        r.getBaseCurrency().getCode().equalsIgnoreCase("USD")
                                && r.getTargetCurrency().getCode().equalsIgnoreCase(targetCode))
                .findFirst().orElseThrow(() -> new RuntimeException("Нет пары USD/" + targetCode));

        BigDecimal newRate = usdToTarget.getRate().divide(usdToBase.getRate(), 4, RoundingMode.HALF_UP);

        return new ExchangeRate(
                0,
                usdToBase.getTargetCurrency(),
                usdToTarget.getTargetCurrency(),
                newRate
        );

    }

}
