package nikolaiev.v.o.shop.util;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class LinkedDirectoryUtilsTest {

    @Test
    void linkingDirectories () {
        //some directories
        LinkedDirectory directory1 = new LinkedDirectory (){{
            this.setId (1l);
            this.setName ("Good Directory1");
            this.setDirectoryType (DirectoryType.BRAND.toString ());
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
            this.setDirectoryType (DirectoryType.BRAND_LIST.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};
        //directory that need to relate with other directories
        LinkedDirectory mainDirectory = new LinkedDirectory (){{
            this.setId (4l);
            this.setName ("Good Directory3");
            this.setDirectoryType (DirectoryType.PARAMETER_VALUE.toString ());
            this.setRelatedDirectories (new HashSet<> ());
            this.setRelatedDirectoryIds (new HashSet<> ());
        }};

        Set<LinkedDirectory> directorySet = new HashSet<LinkedDirectory> (){{
            add (directory1);
            add (directory2);
            add (directory3);
            add (mainDirectory);
        }};

        LinkedDirectoryUtils.linkingDirectories (directorySet,mainDirectory);
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

}