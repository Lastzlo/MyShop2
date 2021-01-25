package nikolaiev.v.o.shop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
//Спринг пытаеться автоматически создать структуру
//классов поторая подменяет слой MVC
//все будет проходить в фейковом окружении
@AutoConfigureMockMvc
//аннотация указывает на новый файл с настройками
@TestPropertySource("/application-test.properties")
class DirectoryIntegrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DirectoryController controller;

    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-directory-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-directory-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getCore () throws Exception {
        // Execute the GET request
        this.mockMvc.perform(get("/directory/getCore"))
                .andDo(print())                            //вывести полученый результат в консоль
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/directory/getCore"))

                // Validate the returned fields
                .andExpect (jsonPath ("$.id", is(10)))
                .andExpect (jsonPath ("$.name", is("CATEGORY_LIST")))
                .andExpect (jsonPath ("$.directoryType", is("CATEGORY_LIST")))
                .andExpect (jsonPath ("$.children", hasSize (0)))
                .andExpect (jsonPath ("$.relatedDirectoryIds", hasSize (0)))
                .andExpect (jsonPath ("$.productsCount", is(0)));
    }

}