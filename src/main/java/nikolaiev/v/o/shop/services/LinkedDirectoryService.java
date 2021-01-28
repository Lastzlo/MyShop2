package nikolaiev.v.o.shop.services;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.repos.LinkedDirectoryRepo;
import nikolaiev.v.o.shop.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static nikolaiev.v.o.shop.Predicates.LinkedDirectoryPredicates.getPredicateForAddDirectoryToOtherDirectory;
import static nikolaiev.v.o.shop.util.LinkedDirectoryUtils.checkDirectories;
import static nikolaiev.v.o.shop.util.LinkedDirectoryUtils.linkingDirectoryToDirectories;

@Service
public class LinkedDirectoryService {

    //репозиторий товаров
    @Autowired
    private ProductRepo productRepo;
    //репозиторий связанных директорий
    @Autowired
    private LinkedDirectoryRepo directoryRepo;

    //метод возвращает директорию самого высшего уровня DirectoryType.CATEGORY_LIST
    public LinkedDirectory getCore () {
        LinkedDirectory directory = directoryRepo.findByDirectoryType (DirectoryType.CATEGORY_LIST.toString ());
        //если директория не найдена то создаеться
        if (directory==null){
            //создает директорию
            directory = new LinkedDirectory (DirectoryType.CATEGORY_LIST);

            directory = directoryRepo.save (directory);
        }
        return directory;
    }

    //метод возращает список товаров привязаных к директории
    public Set<Product> getProductsByLinkedDirectoryId(String id){
        Optional<LinkedDirectory> optionalLinkedDirectory = directoryRepo.findById (Long.valueOf (id));
        if (optionalLinkedDirectory.isPresent ()){
            return optionalLinkedDirectory.get ().getProducts ();
        } else return new HashSet<> ();

    }

    //метод возращает директорию по ее id
    public LinkedDirectory getOne (String id) {
        return directoryRepo.getOne(Long.valueOf(id));
    }

    //создает директорию,
    public LinkedDirectory create(LinkedDirectory linkedDirectory){
        Optional<LinkedDirectory> optionalFatherDirectory = directoryRepo.findById(Long.valueOf(linkedDirectory.getFather ().getId ()));

        //устонавливаем контейнер для связанных дерикторий
        linkedDirectory.setRelatedDirectories (new HashSet<> ());

        LinkedDirectory child = linkedDirectory;

        //нужна нормальна обработка в виде Optional
        if(optionalFatherDirectory.isPresent ()){
            LinkedDirectory father = optionalFatherDirectory.get ();
            String fatherDirectoryType = father.getDirectoryType ();

            child.setFather (father);

            //проверка что отец CATEGORY_LIST
            if(fatherDirectoryType.equals (DirectoryType.CATEGORY_LIST.toString ())){

                child.setDirectoryType (DirectoryType.CATEGORY.toString ());
                child = directoryRepo.save (child);

                //добавляем в него дочернюю директорию Бренды
                LinkedDirectory brandList = new LinkedDirectory (
                        DirectoryType.PARAMETER_LIST,
                        "Бренд"
                );
                brandList.setFather (child);
                brandList = directoryRepo.save (brandList);

                child.addChild (brandList);
            }

            //проверка что отец CATEGORY
            if(fatherDirectoryType.equals (DirectoryType.CATEGORY.toString ())){
                child.setDirectoryType (DirectoryType.PARAMETER_LIST.toString ());
            }

            //проверка что отец PARAMETER_LIST
            if(fatherDirectoryType.equals (DirectoryType.PARAMETER_LIST.toString ())){
                child.setDirectoryType (DirectoryType.PARAMETER.toString ());
            }

            child = directoryRepo.save (child);

            father.addChild (child);
            directoryRepo.save (father);

            return child;
        } else {
            //return Empty LinkedDirectory
            return new LinkedDirectory ();
        }
    }


    /**
     * Обновляет директорию в БД
     * @param directory
     * @return сохраненную директорию
     */
    public LinkedDirectory update (LinkedDirectory directory) {
        return directoryRepo.save (directory);
    }

    public LinkedDirectory update(
            String id,
            LinkedDirectory directory
    ){
        if(directoryRepo.findById(Long.valueOf(id)).isPresent ()){
            LinkedDirectory directoryFromDb = directoryRepo.findById(Long.valueOf(id)).get();

            directoryFromDb.setName (directory.getName ());
            return directoryRepo.save (directoryFromDb);
        }
        return directory;
    }

