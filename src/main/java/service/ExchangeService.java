package service;

import dao.ExchangeRateDAO;
import dto.ExchangeDTO;
import exception.NegativeExchangeAmountException;
import mapper.ExchangeMapper;
import model.ExchangeRate;

import java.util.List;

public class ExchangeService {
    private static final int ONE = 1;
    ExchangeRateDAO exchangeRateDAO;

    public ExchangeService(ExchangeRateDAO exchangeRateDAO) {
        this.exchangeRateDAO = exchangeRateDAO;
    }

    public ExchangeDTO exchange(String baseCode, String targetCode, double sum) {
        if (sum < 0) {
            throw new NegativeExchangeAmountException(
                    "Сумма для обмена должна быть представлена положительным числом.");
        }

        ExchangeRate direct = getMatch(baseCode, targetCode);
        if (direct != null) {
            return ExchangeMapper.toDto(direct, sum, sum * direct.getRate());

        }

        ExchangeRate reverse = getMatch(targetCode, baseCode);
        if (reverse != null) {
            double rate = 1.0 / reverse.getRate();
            ExchangeRate virtual =
                    new ExchangeRate(0, reverse.getBaseCurrency(), reverse.getTargetCurrency(), rate);
            return ExchangeMapper.toDto(virtual, sum, sum * rate);

        } else {

            ExchangeRate cross = throughUsdMatch(baseCode, targetCode);
            return ExchangeMapper.toDto(cross, sum, sum * cross.getRate());
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

        double newRate = usdToTarget.getRate() / usdToBase.getRate();

        return new ExchangeRate(
                0,
                usdToBase.getTargetCurrency(),
                usdToTarget.getTargetCurrency(),
                newRate
        );

    }

}
