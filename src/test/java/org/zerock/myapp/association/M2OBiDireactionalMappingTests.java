package org.zerock.myapp.association;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;
import org.zerock.myapp.entity.Member;
import org.zerock.myapp.entity.Team;
import org.zerock.myapp.util.PersistenceUnits;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


//@Log4j2
@Slf4j

@NoArgsConstructor

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class M2OBiDireactionalMappingTests {
	private EntityManagerFactory emf;
	private EntityManager em;
	
	
	@BeforeAll
	void beforeAll() {	// 1회성 전처리
		log.trace("beforeAll() invoked.");
		
		// -- 1 ------------
//		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.H2);
		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.ORACLE);
//		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.MYSQL);
		
		Objects.requireNonNull(this.emf);

		// -- 2 ------------
		this.em = this.emf.createEntityManager();
		assertNotNull(this.em);
	} // beforeAll
	
	@AfterAll
	void afterAll() {	// 1회성 전처리
		log.trace("afterAll() invoked.");
		
		this.em.clear();
		
		try { this.em.close(); } catch(Exception _ignored) {}
		try { this.emf.close();} catch(Exception _ignored) {}
	} // afterAll
	
	
//	@Disabled
	@Order(1)
	@Test
//	@RepeatedTest(1)
	@DisplayName("1. prepareData")
	@Timeout(value=1L, unit = TimeUnit.MINUTES, threadMode = ThreadMode.INFERRED)
	void prepareData() {
		log.trace("prepareData() invoked.");
		
		try {
			this.em.getTransaction().begin();

			
			// -- 1 -------------
			
			// 3개의 팀 생성 및 저장
			IntStream.of(1, 2, 3).forEachOrdered(seq -> {
				Team transientTeam = new Team();
				transientTeam.setName("NAME-"+seq);
				
				this.em.persist(transientTeam);
			});	// .forEachOrdered

			
			// -- 2 -------------
			
			Team team1 = this.em.<Team>find(Team.class, 1L);	// MANAGED
			Team team2 = this.em.<Team>find(Team.class, 2L);	// MANAGED
			Team team3 = this.em.<Team>find(Team.class, 3L);	// MANAGED
			
			Objects.requireNonNull(team1);
			Objects.requireNonNull(team2);
			Objects.requireNonNull(team3);
			
			
			// -- 3 -------------
			
			// 9 명의 멤버를 새로 생성/저장하되, 1 ~ 6까지는 Team 1에 소속
			// 나머지 7 ~ 9 까지는 Team 3에 소속시킵시다!.
			
			IntStream.rangeClosed(1, 6).forEachOrdered(seq -> {
				Member transientMember = new Member();
				transientMember.setName("NAME-"+seq);
				
				if(seq != 6) transientMember.setMyTeam(team1);		// Team 1에 소속시킴
				
				this.em.persist(transientMember);
			});	// .forEachOrdered
			
			IntStream.of(7, 8, 9).forEachOrdered(seq -> {
				Member transientMember = new Member();
				transientMember.setName("NAME-"+seq);
				
				transientMember.setMyTeam(team3);		// Team 3에 소속시킴
				
				this.em.persist(transientMember);
			});	// .forEachOrdered
			
			this.em.getTransaction().commit();
		} catch(Exception e) {
			this.em.getTransaction().rollback();
			
			throw e;
		} // try-catch
	} // prepareData
	
	
//	@Disabled
	@Order(2)
	@Test
//	@RepeatedTest(1)
	@DisplayName("2. testObjectGraphTraverseFromTeamToMembers")
	@Timeout(value=1L, unit = TimeUnit.MINUTES, threadMode = ThreadMode.INFERRED)
	void testObjectGraphTraverseFromTeamToMembers() {
		log.trace("testObjectGraphTraverseFromTeamToMembers() invoked.");
	
		LongStream.of(1L, 2L, 3L).forEachOrdered(seq -> {
			Team foundTeam = this.em.<Team>find(Team.class, seq);
			
			Objects.requireNonNull(foundTeam);
			log.info("\t+ foundTeam: {}", foundTeam);
			
			Objects.requireNonNull(foundTeam.getMembers());
			foundTeam.getMembers().forEach(m -> log.info(m.toString()));			
		});	// forEachOrdered
	} // testObjectGraphTraverseFromTeamToMembers
	
	
//	@Disabled
	@Order(3)
	@Test
//	@RepeatedTest(1)
	@DisplayName("3. testObjectGraphTraverseFromMemberToTeam")
	@Timeout(value=1L, unit = TimeUnit.MINUTES, threadMode = ThreadMode.INFERRED)
	void testObjectGraphTraverseFromMemberToTeam() {
		log.trace("testObjectGraphTraverseFromMemberToTeam() invoked.");
	
		LongStream.rangeClosed(1, 9).forEachOrdered(seq -> {
			Member foundMember = this.em.<Member>find(Member.class, seq);
			
			Objects.requireNonNull(foundMember);
			log.info("\t+ foundMember: {}", foundMember);
		});	// forEachOrdered
	} // testObjectGraphTraverseFromMemberToTeam
	

} // end class
