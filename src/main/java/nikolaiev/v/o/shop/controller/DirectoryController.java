package nikolaiev.v.o.shop.controller;

import nikolaiev.v.o.shop.domain.LinkedDirectory;
import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.domain.Views;
import nikolaiev.v.o.shop.services.LinkedDirectoryService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("directory")
public class DirectoryController {

    @Autowired
    private LinkedDirectoryService directoryService;

    @GetMapping("getCore")
    @JsonView(Views.FullLinkedDirectory.class)
    public LinkedDirectory getCore(){
        return directoryService.getCore();
    }

    @GetMapping("getProductByDirectoryId/{id}")
    @JsonView(Views.FullMessage.class)
    public Set<Product> getProductsByLinkedDirectoryId(@PathVariable String id){
        return directoryService.getProductsByLinkedDirectoryId (id);
    }

    @GetMapping("{id}")
    @JsonView(Views.FullLinkedDirectory.class)
    public LinkedDirectory getOne(@PathVariable String id){
        return directoryService.getOne(id);
    }

    @PostMapping
    @JsonView(Views.FullLinkedDirectory.class)
    public LinkedDirectory create(
            @RequestBody LinkedDirectory linkedDirectory
    ){
        return directoryService.create (linkedDirectory);
    }

    @PutMapping("{id}")
    @JsonView(Views.FullLinkedDirectory.class)
    public LinkedDirectory update(
            @PathVariable String id,
            @RequestBody LinkedDirectory directory
    ){
        return directoryService.update (id, directory);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        directoryService.delete (id);
    }

}
