package kz.teamvictus.store.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.teamvictus.store.core.util.audit.Auditable;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "All details about the User. ")
public class User extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated employee ID")
    private long id;

    @NotNull
    @Size(min = 2, message = "First Name should have atleast 2 characters")
    @Column(name = "first_name", nullable = false)
    @ApiModelProperty(notes = "The user first name")
    private String firstName;


    @NotNull
    @Size(min = 2, message = "First Name should have atleast 2 characters")
    @Column(name = "last_name", nullable = false)
    @ApiModelProperty(notes = "The user last name")
    private String lastName;

    @Email
    @NotBlank
    @Column(name = "email_address", nullable = false)
    @ApiModelProperty(notes = "The user email id")
    private String email;

    @NotNull
    @Size(min = 2, message = "Passport should have atleast 2 characters")
    private String password;

    @Column(name = "state", nullable = false)
    private Integer state;
}
