package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;


@Data

@Entity(name = "Team")
@Table(name="team")
public class Team implements Serializable { // Parent, One(1)
	@Serial private static final long serialVersionUID = 1L;

	// 1. Set PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "team_id")
	private Long id;				// PK
	
	
	// 2. 일반속성들
	@Basic(optional = false)		// Not Null Contraint
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
	
	// 주의사항: mappedBy 속성에 지정하는 이름은, 부모(1)가 찾아가야 자식(Many)엔티티에
	//           선언된 "FK역할을 필드(=속성)의 이름"을 지정해야만 합니다.
	
//	@OneToMany(mappedBy = "myTeam")									// 1
	@OneToMany(mappedBy = "myTeam", targetEntity = Member.class)	// 2
//	@OneToMany(mappedBy = "myTeam", targetEntity = Member.class, 
//				fetch = FetchType.LAZY)								// 3, EAGER: 즉시로딩, LAZY: 지연로딩
//	@OneToMany(mappedBy = "myTeam", targetEntity = Member.class,
//				cascade = CascadeType.ALL)							// 4
	
	// 부모 엔티티에 속한 자식 엔티티를 보관할 리스트는 
	// 반드시 초기값으로 비어있는 리스트를 만들어야 합니다.	<--- ***
	// 이유는, JPA구현체(= JPA Provider)가 연관관계에 따라,
	// 팀을 조회시(em.find 메소드 수행시), 이 리스트에 요소는 만들어 넣어주지만
	// 리스트 자체는 만들어 주지 않기 때문에, 개발자가 비어있는 리스트를 초기값으로
	// 반드시 만들어 넣어주셔야 합니다.
	
	@ToString.Exclude
	private List<Member> members = new ArrayList<>(); 	// Set Children
	
	
   
} // end class

