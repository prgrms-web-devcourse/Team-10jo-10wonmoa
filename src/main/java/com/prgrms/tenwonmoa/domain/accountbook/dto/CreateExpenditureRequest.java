package com.prgrms.tenwonmoa.domain.accountbook.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.user.User;

public class CreateExpenditureRequest {

	@NotNull
	private final LocalDateTime registerDate;

	@Min(0L)
	@Max(1000000000000L)
	private final Long amount;

	@Size(max = 50)
	private final String content;

	@NotNull
	private final Long userCategoryId;

	public CreateExpenditureRequest(LocalDateTime registerDate, Long amount, String content, Long userCategoryId) {
		this.registerDate = registerDate;
		this.amount = amount;
		this.content = content;
		this.userCategoryId = userCategoryId;
	}

	public Expenditure toEntity(User user, UserCategory userCategory, String categoryName) {
		return new Expenditure(
			this.registerDate,
			this.amount,
			this.content,
			categoryName,
			user,
			userCategory
		);
	}

	public Long getUserCategoryId() {
		return userCategoryId;
	}
}
