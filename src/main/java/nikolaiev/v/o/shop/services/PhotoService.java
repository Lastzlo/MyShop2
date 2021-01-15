package nikolaiev.v.o.shop.services;

import nikolaiev.v.o.shop.domain.Photo;
import nikolaiev.v.o.shop.repos.PhotoRepo;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepo photoRepo;

    //путь для загрузки изображений на сервер
    @Value("${upload.picture.path}")
    private String uploadPath;

    //путь для выгрузки изображений с сервера
    @Value("${unload.picture.path}")
    private String unloadPath;

    //типы фото которые разрешено загружать
    private Set<String> allowedFileTypes = new HashSet<String> (){{
        add ("image/png");
        add ("image/jpeg");
    }};

    /**
     * Сохраняет полученые фото в БД и на сервере
     *
     * @param files список файлов
     * @return список файлов которые записаны в БД
     */
    public Set<Photo> saveFiles (Optional<MultipartFile[]> files) {
        //список фото которые записаны в БД
        Set <Photo> saveFileSet = new HashSet<> ();

        //получаем список файлов
        final MultipartFile[] multipartFiles = files.get ();

        File uploadDir = new File (uploadPath);
        //проверяет создана ли папка для хранения
        if(!uploadDir.exists ()){
            uploadDir.mkdir ();
        }

        //рандомный индентификатор
        String uuidFile = UUID.randomUUID ().toString ();
        //получаем путь
        Path rootLocation = Paths.get(String.valueOf(uploadDir));

        for (MultipartFile multipartFile: multipartFiles
        ) {
            //тип файла
            final String fileContentType = multipartFile.getContentType ();
            System.out.println ("fileContentType = "+fileContentType);

            //проверка что тип multipartFile файла, есть в списке allowedFileTypes
            if(allowedFileTypes.contains (fileContentType)){

                //дополняем имя файла чтобы не возникало коллизий
                String resultFilename = uuidFile + "." + multipartFile.getOriginalFilename ();

                //Сохранение файла на сервере
                try {
                    try (InputStream inputStream = multipartFile.getInputStream()) {
                        Files.copy(inputStream, rootLocation.resolve(resultFilename),
                                StandardCopyOption.REPLACE_EXISTING);
                    }

                    //путь по которому можна получить файл
                    String src = unloadPath + resultFilename;

                    Photo photo = new Photo (resultFilename, src);
                    //сохраняем информацию о файле в БД
                    final Photo savePhoto = photoRepo.save (photo);

                    //добавляем в список файлов которые записаны в БД
                    saveFileSet.add (savePhoto);
                }
                catch (IOException e) {
                    System.out.println ("Failed to store file ");
                }
            }
        }

        return saveFileSet;
    }

}
