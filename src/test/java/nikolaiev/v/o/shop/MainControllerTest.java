package nikolaiev.v.o.shop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contexLoads() throws Exception {
        this.mockMvc.perform(get("/"))       //выполнить гет запрос на "/"
                .andDo(print())                       //вывести получ результат в консоль
                .andExpect(status()
                        .isOk())           //ожидать статус 200
                .andExpect(content()
                        .string(containsString("MyShop")));
    }

    @Test
    public void notFoundSettingPage() throws Exception {
        this.mockMvc.perform (get ("/setting"))
                .andDo (print ())
                .andExpect (status ().isNotFound ());
    }

}
