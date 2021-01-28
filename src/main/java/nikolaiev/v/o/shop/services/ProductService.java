package nikolaiev.v.o.shop.services;

import com.google.common.collect.Sets;
import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Photo;
import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.repos.LinkedDirectoryRepo;
import nikolaiev.v.o.shop.repos.PhotoRepo;
import nikolaiev.v.o.shop.repos.ProductRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static nikolaiev.v.o.shop.Predicates.LinkedDirectoryPredicates.getDirectoryPredicateForAddDirectoryToProduct;
import static nikolaiev.v.o.shop.Predicates.LinkedDirectoryPredicates.getPredicateForAddDirectoryToOtherDirectory;
import static nikolaiev.v.o.shop.util.LinkedDirectoryUtils.*;

@Service
public class ProductService {

    @Autowired
    private LinkedDirectoryRepo directoryRepo;
    @Autowired
    private LinkedDirectoryService directoryService;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private PhotoRepo photoRepo;

    //Сервис для управляения фото
    @Autowired
    private PhotoService photoService;

    //путь для загрузки изображений на сервер
    /*@Value("${upload.picture.path}")
    private String uploadPath;*/

    //путь для выгрузки изображений с сервера
    @Value("${unload.picture.path}")
    private String unloadPath;

    public List<Product> getAllProducts () {
        return productRepo.findAll();
    }

    public Product getProduct (Long id) {
        return productRepo.getOne(id);
    }

    //сохранить товар директориями и файлами
    public Product saveProduct1 (Product product, Optional<MultipartFile[]> files) {

        //установить время добавления
        product.setCreationDate (LocalDateTime.now ());

        //список директорий товара
        final Set<LinkedDirectory> productDirectories = new HashSet<LinkedDirectory> (){{
            addAll (product.getDirectories ());
        }};

        //очистить список директорий товара
        product.getDirectories ().clear ();

        //сохранить товар в БД
        final Product productFromDB = productRepo.save(product);

        /*//добавляем фото к товару
        addPhotosToProduct (product, files);*/

        //получить копию директорий с БД
        final Set<LinkedDirectory> productDirectoriesFromDB = directoryService.getDirectoriesCopyFromDB (productDirectories);

        //условие которое должна выполнить директория чтобы ее можно было добавить к товару
        final Predicate<LinkedDirectory> predicateForAddDirectoryToProduct = getDirectoryPredicateForAddDirectoryToProduct ();

        //список директорий которые выполняют условие
        Set<LinkedDirectory> checkedDirectories = checkDirectories (productDirectoriesFromDB, predicateForAddDirectoryToProduct);

        //сценарий при котором нужно связать товар без директорий со списком директорий
        scenaryIfProductFromDBHaveDirectories (productFromDB, checkedDirectories);

        //сохранить товар в БД
        final Product finalProduct = productRepo.save(productFromDB);

        return finalProduct;
    }

    public void scenaryIfProductFromDBHaveDirectories (Product productFromDB, Set<LinkedDirectory> checkedDirectories) {
        //условие при котором тип директории подходит чтобы ее добавить к другим директориям
        final Predicate<LinkedDirectory> PredicateForAddDirectoryToDirectory = getPredicateForAddDirectoryToOtherDirectory ();

        //связать директории между собой
        directoryService.linkingDirectorieByPredicate(checkedDirectories,PredicateForAddDirectoryToDirectory);

        //добавляем товар из бд к тегам(директориям)
        directoryService.addProductToDirectories1 (productFromDB, checkedDirectories);

        //сохранить checkedDirectories в БД
        checkedDirectories.forEach (directory -> directoryService.update(directory));


        //добавить директории к товару c БД
        directoryService.addDirectoriesToProduct1(checkedDirectories, productFromDB);


        //сохранить товар в бд
        productRepo.save(productFromDB);
    }


    //добавляем фото к товару
    private void addPhotosToProduct (Product product, Optional<MultipartFile[]> files) {
        //сохраняем файлы на сервере
        Set<Photo> photosSet = saveFilesOnServer (product, files);

        //добавляем к товару все фото
        photosSet.forEach (product::addPhoto);

    }

