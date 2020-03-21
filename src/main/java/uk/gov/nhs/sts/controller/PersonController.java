package uk.gov.nhs.sts.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import uk.gov.nhs.sts.model.dto.PersonDTO;
import uk.gov.nhs.sts.service.PeopleManagementService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/people")
@Api(value = "/api/people", tags = {"People API"}, protocols = "HTTP")
public class PersonController {

  private final PeopleManagementService service;

  @GetMapping(value = "/{staffNumber}", produces = APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Gets a person's details",
      notes = "This endpoint fetches a person's details based on the staff number.",
      response = PersonDTO.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Person details retrieved"),
      @ApiResponse(code = 404, message = "Person not found"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<PersonDTO> getPerson(
      @ApiParam(value = "Staff number of the person to be created",
          required = true) @PathVariable("staffNumber") final String staffNumber) {
    final PersonDTO person = this.service.fetchPersonByStaffNumber(staffNumber);
    if (person != null) {
      return new ResponseEntity<>(person, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Gets all the people details",
      notes = "This endpoint fetches all the people details.", response = List.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "People retrieved"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<List<PersonDTO>> getPeople() {
    final List<PersonDTO> personDtos = this.service.fetchPeople();
    return new ResponseEntity<>(personDtos, HttpStatus.OK);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Creates a new person",
      notes = "This endpoint creates a new person, optionally creating the skills if they don't exist.",
      response = PersonDTO.class)
  @ApiResponses(value = {@ApiResponse(code = 201, message = "Person created"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 409, message = "Person with provided staff number already exists"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody final PersonDTO personDto,
      final BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (this.service.fetchPersonByStaffNumber(personDto.getStaffNumber()) != null) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    this.service.createOrUpdatePersonWithSkills(personDto, false);

    return new ResponseEntity<>(personDto, HttpStatus.CREATED);
  }

  @PutMapping("/{staffNumber}")
  @ApiOperation(value = "Updates a person's details",
      notes = "This endpoint updates the details of a person. Skills of a person can be managed (created/updated/deleted) using this endpoint.",
      response = PersonDTO.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Person updated successfully"),
      @ApiResponse(code = 404, message = "Person not found"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<PersonDTO> updatePerson(
      @ApiParam(value = "Staff number of the person to be updated",
          required = true) @PathVariable("staffNumber") final String staffNumber,
      @Valid @RequestBody final PersonDTO personDto, final BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (this.service.fetchPersonByStaffNumber(staffNumber) == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    this.service.createOrUpdatePersonWithSkills(personDto, true);

    return new ResponseEntity<>(personDto, HttpStatus.OK);
  }

  @DeleteMapping("/{staffNumber}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Deletes a person",
      notes = "This endpoint deletes a person and it's skill associations.", response = Void.class)
  @ApiResponses(value = {@ApiResponse(code = 204, message = "Person deleted"),
      @ApiResponse(code = 404, message = "Person not found"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<Void> deletePerson(
      @ApiParam(value = "Staff number of the person to be deleted",
          required = true) @PathVariable("staffNumber") final String staffNumber) {

    final PersonDTO person = this.service.fetchPersonByStaffNumber(staffNumber);
    if (person != null) {
      this.service.deletePerson(person);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
