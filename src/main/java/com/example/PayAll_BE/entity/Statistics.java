package com.example.PayAll_BE.entity;

import java.time.LocalDateTime;

import com.example.PayAll_BE.entity.enums.StatisticsCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistics {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long statisticsId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_Statistics_userId_User"))
	private User user;

	@Enumerated(EnumType.STRING)
	private StatisticsCategory statisticsCategory;

	@Column(nullable = false)
	private long statisticsAmount;

	@Column(nullable = false)
	private LocalDateTime statisticsDate;
}
