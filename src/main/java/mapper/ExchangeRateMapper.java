package mapper;

import dto.ExchangeRateDTO;
import model.ExchangeRate;

public final class ExchangeRateMapper {

    private ExchangeRateMapper() {
    }

    public static ExchangeRateDTO toDto(ExchangeRate entity) {
        if (entity == null) return null;
        return new ExchangeRateDTO(
                entity.getId(),
                CurrencyMapper.toDto(entity.getBaseCurrency()),
                CurrencyMapper.toDto(entity.getTargetCurrency()),
                entity.getRate()
        );

    }

    public static ExchangeRate toEntity(ExchangeRateDTO dto) {
        if (dto == null) return null;
        return new ExchangeRate(
                dto.getId(),
                CurrencyMapper.toEntity(dto.getBaseCurrency()),
                CurrencyMapper.toEntity(dto.getTargetCurrency()),
                dto.getRate()
        );

    }

}
