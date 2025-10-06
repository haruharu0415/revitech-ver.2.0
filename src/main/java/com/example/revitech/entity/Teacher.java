package com.example.revitech.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "home")
@Data
@NoArgsConstructor
public class Teacher {
//aiueo

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Integer id;
	
	private String name;
	
	private String email;
	
	private String password;
	
	private String status;
	
	private String role;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate create_at;
	

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate udpate_at;

}
