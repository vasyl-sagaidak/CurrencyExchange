import dao.CurrencyDAO;
import dao.ExchangeRateDAO;
import dto.CurrencyDTO;
import model.ExchangeRate;
import service.CurrencyService;
import util.DatabaseConnector;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

public class DBTestMain {
    public static void main(String[] args) {
        DatabaseConnector.registerSQLiteDriver();
        Connection connection = DatabaseConnector.getConnection();
        if (connection != null) {
            System.out.println("Connection to SQLite has been eshablished!");
        }
        try {
            if (connection != null) {
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println(metaData.getDriverName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

//        CurrencyDAO dao = new CurrencyDAO(connection);
//        List<Currency> allCurrencies = dao.getAllCurrencies();
//        allCurrencies.forEach(System.out::println);

//        ExchangeRateDAO rateDao = new ExchangeRateDAO(connection);
//        List<ExchangeRate> rates = rateDao.getAll();
//        rates.forEach(System.out::println);

        CurrencyDAO dao = new CurrencyDAO(connection);
        CurrencyService s = new CurrencyService(dao);
        List<CurrencyDTO> currencies = s.getCurrencies();
        currencies.forEach(System.out::println);
    }

}
