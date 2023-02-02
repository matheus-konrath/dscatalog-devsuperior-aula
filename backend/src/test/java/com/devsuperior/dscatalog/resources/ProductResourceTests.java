package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDto;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private PageImpl<ProductDto> page;
    private ProductDto productDTO;
    private long existingID;
    private long nonExistingID;
    private long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        productDTO = Factory.createProductDto();
        page = new PageImpl<>(List.of(productDTO));
        existingID = 1L;
        nonExistingID = 2L;
        dependentId = 3L;

        when(productService.findAllPaged(any())).thenReturn(page);

        when(productService.findById(existingID)).thenReturn(productDTO);
        when(productService.findById(nonExistingID)).thenThrow(ResourceNotFoundException.class);

        when(productService.update(eq(existingID), any())).thenReturn(productDTO);
        when(productService.update(eq(nonExistingID), any())).thenThrow(ResourceNotFoundException.class);

        when(productService.insert(any())).thenReturn(productDTO);

        doNothing().when(productService).delete(existingID);
        doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingID);
        doThrow(DatabaseException.class).when(productService).delete(dependentId);
    }

    @Test
    public void findAllPageShouldReturnPage() throws Exception {
        ResultActions result = mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {

        ResultActions result = mockMvc.perform(get("/products/{id}", existingID)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());   //Acessa o objeto da resposta
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void finByIdShouldThrowResourceExceptionWhenIdDoesNotExist() throws Exception{

        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingID)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductWhenIdExists() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO); //transforma um corpo json em String

        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingID)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }
    @Test
    public void updateShouldThrowResourceExceptionWhenIdDoesNotExist() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingID)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                );
        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnProduct()throws Exception{

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        //ResultActions result =
                ResultActions result = mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception{

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingID)
                        .accept(MediaType.APPLICATION_JSON)
                );

        result.andExpect(status().isNoContent());

    }

    @Test
    public void deleteShouldThrowNotFoundWhenIdDoesNotExist() throws Exception{

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", nonExistingID)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());

    }


}
