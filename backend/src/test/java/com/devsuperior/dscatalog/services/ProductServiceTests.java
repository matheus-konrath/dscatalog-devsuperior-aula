package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repository.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingID;
    private long nonExistsID;
    private long dependentID;
    @BeforeEach
    void setUp() throws Exception {
        existingID = 1L;
        nonExistsID = 1000L;
        dependentID = 4L;

        Mockito.doNothing().when(repository).deleteById(existingID); //
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




}
