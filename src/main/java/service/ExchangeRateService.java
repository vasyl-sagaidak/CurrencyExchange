package service;

import dao.CurrencyDAO;
import dao.ExchangeRateDAO;
import dto.ExchangeRateDTO;
import exception.EntityNotFoundException;
import mapper.ExchangeRateMapper;
import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyDAO currencyDAO;

    public ExchangeRateService(ExchangeRateDAO exchangeRateDAO, CurrencyDAO currencyDAO) {
        this.exchangeRateDAO = exchangeRateDAO;
        this.currencyDAO = currencyDAO;
    }

    public List<ExchangeRateDTO> getAllRates() {
        return exchangeRateDAO.getAll().stream().map(ExchangeRateMapper::toDto).toList();

    }

    public ExchangeRateDTO getRateByCodes(String baseCode, String targetCode) {
        return exchangeRateDAO.getAll().stream()
                .filter(r -> r.getBaseCurrency().getCode().equalsIgnoreCase(baseCode))
                .filter(r -> r.getTargetCurrency().getCode().equalsIgnoreCase(targetCode))
                .findFirst()
                .map(ExchangeRateMapper::toDto)
                .orElse(null);
    }

    public ExchangeRateDTO add(String baseCode, String targetCode, BigDecimal rate) {
        // 1. Достаем полные обьекты валют из БД по их кодам
        Currency base = currencyDAO.getCurrencyByCode(baseCode);
        Currency target = currencyDAO.getCurrencyByCode(targetCode);

        // 2. Если их нет - это 404 (бросаем исключение)
        if (base == null || target == null) {
            throw new EntityNotFoundException("Валюта не найдена");
        }

        // 3. Теперь у нас есть обьекты с правильным ID. Создаем Entity курса.
        ExchangeRate entity = new ExchangeRate(0, base, target, rate);

        // 4. Сохраняем и маппим в DTO для ответа
        return ExchangeRateMapper.toDto(exchangeRateDAO.add(entity));
    }

    public ExchangeRateDTO patchByPairCodes(String baseCode, String targetCode, BigDecimal newRate) {
        if (newRate == null || newRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rate should be positive number");
        }
        ExchangeRate pair = ExchangeRateMapper.toEntity(getRateByCodes(baseCode, targetCode));
        return ExchangeRateMapper.toDto(exchangeRateDAO.update(pair.getId(), newRate));
    }

}
