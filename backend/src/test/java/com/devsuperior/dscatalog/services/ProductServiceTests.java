package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repository.CategoryRepository;
import com.devsuperior.dscatalog.repository.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingID;
    private long nonExistsID;
    private long dependentID;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    @BeforeEach
    void setUp() throws Exception {
        existingID = 1L;
        nonExistsID = 1000L;
        dependentID = 4L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        category = Factory.createCategory();

        // quando retornar algo começa pelo  Mockito.when
        Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(repository.findById(existingID)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistsID)).thenReturn(Optional.empty());

        Mockito.when(repository.getOne(existingID)).thenReturn(product);
        Mockito.when(repository.getOne(nonExistsID)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getOne(existingID)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(nonExistsID)).thenThrow(ResourceNotFoundException.class);

        // quando for um Void comoça pela ação - EX:.Mockito.doNothing
        Mockito.doNothing().when(repository).deleteById(existingID);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistsID);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentID);
    }

    @Test
    public void deleteIdShouldDoNothingWhenIdExists(){

        Assertions.assertDoesNotThrow(() ->{
            service.delete(existingID);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingID);

    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, () ->{
           service.delete(nonExistsID);

           Mockito.verify(repository,Mockito.times(1)).deleteById(nonExistsID);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependtID(){

        Assertions.assertThrows(DatabaseException.class, () ->{
           service.delete(dependentID);

           Mockito.verify(repository, Mockito.times(1)).deleteById(dependentID);
        });
    }

    @Test
    public void findAllPagedShouldReturnPage(){

        Pageable page = PageRequest.of(0,10);
        Page<ProductDto> result =  service.findAllPaged(page);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(page);

    }

    @Test
    public void findByIdShouldReturnProductDTOWhenExistsId(){


        ProductDto result = service.findById(existingID);

        Assertions.assertEquals(existingID, result.getId());
        Assertions.assertNotNull(result);

        Mockito.verify(repository, Mockito.times(1)).findById(result.getId());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class, () ->{
            service.findById(nonExistsID);

            Mockito.verify(repository).findById(nonExistsID);
        });
    }

    @Test
    public void updateShouldReturnProductDTOWhenExistsId(){

        ProductDto dto = Factory.createProductDto();
        ProductDto result = service.update(existingID, dto);

        Assertions.assertNotNull(result);

    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            ProductDto dto = Factory.createProductDto();
            service.update(nonExistsID, dto) ;
        });
    }


}
