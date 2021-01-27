package nikolaiev.v.o.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nikolaiev.v.o.shop.controller.ProductController;
import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Product;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;
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
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductController controller;

    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-product-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-product-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllProducts() throws Exception {
        // Execute the GET request
        this.mockMvc.perform(get("/product"))
                //вывести результат в консоль
                .andDo(print())
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/product"))

                // Validate the returned fields
                .andExpect (jsonPath ("$", hasSize (1)))
                .andExpect (jsonPath ("$[0].id", is(20)))
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
    public void addProduct() throws Exception {

        Product productToPost = new Product (){{
            this.setProductName ("Xiaomi mi 5");
            this.setPhotos (new HashSet<> ());
            this.setDirectories (new HashSet<> ());
        }};
        byte[] productToPostAsBytes = objectMapper.writeValueAsBytes (productToPost);

        MockMultipartFile jsonFile = new MockMultipartFile (
                "product",
                "",
                "application/json",
                productToPostAsBytes);

        MockMultipartFile firstFile = new MockMultipartFile(
                "files",
                "filename.txt",
                "text/plain",
                "some xml".getBytes());

        // Execute the POST request
        String resultJson= this.mockMvc.perform (MockMvcRequestBuilders.multipart ("/product")
                .file (jsonFile)
                .file (firstFile))

                //вывести результат в консоль
                .andDo (print ())

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/product"))

                .andReturn ()
                .getResponse ()
                .getContentAsString ();

        Product resultProduct = objectMapper.readValue(resultJson, Product.class);

        assertEquals (50l, resultProduct.getId ());
        assertEquals (productToPost.getProductName (), resultProduct.getProductName ());
        assertEquals (productToPost.getPhotos (), resultProduct.getPhotos ());
        assertEquals (productToPost.getDirectories (), resultProduct.getDirectories ());
        assertNotNull (resultProduct.getCreationDate ());
    }

    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-product-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-product-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addProductWithDirectories() throws Exception {
        //given
        //directory1
        LinkedDirectory directory1 = new LinkedDirectory ();
        directory1.setId (1l);

        //directory2
        LinkedDirectory directory2 = new LinkedDirectory ();
        directory2.setId (2l);

        //directory3
        LinkedDirectory directory3 = new LinkedDirectory ();
        directory3.setId (3l);

        //directory4
        LinkedDirectory directory4 = new LinkedDirectory ();
        directory4.setId (4l);

        //directories
        Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (directory3);
            add (directory4);
        }};


        Product productToPost = new Product (){{
            this.setProductName ("Xiaomi mi 5");
            this.setPhotos (new HashSet<> ());
            this.setDirectories (directories);
        }};
        byte[] productToPostAsBytes = objectMapper.writeValueAsBytes (productToPost);

        MockMultipartFile jsonFile = new MockMultipartFile (
                "product",
                "",
                "application/json",
                productToPostAsBytes);

        MockMultipartFile firstFile = new MockMultipartFile(
                "files",
                "filename.txt",
                "text/plain",
                "some xml".getBytes());

        // Execute the POST request
        String resultJson= this.mockMvc.perform (MockMvcRequestBuilders.multipart ("/product")
                .file (jsonFile)
                .file (firstFile))

                //вывести результат в консоль
                .andDo (print ())

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/product"))

                .andReturn ()
                .getResponse ()
                .getContentAsString ();

        Product resultProduct = objectMapper.readValue(resultJson, Product.class);

        assertEquals (50l, resultProduct.getId (),"resultProduct.getId () should be 50");
        assertEquals (productToPost.getProductName (), resultProduct.getProductName ());
        assertNotEquals (productToPost.getDirectories (), resultProduct.getDirectories ());
        assertEquals (3,resultProduct.getDirectories ().size (),"resultProduct.getDirectories ().size () should be 3");
        assertNotNull (resultProduct.getCreationDate ());

    }


    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-product-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-product-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateProduct() throws Exception {
        String jsonRequestText = "{\"id\":20,\"productName\":\"Samsung s5\",\"productDiscription\":null,\"photos\":[],\"directories\":[],\"price\":null,\"creationDate\":null}";

        MockMultipartFile jsonFile = new MockMultipartFile (
                "product",
                "",
                "application/json",
                jsonRequestText.getBytes());

        String jsonResponseText = "{\"id\":20,\"productName\":\"Samsung s5\",\"productDiscription\":null,\"photos\":[],\"directories\":[],\"price\":null,\"creationDate\":null}";

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
    public void deleteProduct() throws Exception {
        long id = 20;

        this.mockMvc.perform (MockMvcRequestBuilders.delete ("/product/"+id))
                .andDo (print ())
                .andExpect(status().isOk());
    }


}


