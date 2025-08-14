package com.boringland.mocardserver.entity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


@Repository
public interface UserReceiptRepository extends JpaRepository<UserReceipt, Integer>, JpaSpecificationExecutor<UserReceipt>{
    List<UserReceipt> findByOrderId(Integer orderId);

    List<UserReceipt> findByTransactionId(String transactionId);

    List<UserReceipt> findByOriginalTransactionId(String originalTransactionId);

    List<UserReceipt> findByDeviceIdAndProductId(String deviceId, String productId);



}
