package com.hexplosif.OptimodBackEnd;

import com.hexplosif.OptimodBackEnd.repository.DeliveryRequestRepository;
import com.hexplosif.OptimodBackEnd.repository.NodeRepository;
import com.hexplosif.OptimodBackEnd.repository.SegmentRepository;
import com.hexplosif.OptimodBackEnd.service.OptimodService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.w3c.dom.Document;

import java.io.File;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OptimodServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OptimodService optimodService;

    @ParameterizedTest
    @ValueSource(strings = {"petitPlan.xml", "moyenPlan.xml", "grandPlan.xml"})
    public void testCorrectLoadNode(String XMLFileName) throws Exception {
        optimodService.loadNode(XMLFileName);
    }
}