    //сохраняем файлы на сервере
    private Set<Photo> saveFilesOnServer (Product product, Optional<MultipartFile[]> files){
        //сохраняем файлы на сервере
        Set<Photo> photosSet = new HashSet<> ();

        /*if(files.isPresent ()){
            final MultipartFile[] multipartFiles = files.get ();

            File uploadDir = new File (uploadPath);

            //если загрузочная директория не найдена то создаеться
            if(!uploadDir.exists ()){
                uploadDir.mkdir ();
            }

            //инициализирует массив для фото
            product.setPhotos (new HashSet<> ());

            //рандомный индентификатор
            String uuidFile = UUID.randomUUID ().toString ();

            //получаем путь (можно и заранье)
            Path rootLocation = Paths.get(String.valueOf(uploadDir));
            //System.out.println("rootLocation = "+rootLocation.toString());

            for (MultipartFile multipartFile: multipartFiles
            ) {
                //дополняем имя рандомный индентификатором чтобы не возникало коллизий в именах файлов
                String resultFilename = uuidFile + "." + multipartFile.getOriginalFilename ();

                try {
                    try (InputStream inputStream = multipartFile.getInputStream()) {
                        Files.copy(inputStream, rootLocation.resolve(resultFilename),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                catch (IOException e) {
                    System.out.println ("Failed to store file ");
                }

                String src = "/img/" + resultFilename;

                //сохраняем метаданные в новый обьект
                //сохраняем информацию в БД
                Photo photo = photoRepo.save (new Photo (resultFilename, src));

                photosSet.add (photo);
            }

        }*/
        return photosSet;
    }



    //обновляет информацию о товаре и картинки
    public Product updateProduct (Product product, Optional<MultipartFile[]> files) {
        //проверка что такой товар есть в бд
        Optional<Product> optionalProductFromDb = productRepo.findById (product.getId ());

        if(optionalProductFromDb.isPresent ()){
            //товар из бд
            final Product productFromDb = optionalProductFromDb.get ();

            //обновить информацию о товаре
            //скопировать заданые значения из product в productFromDb
            BeanUtils.copyProperties (product, productFromDb, "id", "photos", "photoToDelete", "directories","creationDate");

            //обновляем теги товара
            updateProductDirectories (product, productFromDb);

            //часть отвечает за обновление фотографий привязанных к товару
            /*//проверяет есть ли ненужные фото
            if(!product.getPhotoToDelete ().isEmpty ()){
                //удаляем фото товара которые не используються
                product.getPhotoToDelete ().forEach (
                        photo -> {
                            photoRepo.findById (photo.getId ()).ifPresent (
                                    item -> {
                                        //удаление фото с товара
                                        productFromDb.deletePhoto(item);
                                        photoRepo.delete (item);

                                        //удаление фото с хранилища
                                        new File (uploadPath + "/" + item.getName ()).delete ();
                                    }
                            );
                        }
                );
            }

            //проверяет есть ли новые фото к товару
            if(files.isPresent ()){
                //Получаем список сохраненных фото
                Set<Photo> savePhotoSet = photoService.saveFiles (files);

                //записываем в товар список фото
                savePhotoSet.forEach (productFromDb::addPhoto);
            }*/

            //сохранить товар в БД
            return productRepo.save (productFromDb);
        } else {
            //вернуть тот же товар если не было такого в бд
            return product;
        }
    }

