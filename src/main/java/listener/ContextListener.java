package listener;

import dao.CurrencyDAO;
import dao.ExchangeRateDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.CurrencyService;
import service.ExchangeRateService;
import service.ExchangeService;
import util.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {
    private Connection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            // 1. Создаем соединение (в реальном проекте тут будет DataSource/HikariCP)
            DatabaseConnector.registerSQLiteDriver();

            // 2. Получаем соединение
            this.connection = DatabaseConnector.getConnection();

            // 3. Собираем зависимости DAO
            CurrencyDAO currencyDAO = new CurrencyDAO(connection);
            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO(connection);

            // 4. Собираем сервисы
            CurrencyService currencyService = new CurrencyService(currencyDAO);
            ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDAO, currencyDAO);
            ExchangeService exchangeService = new ExchangeService(exchangeRateDAO);

            // 5. Сохраняем в контекст приложения
            context.setAttribute("currencyService", currencyService);
            context.setAttribute("exchangeRateService", exchangeRateService);
            context.setAttribute("exchangeService", exchangeService);

        } catch (Exception e) {
            // Если база не заведется - приложение не должно стартовать
            throw new RuntimeException("Критическая ошибка при старте приложения", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed safely");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
