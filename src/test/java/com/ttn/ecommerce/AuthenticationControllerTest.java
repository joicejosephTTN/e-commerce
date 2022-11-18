package com.ttn.ecommerce;

import com.ttn.ecommerce.controller.DummyController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(SpringExtension.class)
@WebMvcTest(DummyController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    private String obtainAccessToken(String username, String password) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);

        ResultActions result
                = mvc.perform(post("/oauth/token")
                        .params(params)
                        .with(httpBasic("ecommerce-app","supersecret"))
                        .accept("application/json;charset=UTF-8"));

        System.out.println("result>>>>" + result);

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    @Test
    void testIndex() throws Exception {
        String accessToken = obtainAccessToken("superuser@gmail.com", "supersecret");

        System.out.println("AccessToken>>>>>>>" + accessToken);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/")
                .header("Authorization", "Bearer " + accessToken);
        MvcResult result = mvc.perform(request).andReturn();
        Assertions.assertEquals("Products", result.getResponse().getContentAsString());
    }
}
