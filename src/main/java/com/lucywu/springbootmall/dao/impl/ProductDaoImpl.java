package com.lucywu.springbootmall.dao.impl;

import com.lucywu.springbootmall.dao.ProductDao;
import com.lucywu.springbootmall.dao.ProductQueryParams;
import com.lucywu.springbootmall.dto.ProductRequest;
import com.lucywu.springbootmall.model.Product;
import com.lucywu.springbootmall.rowmapper.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countProduct(ProductQueryParams productQueryParams) {
        String sql = "SELECT count(*) FROM product WHERE 1=1";

        Map<String,Object> map = new HashMap<>();
        //AND前面一定要加上空白鍵
        //檢查前端傳來的值是否為null，如果是null，才需要下方的SQL語句

        //查詢條件
        sql = addfilteringSql(sql, map, productQueryParams);

        //queryForObject：能將count的值轉換成Integer類型
        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return total;
    }

    //where 1=1 可以更簡單的讓後面的查詢語句做拼接
    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        String sql = "SELECT product_id,product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "FROM product WHERE 1=1 ";

        Map<String,Object> map = new HashMap<>();
        //AND前面一定要加上空白鍵
        //檢查前端傳來的值是否為null，如果是null，才需要下方的SQL語句

        //查詢條件
        sql = addfilteringSql(sql, map, productQueryParams);


        //ORDER只能用字串拼接
        //已為兩個參數加上了預設值，所以預設不會是null
        //SQL語句拼接時，前跟後一定要留空白，才不會都黏在一起

        //排序
        sql = sql + " ORDER BY " + productQueryParams.getOrderBy() + " " + productQueryParams.getSort();

        //分頁
        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit",productQueryParams.getLimit());
        map.put("offset",productQueryParams.getOffset());

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        return productList;
    }

    @Override
    public Product getProductById(Integer productId) {

        String sql = "SELECT product_id,product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "FROM product WHERE product_id =:productId";
        Map<String,Object> map = new HashMap<>();
        map.put("productId",productId);


        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());


       if (productList.size() > 0 ){
           return productList.get(0);
       }else{
           return null;
       }
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {
        String sql = "INSERT INTO product(product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date )" +
                "VALUES (:productName, :category, :imageUrl, :price, :stock, :description, :createdDate, :lastModifiedDate)";


        Map<String,Object> map = new HashMap<>();
        map.put("productName",productRequest.getProductName());
        map.put("category",productRequest.getCategory().toString());
        map.put("imageUrl",productRequest.getImageUrl());
        map.put("price",productRequest.getPrice());
        map.put("stock",productRequest.getStock());
        map.put("description",productRequest.getDescription());


        Date now = new Date();
        map.put("createdDate",now);
        map.put("lastModifiedDate",now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map),keyHolder);

        int productId = keyHolder.getKey().intValue();

        return productId;
    }

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {

        String sql = "UPDATE product SET product_name = :productName, category = :category, image_url = :imageUrl, " +
                "price = :price, stock = :stock, description = :description, last_modified_date = :lastModifiedDate " +
                " WHERE product_id = :productId";

        Map<String,Object> map = new HashMap<>();
        map.put("productId",productId);

        map.put("productName",productRequest.getProductName());
        map.put("category",productRequest.getCategory().toString());
        map.put("imageUrl",productRequest.getImageUrl());
        map.put("price",productRequest.getPrice());
        map.put("stock",productRequest.getStock());
        map.put("description",productRequest.getDescription());

        map.put("lastModifiedDate",new Date());

        namedParameterJdbcTemplate.update(sql,map);

    }
    @Override
    public void deleteProductById(Integer productId) {

        String sql = "DELETE FROM product WHERE product_id = :productId";
        Map<String,Object> map = new HashMap<>();
        map.put("productId",productId);

        namedParameterJdbcTemplate.update(sql,map);

    }

    //提煉程式實，建議用private方法，就比較不會影響其他程式
    private String addfilteringSql(String sql,Map<String,Object > map,ProductQueryParams productQueryParams){
        //查詢條件
        if(productQueryParams.getCategory() != null){

            sql = sql + " AND category =:category";
            map.put("category", productQueryParams.getCategory().name());

        }

        if (productQueryParams.getSearch() != null){

            sql = sql + " AND product_name LIKE :search";
            //模糊查詢的百分比一定要寫在map的值裡面，不可寫在上面sql裡>>JdbcTemplate的限制
            map.put("search","%" + productQueryParams.getSearch() + "%");
        }

        return sql;
    }
}
