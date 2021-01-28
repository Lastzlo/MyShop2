package nikolaiev.v.o.shop.util;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Product;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;


public class LinkedDirectoryUtils {

    /**
     * @return условие которое должна выполнить директория
     */
    public static Predicate<LinkedDirectory> getDirectoryPredicate (){
        //условие
        Predicate<LinkedDirectory> isDirectorySuitable = linkedDirectory -> {
            return linkedDirectory.getDirectoryType ().equals (DirectoryType.PARAMETER.toString ());
        };

        return isDirectorySuitable;
    }

    /**
     * Связать список директорий, с директорией
     *
     * @param inputDirectories список директорий
     * @param directory директория
     */
    public static void linkingDirectoryToDirectories (LinkedDirectory directory, Set<LinkedDirectory> inputDirectories) {
        inputDirectories.forEach (inputDirectory -> {
            if (directory != inputDirectory) {
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
     * Убрать у директорий directories связи с директориями directoriesToDislink
     * не смотря на то что их может связывать product
     *
     * @param directories
     * @param directoriesToDislink
     * @param product
     */
    public static void dislinkDirectoriesButProduct (Set<LinkedDirectory> directoriesToDislink, Set<LinkedDirectory> directories, Product product) {
        directoriesToDislink.forEach (
                directoryToDislink->{
                    dislinkDirectoryWithDirectoriesButProduct (directoryToDislink, directories, product);
                }
        );
    }

    /**
     * Убрать связи между директориями directories
     * не смотря на то что их может связывать product
     *
     * @param directories
     * @param product
     */
    public static void dislinkDirectoriesButProduct (Set<LinkedDirectory> directories, Product product) {
        Set<LinkedDirectory> directoriesToDislink = new HashSet<LinkedDirectory> (){{
            addAll (directories);
        }};

        directoriesToDislink.forEach (
                directoryToDislink->{
                    dislinkDirectoryWithDirectoriesButProduct (directoryToDislink, directories, product);
                }
        );
    }

    /**
     * Убрать у директорий directories связи с директорией directoryToDislink
     * не смотря на то что их может связывать product
     *
     * @param directories
     * @param directoryToDislink
     * @param product
     */
    private static void dislinkDirectoryWithDirectoriesButProduct (LinkedDirectory directoryToDislink, Set<LinkedDirectory> directories, Product product) {

        if (
                //если directoryToDislink не имеет привязаных продуктов
                directoryToDislink.getProducts ().size () == 0
                ||(
                        //или имеет один продукт который product
                        directoryToDislink.getProducts ().size () == 1
                        && directoryToDislink.getProducts ().contains (product)
                )
        ) {
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
                            if (
                                    !product1.equals (product) &&
                                    product1.getDirectories ().contains (directory)
                            ) {
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


    /**
     * Возвращает список директорий которые выполняют условие
     *
     * @param directories список директорий
     * @param isDirectorySuitable условие
     *
     * @return список директорий которые выполняют условие
     */
    public static Set<LinkedDirectory> checkDirectories (Set<LinkedDirectory> directories, Predicate<LinkedDirectory> isDirectorySuitable) {

        //список директорий которые выполняют условие
        Set<LinkedDirectory> resultSet = new HashSet<> ();

        directories.forEach (directory -> {
                    //преверить выполнение условия
                    if(isDirectorySuitable
                            .test (directory)
                    ){
                        //добавить в resultSet
                        resultSet.add (directory);
                    }
                }
        );

        return resultSet;
    }


}
