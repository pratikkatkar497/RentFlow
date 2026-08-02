package com.RentFlow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.RentFlow.enums.RoleType;

@Entity
@Table(name = "roles")

public class Role extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true)
	private RoleType name;

	public RoleType getName() {
		return name;
	}

	public void setName(RoleType name) {
		this.name = name;
	}

	public Role(RoleType name) {
		this.name = name;
	}
	public Role() {
	}
}