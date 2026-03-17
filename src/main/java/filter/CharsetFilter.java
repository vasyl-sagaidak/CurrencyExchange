package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

@WebFilter("/api/*")
public class CharsetFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 1. Устанавливаем заголовок для ответа
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 2. Устанавливаем кодировку для чтения параметров (чтобы POST не бился)
        request.setCharacterEncoding("UTF-8");

        // 3. Передаем управление дальше (следующему фильтру или сервлету)
        chain.doFilter(request, response);
    }
}
