package mapper;

import dto.CurrencyDTO;
import model.Currency;

public final class CurrencyMapper {

    private CurrencyMapper() {}

    public static CurrencyDTO toDto(Currency entity) {
        if (entity == null) return null;
        return new CurrencyDTO(
                entity.getId(),
                entity.getCode(),
                entity.getFullName(),
                entity.getSign()
        );
    }

    public static Currency toEntity(CurrencyDTO dto) {
        if (dto == null) return null;
        return new Currency(
                dto.getId(),
                dto.getCode(),
                dto.getFullName(),
                dto.getSign()
        );
    }
}
