package nikolaiev.v.o.shop.Predicates;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;

import java.util.function.Predicate;


/**
 * Условия для LinkedDirectory
 */
public class LinkedDirectoryPredicates {
    /**
     * Возвращает условие при котором тип директории
     * подходит чтобы ее добавить к другим директориям
     *
     * @return условие
     */
    public static Predicate<LinkedDirectory> getPredicateForAddDirectoryToOtherDirectory (){
        //условие
        Predicate<LinkedDirectory> isDirectorySuitable = linkedDirectory -> {
            return linkedDirectory.getDirectoryType ().equals (DirectoryType.PARAMETER.toString ());
        };

        return isDirectorySuitable;
    }

    /**
     * Возвращает условие которое должна выполнить директория
     * чтобы ее можно было добавить к товару
     *
     * @return условие
     */
    public static Predicate<LinkedDirectory> getDirectoryPredicateForAddDirectoryToProduct (){
        //условие
        Predicate<LinkedDirectory> isDirectorySuitable = linkedDirectory -> {
            return (linkedDirectory
                    .getDirectoryType ().equals (DirectoryType.CATEGORY.toString ()))
                    ||
                    (linkedDirectory
                            .getDirectoryType ().equals (DirectoryType.PARAMETER.toString ()));
        };

        return isDirectorySuitable;
    }

}
