package mapper;

import dto.ExchangeDTO;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ExchangeMapper {

    private ExchangeMapper() {
    }

    public static ExchangeDTO toDto(ExchangeRate entity, BigDecimal sum, BigDecimal convertedAmount) {
        if (entity == null) return null;
        BigDecimal rate = entity.getRate();

        return new ExchangeDTO(
                entity.getId(),
                CurrencyMapper.toDto(entity.getBaseCurrency()),
                CurrencyMapper.toDto(entity.getTargetCurrency()),
                round(rate, 4),
                round(sum, 2),
                round(convertedAmount, 2)
        );
    }

    private static BigDecimal round(BigDecimal value, int places) {
        if (value == null) return null;

        // setScale устанавливает количество знаков после запятой
        // RoundingMode.HALF_UP — это стандартное математическое округление (от 5 вверх)
        return value.setScale(places, RoundingMode.HALF_UP);
    }
}
