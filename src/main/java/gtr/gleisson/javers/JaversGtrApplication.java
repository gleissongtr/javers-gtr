package gtr.gleisson.javers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnore;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class JaversGtrApplication {

	public static void main(String[] args) {
		SpringApplication.run(JaversGtrApplication.class, args);
	}

}

@Configuration
@EnableSwagger2
class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();
	}

}

@Entity
@Table(name = "Person")
class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	String id;
	String name;
	String description;

	@JsonIgnore
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	List<Phone> phones = new ArrayList<>();

	public Person() {
	}

	public Person(String id, String name, String description, List<Phone> phones) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.phones = phones;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

@Entity
@Table(name = "Phone")
class Phone implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	String id;
	String number;

	@ManyToOne(targetEntity = Person.class)
	@JoinColumn(name = "id_person")
	Person person;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Phone other = (Phone) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

// @Repository
// @RepositoryRestController()
// @RepositoryRestResource(path = "person")
@JaversSpringDataAuditable
interface PersonRepo extends CrudRepository<Person, String> {
}

@JaversSpringDataAuditable
interface PhoneRepo extends CrudRepository<Phone, String> {
}

@RestController
@RequestMapping("/person")
class WelcomeController {
	PersonRepo personRepo;

	public WelcomeController(PersonRepo personRepo) {
		this.personRepo = personRepo;
	}

	@GetMapping("/")
	public Iterable<Person> getPerson() {
		return personRepo.findAll();
	}

	@GetMapping("/{id}")
	public Person getPerson(@PathVariable String id) {
		return personRepo.findOne(id);
	}

	@PutMapping("/")
	public Person update(@RequestBody Person person) {
		return personRepo.save(person);
	}

	@PostMapping("/")
	public Person save(@RequestBody Person person) {
		return personRepo.save(person);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable String id) {
		personRepo.delete(id);
	}
}

@RestController
@RequestMapping("/phone")
class PhoneController {
	PhoneRepo phoneRepo;

	public PhoneController(PhoneRepo phoneRepo) {
		this.phoneRepo = phoneRepo;
	}

	@GetMapping("/")
	public Iterable<Phone> getPhone() {
		return phoneRepo.findAll();
	}

	@PutMapping("/")
	public Phone update(@RequestBody Phone phone) {
		return phoneRepo.save(phone);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable String id) {
		phoneRepo.delete(id);
	}
}

@RestController
@RequestMapping(value = "/audit")
class AuditController {

	private final Javers javers;

	@Autowired
	public AuditController(Javers javers) {
		this.javers = javers;
	}

	@GetMapping(value = "/person", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getPersonChanges() {
		findGrouped();

		// findSnapshotsTest();
		// findShadowsTest();
		// findChangesTest();
		return "";
	}

	private String findGrouped() {
		String trace = "=============================";

		QueryBuilder jqlQuery = QueryBuilder.anyDomainObject();
		Changes changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());

		changes.groupByCommit().forEach(byCommit -> {
			System.out.println(trace);
			System.out.println("COMMIT ID: " + byCommit.getCommit().getId());
			System.out.println("AUTOR: " + byCommit.getCommit().getAuthor());
			System.out.println("DATA: " + byCommit.getCommit().getCommitDate());
			// for (Change changeByCommit : byCommit.get()) {
			// System.out.println("TIPO DA MUDANÇA:
			// "+changeByCommit.getClass().getSimpleName());
			// if(changeByCommit instanceof ValueChange){
			// ValueChange valueChange = (ValueChange) changeByCommit;
			// System.out.println(valueChange.getPropertyName()+
			// " MUDOU DE '"+valueChange.getLeft()+"' PARA '"+valueChange.getRight()+"'");
			// }
			// }

			byCommit.groupByObject().forEach(byObject -> {
				System.out.println("***************");
				System.out.println("ENTIDADE: " + byObject.getGlobalId().getTypeName());
				for (Change changeByCommit : byCommit.get()) {
					System.out.println("TIPO DA MUDANÇA: " + changeByCommit.getClass().getSimpleName());
					if (changeByCommit instanceof ValueChange) {
						ValueChange valueChange = (ValueChange) changeByCommit;
						System.out.println(valueChange.getPropertyName() + " MUDOU DE '" + valueChange.getLeft()
								+ "' PARA '" + valueChange.getRight() + "'");
					}
					if (changeByCommit instanceof ReferenceChange) {
						ReferenceChange referenceChange = (ReferenceChange) changeByCommit;
						System.out.println(referenceChange.getPropertyName() + " MUDOU DE '" + referenceChange.getLeft()
								+ "' PARA '" + referenceChange.getRight() + "'");
					}
				}
			});

		});

		// changes.groupByCommit().forEach(byCommit -> {
		// System.out.println("commit " + byCommit.getCommit().getId()
		// +" author " + byCommit.getCommit().getAuthor()
		// +" data " + byCommit.getCommit().getCommitDate()
		// +" properties " + byCommit.getCommit().getProperties());
		// byCommit.groupByObject().forEach(byObject -> {
		// System.out.println("new: "+ byObject.getNewObjects());
		// System.out.println("removed: "+ byObject.getObjectsRemoved());
		// System.out.println("changes: "+ byObject.getPropertyChanges());
		//
		// System.out.println(" changes on " + byObject.getGlobalId().value() + " : ");
		// byObject.get().forEach(change -> {
		// System.out.println(" - " + change);
		// });
		// });
		// });

		return javers.getJsonConverter().toJson(changes);
	}

