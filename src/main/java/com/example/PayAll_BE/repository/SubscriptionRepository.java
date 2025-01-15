package com.example.PayAll_BE.repository;

import com.example.PayAll_BE.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	@Query("SELECT s FROM Subscription s WHERE s.shoppingBenefitRate = " +
		"(SELECT MAX(sub.shoppingBenefitRate) FROM Subscription sub)")
	List<Subscription> findTopShoppingBenefit();

	@Query("SELECT s FROM Subscription s WHERE s.educationBenefitRate = " +
		"(SELECT MAX(sub.educationBenefitRate) FROM Subscription sub)")
	List<Subscription> findTopEducationBenefit();

	@Query("SELECT s FROM Subscription s WHERE s.livingBenefitRate = " +
		"(SELECT MAX(sub.livingBenefitRate) FROM Subscription sub)")
	List<Subscription> findTopLivingBenefit();

	@Query("SELECT s FROM Subscription s WHERE s.transportBenefitRate = " +
		"(SELECT MAX(sub.transportBenefitRate) FROM Subscription sub)")
	List<Subscription> findTopTransportBenefit();

	@Query("SELECT s FROM Subscription s WHERE s.cultureBenefitRate = " +
		"(SELECT MAX(sub.cultureBenefitRate) FROM Subscription sub)")
	List<Subscription> findTopCultureBenefit();

	@Query("SELECT s FROM Subscription s WHERE s.restaurantBenefitRate = " +
		"(SELECT MAX(sub.restaurantBenefitRate) FROM Subscription sub)")
	List<Subscription> findTopRestaurantBenefit();

	@Query("SELECT s FROM Subscription s WHERE s.cafeBenefitRate = " +
		"(SELECT MAX(sub.cafeBenefitRate) FROM Subscription sub)")
	List<Subscription> findTopCafeBenefit();

	@Query("SELECT s FROM Subscription s WHERE s.healthBenefitRate = " +
		"(SELECT MAX(sub.healthBenefitRate) FROM Subscription sub)")
	List<Subscription> findTopHealthBenefit();
}
