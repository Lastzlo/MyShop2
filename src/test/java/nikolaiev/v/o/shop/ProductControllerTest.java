package nikolaiev.v.o.shop;

import nikolaiev.v.o.shop.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//Спринг пытаеться автоматически создать структуру
//классов поторая подменяет слой MVC
//все будет проходить в фейковом окружении
@AutoConfigureMockMvc
//аннотация указывает на новый файл с настройками
@TestPropertySource("/application-test.properties")
//перед тестом выполнить очистку и заполнение БД
@Sql(value = {"/create-product-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//после теста выполнить очистку БД
@Sql(value = {"/create-product-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductController controller;

    @Test
    public void test(){
        assertThat(controller).isNotNull();
    }

    @Test
    public void getAllProductsTest() throws Exception {
        String testJSONText = "[{" +
                "\"id\":1," +
                "\"productName\":\"Apple iPhone 10\"," +
                "\"productDiscription\":\"\"," +
                "\"photos\":[]," +
                "\"directories\":[]," +
                "\"price\":\"\"," +
                "\"creationDate\":\"\"" +
                "}]";
        this.mockMvc.perform(get("/product"))       //выполнить гет запрос на "/"
                .andDo(print());                             //вывести полученый результат в консоль
                //.andExpect (content().json (testJSONText));
    }




}