	private Class<? extends Change> getTipoMudanca(Change change) {
		if (change instanceof NewObject) {
			return NewObject.class;
		}
		if (change instanceof ObjectRemoved) {
			return ObjectRemoved.class;
		}
		if (change instanceof ReferenceChange) {
			return ReferenceChange.class;
		}
		if (change instanceof ValueChange) {
			return ValueChange.class;
		}
		return null;
	}

	private void findSnapshotsTest() {
		System.out.println("");
		System.out.println("FIND SNAPSHOTS");
		QueryBuilder jqlQuery = QueryBuilder.byClass(Person.class);

		List<CdoSnapshot> cdoSnapshots = javers.findSnapshots(jqlQuery.withNewObjectChanges(true).build());

		// Diff diff = javers.compare(new Person("1","11","111"), new
		// Person("1","22","222"));
		// System.out.println(diff.getChanges());

		for (CdoSnapshot snap : cdoSnapshots) {
			System.out.println("*********************************");
			System.out.println("AUTOR: " + snap.getCommitMetadata().getAuthor());
			System.out.println("COMMIT: " + snap.getCommitMetadata().getId());
			System.out.println("DATA: " + snap.getCommitMetadata().getCommitDate());
			System.out.println("TIPO: " + snap.getType());
			// System.out.println("ESTADO ATUAL: ");
			// for (String propertieAtual : snap.getState().getPropertyNames()) {
			// System.out.println(" " + propertieAtual + ": " +
			// snap.getPropertyValue(propertieAtual));
			// }

			System.out.println("ATRIBUTOS ALTERADOS: ");
			for (String propertieAterado : snap.getChanged()) {
				System.out.println("    " + propertieAterado + ": " + snap.getPropertyValue(propertieAterado));
			}
		}
	}

	private void findShadowsTest() {
		System.out.println("");
		System.out.println("FIND SHADOWS");
		QueryBuilder jqlQuery = QueryBuilder.byClass(Person.class);
		List<Shadow<Person>> sdws = javers.findShadows(jqlQuery.withNewObjectChanges(true).build());

		for (Shadow<Person> s : sdws) {
			System.out.println("---------------------------------");
			System.out.println(javers.getJsonConverter().toJson(s));
			System.out.println(javers.getJsonConverter().toJson(s.get()));
		}
	}

	private String findChangesTest() {
		System.out.println("");
		System.out.println("FIND CHANGES");

		QueryBuilder jqlQuery = QueryBuilder.byClass(Person.class);

		List<Change> changes = javers.findChanges(jqlQuery.withNewObjectChanges().build());

		BigDecimal id = null;
		String trace = "=============================";
		System.out.println(trace);
		System.out.println(changes.get(0).getCommitMetadata().get().getId());
		for (Change change : changes) {
			if (id == null || change.getCommitMetadata().get().getId().valueAsNumber().equals(id)) {
				System.out.println(change);
				id = change.getCommitMetadata().get().getId().valueAsNumber();

				// changeMetadataDTO.getChanges().add(
				// new ValueChangeDTO(change.getCommitMetadata().get().getProperties()));
				// System.out.println("properties "+change.getAffectedObject().get());

			} else {
				System.out.println(trace);
				System.out.println(change.getCommitMetadata().get().getId());
				id = change.getCommitMetadata().get().getId().valueAsNumber();
			}
		}

		// for (ChangeMetadataDTO changeDTO : changeMetadataDTOs) {
		// System.out.println(changeDTO.getId());
		// System.out.println(changeDTO.getAuthor());
		// System.out.println(changeDTO.getCommitDate());
		// for (ValueChangeDTO valueChangeDTO : changeDTO.getChanges()) {
		// System.out.println(valueChangeDTO.getProperty());
		// System.out.println(valueChangeDTO.getLeft()+" - "+valueChangeDTO.getRight());
		// }
		// }

		// List<CdoSnapshot> snap = javers.findSnapshots(jqlQuery.build());
		// for (CdoSnapshot cdoSnapshot : snap) {
		// System.out.println("COMMIT: "+cdoSnapshot.getCommitMetadata().getId());
		// System.out.println("TIPO: "+cdoSnapshot.getType());
		// System.out.println("ESTADO: "+cdoSnapshot.getState());
		// System.out.println("MUDANÇA: ");
		// for (String propertie : cdoSnapshot.getChanged()) {
		// System.out.println(">>>"+propertie+":
		// "+cdoSnapshot.getPropertyValue(propertie));
		// }

		// }
		System.out.println(javers.getJsonConverter().toJson(changes));
		return javers.getJsonConverter().toJson(changes);
	}
}
