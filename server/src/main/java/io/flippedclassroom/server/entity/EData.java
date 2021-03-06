package io.flippedclassroom.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 电子学习资料，考虑上传后保存资料的位置
 */
@ApiModel(value = "电子资料", description = "与课前预习资料存放在不同位置")
@Entity
public class EData implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	private String position;    // 资料保存位置
	private String date;
	private long size;
	private String author;

	// 电子资料与课程的多对一关系
	@ManyToOne
	@JoinColumn(name = "course_id")
	@JsonIgnore
	private Course course;

	public EData() {
		super();
		this.date = LocalDate.now().toString();
	}

	public EData(String position) {
		this.position = position;
		this.date = LocalDate.now().toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
