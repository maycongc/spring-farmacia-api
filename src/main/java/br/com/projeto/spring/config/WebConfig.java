package br.com.projeto.spring.config;

// ...existing code...
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra interceptors MVC.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private LoggingInterceptor loggingInterceptor;

    public WebConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(

            @NonNull
            InterceptorRegistry registry) {

        registry.addInterceptor(loggingInterceptor).addPathPatterns("/**");
    }
}
