package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CurrencyDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;

@WebServlet("/api/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private CurrencyService currencyService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.currencyService = (CurrencyService) context.getAttribute(
                "currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // Вернет "/EUR"

        // 1. Проверка на 400 (код отсутствует)
        if (pathInfo == null || pathInfo.equals("/")) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Код валюты отсутствует в адресе");
            return;
        }

        // Убираем лишний слеш, чтобы получить "EUR"
        String currencyCode = pathInfo.replace("/", "").toUpperCase();

        try {
            // 2. Ищем валюту через сервис
            CurrencyDTO currency = currencyService.getCurrencyByCode(currencyCode);

            // 3. Проверка на 404 (не найдена)
            if (currency == null) {
                ServletUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Валюта не найдена");
                return;
            }

            // 4. Успех 200
            gson.toJson(currency, resp.getWriter());

        } catch (Exception e) {
            // 5. Ошибка 500
            ServletUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ошибка базы данных");
        }
    }


}

