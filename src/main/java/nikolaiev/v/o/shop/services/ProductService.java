package nikolaiev.v.o.shop.services;

import nikolaiev.v.o.shop.domain.DirectoryType;
import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Photo;
import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.repos.LinkedDirectoryRepo;
import nikolaiev.v.o.shop.repos.PhotoRepo;
import nikolaiev.v.o.shop.repos.ProductRepo;
import com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

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

    //сохраняем товар с картинками
    public Product saveProduct (Product product, Optional<MultipartFile[]> files) {
        //сюда можно добавить проверку полей продукта

        /*//добавляем фото к товару
        addPhotosToProduct (product, files);*/

        //добавляем теги(директории) к товару
        addTegsToProduct (product);

        //устонавливаем время добавления
        product.setCreationDate (LocalDateTime.now ());

        //сохраняем товар в бд
        final Product finalProduct = productRepo.save(product);

        //добавляем товар из бд к тегам(директориям)
        addProductToDirectories (finalProduct);

        return finalProduct;

    }

    //добавляем фото к товару
    private void addPhotosToProduct (Product product, Optional<MultipartFile[]> files) {
        //сохраняем файлы на сервере
        Set<Photo> photosSet = saveFilesOnServer (product, files);

        //добавляем к продукту все фото
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

    //добавляем теги(директории) к товару
    private void addTegsToProduct (Product product) {
        //записываем спикос тегов которые пришли с товаром
        Set<LinkedDirectory> directories = new HashSet<LinkedDirectory>(){{addAll (product.getDirectories ());}};
        //очищаем список тегов
        product.getDirectories ().clear ();
        //проходим все теги которые пришли с товаром
        directories.forEach (directory -> {
                    //ести тег есть в бд
                    this.directoryRepo.findById (directory.getId ()).ifPresent (
                            directoryFromDb -> {
                                //добавляем к тег товару
                                product.addDirectory (directoryFromDb);
                            }
                    );
                }
        );

    }

    //добавляем товар из бд к тегам(директориям)
    private void addProductToDirectories (Product finalProduct) {
        finalProduct.getDirectories ().forEach (directory -> {
            //добавляем продукт
            directory.addProduct (finalProduct);

            //обновляем количество продуктов связных с тегом
            directory.setProductsCount ((long) directory.getProducts ().size ());

            //проверка что проверям что тип директории PARAMETER или BRAND
            if(
                    directory.getDirectoryType ().equals (DirectoryType.PARAMETER.toString ())
                            || directory.getDirectoryType ().equals (DirectoryType.BRAND.toString ())
            ){
                // связываем список директорий с деректорией
                linkingDirectories (finalProduct.getDirectories (), directory);
            }

            //сохраняем тег в БД
            directoryRepo.save (directory);

        });
    }

    //обновляет информацию о товаре и картинки

    public Product updateProduct (Product product, Optional<MultipartFile[]> files) {
        //проверка что такой товар есть в бд
        Optional<Product> optionalProductFromDb = productRepo.findById (product.getId ());

        if(optionalProductFromDb.isPresent ()){
            //товар из бд
            final Product productFromDb = optionalProductFromDb.get ();

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

            //сохраняем продукт в бд
            return productRepo.save (productFromDb);
        } else {
            //нужно обработать ответ если вдруг небыло такого продукта
            return null;
        }
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

            //добавляем связи в новых тегов newdirectorys с новыми newdirectorys, старыми тегами oldNeededDirectories, а также с продуктами
            newdirectorys.forEach (directory -> {
                //связываем только те директории у которых тип PARAMETER или BRAND
                if(
                        directory.getDirectoryType ().equals (DirectoryType.PARAMETER.toString ())
                                || directory.getDirectoryType ().equals (DirectoryType.BRAND.toString ())
                ){
                    //связываем список старых новых тегов newdirectorys, с тегом newdirectorys
                    linkingDirectories (newdirectorys, directory);

                    //связываем список старых тегов oldNeededDirectories, с тегом newdirectorys
                    linkingDirectories (oldNeededDirectories, directory);
                }


                //добавляем продукт
                directory.addProduct (productFromDb);
                //добавляем к товару тег
                productFromDb.addDirectory (directory);
                //обновляем количество продуктов связных с тегом
                directory.setProductsCount ((long) directory.getProducts ().size ());


                //обновляем directory в БД
                directoryRepo.save (directory);
            });

            //добавляем связи в старыми тегов oldNeededDirectories с новыми тегами newdirectorys
            oldNeededDirectories.forEach (directory -> {
                //связываем список новых тегов newdirectorys, с тегом oldNeededDirectories
                linkingDirectories (newdirectorys, directory);

                //обновляем directory в БД
                directoryRepo.save (directory);
            });

//            System.out.println ("finish work with directory!");
        }

    }

    /**
     * Связать список директорий, с директорией
     *
     * @param inputDirectories список директорий
     * @param directory директория
     */
    private void linkingDirectories (Set<LinkedDirectory> inputDirectories, LinkedDirectory directory) {
        inputDirectories.forEach (inputDirectory -> {
            //проверям что директория к которой хотим связать не такая же, а также ее тип PARAMETER или BRAND
            if (
                    directory != inputDirectory
                            && (
                            inputDirectory.getDirectoryType ()
                                    .equals (DirectoryType.PARAMETER.toString ())
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
    private void dislinkDirectories (Set<LinkedDirectory> directorysToDeleteFromProduct, Set<LinkedDirectory> oldNeededDirectories) {
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

    /**
     * Удалить товар с БД
     *
     * @param id идентификатор товара в бд
     */
    public void deleteProduct (Long id) {
        //проверка есть ли такой товар в БД
        productRepo.findById (id).ifPresent (
                product -> {
                    //удаляем связь директорий с продуктом
                    product.getDirectories ().forEach (
                            directory -> {
                                //удалить связь директории с продуктом
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

                    //заготовка для удаления фото продукта
                    /*//удаляем связь между продуктом и фотографиями

                    //создать копию массива фотографий продукта
                    //цель: после того как уберуться связи с продуктом, необходимо удалить фотографии
                    final Set<Photo>
                            productPhotosCopy = new HashSet<Photo> () {{
                        addAll (.getPhotos ());
                    }};

                    //удалить свяproductзи
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
