package dao;

import exception.ExchangeRateAlreadyExistsException;
import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO {
    private final Connection connection;

    public ExchangeRateDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ExchangeRate> getAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        final String getAll = """
                SELECT
                er.id AS id,
                er.rate AS rate,
                bc.id AS base_id, bc.code AS base_code, bc.fullName AS base_name,
                bc.sign AS base_sign, tc.id AS target_id, tc.code AS target_code, tc.fullName AS target_name,
                tc.sign AS target_sign
                FROM exchange_rates er
                JOIN currencies bc ON er.BaseCurrencyId = bc.id
                JOIN currencies tc ON er.TargetCurrencyId = tc.id
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(getAll)) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while(rs.next()) {
                    exchangeRates.add(
                            combineEntity(rs, makeCurrency(rs, "base_"), makeCurrency(rs, "target_"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    public ExchangeRate add(ExchangeRate exchangeRate) {
        final String insert = "INSERT INTO exchange_rates " +
                "(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);

                    return new ExchangeRate(
                            generatedId,
                            exchangeRate.getBaseCurrency(),
                            exchangeRate.getTargetCurrency(),
                            exchangeRate.getRate()
                    );
                } else {
                    throw new SQLException("Не удалось получить ІD для нового курса");
                }
            }
        } catch (SQLException e) {
            // Код ошибки 19 в SQLite - нарушение UNIQUE (если пара валют уже есть)
            if(e.getErrorCode() == 19) {
                throw new ExchangeRateAlreadyExistsException(
                        "Обменный курс для данной пары уже сущестует");
            }
            throw new RuntimeException("Ошибка базы данных", e);
        }
    }

    public ExchangeRate get(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id should be positive.");
        }
        final String getById = """
                SELECT er.id, er.rate,
                bc.id AS base_id, bc.code AS base_code, bc.fullName AS base_name, bc.sign AS base_sign,
                tc.id AS target_id, tc.code AS target_code, tc.fullName AS target_name, tc.sign AS target_sign
                FROM exchange_rates er
                JOIN currencies bc ON er.baseCurrencyId = bc.id
                JOIN currencies tc ON er.targetCurrencyId = tc.id
                WHERE er.id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(getById)) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return combineEntity(rs, makeCurrency(rs, "base_"), makeCurrency(rs, "target_"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске курса с id " + id, e);
        }
        return null;
    }

    public ExchangeRate update(int id, BigDecimal rate) {
        final String updateRate = "UPDATE exchange_rates SET Rate = ? WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateRate)) {
                preparedStatement.setBigDecimal(1, rate);
                preparedStatement.setInt(2, id);
            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Обновление не удалось, курс с id " + id + ", не найден.");
            }
            return get(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при попытке обновления курса валюты по индексу " + id, e);
        }
    }

    public void delete(int id) {
        final String delete = "DELETE FROM exchange_rates WHERE Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(delete)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при попытке удаления строки из таблицы по индексу " + id, e);
        }
    }

    private Currency makeCurrency(ResultSet resultSet, String prefix) throws SQLException {
        return new Currency(
                resultSet.getInt(prefix + "id"),
                resultSet.getString(prefix + "code"),
                resultSet.getString(prefix + "name"),
                resultSet.getString(prefix + "sign")
        );
    }

    private ExchangeRate combineEntity(ResultSet resultSet, Currency base, Currency target)
            throws SQLException{
        return new ExchangeRate(
                resultSet.getInt("id"),
                base,
                target,
                resultSet.getBigDecimal("rate")
        );
    }
}
