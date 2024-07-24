package com.bezkoder.spring.jwt.mongodb.controllers;

import com.bezkoder.spring.jwt.mongodb.models.Product;
import com.bezkoder.spring.jwt.mongodb.payload.request.PageRequestDto;
import com.bezkoder.spring.jwt.mongodb.repository.ProductRepository;
import com.bezkoder.spring.jwt.mongodb.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/api/v1")
public class productController {

    @Autowired
    private ProductRepository productRepository;
    private final ProductService productService;

    public productController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping("/product")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product newProduct = productService.saveProduct(product);
        return ResponseEntity.ok(newProduct);
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/{product_id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer product_id) {
        Optional<Product> product = productService.getProductById(product_id);
        return product.map(ResponseEntity ::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/products/category/{name}")
    public List<Product> getProductsByCategory(@PathVariable String name) {
        return productService.getProductsByCategory(name);
    }


    @PutMapping("/product/{product_id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer product_id, @RequestBody Product product){
        Product updatedProduct = productService.updateProduct(product_id,product);
        return ResponseEntity.ok(updatedProduct);
    }
    @DeleteMapping("/products/{product_id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer product_id) {
        productService.deleteProduct(product_id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PostMapping("/pageableProducts")
    public Page<Product> getAllProductsUsingPagination(@RequestBody PageRequestDto dto){
        Pageable pageable= new PageRequestDto().getPageable(dto);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage;
    }
    @PostMapping("/pageableList")
    public Page<Product> getAllProductsUsingPaginationList(@RequestBody PageRequestDto dto){

        List<Product> prouctList = productRepository.findAll();

        //1. PageListHolder
        PagedListHolder<Product> pagedListHolder = new PagedListHolder<Product>(prouctList);
        pagedListHolder.setPage(dto.getPageNo());
        pagedListHolder.setPageSize(dto.getPageSize());

        //2. PropertyComparator
        List<Product> pageSlice = pagedListHolder.getPageList();
        boolean ascending = dto.getSort().isAscending();
        PropertyComparator.sort(pageSlice, new MutableSortDefinition(dto.getSortByColumn(), true, true));
        //3. PageImpl
        Page<Product> products = new PageImpl<Product>(pageSlice, new PageRequestDto().getPageable(dto), prouctList.size());
    return products;
    }

}