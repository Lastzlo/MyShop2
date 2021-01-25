package nikolaiev.v.o.shop.controller;

import nikolaiev.v.o.shop.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
//Спринг пытаеться автоматически создать структуру
//классов поторая подменяет слой MVC
//все будет проходить в фейковом окружении
@AutoConfigureMockMvc
//аннотация указывает на новый файл с настройками
@TestPropertySource("/application-test.properties")
public class ProductControllerIntegrationTest {

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
        // Execute the GET request
        this.mockMvc.perform(get("/product"))
                .andDo(print())                            //вывести полученый результат в консоль
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/product"))

                // Validate the returned fields
                .andExpect (jsonPath ("$", hasSize (1)))
                .andExpect (jsonPath ("$[0].id", is(1)))
                .andExpect (jsonPath ("$[0].productName", is("Apple iPhone 10")))
                .andExpect (jsonPath ("$[0].productDiscription", nullValue ()))
                .andExpect (jsonPath ("$[0].photos", hasSize (0)))
                .andExpect (jsonPath ("$[0].directories", hasSize (0)))
                .andExpect (jsonPath ("$[0].price", nullValue ()))
                .andExpect (jsonPath ("$[0].creationDate", nullValue ()));
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

