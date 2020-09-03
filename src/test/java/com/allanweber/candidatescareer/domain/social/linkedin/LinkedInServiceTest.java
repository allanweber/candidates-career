package com.allanweber.candidatescareer.domain.social.linkedin;

import com.allanweber.candidatescareer.domain.candidate.SocialServiceHelper;
import com.allanweber.candidatescareer.domain.helper.ObjectMapperHelper;
import com.allanweber.candidatescareer.domain.social.linkedin.dto.LinkedInProfile;
import com.allanweber.candidatescareer.infrastructure.configuration.LinkedInConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LinkedInServiceTest {

    @Mock
    LinkedInConfiguration linkedInConfiguration;

    @Mock
    RestTemplate restTemplate;

    @Mock
    SocialServiceHelper socialServiceHelper;

    @InjectMocks
    LinkedInService linkedInService;

    @Test
    void test_generated_url() {
        mockLinkedInConfiguration();
        String state = UUID.randomUUID().toString();
        String expected = "https://www.linkedin.com/oauth/v2/authorization?response_type=code&scope=r_liteprofile%20r_emailaddress&client_id=123456&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Fcallback&state=" + state;

        String url = linkedInService.getAuthorizationUri(state);
        assertEquals(expected, url);
    }

    @Test
    void callBackLinkedIn() throws JsonProcessingException {
        String authorizationCode = "authorizationCode";
        mockLinkedInConfiguration();
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{ \"access_token\":\"access_token123456\" }");

        String json = mockData(true, true);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(ResponseEntity.ok(json));

        LinkedInProfile linkedInProfile = linkedInService.callback(authorizationCode);

        assertEquals("name", linkedInProfile.getFirstName());
        assertEquals("name", linkedInProfile.getFirstName());
    }

    @Test
    void callBackLinkedIn_null_Name() throws JsonProcessingException {
        String authorizationCode = "authorizationCode";
        mockLinkedInConfiguration();
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{ \"access_token\":\"access_token123456\" }");

        String json = mockData(false, false);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(ResponseEntity.ok(json));

        LinkedInProfile linkedInProfile = linkedInService.callback(authorizationCode);

        assertNull(linkedInProfile.getFirstName());
        assertNull(linkedInProfile.getFirstName());
    }

    @Test
    void callBackLinkedIn_null_NameValues() throws JsonProcessingException {
        String authorizationCode = "authorizationCode";
        mockLinkedInConfiguration();
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{ \"access_token\":\"access_token123456\" }");

        String json = mockData(true, false);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(ResponseEntity.ok(json));

        LinkedInProfile linkedInProfile = linkedInService.callback(authorizationCode);

        assertNull(linkedInProfile.getFirstName());
        assertNull(linkedInProfile.getFirstName());
    }

    @Test
    void callBackLinkedIn_null_ProfilePictureObject() throws JsonProcessingException {
        String authorizationCode = "authorizationCode";
        mockLinkedInConfiguration();
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{ \"access_token\":\"access_token123456\" }");

        LinkedInData linkedInData = new LinkedInData();
        String json = ObjectMapperHelper.get().writerFor(LinkedInData.class).writeValueAsString(linkedInData);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(ResponseEntity.ok(json));

        LinkedInProfile linkedInProfile = linkedInService.callback(authorizationCode);

        assertNull(linkedInProfile.getProfilePicture());
    }

    @Test
    void callBackLinkedIn_no_public_image() throws JsonProcessingException {
        String authorizationCode = "authorizationCode";
        mockLinkedInConfiguration();
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("{ \"access_token\":\"access_token123456\" }");

        LinkedInData linkedInData = new LinkedInData();
        ProfilePicture profilePicture = new ProfilePicture();
        DisplayImage displayImage = new DisplayImage();
        List<Element> elementList = new ArrayList<>();
        Element element = new Element();
        element.setAuthorizationMethod("PRIVATE");
        elementList.add(element);
        displayImage.setElements(elementList);
        profilePicture.setDisplayImage(displayImage);
        linkedInData.setProfilePicture(profilePicture);
        String json = ObjectMapperHelper.get().writerFor(LinkedInData.class).writeValueAsString(linkedInData);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(ResponseEntity.ok(json));

        LinkedInProfile linkedInProfile = linkedInService.callback(authorizationCode);

        assertNull(linkedInProfile.getProfilePicture());
    }

    String mockData(boolean addNames, boolean addNameValue) throws JsonProcessingException {
        LinkedInData linkedInData = new LinkedInData();
        if (addNames) {
            LinkedInName name = null;
            if (addNameValue) {
                name = new LinkedInName(new HashMap<>() {{
                    put("pt-br", "name");
                }});
            } else {
                name = new LinkedInName(null);
            }
            linkedInData.setFirstName(name);
            linkedInData.setLastName(name);
        }

        ProfilePicture profilePicture = new ProfilePicture();
        DisplayImage displayImage = new DisplayImage();
        List<Element> elementList = new ArrayList<>();
        Element element = new Element();
        element.setAuthorizationMethod("PUBLIC");
        ImageData imageData = new ImageData();
        StillImage stillImage = new StillImage();
        StorageSize storageSize = new StorageSize();
        storageSize.setHeight(400);
        storageSize.setWidth(400);
        stillImage.setStorageSize(storageSize);
        imageData.setStillImage(stillImage);
        element.setData(imageData);
        IdentifierImage identifierImage = new IdentifierImage();
        identifierImage.setIdentifier("image identifier");
        element.setIdentifiers(Collections.singletonList(identifierImage));
        elementList.add(element);
        displayImage.setElements(elementList);
        profilePicture.setDisplayImage(displayImage);
        linkedInData.setProfilePicture(profilePicture);

        return ObjectMapperHelper.get().writerFor(LinkedInData.class).writeValueAsString(linkedInData);
    }

    private void mockLinkedInConfiguration() {
        when(linkedInConfiguration.getClientId()).thenReturn("123456");
        when(linkedInConfiguration.getClientSecret()).thenReturn("456789");
        when(linkedInConfiguration.getRedirectHost()).thenReturn("http://localhost:8080");
    }
}