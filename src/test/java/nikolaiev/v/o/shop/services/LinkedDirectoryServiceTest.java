package nikolaiev.v.o.shop.services;


import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;

import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.repos.LinkedDirectoryRepo;
import nikolaiev.v.o.shop.util.LinkedDirectoryUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

@SpringBootTest
class LinkedDirectoryServiceTest {

    @Autowired
    private LinkedDirectoryService directoryService;

    @MockBean
    private LinkedDirectoryRepo directoryRepo;

    @Test
    public void addDirectoriesToProduct () {
        //given
        final LinkedDirectory directory1 = new LinkedDirectory (){{
            this.setName ("directory1");
        }};
        final LinkedDirectory directory2 = new LinkedDirectory (){{
            this.setName ("directory2");
        }};

        final Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
        }};

        Product product = new Product (){{
            this.setDirectories (new HashSet<> ());
        }};

        //when
        final Product actualProduct = directoryService.addDirectoriesToProduct (directories, product);

        //then
        Assertions.assertEquals (2, actualProduct.getDirectories ().size (), "actualProduct.getDirectories ().size () should be 2");
        Assertions.assertTrue (actualProduct.getDirectories ().contains (directory1), "actualProduct getDirectories () should be contain directory1");
    }

    /*@Test
    public void addDirectoriesToProductButDirectoryTypeParameter () {
        //given
        final LinkedDirectory directory1 = new LinkedDirectory (){{
            this.setId (1l);
            this.setName ("Name");
            this.setDirectoryType (DirectoryType.PARAMETER.toString ());
        }};
        final Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
        }};

        Product product = new Product (){{
            this.setDirectories (new HashSet<> ());
        }};

        // Setup mock scenari
        when (directoryRepo.findById(1l))
                .thenReturn (Optional.of (directory1));


        //when
        final Product actualProduct = directoryService.addDirectoriesToProduct (directories, product);


        //then
        Assertions.assertEquals (1, actualProduct.getDirectories ().size ());
        Mockito.verify (directoryRepo, Mockito.times (1))
                .findById(
                        ArgumentMatchers.anyLong ()
                );
    }

    @Test
    public void addDirectoriesToProductButDirectoriesNotFound () {
        //given
        final LinkedDirectory directory1 = new LinkedDirectory (){{
            this.setId (1l);
            this.setName ("Name");
        }};
        final Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
        }};

        Product product = new Product (){{
            this.setDirectories (new HashSet<> ());
        }};

        // Setup mock scenari
        when (directoryRepo.findById(1l))
                .thenReturn (Optional.empty ());


        //when
        final Product actualProduct = directoryService.addDirectoriesToProduct (directories, product);


        //then
        Assertions.assertEquals (0, actualProduct.getDirectories ().size ());
        Mockito.verify (directoryRepo, Mockito.times (1))
                .findById(
                        ArgumentMatchers.anyLong ()
                );
    }

    @Test
    public void addDirectoriesToProductButDirectoriyTypeParameterList () {
        //given
        final LinkedDirectory directory1 = new LinkedDirectory (){{
            this.setId (1l);
            this.setName ("Name");
            this.setDirectoryType (DirectoryType.PARAMETER_LIST.toString ());
        }};
        final Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
        }};

        Product product = new Product (){{
            this.setDirectories (new HashSet<> ());
        }};

        // Setup mock scenari
        when (directoryRepo.findById(1l))
                .thenReturn (Optional.of (directory1));


        //when
        final Product actualProduct = directoryService.addDirectoriesToProduct (directories, product);


        //then
        Assertions.assertEquals (0, actualProduct.getDirectories ().size ());
        Mockito.verify (directoryRepo, Mockito.times (1))
                .findById(
                        ArgumentMatchers.anyLong ()
                );
    }*/

    @Test
    public void addProductToDirectories () {
        //given
        Product product1 = new Product (){{
           this.setProductName ("Product that been in directory1");
        }};
        LinkedDirectory directory1 = new LinkedDirectory (){{
            this.setProductsCount (1l);
            this.setProducts (new HashSet<Product> (){{add(product1);}});
        }};
        LinkedDirectory directory2 = new LinkedDirectory (){{
            this.setProductsCount (2l);
            this.setProducts (new HashSet<> ());
        }};
        LinkedDirectory directory3 = new LinkedDirectory (){{
            this.setProductsCount (3l);
            this.setProducts (new HashSet<> ());
        }};

        //directories
        Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (directory3);
        }};

        //Product than need to add to directories
        Product product = new Product (){{
            this.setProductName ("Product than need to add to directories");
        }};

        //when
        directoryService.addProductToDirectories (product,directories);

        //then
        //test directory1
        Assertions.assertEquals (2l, directory1.getProductsCount (), "directory1 productsCount should be 2");
        Assertions.assertTrue (directory1.getProducts ().contains (product), "directory1 products should contains product");
        //test directory3
        Assertions.assertEquals (1l, directory3.getProductsCount (), "directory3 productsCount should be 1");
        Assertions.assertTrue (directory3.getProducts ().contains (product), "directory3 products should contains product");

    }

    @Test
    public void linkingDirectories(){
        //given
        //some directories
        LinkedDirectory directory1 = new LinkedDirectory (){{
            this.setId (1l);
            this.setName ("Good Directory1");
            this.setDirectoryType (DirectoryType.PARAMETER.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};
        LinkedDirectory directory2 = new LinkedDirectory (){{
            this.setId (2l);
            this.setName ("Good Directory2");
            this.setDirectoryType (DirectoryType.PARAMETER.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};
        LinkedDirectory directory3 = new LinkedDirectory (){{
            this.setId (4l);
            this.setName ("Good Directory3");
            this.setDirectoryType (DirectoryType.PARAMETER.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};

        Set<LinkedDirectory> directorySet = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (directory3);
        }};


        //when
        directoryService.linkingDirectories (directorySet);

        //then
        //test directory1
        Assertions.assertEquals (2, directory1.getRelatedDirectories ().size (),  "directory1 related directories size should be 2");
        Assertions.assertEquals (2, directory1.getRelatedDirectoryIds ().size (), "directory1 related directories Id size should be 2");
        Assertions.assertTrue (directory1.getRelatedDirectories ().contains (directory2), "directory1 related directories should be contain directory2");

        Assertions.assertFalse (directory1.getRelatedDirectories ().contains (directory1), "directory1 related directories should not contain directory1");

        //test directory2
        Assertions.assertEquals (2, directory2.getRelatedDirectories ().size (),  "directory2 related directories size should be 2");
        Assertions.assertEquals (2, directory2.getRelatedDirectoryIds ().size (), "directory2 related directories Id size should be 2");
        Assertions.assertTrue (directory2.getRelatedDirectories ().contains (directory3), "directory2 related directories should be contain directory3");









    }

    @Test
    public void linkingDirectories2(){
        //given
        //some directories
        LinkedDirectory directory1 = new LinkedDirectory (){{
            this.setId (1l);
            this.setName ("Good Directory");
            this.setDirectoryType (DirectoryType.PARAMETER.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};
        LinkedDirectory directory2 = new LinkedDirectory (){{
            this.setId (2l);
            this.setName ("Good Directory");
            this.setDirectoryType (DirectoryType.PARAMETER.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};
        LinkedDirectory directory3 = new LinkedDirectory (){{
            this.setId (3l);
            this.setName ("Bad Directory");
            this.setDirectoryType (DirectoryType.CATEGORY.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};
        LinkedDirectory directory4 = new LinkedDirectory (){{
            this.setId (4l);
            this.setName ("Bad Directory");
            this.setDirectoryType (DirectoryType.CATEGORY_LIST.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};

        Set<LinkedDirectory> directorySet = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (directory3);
            add (directory4);
        }};


        //when
        directoryService.linkingDirectories2 (directorySet);


        //then
        //test directory1
        Assertions.assertEquals (1, directory1.getRelatedDirectories ().size (),  "directory1 related directories size should be 1");
        Assertions.assertEquals (1, directory1.getRelatedDirectoryIds ().size (), "directory1 related directories Id size should be 1");
        Assertions.assertTrue (directory1.getRelatedDirectories ().contains (directory2), "directory1 related directories should be contain directory2");

        Assertions.assertFalse (directory1.getRelatedDirectories ().contains (directory1), "directory1 related directories should not contain directory1");

        //test directory3
        Assertions.assertEquals (0, directory3.getRelatedDirectories ().size (),  "directory3 related directories size should be 0");
        Assertions.assertEquals (0, directory3.getRelatedDirectoryIds ().size (), "directory3 related directories Id size should be 0");











    }
}