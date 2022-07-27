package com.example.demo.domain;

import lombok.*;
import javax.persistence.*;
import java.time.*;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long id;

	@Column(name = "VALUE")
	@Getter @Setter private String value;

}
