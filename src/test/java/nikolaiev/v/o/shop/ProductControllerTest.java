package nikolaiev.v.o.shop;

import nikolaiev.v.o.shop.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductController controller;

    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-product-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-product-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllProductsTest() throws Exception {
        String testJSONText = "[{\"id\":1,\"productName\":\"Apple iPhone 10\",\"productDiscription\":null,\"photos\":[],\"directories\":[],\"price\":null,\"creationDate\":null}]";
        this.mockMvc.perform(get("/product"))       //выполнить гет запрос на "/"
                .andDo(print())                            //вывести полученый результат в консоль
                .andExpect (content().json (testJSONText, true));
    }

    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-product-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-product-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addProductTest() throws Exception {
        String jsonRequestText = "{\"id\":null,\"productName\":\"Xiaomi mi 5\",\"productDiscription\":null,\"photos\":[],\"directories\":[],\"price\":null,\"creationDate\":null}";

        MockMultipartFile jsonFile = new MockMultipartFile (
                "product",
                "",
                "application/json",
                jsonRequestText.getBytes());

        MockMultipartFile firstFile = new MockMultipartFile("files", "filename.txt", "text/plain", "some xml".getBytes());

        String jsonResponseText = "{\"id\":10,\"productName\":\"Xiaomi mi 5\",\"productDiscription\":null,\"photos\":[],\"directories\":[],\"price\":null}";

        this.mockMvc.perform (MockMvcRequestBuilders.multipart ("/product")
                    .file (jsonFile)
                    .file (firstFile))
                .andDo (print ())
                .andExpect(status().isOk())
                .andExpect(content().json (jsonResponseText));

    }

    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-product-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-product-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateProductTest() throws Exception {
        String jsonRequestText = "{\"id\":1,\"productName\":\"Samsung s5\",\"productDiscription\":null,\"photos\":[],\"directories\":[],\"price\":null,\"creationDate\":null}";

        MockMultipartFile jsonFile = new MockMultipartFile (
                "product",
                "",
                "application/json",
                jsonRequestText.getBytes());

        String jsonResponseText = "{\"id\":1,\"productName\":\"Samsung s5\",\"productDiscription\":null,\"photos\":[],\"directories\":[],\"price\":null,\"creationDate\":null}";

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart ("/product");

        builder.with (new RequestPostProcessor () {
            @Override
            public MockHttpServletRequest postProcessRequest (MockHttpServletRequest request) {
                request.setMethod ("PUT");
                return request;
            }
        });

        this.mockMvc.perform (builder
                .file (jsonFile))
                .andDo (print ())
                .andExpect(status().isOk())
                .andExpect(content().json (jsonResponseText));
    }


    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-product-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-product-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteProductTest() throws Exception {
        long id = 1;

        this.mockMvc.perform (MockMvcRequestBuilders.delete ("/product/"+id))
                .andDo (print ())
                .andExpect(status().isOk());
    }


}


