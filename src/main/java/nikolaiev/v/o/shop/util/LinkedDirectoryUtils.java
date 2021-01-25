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
    public static void linkingDirectories (Set<LinkedDirectory> inputDirectories, LinkedDirectory directory) {
        inputDirectories.forEach (inputDirectory -> {
            //проверям что директория к которой хотим связать не такая же, а также ее тип PARAMETER или BRAND
            if (
                    directory != inputDirectory
                            && (
                            inputDirectory.getDirectoryType ()
                                    .equals (DirectoryType.PARAMETER.toString ())
                                    ||
                                    inputDirectory.getDirectoryType ()
                                            .equals (DirectoryType.PARAMETER_VALUE.toString ())
                                    ||
                                    inputDirectory.getDirectoryType ()
                                            .equals (DirectoryType.BRAND.toString ()))

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
                    //если directoryToDislink не имеет привязаных продуктов
                    if(directoryToDislink.getProductsCount () == 0){
//                        System.out.println ("directoryToDislink не имеет привязаных продуктов, можно удалялть связи с directories");

                        directories.forEach (
                                oldNeededDirectory ->{

//                                    System.out.println ("//удалили directoryToDislink с oldNeededDirectoryId");
                                    oldNeededDirectory.getRelatedDirectories ().remove (directoryToDislink);
                                    oldNeededDirectory.getRelatedDirectoryIds ().remove (directoryToDislink.getId ());

//                                    System.out.println ("//удалили oldNeededDirectoryId с directoryToDislink");
                                    directoryToDislink.getRelatedDirectories ().remove (oldNeededDirectory);
                                    directoryToDislink.getRelatedDirectoryIds ().remove (oldNeededDirectory.getId ());

                                }

                        );

                    }
                    else {
//                        System.out.println ("directoryToDislink имеет ("+directoryToDislink.getProductsCount ()+") привязаных продуктов");

                        directories.forEach (
                                oldNeededDirectory ->{
                                    //встречаеться ли хоть один раз oldNeededDirectory в directoryToDislink.getProducts.Product.getDirectories
                                    boolean isOldNeededDirectoryInDirectoryToDeleteProductsDirectories = false;

                                    for (Product product1: directoryToDislink.getProducts ()
                                    ) {
                                        if(product1.getDirectories ().contains (oldNeededDirectory)){
                                            isOldNeededDirectoryInDirectoryToDeleteProductsDirectories = true;

                                            break;
                                        }

                                    }

                                    //встречаеться ли хоть один раз oldNeededDirectory в directoryToDislink.getProducts.Product.getDirectories
                                    if(!isOldNeededDirectoryInDirectoryToDeleteProductsDirectories){
//                                        System.out.println ("!isOldNeededDirectoryInDirectoryToDeleteProductsDirectories");
//                                        System.out.println ("Нет, не встречаеться oldNeededDirectory в directoryToDislink.getProducts.Product.getDirectories");

//                                        System.out.println ("oldNeededDirectory = "+oldNeededDirectory);
//                                        System.out.println ("directoryToDislink = "+directoryToDislink);

//                                        System.out.println ("//удалили directoryToDislink с oldNeededDirectoryId");
                                        oldNeededDirectory.getRelatedDirectories ().remove (directoryToDislink);
                                        oldNeededDirectory.getRelatedDirectoryIds ().remove (directoryToDislink.getId ());

//                                        System.out.println ("//удалили oldNeededDirectoryId с directoryToDislink");
                                        directoryToDislink.getRelatedDirectories ().remove (oldNeededDirectory);
                                        directoryToDislink.getRelatedDirectoryIds ().remove (oldNeededDirectory.getId ());
                                    } else {
//                                        System.out.println ("isOldNeededDirectoryInDirectoryToDeleteProductsDirectories");
//                                        System.out.println ("Да встречаеться oldNeededDirectory в directoryToDislink.getProducts.Product.getDirectories");
                                    }

                                }
                        );


                    }

                }
        );
    }

}
