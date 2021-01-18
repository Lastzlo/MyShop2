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

    //принимает FormData который состоит из Файлов и JSON
    @PostMapping
    @JsonView(Views.FullMessage.class)
    private Product create(
            @RequestPart(value = "files") Optional<MultipartFile[]> files,
            @RequestPart(value = "product") Product product
    ){
        return productService.saveProduct(product, files);
    }


    @PutMapping
    @JsonView(Views.FullMessage.class)
    private Product update(
            @RequestPart(value = "files") Optional<MultipartFile[]> files,
            @RequestPart(value = "product") Product product
    ){
        return productService.updateProduct(product, files);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        productService.deleteProduct(Long.valueOf(id));
    }

}
