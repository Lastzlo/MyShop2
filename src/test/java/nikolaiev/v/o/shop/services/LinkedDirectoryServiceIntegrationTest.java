package nikolaiev.v.o.shop.services;

import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.repos.LinkedDirectoryRepo;
import nikolaiev.v.o.shop.repos.ProductRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
//Спринг пытаеться автоматически создать структуру
//классов поторая подменяет слой MVC
//все будет проходить в фейковом окружении
@AutoConfigureMockMvc
//аннотация указывает на новый файл с настройками
@TestPropertySource("/application-test.properties")
class LinkedDirectoryServiceIntegrationTest {

    @Autowired
    private LinkedDirectoryService directoryService;

    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-directory-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-directory-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addDirectoriesToProduct () {
        //given
        final LinkedDirectory directory1 = new LinkedDirectory () {{
            this.setId (1l);
        }};
        final LinkedDirectory directory2 = new LinkedDirectory () {{
            this.setId (2l);
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
        Assertions.assertEquals (2, actualProduct.getDirectories ().size ());
    }

    /*@Test
    public void addDirectoriesToProductButDirectoriesNotFound () {
        //given
        final LinkedDirectory directory1 = new LinkedDirectory () {{
            this.setId (55l);
        }};
        final LinkedDirectory directory2 = new LinkedDirectory () {{
            this.setId (66l);
        }};

        final Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
        }};

        Product product = new Product (){{
            this.setDirectories (new HashSet<> ());
        }};

        final Product actualProduct = directoryService.addDirectoriesToProduct (directories, product);

        Assertions.assertEquals (0, actualProduct.getDirectories ().size ());
    }*/

    /*@Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-directory-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-directory-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addDirectoriesToProductButDirectoryTypeParameterList () {
        //given
        final LinkedDirectory directory1 = new LinkedDirectory () {{
            this.setId (4l);
        }};

        final Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
        }};

        Product product = new Product (){{
            this.setDirectories (new HashSet<> ());
        }};

        //when
        final Product actualProduct = directoryService.addDirectoriesToProduct (directories, product);

        //then
        Assertions.assertEquals (0, actualProduct.getDirectories ().size ());
    }*/


    @Test
    //перед тестом выполнить очистку и заполнение БД
    @Sql(value = {"/create-directory-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    //после теста выполнить очистку БД
    @Sql(value = {"/create-directory-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getDirectoriesCopyFromDB(){
        //given
        //directory1 is in the database
        final LinkedDirectory directory1 = new LinkedDirectory () {{
            this.setId (1l);
        }};
        //directory2 is in the database
        final LinkedDirectory directory2 = new LinkedDirectory () {{
            this.setId (2l);
        }};
        //directory3 is not present in the database
        final LinkedDirectory directory3 = new LinkedDirectory () {{
            this.setId (99l);
        }};

        final Set<LinkedDirectory> directories = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (directory3);
        }};

        //when
        final Set<LinkedDirectory> directoriesFromDB = directoryService.getDirectoriesCopyFromDB (directories);

        //then
        Assertions.assertEquals (2, directoriesFromDB.size (), "directoriesFromDB.size should be 2");
    }


}

