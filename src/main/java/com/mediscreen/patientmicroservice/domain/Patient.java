package com.mediscreen.patientmicroservice.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 30)
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 30, min = 3)
    private String lastName;

    @Column(length = 30)
    @NotBlank(message = "First name is mandatory")
    @Size(max = 30, min = 3)
    private String firstName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth is mandatory")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Column(length = 1)
    @NotBlank(message = "Gender is mandatory")
    @Size(max = 1, min = 1)
    private String sex;

    @Column(length = 120)
    @NotBlank(message = "Address is mandatory")
    @Size(max = 120, min = 10)
    private String homeAddress;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}", message = "Phone number must be in xxx-xxx-xxxx format")
    private String phoneNumber;

    public Patient() {
    }

    public Patient(Long id, String lastName, String firstName, LocalDate dateOfBirth, String sex, String homeAddress, String phoneNumber) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.homeAddress = homeAddress;
        this.phoneNumber = phoneNumber;
    }

    public Patient(String lastName, String firstName, LocalDate dateOfBirth, String sex, String homeAddress, String phoneNumber) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.homeAddress = homeAddress;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id) && Objects.equals(lastName, patient.lastName) && Objects.equals(firstName, patient.firstName) && Objects.equals(dateOfBirth, patient.dateOfBirth) && Objects.equals(sex, patient.sex) && Objects.equals(homeAddress, patient.homeAddress) && Objects.equals(phoneNumber, patient.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastName, firstName, dateOfBirth, sex, homeAddress, phoneNumber);
    }
}
