package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repository.ProductRepository;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
    
    @Autowired
    private ProductRepository repository;

    private long existingId ;
    private  long nonexistingId ;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
       existingId = 1L;
       nonexistingId = 50L;
       countTotalProducts = 25L;
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Optional<Product> result =  repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void deleteShouldThrowExceptionWhenIdDoesntExists() {

        Assertions.assertThrows(EmptyResultDataAccessException.class, () ->{
            repository.deleteById(nonexistingId);
        });
    }

    @Test
    void findByIdShouldReturnOptionalWhenExistId() {

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertTrue(result.isPresent());
    } 

    @Test
    void findByIdShouldReturnOptionalWhenDoesntExistId() {

        Optional<Product> result = repository.findById(nonexistingId);

        Assertions.assertTrue(result.isEmpty());

    }
}
