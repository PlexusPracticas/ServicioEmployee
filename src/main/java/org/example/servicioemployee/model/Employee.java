package org.example.servicioemployee.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="employees")
public class Employee {
    @Id
    /**se autoGenera un valor cada que se inserta un dato
     strategy = GenerationType.IDENTITY se autoincrementa el valor*/
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**indica que la columna no puede ser nulo*/
    @Column(length=50,name="name",nullable = false)
    @NotBlank(message = "name – Obligatorio. Longitud máxima de 50 caractere")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String name;

    @Column(length=150,name="surname",nullable = false)
    @NotBlank(message = "Surname – Obligatorio. Longitud máxima de 150 caractere")
    @Size(max = 150, message = "El surname no puede superar los 150 caracteres")
    private String surname;

    /**indica que el veradero nombre que aparecera en la columna serea mail_plexus*/
    @Column(length=200,name="mail_plexus",nullable = false)
    @NotBlank(message = "PlexusMail – Obligatorio. Error de formato de email")
    @Email
    @Size(max = 200, message = "El Mail Plexus no puede superar los 200 caracteres")
    private String mailPlexus;

    @Column(length=200,name="mail_client")
    @Email
    @Size(max = 200, message = "mailClient - Error de formato de email")
    private String mailClient;

    @Column(length = 50,name="client_id")
    @Size(max = 50, message = "ClientId – Debe ser un alfanumerico entre 0 y 50 caracteres")
    private String clientId;

    @Pattern(regexp = "^[0-9]{9}$", message = "phoneNumber - El numero de telefono solo puede contener caracteres numericos. Debe tener 9 caracteres de longitud")
    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="phone_sn")
    private String phoneSn;



}