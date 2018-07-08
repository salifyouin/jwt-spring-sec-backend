package org.sid;

import java.util.stream.Stream;

import org.sid.dao.TaskRepository;
import org.sid.entities.AppRole;
import org.sid.entities.AppUser;
import org.sid.entities.Task;
import org.sid.service.AccountServiceImpl;
import org.sid.service.AcountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JwtSpringSecApplication implements CommandLineRunner{
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private AcountService acountService;
	public static void main(String[] args) {
		SpringApplication.run(JwtSpringSecApplication.class, args);
	}
 
	@Bean
	public BCryptPasswordEncoder getBCPE() {
		return new BCryptPasswordEncoder();
	}
	@Override
	public void run(String... args) throws Exception {
		
		acountService.saveUser(new AppUser(null,"admin","1234",null));
		acountService.saveUser(new AppUser(null,"user","1234",null));
		acountService.saveRole(new AppRole(null,"ADMIN"));
		acountService.saveRole(new AppRole(null,"USER"));
		acountService.addRoleToUser("admin", "ADMIN");
		acountService.addRoleToUser("admin", "USER");
		acountService.addRoleToUser("user", "USER");
		Stream.of("T1","T2","T3").forEach(t->{
			taskRepository.save(new Task(null,t));
		});
		taskRepository.findAll().forEach(t->{
			System.out.println(t.getTaskName());
		});
		
	}
}