    public void delete(String id){
        directoryRepo.findById (Long.valueOf (id)).ifPresent (
                linkedDirectory -> {
                    //удаление тега из всех товаров
                    linkedDirectory.getProducts ().forEach (
                            product -> {
                                product.deleteDirectory (linkedDirectory);
                                productRepo.save (product);
                            }
                    );

                    //удаление связей с другими тегами
                    linkedDirectory.getRelatedDirectories ().forEach (
                            reletaedDirectory -> {
                                //удалили reletaedDirectory с linkedDirectory
                                reletaedDirectory.getRelatedDirectories ().remove (linkedDirectory);
                                reletaedDirectory.getRelatedDirectoryIds ().remove (linkedDirectory.getId ());

                                directoryRepo.save (reletaedDirectory);
                            }
                    );
                    linkedDirectory.getRelatedDirectories ().clear ();
                    linkedDirectory.getRelatedDirectoryIds ().clear ();

                    //удаление у родителя
                    if(linkedDirectory.getFather ()!=null){
                        LinkedDirectory father = linkedDirectory.getFather ();

                        father.deleteChild (linkedDirectory);
                        directoryRepo.save (father);
                    }

                    //удаление всех детей
                    if(linkedDirectory.getChildren ().size () != 0){
                        Set<LinkedDirectory> children = linkedDirectory.getChildren ();

                        children.forEach (item-> item.setFather (null));
                        linkedDirectory.getChildren ().clear ();

                        children.forEach (item-> {
                            //удаление тега из всех товаров
                            item.getProducts ().forEach (
                                    product -> {
                                        product.deleteDirectory (item);
                                        productRepo.save (product);
                                    }
                            );

                            directoryRepo.delete (item);
                        });
                    }

                    directoryRepo.delete (linkedDirectory);
                }
        );
    }

    /**
     * Проверить что у директории не исчезла связь с другими директориями
     *
     * @param directory директорию которую нужно проверить
     * @param directoryRepo ропизиторий для доступа к БД
     */
    public void checkDirectory (LinkedDirectory directory, LinkedDirectoryRepo directoryRepo) {
        //проверить что к директории привязанно 0 товаров
        if(directory.getProductsCount () == 0){
            /*значит у директории больше нет привязанных товаров

            это значит что больше нет товаров которые могут
            связать данную директорию с другими

            это значит что нужно удалить свзь между
            связанными директориями и директорией*/

            //удалить связь директории с привязанными директориями
            System.out.println ("directory не имеет привязаных продуктов, " +
                    "удалить связи directory с directory.getRelatedDirectories ()");


            directory.getRelatedDirectories ().forEach (
                    relatedDirectory -> {
                        //удалить связь привязаной директории relatedDirectory
                        //с директорией directory
                        System.out.println ("удалить directory с relatedDirectory");
                        System.out.println ("relatedDirectory = "+relatedDirectory.getName ());
                        System.out.println ("directory = "+directory.getName ());

                        relatedDirectory.getRelatedDirectories ().remove (directory);
                        relatedDirectory.getRelatedDirectoryIds ().remove (directory.getId ());

                        //обновить привязаную директорию в БД
                        directoryRepo.save (relatedDirectory);
                    }
            );

            //очистить список привзаных директорий relatedDirectory
            //к директории directory
            directory.getRelatedDirectories ().clear ();
            //очистить список их id
            directory.getRelatedDirectoryIds ().clear ();

        } else {
            //значит у директории есть другие привязанные товары
            System.out.println ("directory имеет ("
                    +directory.getProductsCount ()+") привязаных продуктов");

            //создать копию массива привязанных в директории директорий
            //цель: избежать появления ошибки во время удаления элементов
            final Set<LinkedDirectory>
                    directoryRelatedDirectoriesCopy = new HashSet<LinkedDirectory> () {{
                addAll (directory.getRelatedDirectories ());
            }};


            directoryRelatedDirectoriesCopy.forEach (
                    relatedDirectory -> {

                        /*переменная которая отвечает за результат
                        встречаеться ли хоть один раз привязаная директория
                        relatedDirectory в продукте product директории directory

                        (перефразировал) есть ли еще один товар product
                        который связывает directory и relatedDirectory*/
                        boolean isRelatedDirectoryInDirectoryProduct = false;

                        for (Product product1: directory.getProducts ()
                        ) {
                            //встречаеться ли хоть один раз привязаная директория relatedDirectory
                            //в продукте product директории directory
                            if(product1.getDirectories ().contains (relatedDirectory)){
                                isRelatedDirectoryInDirectoryProduct = true;
                                break;
                            }
                        }

                        //встречаеться ли хоть один раз привязаная директория relatedDirectory
                        //в продукте product директории directory
                        if (isRelatedDirectoryInDirectoryProduct) {
                            //значит что не нужно удалять связи
                            System.out.println ("Да встречаеться relatedDirectory " +
                                    "в directory.getProducts ().product1.getDirectories");
                        } else {
                            //одиночная очистка

                            /*значит что нужно удалить связи между relatedDirectory
                            и directory это тот случай когда у relatedDirectory
                            и directory нет общих продуктов

                            чтобы смоделировать данную ситуацию нужно создать продук с двумя директориями
                            и продукт с одной из директорий
                            */
                            System.out.println ("Нет, не встречаеться relatedDirectory " +
                                    "в directory.getProducts ().product1.getDirectories");

                            System.out.println ("relatedDirectory = "+relatedDirectory);
                            System.out.println ("directory = "+directory);

                            System.out.println ("удаляем directory с relatedDirectory");

                            //удалить directoryToDelete с oldNeededDirectoryId
                            relatedDirectory.getRelatedDirectories ().remove (directory);
                            relatedDirectory.getRelatedDirectoryIds ().remove (directory.getId ());

                            System.out.println ("//удаляем relatedDirectory с directory");
                            //удалить oldNeededDirectoryId с directoryToDelete
                            directory.getRelatedDirectories ().remove (relatedDirectory);
                            directory.getRelatedDirectoryIds ().remove (relatedDirectory.getId ());

                            //обновить relatedDirectory в бд
                            directoryRepo.save (relatedDirectory);
                        }

                    }
            );

        }
    }

