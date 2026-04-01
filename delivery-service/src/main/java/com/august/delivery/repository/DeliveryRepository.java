package com.august.delivery.repository;

import com.august.delivery.document.Delivery;
import com.august.delivery.document.DeliveryStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends MongoRepository<Delivery, String> {
    List<Delivery> findByCourierIdAndStatus(String courierId, DeliveryStatus status);
}

