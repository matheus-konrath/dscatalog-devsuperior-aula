package com.devsuperior.dscatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.dscatalog.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
