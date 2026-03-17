package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ExchangeRateDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context
                .getAttribute("exchangeRateService");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            // Включаем поддержку PATCH, так как по умолчанию HttpServlet eго не обрабатывает
            throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // 1. Проверка на 400 (Коды валют отсутствуют в адресе)
        if (pathInfo == null || pathInfo.equals("/")) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Коды валют пары отсутствуют в адресе"); // 400
            return;
        }

        // Уберем слеш и получаем "USDUAH"
        String pair = pathInfo.replace("/", "").toUpperCase();

        // Проверяем, что в строке ровно 6 символов (3 для base, 3 для target)
        if (pair.length() != 6) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Некорректный формат валютной пары"); // 400
            return;
        }

        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3);

        try {
            // 2. Ищем курс через сервис
            ExchangeRateDTO rate = exchangeRateService.getRateByCodes(baseCode, targetCode);

            // 3. Проверка на 404 (Курс не найден)
            if (rate == null) {
                ServletUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Обменный курс для пары не найден"); // 404
                return;
            }

            // 4. Успех 200
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            gson.toJson(rate, resp.getWriter());

        } catch (Exception e) {
            // 5. Ошибка 500
            ServletUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ошибка базы данных"); // 500
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // 1. Проверка на наличие кодов в пути (400)
        if (pathInfo == null || pathInfo.replace("/", "").length() != 6) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Коды валют пары некорректны или отсутствуют");
            return;
        }

        // Достаем USDUAH -> USD и UAH
        String pair = pathInfo.replace("/", "").toUpperCase();
        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3);

        // 2. Читаем поле rate из тела (x-www-form-urlencoded)
        // ВНИМАНИЕ: Для PATCH getParameter() может не работать "из коробки" в некоторых версиях Tomcat
        // если пусто, придется читать тело через BufferedReader вручную
        String rateParam = req.getReader().readLine();
        if (rateParam == null || !rateParam.contains("rate=")) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Отсутствует нужное поле формы (rate)");
            return;
        }

        double newRate;

        try {
            newRate = Double.parseDouble(rateParam.replace("rate=", ""));
        } catch (NumberFormatException e) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Некорректное значение курса");
            return;
        }

        try {
            // 3. Обновляем через сервис
            ExchangeRateDTO updatedRate = exchangeRateService.patchByPairCodes(baseCode, targetCode, newRate);

            // 4. Проверка 404 (Если пары нет в базе)
            if (updatedRate == null) {
                ServletUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Валютная пара отсутствует в базе данных");
                return;
            }

            // 5. Успех 200
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            gson.toJson(updatedRate, resp.getWriter());

        } catch (Exception e) {
            ServletUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ошибка базы данных");
        }
    }

}
