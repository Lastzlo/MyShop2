package nikolaiev.v.o.shop.util;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.repos.LinkedDirectoryRepo;
import nikolaiev.v.o.shop.services.LinkedDirectoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static nikolaiev.v.o.shop.util.LinkedDirectoryUtils.dislinkDirectories;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class LinkedDirectoryUtilsTest {

    @Autowired
    private LinkedDirectoryService directoryService;

    @MockBean
    private LinkedDirectoryRepo directoryRepo;

    @Test
    void linkingDirectoryToDirectories () {
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
            this.setId (3l);
            this.setName ("Bad Directory");
            this.setDirectoryType (DirectoryType.CATEGORY.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};
        //directory that need to relate with other directories
        LinkedDirectory mainDirectory = new LinkedDirectory (){{
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
            add (mainDirectory);
        }};

        LinkedDirectoryUtils.linkingDirectoryToDirectories (mainDirectory,directorySet);
        //test mainDirectory
        Assertions.assertEquals (2, mainDirectory.getRelatedDirectories ().size (),  "mainDirectory related directories size should be 2");
        Assertions.assertEquals (2, mainDirectory.getRelatedDirectoryIds ().size (), "mainDirectory related directories Id size should be 2");
        Assertions.assertTrue (mainDirectory.getRelatedDirectories ().contains (directory1), "mainDirectory related directories should be contains directory1");
        Assertions.assertTrue (mainDirectory.getRelatedDirectories ().contains (directory2), "mainDirectory related directories should be contains directory2");
        Assertions.assertFalse (mainDirectory.getRelatedDirectories ().contains (directory3), "mainDirectory related directories should not be contains directory3");

        //test directory1
        Assertions.assertEquals (0, directory2.getRelatedDirectories ().size (), "directory2 related directories size should be 0");

        //test directory3
        Assertions.assertEquals (0, directory3.getRelatedDirectories ().size (), "directory2 related directories size should be 0");

    }

    @Test
    void dislinkDirectoryWithDirectoriesButProductsCount0 () {
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
        //directory that need to dislink with other directories
        LinkedDirectory dislinkDirectory = new LinkedDirectory (){{
            this.setId (4l);
            this.setName ("Good Directory3");
            this.setDirectoryType (DirectoryType.PARAMETER.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());

            //setProductsCount = 0
            this.setProductsCount (0l);
        }};

        //1) create directorySet
        Set<LinkedDirectory> directorySet = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (dislinkDirectory);
        }};

        //2) link all directory in directorySet
        directoryService.linkingDirectories (directorySet);


        //when
        LinkedDirectoryUtils.dislinkDirectoryWithDirectories (dislinkDirectory, directorySet);

        //than
        //test dislinkDirectory
        Assertions.assertEquals (0, dislinkDirectory.getRelatedDirectories ().size (),"dislinkDirectory related directories size should be 0");
        Assertions.assertEquals (0, dislinkDirectory.getRelatedDirectoryIds ().size (),"dislinkDirectory related directories id size should be 0");
        //test directory1
        Assertions.assertEquals (1, directory1.getRelatedDirectories ().size (),"directory1 related directories size should be 1");
        Assertions.assertEquals (1, directory1.getRelatedDirectoryIds ().size (),"directory1 related directories id size should be 1");
    }

}