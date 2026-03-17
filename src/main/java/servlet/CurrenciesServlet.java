package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CurrencyDTO;
import exception.CurrencyAlreadyExistsException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void init()  {
        ServletContext context = getServletContext();
        this.currencyService = (CurrencyService) context.getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 1. Получаем данные из сервиса
            List<CurrencyDTO> currencies = currencyService.getCurrencies();

            // 2. Настраиваем ответ
            resp.setStatus(HttpServletResponse.SC_OK); // 200

            // 3. Сериализуем список в JSON прямо в поток ответа
            gson.toJson(currencies, resp.getWriter());

        } catch (Exception e) {
            // 4. Обработка ошибки 500 (БД недоступна итд).
            ServletUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. Извлекаем параметры формы
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        // 2. Проверка на 400 (Отсутствует поле)
        if (isInvalid(name) || isInvalid(code) || isInvalid(sign)) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Отсутствует нужное поле формы (name, code или sign)");
            return;
        }

        try {
            // 3. Создаем DTO для передачи в сервис
            // ID передаем 0, так как база сама его сгенерирует
            CurrencyDTO newCurrencyDto = new CurrencyDTO(0, code.toUpperCase(), name, sign);

            // 4. Пытаемся добавить через сервис
            CurrencyDTO savedCurrencyDto = currencyService.addCurrency(newCurrencyDto);

            // 5. Успех - 201 Created
            resp.setStatus(HttpServletResponse.SC_CREATED);
            gson.toJson(savedCurrencyDto, resp.getWriter());

        } catch (CurrencyAlreadyExistsException e) {
            // 6. Ошибка 409 (Конфликт - валюта уже есть)
            ServletUtil.sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            // 7. Ошибка 500
            ServletUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ошибка базы данных: " + e.getMessage());
        }
    }

    private boolean isInvalid(String param) {
        return param == null || param.isBlank();
    }

}
