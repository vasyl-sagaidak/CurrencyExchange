package mapper;

import dto.ExchangeDTO;
import model.ExchangeRate;

public final class ExchangeMapper {

    private ExchangeMapper() {
    }

    public static ExchangeDTO toDto(ExchangeRate entity, double sum, double convertedAmount) {
        if (entity == null) return null;
        double rate = entity.getRate();

        return new ExchangeDTO(
                entity.getId(),
                CurrencyMapper.toDto(entity.getBaseCurrency()),
                CurrencyMapper.toDto(entity.getTargetCurrency()),
                round(rate, 4),
                round(sum, 2),
                round(convertedAmount, 2)
        );
    }

    private static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
