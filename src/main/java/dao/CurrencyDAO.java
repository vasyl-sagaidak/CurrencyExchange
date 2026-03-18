package dao;

import model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    private Connection connection;

    public CurrencyDAO(final Connection connection) {
        this.connection = connection;
    }

    public List<Currency> getAll() {

        List<Currency> currencies = new ArrayList<>();

        final String sql = "SELECT id, code, fullName, sign FROM currencies";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    currencies.add(
                            new Currency(
                                    resultSet.getInt("id"),
                                    resultSet.getString("code"),
                                    resultSet.getString("fullName"),
                                    resultSet.getString("sign")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка валют", e);
        }
        return currencies;
    }

    public Currency getCurrencyByCode(String code) {
        final String sql = "SELECT Id, Code, fullName, Sign FROM currencies WHERE Code = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Currency(
                            resultSet.getInt("Id"),
                            resultSet.getString("Code"),
                            resultSet.getString("fullName"),
                            resultSet.getString("Sign")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при поиске валюты по коду " + code, e);

        }
        return null;
    }

    public Currency addCurrency(Currency currency) {
        final String sql = "INSERT INTO currencies (code, fullName, sign) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement
                     = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    currency.setId(generatedKeys.getInt(1)); // Обновляем ID в обьекте
                }
            }
            return currency; // Возвращаем уже "полный" обьект
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления строки в таблицу валют", e);
        }
    }

    public void updateCurrency(int id, double newRate) {
        final String sql = "UPDATE currencies SET rate = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, newRate);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления курса обмена валюты.", e);
        }
    }

    public void deleteCurrency(int id) {
        final String sql = "DELETE FROM currencies WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении строки из таблицы по индексу " + id, e);
        }
    }

}
