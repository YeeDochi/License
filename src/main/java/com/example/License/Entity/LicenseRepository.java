package com.example.License.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LicenseRepository extends JpaRepository<LicenseEntity, Long> {
    

}
