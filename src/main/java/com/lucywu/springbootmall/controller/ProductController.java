package com.lucywu.springbootmall.controller;

import com.lucywu.springbootmall.constant.ProductCategory;
import com.lucywu.springbootmall.dao.ProductQueryParams;
import com.lucywu.springbootmall.dto.ProductRequest;
import com.lucywu.springbootmall.model.Product;
import com.lucywu.springbootmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;


@Validated //加了之後Max及Min註解才會生效
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    //查詢商品列表
    //required = false 不傳值也不會爆錯，賦予null，代表不是必填，是選填
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(
            //查詢條件Filtering
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String search,
            //排序Sorting
            @RequestParam(defaultValue = "created_date") String orderBy, //最新上架日期
            @RequestParam(defaultValue = "desc") String sort,//由大到小

            //分頁
            @RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer limit,  //取得幾筆商品數據
            @RequestParam(defaultValue = "0") @Min(0) Integer offset  //跳過多少筆數據，驗證前端來的值：@Min(0) 為了避免負數
    ) {
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);
        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);
        productQueryParams.setLimit(limit);
        productQueryParams.setOffset(offset);

    List<Product> productList = productService.getProducts(productQueryParams);

    return ResponseEntity.status(HttpStatus.OK).body(productList);
    }

    //查詢商品中的某一個商品
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId){
        Product product = productService.getProductById(productId);

        if (product != null){
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }else{

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest){

    Integer productId = productService.createProduct(productRequest);

    Product product = productService.getProductById(productId);

    return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId,
                                                 @RequestBody @Valid ProductRequest productRequest){

        //檢查product是否存在
        Product product = productService.getProductById(productId);
        if (product == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        //修改商品的數據
        productService.updateProduct(productId,productRequest);

        Product updateProduct = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.OK).body(updateProduct);
    }


    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId){

        productService.deleteProductById(productId);

        //只要確定該商品消失不見，就表示刪除功能是成功的，不需要加上not found404
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

}
