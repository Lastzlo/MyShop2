package nikolaiev.v.o.shop;

import nikolaiev.v.o.shop.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProductTest {
    @Autowired
    private ProductController controller;

    @Test
    public void test(){
        assertThat(controller).isNotNull();
    }

}
