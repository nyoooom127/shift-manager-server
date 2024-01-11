package assignsShifts.configuration;

import assignsShifts.requestFilters.TokenFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class FilterConfiguration {

    @Bean
    @ConditionalOnProperty(value="auth.enabled", havingValue = "true", matchIfMissing = true)
    public TokenFilter tokenFilter(){return new TokenFilter();}

    @Bean
    @ConditionalOnProperty(value="auth.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<TokenFilter> tokenFilterFilterRegistrationBean() {
        FilterRegistrationBean<TokenFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(tokenFilter());
        filterFilterRegistrationBean.addUrlPatterns("/*");

        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        ResourceHttpRequestHandler source = new ResourceHttpRequestHandler();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList("https://shiftmanager-409516.web.app/"));
        // corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000/", "https://shiftmanager-409516.web.app/"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        source.setCorsConfiguration(corsConfiguration);
        filterFilterRegistrationBean.setFilter(new CorsFilter(source));
        filterFilterRegistrationBean.addUrlPatterns("/*");
        filterFilterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return filterFilterRegistrationBean;
    }

//    cd C:\Projects\shift-manager-server
//    gcloud app deploy
//    gcloud app logs tail -s shift-manager-server
}
