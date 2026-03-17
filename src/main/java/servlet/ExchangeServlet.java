package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ExchangeDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeService;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeService exchangeService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeService = (ExchangeService) context
                .getAttribute("exchangeService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. Получаем параметры из строки запроса (?from=USD&to=EUR&amount=10)
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        // 2. Валидация параметров (400 Bad Request)
        if (from == null || to == null || amountStr == null || from.isBlank() || to.isBlank()) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Отсутствуют параметры from, to или amount");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            // 3. Вызываем сервис для расчета по 3 сценариям
            ExchangeDTO result = exchangeService.exchange(from.toUpperCase(), to.toUpperCase(), amount);

            // 4. Успех 200
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            gson.toJson(result, resp.getWriter());

        } catch (NumberFormatException e) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Некорректный формат суммы (amount)");
        } catch (RuntimeException e) {
            // Если сервис не нашел курс ни по одному сценарию (404)
            ServletUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ошибка сервера");
        }
    }

}
