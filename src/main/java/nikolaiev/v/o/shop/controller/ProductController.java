package nikolaiev.v.o.shop.controller;

import nikolaiev.v.o.shop.domain.Product;
import nikolaiev.v.o.shop.domain.Views;
import nikolaiev.v.o.shop.services.ProductService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @JsonView(Views.FullMessage.class)
    public ResponseEntity<List<Product>> list(){
        try {
            return ResponseEntity.ok()
                    .location((new URI ("/product")))
                    .body(productService.getAllProducts());
        } catch (URISyntaxException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * Принимает продукт и файлы
     *
     * @param files файлы
     * @param product продукт
     *
     * @return продукт с БД
     */
    //принимает FormData который состоит из Файлов и JSON
    @PostMapping
    @JsonView(Views.FullMessage.class)
    public ResponseEntity<Product> create(
            @RequestPart(value = "files") Optional<MultipartFile[]> files,
            @RequestPart(value = "product") Product product
    ){
        try {
            return ResponseEntity
                    .ok()
                    .location((new URI ("/product")))
                    .body(productService.saveProduct1 (product, files));
        } catch (URISyntaxException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @PutMapping
    @JsonView(Views.FullMessage.class)
    public Product update(
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
