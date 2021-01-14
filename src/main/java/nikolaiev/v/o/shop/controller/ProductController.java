package nikolaiev.v.o.shop.controller;

import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.domain.Views;
import nikolaiev.v.o.shop.services.ProductService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @JsonView(Views.FullMessage.class)
    public List<Product> list(){
        return productService.getAllProducts();
    }

//    @GetMapping("{id}")
//    @JsonView(Views.FullMessage.class)
//    public Product getOne(@PathVariable String id){
//        return productService.getProduct(Long.valueOf(id));
//    }

    /*@PostMapping
    private Product create(
            @RequestPart(value = "file") Optional<MultipartFile> file,
            @RequestPart(value = "product") Product product
    ){
        if (file.isPresent()) {
            //на случай когда будут приходить картинки
            String fileName = file.get ().getOriginalFilename ();
            System.out.println("fileName = "+fileName);
            product.setProductFile (fileName);
        }

        return productRepo.save(product);
    }*/

//    @PostMapping
//    @JsonView(Views.FullMessage.class)
//    private Product create(
//            @RequestBody Product product
//    ){
//        return generalService.saveProduct(product);
//    }

    //принимает FormData который состоит из Файлов и JSON
    @PostMapping
    @JsonView(Views.FullMessage.class)
    private Product create(
            @RequestPart(value = "files") Optional<MultipartFile[]> files,
            @RequestPart(value = "product") Product product
    ){
        return productService.saveProduct(product, files);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        productService.deleteProduct(Long.valueOf(id));
    }

//    @PutMapping("{id}")
//    @JsonView(Views.FullMessage.class)
//    private Product update(
//            @PathVariable String id,
//            @RequestBody Product product
//    ){
//        return generalService.updateProduct(product);
//    }

    @PutMapping
    @JsonView(Views.FullMessage.class)
    private Product update(
            @RequestPart(value = "files") Optional<MultipartFile[]> files,
            @RequestPart(value = "product") Product product
    ){
        return productService.updateProduct(product, files);
    }


    /*@PutMapping("{id}")
    public Product update(
            @PathVariable String id,
            @RequestPart(value = "file") Optional<MultipartFile> file,
            @RequestPart(value = "product") Product product
    ){
        if (file.isPresent()) {
            //на случай когда будут приходить картинки
            String newFileName = file.get ().getOriginalFilename ();
            System.out.println("newFileName = "+newFileName);
            //product.setProductFile (newFileName);
        }

        Product productfromDb = productRepo.findById(Long.valueOf(id)).get();

        BeanUtils.copyProperties (product, productfromDb, "id");    //утила спринга которая копирует все поля из message в messageFromDb кроме id
        return productRepo.save (productfromDb);
    }*/

    /*@PostMapping("{id}")
    private Product update(
            @PathVariable String id,
            @RequestBody Product product
    ){

        Product productfromDb = productRepo.findById(Long.valueOf(id)).get();

        BeanUtils.copyProperties (product, productfromDb, "id");    //утила спринга которая копирует все поля из message в messageFromDb кроме id
        return productRepo.save (productfromDb);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        productRepo.deleteById(Long.valueOf(id));
    }*/

}
