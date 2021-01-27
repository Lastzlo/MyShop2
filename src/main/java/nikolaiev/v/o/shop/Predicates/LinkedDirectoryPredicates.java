package nikolaiev.v.o.shop.Predicates;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;

import java.util.function.Predicate;

public class LinkedDirectoryPredicates {
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

}
