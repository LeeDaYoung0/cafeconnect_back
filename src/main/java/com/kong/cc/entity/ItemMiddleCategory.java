package com.kong.cc.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItemMiddleCategory {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer itemCategoryNum;
	private String itemCategoryName;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="itemCategoryMajorNum")
	private ItemMajorCategory ItemMajorCategoryMd;

	@OneToMany(mappedBy="itemMiddleCategory", fetch=FetchType.LAZY)
	private List<Item> itemList = new ArrayList<>();
	
	@OneToMany(mappedBy="ItemMajorCategorySb", fetch=FetchType.LAZY)
	private List<ItemSubCategory> itemSubCategoryList = new ArrayList<>();
	
}