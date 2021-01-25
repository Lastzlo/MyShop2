package nikolaiev.v.o.shop.services;


import nikolaiev.v.o.shop.domain.LinkedDirectory;

import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.repos.LinkedDirectoryRepo;
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

import static org.mockito.Mockito.doReturn;
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
}