package io.flippedclassroom.server.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 课程评论
 */
@Entity
public class Comment implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	private String content;

	// 课程评论与用户的多对一关系
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	// 课程评论与课程的多对一关系
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "course_id")
	private Course course;

	public Comment() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
}
