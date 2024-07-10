package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer>, JpaSpecificationExecutor<Store> {

    @Query("SELECT s FROM Store s WHERE s.isActive = true AND s.owner.id = :storeId")
    Page<Store> findAllDisplayedStores(Pageable pageable, @Param("storeId") Integer storeId);
    @Query("SELECT s FROM Store s WHERE s.isActive = true AND s.owner.id = :storeId AND s.deliveryAvailable = false")
    Page<Store> findAllDisplayedStoresWithoutDelivery(Pageable pageable, @Param("storeId") Integer storeId);
}