package nikolaiev.v.o.shop.services;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
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
import java.util.Set;

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
        //given
        Product expectedProduct = new Product (){{
            this.setId ((long) 0);
            this.setProductName ("no name");
            this.setProductDiscription ("no productDiscription");
            this.setPhotos (new HashSet<> ());
            this.setDirectories (new HashSet<> ());
        }};
        Optional<MultipartFile[]> files = Optional.empty ();
        // Setup mock scenari
        Mockito.when (productRepo.save (expectedProduct))
                .thenReturn (expectedProduct);

        //when
        final Product actualProduct = productService.saveProduct (expectedProduct, files);

        //then
        Assertions.assertEquals (expectedProduct, actualProduct, "expectedProduct and actualProduct should be equal");
        Assertions.assertNotNull (expectedProduct.getCreationDate (), "expectedProduct.getCreationDate () should be not Null");
    }

    @Test
    void saveProductWithDirectories () {
        //given
        //directory1
        LinkedDirectory directory1 = new LinkedDirectory (
                DirectoryType.PARAMETER,
                "directory1"
        );
        directory1.setId (1l);
        // Setup mock scenery
        Mockito.when (directoryRepo.findById (directory1.getId ()))
                .thenReturn (Optional.of (directory1));

        //directory2
        LinkedDirectory directory2 = new LinkedDirectory (
                DirectoryType.PARAMETER,
                "directory2"
        );
        directory2.setId (2l);
        // Setup mock scenery
        Mockito.when (directoryRepo.findById (directory2.getId ()))
                .thenReturn (Optional.of (directory2));

        //directory3
        LinkedDirectory directory3 = new LinkedDirectory (
                DirectoryType.PARAMETER_LIST,
                "directory3"
        );
        directory3.setId (3l);

        //directory4
        LinkedDirectory directory4 = new LinkedDirectory (
                DirectoryType.CATEGORY,
                "directory4"
        );
        directory4.setId (4l);
        // Setup mock scenery
        Mockito.when (directoryRepo.findById (directory4.getId ()))
                .thenReturn (Optional.of (directory4));

        //directories
        Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (directory3);
            add (directory4);
        }};

        //product
        Product expectedProduct = new Product (){{
            this.setId ((long) 0);
            this.setProductName ("no name");
            this.setProductDiscription ("no productDiscription");
            this.setPhotos (new HashSet<> ());
            this.setDirectories (directories);
        }};
        // Setup mock scenery
        Mockito.when (productRepo.save (expectedProduct))
                .thenReturn (expectedProduct);

        //files
        Optional<MultipartFile[]> files = Optional.empty ();


        //when
        final Product actualProduct = productService.saveProduct (expectedProduct, files);


        //then
        //test product
        Assertions.assertEquals (expectedProduct, actualProduct, "expectedProduct and actualProduct should be equal");
        Assertions.assertNotNull (expectedProduct.getCreationDate (), "expectedProduct.getCreationDate () should be not Null");
        Assertions.assertEquals (3, expectedProduct.getDirectories ().size (), "expectedProduct.getDirectories ().size () should be 3");

        //test directory1
        Assertions.assertEquals (1, directory1.getProductsCount (), " directory1.getProductsCount () should be 1");
        Assertions.assertTrue (directory1.getProducts ().contains (expectedProduct), " directory1.getProducts () should be contain expectedProduct");
        Assertions.assertEquals (1, directory1.getRelatedDirectories ().size (), " directory1.getRelatedDirectories ().size () should be 1");
        Assertions.assertEquals (1, directory1.getRelatedDirectoryIds ().size (), " directory1.getRelatedDirectoryIds ().size () should be 1");
        Assertions.assertTrue (directory1.getRelatedDirectories ().contains (directory2), " directory1.getRelatedDirectories () should be contain directory2");
        Assertions.assertFalse (directory1.getRelatedDirectories ().contains (directory3), " directory1.getRelatedDirectories () should not contain directory3");
        Assertions.assertFalse (directory1.getRelatedDirectories ().contains (directory4), " directory1.getRelatedDirectories () should not contain directory4");

        //test directory4
        Assertions.assertEquals (1, directory4.getProductsCount (), " directory4.getProductsCount () should be 1");
        Assertions.assertTrue (directory4.getProducts ().contains (expectedProduct), " directory4.getProducts () should be contain expectedProduct");

    }

    @Test
    void saveProduct1 () {
        //given
        Product expectedProduct = new Product (){{
            this.setId ((long) 0);
            this.setProductName ("no name");
            this.setProductDiscription ("no productDiscription");
            this.setPhotos (new HashSet<> ());
            this.setDirectories (new HashSet<> ());
        }};
        Optional<MultipartFile[]> files = Optional.empty ();
        // Setup mock scenari
        Mockito.when (productRepo.save (expectedProduct))
                .thenReturn (expectedProduct);

        //when
        final Product actualProduct = productService.saveProduct1 (expectedProduct, files);

        //then
        Assertions.assertEquals (expectedProduct, actualProduct, "expectedProduct and actualProduct should be equal");
        Assertions.assertNotNull (expectedProduct.getCreationDate (), "expectedProduct.getCreationDate () should be not Null");
    }

    @Test
    void saveProductWithDirectories1 () {
        //given
        //directory1
        LinkedDirectory directory1 = new LinkedDirectory (
                DirectoryType.PARAMETER,
                "directory1"
        );
        directory1.setId (1l);
        // Setup mock scenery
        Mockito.when (directoryRepo.findById (directory1.getId ()))
                .thenReturn (Optional.of (directory1));

        //directory2
        LinkedDirectory directory2 = new LinkedDirectory (
                DirectoryType.PARAMETER,
                "directory2"
        );
        directory2.setId (2l);
        // Setup mock scenery
        Mockito.when (directoryRepo.findById (directory2.getId ()))
                .thenReturn (Optional.of (directory2));

        //directory3
        LinkedDirectory directory3 = new LinkedDirectory (
                DirectoryType.PARAMETER_LIST,
                "directory3"
        );
        directory3.setId (3l);

        //directory4
        LinkedDirectory directory4 = new LinkedDirectory (
                DirectoryType.CATEGORY,
                "directory4"
        );
        directory4.setId (4l);
        // Setup mock scenery
        Mockito.when (directoryRepo.findById (directory4.getId ()))
                .thenReturn (Optional.of (directory4));

        //directories
        Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (directory3);
            add (directory4);
        }};

        //product
        Product expectedProduct = new Product (){{
            this.setId ((long) 0);
            this.setProductName ("no name");
            this.setProductDiscription ("no productDiscription");
            this.setPhotos (new HashSet<> ());
            this.setDirectories (directories);
        }};
        // Setup mock scenery
        Mockito.when (productRepo.save (expectedProduct))
                .thenReturn (expectedProduct);

        //files
        Optional<MultipartFile[]> files = Optional.empty ();


        //when
        final Product actualProduct = productService.saveProduct1 (expectedProduct, files);


        //then
        //test product
        Assertions.assertEquals (expectedProduct, actualProduct, "expectedProduct and actualProduct should be equal");
        Assertions.assertNotNull (expectedProduct.getCreationDate (), "expectedProduct.getCreationDate () should be not Null");
        Assertions.assertEquals (3, expectedProduct.getDirectories ().size (), "expectedProduct.getDirectories ().size () should be 3");

        //test directory1
        Assertions.assertEquals (1, directory1.getProductsCount (), " directory1.getProductsCount () should be 1");
        Assertions.assertTrue (directory1.getProducts ().contains (expectedProduct), " directory1.getProducts () should be contain expectedProduct");
        Assertions.assertEquals (1, directory1.getRelatedDirectories ().size (), " directory1.getRelatedDirectories ().size () should be 1");
        Assertions.assertEquals (1, directory1.getRelatedDirectoryIds ().size (), " directory1.getRelatedDirectoryIds ().size () should be 1");
        Assertions.assertTrue (directory1.getRelatedDirectories ().contains (directory2), " directory1.getRelatedDirectories () should be contain directory2");
        Assertions.assertFalse (directory1.getRelatedDirectories ().contains (directory3), " directory1.getRelatedDirectories () should not contain directory3");
        Assertions.assertFalse (directory1.getRelatedDirectories ().contains (directory4), " directory1.getRelatedDirectories () should not contain directory4");

        //test directory4
        Assertions.assertEquals (1, directory4.getProductsCount (), " directory4.getProductsCount () should be 1");
        Assertions.assertTrue (directory4.getProducts ().contains (expectedProduct), " directory4.getProducts () should be contain expectedProduct");

    }
}

