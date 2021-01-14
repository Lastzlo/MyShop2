package nikolaiev.v.o.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration      //этот класс содержит конфигурацию нашего веб слоя
public class MvcConfig implements WebMvcConfigurer {
    //путь для загрузки изображений на сервер
    @Value("${upload.picture.path}")
    private String uploadPath;

    //путь для выгрузки изображений с сервера
    @Value("${unload.picture.path}")
    private String unloadPath;

    @Override
    public void addResourceHandlers (ResourceHandlerRegistry registry) {
        registry.addResourceHandler (unloadPath+"**")
                .addResourceLocations ("file:/" + uploadPath + "/");
    }

//    public void addViewControllers (ViewControllerRegistry registry){
//        registry.addViewController ("/setting").setViewName ("setting.html");
//    }



    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerCustomizer(){
        return container -> {
            //добавили ErrorPage, если не находит даный адрес то перекидывает на "/"
            //связно в содабвлениес vuerouter который сам занимаеться загрузкой контента относително адреса страницы
            container.addErrorPages (new ErrorPage (HttpStatus.NOT_FOUND, "/"));
        };
    }

}
