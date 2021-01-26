package nikolaiev.v.o.shop.util;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Product;

import java.util.Set;


public class LinkedDirectoryUtils {
    /**
     * Связать список директорий, с директорией
     *
     * @param inputDirectories список директорий
     * @param directory директория
     */
    public static void linkingDirectoryToDirectories (LinkedDirectory directory, Set<LinkedDirectory> inputDirectories) {
        inputDirectories.forEach (inputDirectory -> {
            //проверям что директория к которой хотим связать не такая же, а также ее тип PARAMETER или BRAND
            if (
                    directory != inputDirectory
                            && (inputDirectory.getDirectoryType ()
                                    .equals (DirectoryType.PARAMETER.toString ()))

            ) {

                directory.addRelatedDirectory (inputDirectory);
                directory.addRelatedDirectoryId (inputDirectory.getId ());

            }
        });
    }


    /**
     * Убрать у директорий directories связи с директориями directoriesToDislink
     *
     * @param directories
     * @param directoriesToDislink
     */
    public static void dislinkDirectories (Set<LinkedDirectory> directoriesToDislink, Set<LinkedDirectory> directories) {
        directoriesToDislink.forEach (
                directoryToDislink->{
                    dislinkDirectoryWithDirectories (directoryToDislink, directories);
                }
        );
    }

    /**
     * Убрать у директории directoryToDislink связи с директориями directories
     *
     * @param directories
     * @param directoryToDislink
     */
    public static void dislinkDirectoryWithDirectories (LinkedDirectory directoryToDislink, Set<LinkedDirectory> directories) {
        //если directoryToDislink не имеет привязаных продуктов
        if (directoryToDislink.getProductsCount () == 0) {
//                        System.out.println ("directoryToDislink не имеет привязаных продуктов, можно удалялть связи с directories");

            directories.forEach (
                    directory -> {

//                                    System.out.println ("//удалили directoryToDislink с oldNeededDirectoryId");
                        directory.getRelatedDirectories ().remove (directoryToDislink);
                        directory.getRelatedDirectoryIds ().remove (directoryToDislink.getId ());

//                                    System.out.println ("//удалили oldNeededDirectoryId с directoryToDislink");
                        directoryToDislink.getRelatedDirectories ().remove (directory);
                        directoryToDislink.getRelatedDirectoryIds ().remove (directory.getId ());

                    }

            );

        } else {
//                        System.out.println ("directoryToDislink имеет ("+directoryToDislink.getProductsCount ()+") привязаных продуктов");

            directories.forEach (
                    directory -> {
                        //встречаеться ли хоть один раз directory в directoryToDislink.getProducts.Product.getDirectories
                        boolean isOldNeededDirectoryInDirectoryToDeleteProductsDirectories = false;

                        for (Product product1 : directoryToDislink.getProducts ()
                        ) {
                            if (product1.getDirectories ().contains (directory)) {
                                isOldNeededDirectoryInDirectoryToDeleteProductsDirectories = true;

                                break;
                            }

                        }

                        //встречаеться ли хоть один раз directory в directoryToDislink.getProducts.Product.getDirectories
                        if (isOldNeededDirectoryInDirectoryToDeleteProductsDirectories) {
//                                        System.out.println ("isOldNeededDirectoryInDirectoryToDeleteProductsDirectories");
//                                        System.out.println ("Да встречаеться directory в directoryToDislink.getProducts.Product.getDirectories");
                        } else {
//                                        System.out.println ("!isOldNeededDirectoryInDirectoryToDeleteProductsDirectories");
//                                        System.out.println ("Нет, не встречаеться directory в directoryToDislink.getProducts.Product.getDirectories");

//                                        System.out.println ("directory = "+directory);
//                                        System.out.println ("directoryToDislink = "+directoryToDislink);

//                                        System.out.println ("//удалили directoryToDislink с oldNeededDirectoryId");
                            directory.getRelatedDirectories ().remove (directoryToDislink);
                            directory.getRelatedDirectoryIds ().remove (directoryToDislink.getId ());

//                                        System.out.println ("//удалили oldNeededDirectoryId с directoryToDislink");
                            directoryToDislink.getRelatedDirectories ().remove (directory);
                            directoryToDislink.getRelatedDirectoryIds ().remove (directory.getId ());
                        }

                    }
            );


        }
    }



}
