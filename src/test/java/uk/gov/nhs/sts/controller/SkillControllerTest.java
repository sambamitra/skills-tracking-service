package uk.gov.nhs.sts.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.nhs.sts.model.dto.SkillDTO;
import uk.gov.nhs.sts.service.PeopleManagementService;

@RunWith(SpringRunner.class)
@WebMvcTest(SkillController.class)
public class SkillControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PeopleManagementService service;

  private static final String BASE_URI = "/api/skills/";

  @Test
  public void getSkillShouldReturnOkIfSkillExists() throws Exception {
    // given
    final SkillDTO skill = SkillDTO.builder().name("Java").build();
    given(this.service.fetchSkillByName("Java")).willReturn(skill);

    // when/then
    this.mockMvc.perform(get(BASE_URI + "Java")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk()).andExpect(content().json("{\"name\":\"Java\"}"));
  }

  @Test
  public void getSkillShouldReturnNotFoundIfPersonDoesNotExist() throws Exception {
    // given
    given(this.service.fetchSkillByName("NodeJS")).willReturn(null);

    // when/then
    this.mockMvc.perform(get(BASE_URI + "NodeJs")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());
  }

  @Test
  public void getPeopleShouldReturnOk() throws Exception {
    // given
    final SkillDTO skill = SkillDTO.builder().name("Java").build();
    given(this.service.fetchSkills()).willReturn(Arrays.asList(skill));

    // when/then
    this.mockMvc.perform(get(BASE_URI)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk()).andExpect(content().json("[{\"name\":\"Java\"}]"));

  }

  @Test
  public void creatSkillShouldReturnBadRequestIfRequestHasMissingMandatoryFields()
      throws Exception {
    // given
    final SkillDTO skill = SkillDTO.builder().build();
    final String json = json(skill);

    // when/then
    this.mockMvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest());

  }

  @Test
  public void createSkillShouldReturnConflictIfSkillAlreadyExists() throws Exception {
    // given
    final SkillDTO skill = SkillDTO.builder().name("Java").build();
    final String json = json(skill);
    given(this.service.fetchSkillByName("Java")).willReturn(skill);

    // when/then
    this.mockMvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isConflict());

  }

  @Test
  public void createSkillShouldReturnCreated() throws Exception {
    // given
    final SkillDTO skill = SkillDTO.builder().name("Java").build();
    final String json = json(skill);

    // when/then
    this.mockMvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isCreated())
        .andExpect(content().json("{\"name\":\"Java\"}"));

  }

  @Test
  public void updateSkillShouldReturnBadRequestIfRequestHasMissingMandatoryFields()
      throws Exception {
    // given
    final SkillDTO skill = SkillDTO.builder().build();
    final String json = json(skill);

    // when/then
    this.mockMvc
        .perform(put(BASE_URI + "NodeJs").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest());

  }

  @Test
  public void updateSkillShouldReturnNotFoundIfSkillDoesNotExist() throws Exception {
    // given
    final SkillDTO skill = SkillDTO.builder().name("Java").build();
    final String json = json(skill);
    given(this.service.fetchSkillByName("Java")).willReturn(null);

    // when/then
    this.mockMvc
        .perform(put(BASE_URI + "Java").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isNotFound());

  }

  @Test
  public void updatePersonShouldReturnOk() throws Exception {
    // given
    final SkillDTO skill = SkillDTO.builder().name("Java").build();
    final String json = json(skill);
    given(this.service.fetchSkillByName("Java")).willReturn(skill);

    // when/then
    this.mockMvc
        .perform(put(BASE_URI + "Java").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
        .andExpect(content().json("{\"name\":\"Java\"}"));

  }

  @Test
  public void deleteSkillShouldReturnNotFoundIfSkillDoesNotExist() throws Exception {
    // given
    given(this.service.fetchSkillByName("Cucumber")).willReturn(null);

    // when/then
    this.mockMvc.perform(delete(BASE_URI + "Cucumber")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

  }

  @Test
  public void deletePersonShouldReturnNoContent() throws Exception {
    // given
    given(this.service.fetchSkillByName("Selenium"))
        .willReturn(SkillDTO.builder().name("Selenium").build());

    // when/then
    this.mockMvc.perform(delete(BASE_URI + "Selenium")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());

  }

  private static String json(Object o) throws IOException {
    ObjectMapper map = new ObjectMapper();
    return map.writeValueAsString(o);
  }


}