    //обновляет информацию о товаре и картинки
    public Product updateProduct1 (Product product, Optional<MultipartFile[]> files) {
        //получить копию товара с БД
        Optional<Product> optionalProductFromDb = productRepo.findById (product.getId ());

        //проверка что товар существует в БД
        if(optionalProductFromDb.isPresent ()){
            //копия товара с БД
            final Product productFromDb = optionalProductFromDb.get ();

            //робота с информацией о товаре
            updateInfoAboutProduct (product, productFromDb);

            //робота с директориями
            updateProductDirectories1 (product, productFromDb);

            //робота с файлами
            //часть отвечает за обновление фотографий привязанных к товару
            /*//проверяет есть ли ненужные фото
            if(!product.getPhotoToDelete ().isEmpty ()){
                //удаляем фото товара которые не используються
                product.getPhotoToDelete ().forEach (
                        photo -> {
                            photoRepo.findById (photo.getId ()).ifPresent (
                                    item -> {
                                        //удаление фото с товара
                                        productFromDb.deletePhoto(item);
                                        photoRepo.delete (item);

                                        //удаление фото с хранилища
                                        new File (uploadPath + "/" + item.getName ()).delete ();
                                    }
                            );
                        }
                );
            }

            //проверяет есть ли новые фото к товару
            if(files.isPresent ()){
                //Получаем список сохраненных фото
                Set<Photo> savePhotoSet = photoService.saveFiles (files);

                //записываем в товар список фото
                savePhotoSet.forEach (productFromDb::addPhoto);
            }*/

            //сохранить товар в БД
            return productRepo.save (productFromDb);
        } else {
            //вернуть тот же товар если не было такого в БД
            return product;
        }
    }

    public void updateProductDirectories1 (Product product, Product productFromDb) {
        //робота с директориями

        //список директорий полученого товара
        Set<LinkedDirectory> productDirectories = new HashSet<LinkedDirectory> (){{
            addAll (product.getDirectories ());
        }};

        //получить копию директорий с БД
        productDirectories = directoryService.getDirectoriesCopyFromDB (productDirectories);

        //условие которое должна выполнить директория чтобы ее можно было добавить к товару
        final Predicate<LinkedDirectory> predicateForAddDirectoryToProduct =
                getDirectoryPredicateForAddDirectoryToProduct ();

        //список директорий полученого товара которые выполняют условие
        final Set<LinkedDirectory> checkedProductDirectories =
                checkDirectories (productDirectories, predicateForAddDirectoryToProduct);

        if(checkedProductDirectories.isEmpty ()){

            //убрать связть директорий и товара
            dislinkProductFromDirectories (productFromDb, productFromDb.getDirectories ());

            //сохранить директории в БД
            productFromDb.getDirectories ().forEach (directory -> directoryService.update (directory));

            //очистить список директорий
            productFromDb.getDirectories ().clear ();

            //сохранить товар в БД
            productRepo.save (productFromDb);

        } else {
            if (productFromDb.getDirectories ().isEmpty ()){

                //сценарий при котором нужно связать товар без директорий со списком директорий
                scenaryIfProductFromDBHaveDirectories (productFromDb, checkedProductDirectories);

            } else {

                //сценарий при котором нужно связать товар с директориями со списком директорий
                scenaryIfProductFromDBHaveDirectories (productFromDb, predicateForAddDirectoryToProduct, checkedProductDirectories);

            }
        }




    }

