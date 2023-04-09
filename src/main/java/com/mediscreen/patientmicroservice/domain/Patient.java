package com.mediscreen.patientmicroservice.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "Last name is mandatory")
    private String lastName;

    @NotNull(message = "First name is mandatory")
    private String firstName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is mandatory")
    private String sex;

    @NotNull(message = "Address is mandatory")
    private String homeAddress;

    @NotNull(message = "Phone number is mandatory")
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
