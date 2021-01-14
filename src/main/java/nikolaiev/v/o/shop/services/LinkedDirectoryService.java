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
            directory = new LinkedDirectory (DirectoryType.CATEGORY_LIST.toString ());

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
        LinkedDirectory father = directoryRepo.getOne (linkedDirectory.getFather ().getId ());

        //устонавливаем контейнер для связанных дерикторий
        linkedDirectory.setRelatedDirectories (new HashSet<> ());

        LinkedDirectory child = linkedDirectory;

        if(father!=null){
            String fatherDirectoryType = father.getDirectoryType ();

            child.setFather (father);

            //проверка что отец CATEGORY_LIST
            if(fatherDirectoryType.equals (DirectoryType.CATEGORY_LIST.toString ())){

                child.setDirectoryType (DirectoryType.CATEGORY.toString ());
                child = directoryRepo.save (child);

                //добавляем в него дочернюю директорию Бренды
                LinkedDirectory brandList = new LinkedDirectory (

                        DirectoryType.BRAND_LIST.toString ()
                );
                brandList.setFather (child);
                brandList = directoryRepo.save (brandList);

                //добавляем в него дочернюю директорию Параметры
                LinkedDirectory parameterList = new LinkedDirectory (
                        DirectoryType.PARAMETER_LIST.toString ()
                );
                parameterList.setFather (child);
                parameterList = directoryRepo.save (parameterList);

                child.addChild (brandList);
                child.addChild (parameterList);
            }

            //проверка что отец BRAND_LIST
            if(fatherDirectoryType.equals (DirectoryType.BRAND_LIST.toString ())){
                child.setDirectoryType (DirectoryType.BRAND.toString ());
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
            return directoryRepo.save (linkedDirectory);
        }
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
}
