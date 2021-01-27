package nikolaiev.v.o.shop.Predicates;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;

import java.util.function.Predicate;


/**
 * Условия для LinkedDirectory
 */
public class LinkedDirectoryPredicates {
    /**
     * @return условие при котором тип директории подходит чтобы ее добавить к другим директориям
     */
    public static Predicate<LinkedDirectory> getPredicateForAddDirectoryToOtherDirectory (){
        //условие
        Predicate<LinkedDirectory> isDirectorySuitable = linkedDirectory -> {
            return linkedDirectory.getDirectoryType ().equals (DirectoryType.PARAMETER.toString ());
        };

        return isDirectorySuitable;
    }

}
