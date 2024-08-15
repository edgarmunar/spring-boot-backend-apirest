package com.bolsadeideas.spring.boot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


@Entity     
@Table(name="cliente")
public class Cliente  implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//@Column Se puede indicar el nombre, largo, tamaño y @NotEmpty y @Size son validadores para que no lleguen vacios o no sehan largos etc...
	@NotEmpty(message="Ojito el nombre es requerido!")
	@Size(min=4,max=12)	
	@Column(nullable = false)
	private String nombre;
	@NotEmpty
	private String apellido;

	@NotEmpty(message="Ojito el apellido es requerido!")
	@Email
	@Column(nullable = false,unique = true)
	private String email;
	
	@Column(name = "create_at")
	@Temporal(TemporalType.DATE)
	private Date create_at;

	@PrePersist
	public void prePersist(){
		create_at = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return create_at;
	}

	public void setCreateAt(Date createAt) {
		this.create_at = createAt;
	}

	private static final long serialVersionUID = 1L;
}