    public void scenaryIfProductFromDBHaveDirectories (Product productFromDb, Predicate<LinkedDirectory> predicateForAddDirectoryToProduct, Set<LinkedDirectory> checkedProductDirectories) {
        //список директорий товара с БД
        final Set<LinkedDirectory> productFromDBDirectories = new HashSet<LinkedDirectory> (){{
            addAll (productFromDb.getDirectories ());
        }};

        //сравнить список директорий товара с БД - productFromDBDirectories
        //и список директорий полученого товара
        //которые выполняют условие - checkedProductDirectories
        if (productFromDBDirectories.equals (checkedProductDirectories)){

            //списки директорий равны, значит не нужно обновлять связи
            System.out.println ("productFromDBDirectories" +
                    ".equals (checkedProductDirectories)");
        } else {

            //они равны, значи нужно выполнить эти пункты:

            //1)список директорий с которыми нужно убрать связть
            //это
            //директории которые есть в productFromDBDirectories
            //но нет в checkedProductDirectories
            final Set<LinkedDirectory> directoriesToDislink =
                    Sets.difference(productFromDBDirectories, checkedProductDirectories);

            //убрать связи товара и ненужных директорий

            //2)Убрать связи productFromDb
            // с не нужными директориями directoriesToDislink
            dislinkDirectoriesFromProduct (directoriesToDislink, productFromDb);

            //сохранить товар в БД
            productRepo.save (productFromDb);

            //3)Убрать связи directoriesToDislink
            // с товаром productFromDb
            dislinkProductFromDirectories (productFromDb, directoriesToDislink);


            //4)сохранить директории directoriesToDislink в БД
            directoriesToDislink
                    .forEach (directory ->directoryService.update (directory));


            //убрать связи с директорий

            //5)Убрать связи productFromDBDirectories
            // с не нужными директориями directoriesToDislink

            //выделяем только те директории которые могу поддаваться связи

            //условие при котором тип директории подходит чтобы ее добавить к другим директориям
            final Predicate<LinkedDirectory> PredicateForAddDirectoryToDirectory =
                    getPredicateForAddDirectoryToOtherDirectory ();

            //список директорий productFromDBDirectories
            // которые выполняют условие PredicateForAddDirectoryToDirectory
            final Set<LinkedDirectory> checkedProductFromDBDirectories =
                    checkDirectories (productFromDBDirectories, predicateForAddDirectoryToProduct);

            //список директорий directoriesToDislink
            // которые выполняют условие PredicateForAddDirectoryToDirectory
            final Set<LinkedDirectory> checkedDirectoriesToDislink =
                    checkDirectories (directoriesToDislink, predicateForAddDirectoryToProduct);

            //удалить связть директорий - checkedProductFromDBDirectories
            // со список директорий с которыми нужно
            // убрать связть -  checkedDirectoriesToDislink
            // не смотря на то что их может связывать productFromDb
            dislinkDirectoriesButProduct(checkedProductFromDBDirectories, checkedDirectoriesToDislink, productFromDb);

            //6) сохранить директории checkedDirectoriesToDislink в БД
            checkedDirectoriesToDislink
                    .forEach (directory ->directoryService.update (directory));

            //7) сохранить директории checkedProductFromDBDirectories в БД
            checkedProductFromDBDirectories
                    .forEach (directory ->directoryService.update (directory));


            //8) список директорий checkedProductFromDBDirectories без checkedDirectoriesToDislink
            final Set<LinkedDirectory> neededCheckedProductFromDBDirectories =
                    Sets.difference(checkedProductFromDBDirectories, checkedDirectoriesToDislink);


            //связать товар с новыми директориями

            //9) список директорий с которыми нужно связать товар productFromDb
            final Set<LinkedDirectory> directoriesToAddToProduct =
                    Sets.difference(checkedProductDirectories, productFromDBDirectories);

            //10) связать товар productFromDb с directoriesToAddToProduct
            directoryService
                    .addDirectoriesToProduct1(directoriesToAddToProduct, productFromDb);

            //11) сохранить товар в БД
            productRepo.save (productFromDb);


            //12) сохранить директории directoriesToAddToProduct в БД
            directoriesToAddToProduct
                    .forEach (directory ->directoryService.update (directory));

            //связать директории с новыми директориями

            //13) cвязать директории  neededCheckedProductFromDBDirectories
            //с directoriesToAddToProduct

            //список директорий directoriesToAddToProduct
            // которые выполняют условие PredicateForAddDirectoryToDirectory
            final Set<LinkedDirectory> checkedDirectoriesToAddToProduct =
                    checkDirectories (directoriesToAddToProduct, predicateForAddDirectoryToProduct);

            //проверить что списки директорий которые нужно связать не равны
            if(checkedDirectoriesToAddToProduct
                    .equals (neededCheckedProductFromDBDirectories)){
                //Списки равны, ничего связывать не нужно
                System.out.println ("checkedDirectoriesToAddToProduct" +
                        ".equals (neededCheckedProductFromDBDirectories)");
            } else {
                //Списки не равны

                //cвязать директории  checkedDirectoriesToAddToProduct
                //с neededCheckedProductFromDBDirectories
                //и с checkedDirectoriesToAddToProduct
                checkedDirectoriesToAddToProduct.forEach (directory -> {
                    linkingDirectoryToDirectories (
                            directory, neededCheckedProductFromDBDirectories);

                    linkingDirectoryToDirectories (
                            directory, checkedDirectoriesToAddToProduct);
                });

                //cвязать директории  neededCheckedProductFromDBDirectories
                //с checkedDirectoriesToAddToProduct
                neededCheckedProductFromDBDirectories.forEach (directory -> {
                    linkingDirectoryToDirectories (
                            directory, checkedDirectoriesToAddToProduct);
                });

                //14) сохранить директории neededCheckedProductFromDBDirectories в БД
                neededCheckedProductFromDBDirectories
                        .forEach (directory ->directoryService.update (directory));

                //15) сохранить директории checkedDirectoriesToAddToProduct в БД
                checkedDirectoriesToAddToProduct
                        .forEach (directory ->directoryService.update (directory));

            }

        }
    }