    /**
     * Добавить директории из БД к товару
     *
     * @param directories директории
     * @param product товар
     *
     * @return товар с директориями
     */
    public Product addDirectoriesToProduct (Set<LinkedDirectory> directories, Product product) {
        //очистить список директорий товара
        product.getDirectories ().clear ();
        //добавить к товару директории с БД
        directories.forEach (directory -> {
            //добавить директорию товару
            product.addDirectory (directory);
        });

        return product;
    }

    /**
     * Добавить директории из БД к товару
     *
     * @param directories директории
     * @param product товар
     *
     * @return товар с директориями
     */
    public Product addDirectoriesToProduct1 (Set<LinkedDirectory> directories, Product product) {
        //добавить к товару директории с БД
        directories.forEach (directory -> {
            //добавить директорию товару
            product.addDirectory (directory);
        });

        return product;
    }

    /**
     * Добавить товар в директории
     *
     * @param finalProduct товар с БД
     * @param directories список директорий
     */
    public void addProductToDirectories (Product finalProduct, Set<LinkedDirectory> directories) {
        directories.forEach (directory -> {
            //добавляем продукт
            directory.addProduct (finalProduct);

            //обновляем количество продуктов связных с тегом
            directory.setProductsCount ((long) directory.getProducts ().size ());

            //сохраняем директорию в БД
            directoryRepo.save (directory);
        });
    }

    /**
     * Добавить товар в директории
     *
     * @param productFromDB товар с БД
     * @param directories список директорий
     */
    public void addProductToDirectories1 (Product productFromDB, Set<LinkedDirectory> directories) {
        directories.forEach (directory -> {
            //добавляем продукт
            directory.addProduct (productFromDB);

            //обновляем количество продуктов связных с тегом
            directory.setProductsCount ((long) directory.getProducts ().size ());
        });
    }

    /**
     * Связывает список директорий между собой
     *
     * @param directories список директорий
     */
    public void linkingDirectories (Set<LinkedDirectory> directories) {

        //условие при котором тип директории подходит чтобы ее добавить к другим директориям
        Predicate<LinkedDirectory> isDirectorySuitable = getPredicateForAddDirectoryToOtherDirectory ();

        //список директорий которые выполняют условие
        Set<LinkedDirectory> checkedDirectories = checkDirectories (directories, isDirectorySuitable);

        checkedDirectories.forEach (directory -> {

            //связываем список подходящих директорий с деректорией
            linkingDirectoryToDirectories(directory, checkedDirectories);

            //сохраняем директорию в БД
            directoryRepo.save (directory);
        });
    }

    /**
     * Связывает список директорий между собой
     *
     * @param directories список директорий
     * @param directoryPredicate условие при котором тип директории
     * подходит чтобы ее добавить к другим директориям
     */
    public void linkingDirectorieByPredicate (Set<LinkedDirectory> directories, Predicate<LinkedDirectory> directoryPredicate) {

        //список директорий которые выполняют условие
        Set<LinkedDirectory> checkedDirectories = checkDirectories (directories, directoryPredicate);

        checkedDirectories.forEach (directory -> {

            //связываем список подходящих директорий с деректорией
            linkingDirectoryToDirectories(directory, checkedDirectories);
        });
    }

    public Set<LinkedDirectory> getDirectoriesCopyFromDB (Set<LinkedDirectory> directories) {
        Set<LinkedDirectory> resultSet = new HashSet<> ();

        directories.forEach (directory -> {
                    this.directoryRepo.findById (directory.getId ()).ifPresent (
                            //добавить в resultSet
                            resultSet::add
                    );
                }
        );

        return resultSet;
    }
}
