package com.example.PayAll_BE.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Limit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long limitId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_Statistics_userId_User"))
	private User user;

	@Column(nullable = false)
	private long limitPrice;

	@Column(nullable = false)
	private LocalDateTime limitDate;
}
