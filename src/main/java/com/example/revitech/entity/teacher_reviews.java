package com.example.revitech.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
public class teacher_reviews {
	
	private Integer id;
	
	private Integer teacher_user_id; 
	
	private Integer student_user_id;
	
	private Integer follow_up_rating;
	
	private Integer communication_rating;
	
	private Integer explanation_rating;
	
	private Integer voice_volume_rating;
	
	private Integer task_rating;
	
	private Integer star_rating;
	
	private String comment;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate create_at;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate udpate_at;
	
	

}
