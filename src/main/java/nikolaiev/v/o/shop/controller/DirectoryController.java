package nikolaiev.v.o.shop.controller;

import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.domain.Views;
import nikolaiev.v.o.shop.services.LinkedDirectoryService;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@RestController
@RequestMapping("directory")
public class DirectoryController {
    private static final Logger logger = LogManager.getLogger(DirectoryController.class);

    @Autowired
    private LinkedDirectoryService directoryService;

    @GetMapping("/getCore")
    @JsonView(Views.FullLinkedDirectory.class)
    public ResponseEntity<LinkedDirectory> getCore(){
        logger.info("DirectoryController.getCore () is executed");
        try {
            return ResponseEntity.ok()
                    .location((new URI ("/directory/getCore")))
                    .body(directoryService.getCore());
        } catch (URISyntaxException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("getProductByDirectoryId/{id}")
    @JsonView(Views.FullMessage.class)
    public Set<Product> getProductsByLinkedDirectoryId(@PathVariable String id){
        return directoryService.getProductsByLinkedDirectoryId (id);
    }

    @GetMapping("{id}")
    @JsonView(Views.FullLinkedDirectory.class)
    public LinkedDirectory getOne(@PathVariable String id){
        logger.info("DirectoryController.getOne() is executed");
        logger.info("Received id: " +
                "id: " + id);
        return directoryService.getOne(id);
    }

    @PostMapping
    @JsonView(Views.FullLinkedDirectory.class)
    public LinkedDirectory create(
            @RequestBody LinkedDirectory linkedDirectory
    ){
        logger.info("DirectoryController.create() is executed");
        logger.info("Received LinkedDirectory: " +
                "name: " + linkedDirectory.getName ());
        return directoryService.create (linkedDirectory);
    }

    @PutMapping("{id}")
    @JsonView(Views.FullLinkedDirectory.class)
    public LinkedDirectory update(
            @PathVariable String id,
            @RequestBody LinkedDirectory directory
    ){
        logger.info("DirectoryController.update() is executed");
        logger.info("Received id: " +
                "id: " + id);
        logger.info("Received LinkedDirectory: " +
                "name: " + directory.getName ());
        return directoryService.update (id, directory);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        logger.info("DirectoryController.delete() is executed");
        logger.info("Received id: " +
                "id: " + id);
        directoryService.delete (id);
    }

}
