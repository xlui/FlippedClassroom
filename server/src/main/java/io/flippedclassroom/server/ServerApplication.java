package io.flippedclassroom.server;

import io.flippedclassroom.server.entity.*;
import io.flippedclassroom.server.service.*;
import io.flippedclassroom.server.utils.EncryptUtil;
import io.flippedclassroom.server.utils.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

@SpringBootApplication
@RestController
public class ServerApplication extends SpringBootServletInitializer {
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private CourseService courseService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private CommentService commentService;

	/**
	 * 负责数据库的初始化
	 */
	@ApiIgnore
	@RequestMapping("/init")
	public String init() {
		Logger logger = LogUtil.getLogger();

		logger.info("从数据库中查询需要初始化的实体");
		User userStudent = userService.findUserByUsername("1");
		User userTeacher = userService.findUserByUsername("2");
		// 角色默认有三种：学生、教师、管理员
		Role roleStudent = roleService.findRoleByRoleName("student");
		Role roleTeacher = roleService.findRoleByRoleName("teacher");
		Role roleAdmin = roleService.findRoleByRoleName("admin");
		// 课程初始化
		Course courseMath = courseService.findCourseByCourseName("数学");
		Course courseDataStructure = courseService.findCourseByCourseName("数据结构");
		Course courseDatabase = courseService.findCourseByCourseName("数据库");
		// 权限初始化
		Permission permissionUpdate = permissionService.findPermissionByPermissionName("course:update");
		Permission permissionDelete = permissionService.findPermissionByPermissionName("course:delete");
		Permission permissionCreate = permissionService.findPermissionByPermissionName("course:create");
		Permission permissionJoin = permissionService.findPermissionByPermissionName("course:join");
		Permission permissionViewComment = permissionService.findPermissionByPermissionName("course:comment:view");
		Permission permissionAddComment = permissionService.findPermissionByPermissionName("course:comment:add");
		Permission permissionUploadData = permissionService.findPermissionByPermissionName("course:data:upload");
		// 评论初始化
		Comment commentFirst = commentService.findCommentById(1L);
		Comment commentSecond = commentService.findCommentById(2L);

		// 如果数据库中存在这些实体，删除并重新初始化
		logger.info("如果数据库中存在初始化信息，删除");
		if (userStudent != null)
			userService.delete(userStudent);
		if (userTeacher != null)
			userService.delete(userTeacher);
		if (roleAdmin == null)
			roleAdmin = new Role("admin");
		if (roleStudent == null)
			roleStudent = new Role("student");
		if (roleTeacher == null)
			roleTeacher = new Role("teacher");
		if (courseMath != null)
			courseService.delete(courseMath);
		if (courseDataStructure != null)
			courseService.delete(courseDataStructure);
		if (courseDatabase != null)
			courseService.delete(courseDatabase);
		if (permissionUpdate != null)
			permissionService.delete(permissionUpdate);
		if (permissionDelete != null)
			permissionService.delete(permissionDelete);
		if (permissionCreate != null)
			permissionService.delete(permissionCreate);
		if (permissionJoin != null)
			permissionService.delete(permissionJoin);
		if (permissionViewComment != null)
			permissionService.delete(permissionViewComment);
		if (permissionAddComment != null)
			permissionService.delete(permissionAddComment);
		if (permissionUploadData != null)
			permissionService.delete(permissionUploadData);
		if (commentFirst != null)
			commentService.delete(commentFirst);
		if (commentSecond != null)
			commentService.delete(commentSecond);

		// 重新初始化
		logger.info("重新初始化...");
		userStudent = new User("1", "dev");
		userTeacher = new User("2", "std");
		courseMath = new Course("数学", "数学专业");
		courseDataStructure = new Course("数据结构", "计算机专业");
		courseDatabase = new Course("数据库", "计算机专业");
		permissionUpdate = new Permission("course:update");
		permissionDelete = new Permission("course:delete");
		permissionCreate = new Permission("course:create");
		permissionJoin = new Permission("course:join");
		permissionViewComment = new Permission("course:comment:view");
		permissionAddComment = new Permission("course:comment:add");
		permissionUploadData = new Permission("course:data:upload");
		commentFirst = new Comment("Hello World!");
		commentSecond = new Comment("这是第二条评论");

		// 密码加密存储
		EncryptUtil.encrypt(userStudent);
		EncryptUtil.encrypt(userTeacher);

		// 设置关系
		logger.info("设置关系");
		userStudent.setRole(roleStudent);
		userStudent.setCourseList(Arrays.asList(courseDataStructure, courseDatabase));

		userTeacher.setRole(roleTeacher);
		userTeacher.setCourseList(Arrays.asList(courseMath, courseDatabase, courseDataStructure));

		permissionUpdate.setRoleList(Arrays.asList(roleTeacher, roleAdmin));
		permissionDelete.setRoleList(Arrays.asList(roleStudent, roleTeacher, roleAdmin));
		permissionCreate.setRoleList(Arrays.asList(roleTeacher, roleAdmin));
		permissionJoin.setRoleList(Arrays.asList(roleStudent, roleAdmin));
		permissionViewComment.setRoleList(Arrays.asList(roleStudent, roleTeacher, roleAdmin));
		permissionAddComment.setRoleList(Arrays.asList(roleStudent, roleTeacher, roleAdmin));
		permissionUploadData.setRoleList(Arrays.asList(roleTeacher, roleAdmin));

		commentFirst.setUser(userStudent);
		commentFirst.setCourse(courseMath);
		commentSecond.setUser(userTeacher);
		commentSecond.setCourse(courseMath);

		// 保存
		logger.info("保存到数据库");
		userService.save(Arrays.asList(userStudent, userTeacher));
		roleService.save(Arrays.asList(roleStudent, roleTeacher, roleAdmin));
		permissionService.save(Arrays.asList(permissionUpdate, permissionDelete, permissionCreate, permissionJoin,
				permissionViewComment, permissionAddComment, permissionUploadData));
		commentService.save(commentFirst);
		commentSecond.setReply(commentFirst.getId());
		commentService.save(commentSecond);

		return "init success";
	}

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ServerApplication.class);
	}
}
