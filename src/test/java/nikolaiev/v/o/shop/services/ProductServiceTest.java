package nikolaiev.v.o.shop.services;

import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.repos.LinkedDirectoryRepo;
import nikolaiev.v.o.shop.repos.ProductRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    private ProductService productService;

    //@MockBean для логики которую нужно подменить
    @MockBean
    private LinkedDirectoryRepo directoryRepo;

    @MockBean
    private ProductRepo productRepo;

    @Test
    void saveProduct () {
        //Подготовленные данные
        Product product = new Product (){{
            this.setId ((long) 0);
            this.setProductName ("no name");
            this.setProductDiscription ("no productDiscription");
            this.setPhotos (new HashSet<> ());
            this.setDirectories (new HashSet<> ());
        }};


        Optional<MultipartFile[]> files = Optional.empty ();

        // Setup mock scenario
        Mockito.when (productRepo.save (product)).thenReturn (product);

        //Результат метода
        final Product resultProduct = productService.saveProduct (product, files);

        //ожидаемый результат
        final Product expectedProduct = product;
        //актуальны результат
        final Product actualProduct = resultProduct;

        //условие которое проверяет тест
        Assertions.assertEquals (expectedProduct, actualProduct);
    }
}