    /**
     * убрать связи товара со списком директорий
     *
     * @param product товар
     * @param directories список директорий
     */
    public static void dislinkProductFromDirectories (Product product, Set<LinkedDirectory> directories) {
        directories.forEach (directory -> {
            //удалить товара с директории
            directory.deleteProduct (product);
            directory.setProductsCount ((long) directory.getProducts ().size ());
        });
    }

    /**
     * убрать связи директорий с товаром
     *
     * @param directoriesToDislink список директорий
     * @param product товар
     */
    public static void dislinkDirectoriesFromProduct (Set<LinkedDirectory> directoriesToDislink, Product product) {
        directoriesToDislink.forEach (directoryToDislink -> {
            //убрать связи директориb с товара
            product.deleteDirectory (directoryToDislink);
        });
    }


    public void updateInfoAboutProduct (Product product, Product productFromDb) {
        //обновить информацию о товаре


        //установить имя товара
        productFromDb.setProductName (product.getProductName ());
        //установить описание товара
        productFromDb.setProductDiscription (product.getProductDiscription ());

        //сохранить товар в БД
        productRepo.save (productFromDb);
    }

    /**
     * Теги полученого товара устонавливает товару который хранился в бд
     *
     * @param product полученый товар
     * @param productFromDb товар который хранился в бд
     */
    private void updateProductDirectories (Product product, Product productFromDb) {
        //теги товара который записан в бд
        Set<LinkedDirectory> olddirectorysFromDb = new HashSet<LinkedDirectory> (){{
            addAll (productFromDb.getDirectories ());
        }};

        //теги полученого товара
        Set<LinkedDirectory> recivedDirectories = new HashSet<>();
        product.getDirectories ().forEach (directory -> {
                    this.directoryRepo.findById (directory.getId ()).ifPresent (
                            directoryFromDb -> recivedDirectories.add (directoryFromDb)
                    );
                }
        );

        //проверка что olddirectorysFromDb и recivedDirectories равны
        if(olddirectorysFromDb.equals (recivedDirectories)){
            //если нет отличий то пропускаем
//            System.out.println ("olddirectorysFromDb.equals (recivedDirectories)");
        }else {
            //@param directorysToDeleteFromProduct это теги которых нет в recivedDirectories но есть в olddirectorysFromDb
            final Set<LinkedDirectory> directorysToDeleteFromProduct = Sets.difference(olddirectorysFromDb, recivedDirectories);
//            System.out.println ("directorysToDeleteFromProduct:");

            //удаляем связь товара с тегами directorysToDeleteFromProduct
            directorysToDeleteFromProduct.forEach (
                    directory-> {
//                        System.out.println ("directoryId = "+directory.getId ()+" directoryName = "+directory.getName ());

                        //удаляем с товара лишние теги
                        productFromDb.deleteDirectory (directory);

                        //уменьшем количество привязаных товаров к тегу
                        directory.deleteProduct (productFromDb);
                        directory.setProductsCount ((long) directory.getProducts ().size ());

                    }
            );

            //список актуальных, нужных тегов
            Set<LinkedDirectory> oldNeededDirectories = Sets.difference(olddirectorysFromDb, directorysToDeleteFromProduct);

            //убираем у oldNeededDirectories связи с ненужными directorysToDeleteFromProduct тегами
            dislinkDirectories (directorysToDeleteFromProduct, oldNeededDirectories);

            //обновляем directorysToDeleteFromProduct в БД
            directorysToDeleteFromProduct.forEach (directory ->directoryRepo.save (directory));

            //новые теги которые еще не имеют связей, не обработаные теги
            Set<LinkedDirectory> newdirectorys = Sets.difference(recivedDirectories, oldNeededDirectories);

            //добавляем связи в новых тегов newdirectorys с новыми newdirectorys, старыми тегами oldNeededDirectories, а также с товарами
            newdirectorys.forEach (directory -> {
                //связываем только те директории у которых тип PARAMETER или BRAND
                if(
                        directory.getDirectoryType ().equals (DirectoryType.PARAMETER.toString ())
                ){
                    //связываем список старых новых тегов newdirectorys, с тегом newdirectorys
                    linkingDirectoryToDirectories (directory, newdirectorys);

                    //связываем список старых тегов oldNeededDirectories, с тегом newdirectorys
                    linkingDirectoryToDirectories (directory, oldNeededDirectories);
                }

                //добавляем товар
                directory.addProduct (productFromDb);
                //добавляем к товару тег
                productFromDb.addDirectory (directory);
                //обновляем количество товаров связных с тегом
                directory.setProductsCount ((long) directory.getProducts ().size ());


                //обновляем directory в БД
                directoryRepo.save (directory);
            });

            //добавляем связи в старыми тегов oldNeededDirectories с новыми тегами newdirectorys
            oldNeededDirectories.forEach (directory -> {
                //связываем список новых тегов newdirectorys, с тегом oldNeededDirectories
                linkingDirectoryToDirectories (directory, newdirectorys);

                //обновляем directory в БД
                directoryRepo.save (directory);
            });

//            System.out.println ("finish work with directory!");
        }

    }

