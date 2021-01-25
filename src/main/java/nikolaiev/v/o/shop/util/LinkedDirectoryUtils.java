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
     * Убрать у директорий oldNeededDirectories связи с директориями directorysToDeleteFromProduct
     *
     * @param directorysToDeleteFromProduct
     * @param oldNeededDirectories
     */
    public static void dislinkDirectories (Set<LinkedDirectory> directorysToDeleteFromProduct, Set<LinkedDirectory> oldNeededDirectories) {
        directorysToDeleteFromProduct.forEach (
                directoryToDelete->{
                    //если directoryToDelete не имеет привязаных продуктов
                    if(directoryToDelete.getProductsCount () == 0){
//                        System.out.println ("directoryToDelete не имеет привязаных продуктов, можно удалялть связи с oldNeededDirectories");

                        oldNeededDirectories.forEach (
                                oldNeededDirectory ->{

//                                    System.out.println ("//удалили directoryToDelete с oldNeededDirectoryId");
                                    oldNeededDirectory.getRelatedDirectories ().remove (directoryToDelete);
                                    oldNeededDirectory.getRelatedDirectoryIds ().remove (directoryToDelete.getId ());

//                                    System.out.println ("//удалили oldNeededDirectoryId с directoryToDelete");
                                    directoryToDelete.getRelatedDirectories ().remove (oldNeededDirectory);
                                    directoryToDelete.getRelatedDirectoryIds ().remove (oldNeededDirectory.getId ());

                                }

                        );

                    }
                    else {
//                        System.out.println ("directoryToDelete имеет ("+directoryToDelete.getProductsCount ()+") привязаных продуктов");

                        oldNeededDirectories.forEach (
                                oldNeededDirectory ->{
                                    //встречаеться ли хоть один раз oldNeededDirectory в directoryToDelete.getProducts.Product.getDirectories
                                    boolean isOldNeededDirectoryInDirectoryToDeleteProductsDirectories = false;

                                    for (Product product1: directoryToDelete.getProducts ()
                                    ) {
                                        if(product1.getDirectories ().contains (oldNeededDirectory)){
                                            isOldNeededDirectoryInDirectoryToDeleteProductsDirectories = true;

                                            break;
                                        }

                                    }

                                    //встречаеться ли хоть один раз oldNeededDirectory в directoryToDelete.getProducts.Product.getDirectories
                                    if(!isOldNeededDirectoryInDirectoryToDeleteProductsDirectories){
//                                        System.out.println ("!isOldNeededDirectoryInDirectoryToDeleteProductsDirectories");
//                                        System.out.println ("Нет, не встречаеться oldNeededDirectory в directoryToDelete.getProducts.Product.getDirectories");

//                                        System.out.println ("oldNeededDirectory = "+oldNeededDirectory);
//                                        System.out.println ("directoryToDelete = "+directoryToDelete);

//                                        System.out.println ("//удалили directoryToDelete с oldNeededDirectoryId");
                                        oldNeededDirectory.getRelatedDirectories ().remove (directoryToDelete);
                                        oldNeededDirectory.getRelatedDirectoryIds ().remove (directoryToDelete.getId ());

//                                        System.out.println ("//удалили oldNeededDirectoryId с directoryToDelete");
                                        directoryToDelete.getRelatedDirectories ().remove (oldNeededDirectory);
                                        directoryToDelete.getRelatedDirectoryIds ().remove (oldNeededDirectory.getId ());
                                    } else {
//                                        System.out.println ("isOldNeededDirectoryInDirectoryToDeleteProductsDirectories");
//                                        System.out.println ("Да встречаеться oldNeededDirectory в directoryToDelete.getProducts.Product.getDirectories");
                                    }

                                }
                        );


                    }

                }
        );
    }

}
