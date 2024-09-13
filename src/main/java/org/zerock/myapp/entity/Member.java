package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


//@Log4j2
@Slf4j

@Data

@Entity(name = "Member")
@Table(name="member")
public class Member implements Serializable { // Child(Many), (N)	
	@Serial private static final long serialVersionUID = 1L;

	// 1. Set PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;			// PK
	
	
	// 2. 일반속성들
	@Basic(optional = false)	// Not Null Constraint
	private String name;
	
	
	// ==========================================
	// ManyToOne (N:1), Bi-directional Association
	// ==========================================	
	
	/**
	 * -------------------------------
	 * Important: Set the owner of an Bi-directional association.
	 * -------------------------------
	 *	(1) In *Uni-directional association, do *Not set the owner of this association.	(***)
	 *	(2) In only *Bi-directional association, set the *Owner of this association
	 * 		with `mappedBy` element in the (1) @OneToOne (2) @OneToMany (3) @ManyToMany annotations.
	 *	(3) There is *No `mappedBy` element in the @ManyToOne annotation
	 *		Because Many (Child) always have an FK columm, thus no `mappedBy` element. (***)
	 * -------------------------------
	 */
	
	@ManyToOne(optional = true, targetEntity = Team.class)
	
	/**
	 * -------------------------------
	 * @JoinColumn
	 * -------------------------------
	 * (1) Set the FK column name of Many (Child) table. (***)
	 * (2) If @JoinColumn annotation abbreviated,
	 * 	   The default FK column name = 
	 * 			The name of the entity + "_" + The name of the referenced primary key column.
	 * (3) @JoinColumn(table): The name of the table that contains the FK column.
	 * 	   If `table` propery *Not specified,
	 * 	   The FK column is assumed to be in the primary table of the applicable entity. 
	 * -------------------------------
	 */
	
	@JoinColumn(
		name = "my_team",
		table = "member", 
		referencedColumnName = "team_id")
	
	private Team myTeam;		// Set FK
	
	
	// 양방향 연관관계에서는, FK속성에 대한 Setter 메소드를 직접 선언하셔야 합니다.
	// 그런데, @Data 어노테이션이 기본으로 만들어주는 Setter 메소드를 제외하고
	// 왜 아래와 같이 개발자가 직접 선언해야 할까??? 코드 보시면 앱!니다.
	public void setMyTeam(Team myTeam) {
		log.trace("setMyTeam({}) invoked.", myTeam);
		
		Team oldMyTeam = this.getMyTeam();	// 현재 소속된 팀을 얻어내고...

		// (1) 이전 팀에서 현재의 멤버를 제거하고,
		if(oldMyTeam != null) {	// 이미 어떤 기존 팀에 소속되어있는 팀원이라면...
			boolean isRemoved = oldMyTeam.getMembers().remove(this); // <--- *******
			log.info("\t+ isRemoved: {}", isRemoved);
		} // if
		
		// (2) 새롭게 매개변수로 전달된 새로운 팀의 멤버로 참여시켜야 하겠죠!?
		if(myTeam != null) {
			// 2-1. PK 속성필드에 값 설정.
			this.myTeam = myTeam;
			
			// 2-2. 새로운 팀의 멤버로 현재 멤버를 등록시킴. // <--- *******
			this.myTeam.getMembers().add(this);
		} // if
	} // setMyTeam
	
	
	
} // end class