    /**
     * Удалить товар с БД
     *
     * @param id идентификатор товара в бд
     */
    public void deleteProduct (Long id) {
        //проверка есть ли такой товар в БД
        productRepo.findById (id).ifPresent (
                product -> {
                    //удаляем связь директорий с товаром
                    product.getDirectories ().forEach (
                            directory -> {
                                //удалить связь директории с товаром
                                directory.deleteProduct (product);
                                //обновить счетчик количества привязаных товаров к тегу
                                directory.setProductsCount ((long) directory.getProducts ().size ());

                                //проверить что у директории
                                //не исчезла связь с другими директориями
                                directoryService.checkDirectory (directory, directoryRepo);

                                //обновляем directory в бд
                                directoryRepo.save (directory);
                            }
                    );
                    //очистить список
                    //нет необходимости ведь удаляем product из бд
                    //product.getDirectories ().clear ();

                    //заготовка для удаления фото товара
                    /*//удаляем связь между товаром и фотографиями

                    //создать копию массива фотографий товара
                    //цель: после того как уберуться связи с товаром, необходимо удалить фотографии
                    final Set<Photo>
                            productPhotosCopy = new HashSet<Photo> () {{
                        addAll (.getPhotos ());
                    }};

                    //удалить связи
                    product.getPhotos ().clear ();
                    //зафиксировать в БД
                    productRepo.save (product);

                    //удалить фото
                    productPhotosCopy.forEach (
                            photo -> {
                                photoRepo.findById (photo.getId ()).ifPresent (
                                        item -> {
                                            //удаление фото с БД
                                            photoRepo.delete (item);

                                            //удаление фото с хранилища
                                            String uploadPath="C:/JavaProjects/other/forProjects/myshop1";
                                            new File (uploadPath + "/" + item.getName ()).delete ();
                                        }
                                );
                            }
                    );
                    //очистить список
                    //нет необходимости ведь удаляем product из бд
                    //product.getPhotos ().clear ();*/
                }
        );

        //удалить товар с БД
        productRepo.deleteById(id);
    }


}
