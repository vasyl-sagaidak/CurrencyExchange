package servlet;

import com.google.gson.Gson;
import dto.ExchangeRateDTO;
import exception.CurrencyNotFoundException;
import exception.ExchangeRateAlreadyExistsException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final Gson gson = new Gson();
    ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context.getAttribute(
                "exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 1. Получаем список всех курсов (с вложенными валютами)
            List<ExchangeRateDTO> allRates = exchangeRateService.getAllRates();

            // 2. Настраиваем заголовки
            resp.setStatus(HttpServletResponse.SC_OK); // 200

            // 3. Отправляем список в формате JSON
            gson.toJson(allRates, resp.getWriter());

        } catch (Exception e) {
            // 4. Ошибка 500 (БД недоступна или ошибка маппинга)
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"Ошибка базы данных: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. Извлекаем параметры формы
        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");

        // 2. Проверка на 400 (Отсутствует поле)
        if (isInvalid(baseCode) || isInvalid(targetCode) || isInvalid(rateStr)) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Отсутствуют нужные поля формы");
            return;
        }

        try {
            double rate = Double.parseDouble(rateStr);

            // 3. Пытаемся добавить курс через сервис
            // Сервис внутри должен проверить наличие валют и уникальность пары
            ExchangeRateDTO savedRate = exchangeRateService.add(baseCode, targetCode, rate);

            // 4. Успех - 201 Created
            resp.setStatus(HttpServletResponse.SC_CREATED);
            gson.toJson(savedRate, resp.getWriter());

        } catch (NumberFormatException e) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Некорректный формат числа rate");
        } catch (CurrencyNotFoundException e) {
            // 5. Ошибка 404 (Валюта не найдена в БД)
            ServletUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ExchangeRateAlreadyExistsException e) {
            // 6. Ошибка 409 (Пара уже есть)
            ServletUtil.sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            // 7. Ошибка 500
            ServletUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ошибка базы данных");
        }
    }

    private boolean isInvalid(String param) {
        return param == null || param.isBlank();
    }

}
