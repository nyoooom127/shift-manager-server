package assignsShifts.requestFilters;

import assignsShifts.entities.user.entity.User;
import com.auth0.jwt.JWTVerifier;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TokenFilter extends OncePerRequestFilter {
  @Autowired private JWTVerifier jwtVerifier;
  @Autowired private Gson gson;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();

    return path.contains("login");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = request.getHeader("token");

    if (token == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token header not present");

      return;
    }

    String json = jwtVerifier.verify(token).getClaim("user").asString();
    User user = gson.fromJson(json, User.class);

    request.setAttribute("user", user);

    filterChain.doFilter(request, response);
  }
}
