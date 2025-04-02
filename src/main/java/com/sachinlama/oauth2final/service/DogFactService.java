package com.sachinlama.oauth2final.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class DogFactService {

    private static final String DOG_API_URL = "https://dogapi.dog/api/v2/facts";

    public String getRandomDogFact() {
        RestTemplate restTemplate = new RestTemplate();
        DogFactResponse response = restTemplate.getForObject(DOG_API_URL, DogFactResponse.class);

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            return response.getData().get(0).getAttributes().getBody();
        }

        return "No dog fact available at the moment.";
    }

    // Inner classes to parse the JSON response
    private static class DogFactResponse {
        private List<DogFact> data;

        public List<DogFact> getData() {
            return data;
        }

    }

    private static class DogFact {

        private Attributes attributes;


        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }
    }

    private static class Attributes {
        private String body;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}