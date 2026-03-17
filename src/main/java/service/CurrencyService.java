package service;

import dao.CurrencyDAO;
import dto.CurrencyDTO;
import mapper.CurrencyMapper;
import model.Currency;

import java.util.List;

public class CurrencyService {
    CurrencyDAO currencyDAO;

    public CurrencyService(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
    }

    public List<CurrencyDTO> getCurrencies() {
        return currencyDAO.getAll().stream().map(CurrencyMapper::toDto).toList();
    }

    public CurrencyDTO getCurrencyByCode(String code) {
        Currency currency = currencyDAO.getCurrencyByCode(code);
        return (currency != null) ? CurrencyMapper.toDto(currency) : null;
    }

    public CurrencyDTO addCurrency(CurrencyDTO currencyDTO) {
        currencyDAO.addCurrency(CurrencyMapper.toEntity(currencyDTO));
        return currencyDTO;
    }


}
