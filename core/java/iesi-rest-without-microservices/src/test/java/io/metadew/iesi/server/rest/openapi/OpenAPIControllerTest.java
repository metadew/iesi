package io.metadew.iesi.server.rest.openapi;


import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.openapi.TransformResult;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.ComponentParameterDto;
import io.metadew.iesi.server.rest.component.dto.ComponentVersionDto;
import io.metadew.iesi.server.rest.configuration.IesiConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionEnvironmentDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionParameterDto;
import io.metadew.iesi.server.rest.openapi.dto.TransformResultDto;
import io.metadew.iesi.server.rest.openapi.dto.TransformResultDtoResourceAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OpenAPIController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {OpenAPIController.class, TransformResultDtoResourceAssembler.class,
        TransformResultDto.class, TestConfiguration.class, IesiConfiguration.class, IesiSecurityChecker.class})
@ActiveProfiles("test")
@DirtiesContext
class OpenAPIControllerTest {

    byte[] yamlFile;
    byte[] jsonFile;
    String title;
    String version;

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OpenAPIService openAPIService;
    @MockBean
    private TransformResultDtoResourceAssembler transformResultDtoResourceAssembler;

    @BeforeEach
    public void init() {
        String ymlBase64 = "b3BlbmFwaTogMy4wLjINCmluZm86DQogIHRpdGxlOiBTd2FnZ2VyIFBldHN0b3JlIC0gT3BlbkFQSSAzLjANCiAgZGVzY3JpcHRpb246IHwtDQogICAgVGhpcyBpcyBhIHNhbXBsZSBQZXQgU3RvcmUgU2VydmVyIGJhc2VkIG9uIHRoZSBPcGVuQVBJIDMuMCBzcGVjaWZpY2F0aW9uLiAgWW91IGNhbiBmaW5kIG91dCBtb3JlIGFib3V0DQogICAgU3dhZ2dlciBhdCBbaHR0cDovL3N3YWdnZXIuaW9dKGh0dHA6Ly9zd2FnZ2VyLmlvKS4gSW4gdGhlIHRoaXJkIGl0ZXJhdGlvbiBvZiB0aGUgcGV0IHN0b3JlLCB3ZSd2ZSBzd2l0Y2hlZCB0byB0aGUgZGVzaWduIGZpcnN0IGFwcHJvYWNoIQ0KICAgIFlvdSBjYW4gbm93IGhlbHAgdXMgaW1wcm92ZSB0aGUgQVBJIHdoZXRoZXIgaXQncyBieSBtYWtpbmcgY2hhbmdlcyB0byB0aGUgZGVmaW5pdGlvbiBpdHNlbGYgb3IgdG8gdGhlIGNvZGUuDQogICAgVGhhdCB3YXksIHdpdGggdGltZSwgd2UgY2FuIGltcHJvdmUgdGhlIEFQSSBpbiBnZW5lcmFsLCBhbmQgZXhwb3NlIHNvbWUgb2YgdGhlIG5ldyBmZWF0dXJlcyBpbiBPQVMzLg0KICAgIFNvbWUgdXNlZnVsIGxpbmtzOg0KICAgIC0gW1RoZSBQZXQgU3RvcmUgcmVwb3NpdG9yeV0oaHR0cHM6Ly9naXRodWIuY29tL3N3YWdnZXItYXBpL3N3YWdnZXItcGV0c3RvcmUpDQogICAgLSBbVGhlIHNvdXJjZSBBUEkgZGVmaW5pdGlvbiBmb3IgdGhlIFBldCBTdG9yZV0oaHR0cHM6Ly9naXRodWIuY29tL3N3YWdnZXItYXBpL3N3YWdnZXItcGV0c3RvcmUvYmxvYi9tYXN0ZXIvc3JjL21haW4vcmVzb3VyY2VzL29wZW5hcGkueWFtbCkNCiAgdGVybXNPZlNlcnZpY2U6IGh0dHA6Ly9zd2FnZ2VyLmlvL3Rlcm1zLw0KICBjb250YWN0Og0KICAgIGVtYWlsOiBhcGl0ZWFtQHN3YWdnZXIuaW8NCiAgbGljZW5zZToNCiAgICBuYW1lOiBBcGFjaGUgMi4wDQogICAgdXJsOiBodHRwOi8vd3d3LmFwYWNoZS5vcmcvbGljZW5zZXMvTElDRU5TRS0yLjAuaHRtbA0KICB2ZXJzaW9uOiAxDQpleHRlcm5hbERvY3M6DQogIGRlc2NyaXB0aW9uOiBGaW5kIG91dCBtb3JlIGFib3V0IFN3YWdnZXINCiAgdXJsOiBodHRwOi8vc3dhZ2dlci5pbw0Kc2VydmVyczoNCiAgLSB1cmw6ICJodHRwczovL3BldHN0b3JlMy5zd2FnZ2VyLmlvL2FwaS92My8iDQp0YWdzOg0KICAtIG5hbWU6IHBldA0KICAgIGRlc2NyaXB0aW9uOiBFdmVyeXRoaW5nIGFib3V0IHlvdXIgUGV0cw0KICAgIGV4dGVybmFsRG9jczoNCiAgICAgIGRlc2NyaXB0aW9uOiBGaW5kIG91dCBtb3JlDQogICAgICB1cmw6IGh0dHA6Ly9zd2FnZ2VyLmlvDQogIC0gbmFtZTogc3RvcmUNCiAgICBkZXNjcmlwdGlvbjogT3BlcmF0aW9ucyBhYm91dCB1c2VyDQogIC0gbmFtZTogdXNlcg0KICAgIGRlc2NyaXB0aW9uOiBBY2Nlc3MgdG8gUGV0c3RvcmUgb3JkZXJzDQogICAgZXh0ZXJuYWxEb2NzOg0KICAgICAgZGVzY3JpcHRpb246IEZpbmQgb3V0IG1vcmUgYWJvdXQgb3VyIHN0b3JlDQogICAgICB1cmw6IGh0dHA6Ly9zd2FnZ2VyLmlvDQpwYXRoczoNCiAgIi9wZXQiOg0KICAgIHB1dDoNCiAgICAgIHRhZ3M6DQogICAgICAgIC0gcGV0DQogICAgICBzdW1tYXJ5OiBVcGRhdGUgYW4gZXhpc3RpbmcgcGV0DQogICAgICBkZXNjcmlwdGlvbjogVXBkYXRlIGFuIGV4aXN0aW5nIHBldCBieSBJZA0KICAgICAgb3BlcmF0aW9uSWQ6IHVwZGF0ZVBldA0KICAgICAgcmVxdWVzdEJvZHk6DQogICAgICAgIGRlc2NyaXB0aW9uOiBVcGRhdGUgYW4gZXhpc3RlbnQgcGV0IGluIHRoZSBzdG9yZQ0KICAgICAgICBjb250ZW50Og0KICAgICAgICAgIGFwcGxpY2F0aW9uL2pzb246DQogICAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICAgICAgICBhcHBsaWNhdGlvbi94bWw6DQogICAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICAgICAgICBhcHBsaWNhdGlvbi94LXd3dy1mb3JtLXVybGVuY29kZWQ6DQogICAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICAgICAgcmVxdWlyZWQ6IHRydWUNCiAgICAgIHJlc3BvbnNlczoNCiAgICAgICAgJzIwMCc6DQogICAgICAgICAgZGVzY3JpcHRpb246IFN1Y2Nlc3NmdWwgb3BlcmF0aW9uDQogICAgICAgICAgY29udGVudDoNCiAgICAgICAgICAgIGFwcGxpY2F0aW9uL3htbDoNCiAgICAgICAgICAgICAgc2NoZW1hOg0KICAgICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICAgICAgICAgIGFwcGxpY2F0aW9uL2pzb246DQogICAgICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiDQogICAgICAgICc0MDAnOg0KICAgICAgICAgIGRlc2NyaXB0aW9uOiBJbnZhbGlkIElEIHN1cHBsaWVkDQogICAgICAgICc0MDQnOg0KICAgICAgICAgIGRlc2NyaXB0aW9uOiBQZXQgbm90IGZvdW5kDQogICAgICAgICc0MDUnOg0KICAgICAgICAgIGRlc2NyaXB0aW9uOiBWYWxpZGF0aW9uIGV4Y2VwdGlvbg0KICAgICAgc2VjdXJpdHk6DQogICAgICAgIC0gcGV0c3RvcmVfYXV0aDoNCiAgICAgICAgICAgIC0gd3JpdGU6cGV0cw0KICAgICAgICAgICAgLSByZWFkOnBldHMNCmNvbXBvbmVudHM6DQogIHNjaGVtYXM6DQogICAgT3JkZXI6DQogICAgICB0eXBlOiBvYmplY3QNCiAgICAgIHByb3BlcnRpZXM6DQogICAgICAgIGlkOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBmb3JtYXQ6IGludDY0DQogICAgICAgICAgZXhhbXBsZTogMTANCiAgICAgICAgcGV0SWQ6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50NjQNCiAgICAgICAgICBleGFtcGxlOiAxOTg3NzINCiAgICAgICAgcXVhbnRpdHk6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50MzINCiAgICAgICAgICBleGFtcGxlOiA3DQogICAgICAgIHNoaXBEYXRlOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGZvcm1hdDogZGF0ZS10aW1lDQogICAgICAgIHN0YXR1czoNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBkZXNjcmlwdGlvbjogT3JkZXIgU3RhdHVzDQogICAgICAgICAgZXhhbXBsZTogYXBwcm92ZWQNCiAgICAgICAgICBlbnVtOg0KICAgICAgICAgICAgLSBwbGFjZWQNCiAgICAgICAgICAgIC0gYXBwcm92ZWQNCiAgICAgICAgICAgIC0gZGVsaXZlcmVkDQogICAgICAgIGNvbXBsZXRlOg0KICAgICAgICAgIHR5cGU6IGJvb2xlYW4NCiAgICAgIHhtbDoNCiAgICAgICAgbmFtZTogb3JkZXINCiAgICBDdXN0b21lcjoNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgaWQ6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50NjQNCiAgICAgICAgICBleGFtcGxlOiAxMDAwMDANCiAgICAgICAgdXNlcm5hbWU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogZmVoZ3V5DQogICAgICAgIGFkZHJlc3M6DQogICAgICAgICAgdHlwZTogYXJyYXkNCiAgICAgICAgICB4bWw6DQogICAgICAgICAgICBuYW1lOiBhZGRyZXNzZXMNCiAgICAgICAgICAgIHdyYXBwZWQ6IHRydWUNCiAgICAgICAgICBpdGVtczoNCiAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL0FkZHJlc3MiDQogICAgICB4bWw6DQogICAgICAgIG5hbWU6IGN1c3RvbWVyDQogICAgQWRkcmVzczoNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgc3RyZWV0Og0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6IDQzNyBMeXR0b24NCiAgICAgICAgY2l0eToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBQYWxvIEFsdG8NCiAgICAgICAgc3RhdGU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogQ0ENCiAgICAgICAgemlwOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6ICc5NDMwMScNCiAgICAgIHhtbDoNCiAgICAgICAgbmFtZTogYWRkcmVzcw0KICAgIENhdGVnb3J5Og0KICAgICAgdHlwZTogb2JqZWN0DQogICAgICBwcm9wZXJ0aWVzOg0KICAgICAgICBpZDoNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZm9ybWF0OiBpbnQ2NA0KICAgICAgICAgIGV4YW1wbGU6IDENCiAgICAgICAgbmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBEb2dzDQogICAgICB4bWw6DQogICAgICAgIG5hbWU6IGNhdGVnb3J5DQogICAgVXNlcjoNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgaWQ6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50NjQNCiAgICAgICAgICBleGFtcGxlOiAxMA0KICAgICAgICB1c2VybmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiB0aGVVc2VyDQogICAgICAgIGZpcnN0TmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBKb2huDQogICAgICAgIGxhc3ROYW1lOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6IEphbWVzDQogICAgICAgIGVtYWlsOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6IGpvaG5AZW1haWwuY29tDQogICAgICAgIHBhc3N3b3JkOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6ICcxMjM0NScNCiAgICAgICAgcGhvbmU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogJzEyMzQ1Jw0KICAgICAgICB1c2VyU3RhdHVzOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBkZXNjcmlwdGlvbjogVXNlciBTdGF0dXMNCiAgICAgICAgICBmb3JtYXQ6IGludDMyDQogICAgICAgICAgZXhhbXBsZTogMQ0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiB1c2VyDQogICAgVGFnOg0KICAgICAgdHlwZTogb2JqZWN0DQogICAgICBwcm9wZXJ0aWVzOg0KICAgICAgICBpZDoNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZm9ybWF0OiBpbnQ2NA0KICAgICAgICBuYW1lOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiB0YWcNCiAgICBQZXQ6DQogICAgICByZXF1aXJlZDoNCiAgICAgICAgLSBuYW1lDQogICAgICAgIC0gcGhvdG9VcmxzDQogICAgICB0eXBlOiBvYmplY3QNCiAgICAgIHByb3BlcnRpZXM6DQogICAgICAgIGlkOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBmb3JtYXQ6IGludDY0DQogICAgICAgICAgZXhhbXBsZTogMTANCiAgICAgICAgbmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBkb2dnaWUNCiAgICAgICAgY2F0ZWdvcnk6DQogICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvQ2F0ZWdvcnkiDQogICAgICAgIHBob3RvVXJsczoNCiAgICAgICAgICB0eXBlOiBhcnJheQ0KICAgICAgICAgIHhtbDoNCiAgICAgICAgICAgIHdyYXBwZWQ6IHRydWUNCiAgICAgICAgICBpdGVtczoNCiAgICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgICAgeG1sOg0KICAgICAgICAgICAgICBuYW1lOiBwaG90b1VybA0KICAgICAgICB0YWdzOg0KICAgICAgICAgIHR5cGU6IGFycmF5DQogICAgICAgICAgeG1sOg0KICAgICAgICAgICAgd3JhcHBlZDogdHJ1ZQ0KICAgICAgICAgIGl0ZW1zOg0KICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvVGFnIg0KICAgICAgICBzdGF0dXM6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZGVzY3JpcHRpb246IHBldCBzdGF0dXMgaW4gdGhlIHN0b3JlDQogICAgICAgICAgZW51bToNCiAgICAgICAgICAgIC0gYXZhaWxhYmxlDQogICAgICAgICAgICAtIHBlbmRpbmcNCiAgICAgICAgICAgIC0gc29sZA0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiBwZXQNCiAgICBBcGlSZXNwb25zZToNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgY29kZToNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZm9ybWF0OiBpbnQzMg0KICAgICAgICB0eXBlOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICBtZXNzYWdlOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiAiIyNkZWZhdWx0Ig0KICByZXF1ZXN0Qm9kaWVzOg0KICAgIFBldDoNCiAgICAgIGRlc2NyaXB0aW9uOiBQZXQgb2JqZWN0IHRoYXQgbmVlZHMgdG8gYmUgYWRkZWQgdG8gdGhlIHN0b3JlDQogICAgICBjb250ZW50Og0KICAgICAgICBhcHBsaWNhdGlvbi9qc29uOg0KICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICAgICAgYXBwbGljYXRpb24veG1sOg0KICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICBVc2VyQXJyYXk6DQogICAgICBkZXNjcmlwdGlvbjogTGlzdCBvZiB1c2VyIG9iamVjdA0KICAgICAgY29udGVudDoNCiAgICAgICAgYXBwbGljYXRpb24vanNvbjoNCiAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICB0eXBlOiBhcnJheQ0KICAgICAgICAgICAgaXRlbXM6DQogICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1VzZXIiDQogIHNlY3VyaXR5U2NoZW1lczoNCiAgICBwZXRzdG9yZV9hdXRoOg0KICAgICAgdHlwZTogb2F1dGgyDQogICAgICBmbG93czoNCiAgICAgICAgaW1wbGljaXQ6DQogICAgICAgICAgYXV0aG9yaXphdGlvblVybDogaHR0cHM6Ly9wZXRzdG9yZTMuc3dhZ2dlci5pby9vYXV0aC9hdXRob3JpemUNCiAgICAgICAgICBzY29wZXM6DQogICAgICAgICAgICB3cml0ZTpwZXRzOiBtb2RpZnkgcGV0cyBpbiB5b3VyIGFjY291bnQNCiAgICAgICAgICAgIHJlYWQ6cGV0czogcmVhZCB5b3VyIHBldHMNCiAgICBhcGlfa2V5Og0KICAgICAgdHlwZTogYXBpS2V5DQogICAgICBuYW1lOiBhcGlfa2V5DQogICAgICBpbjogaGVhZGVy";
        String jsonBase64 = "77u/ewogICJvcGVuYXBpIjogIjMuMC4yIiwKICAiaW5mbyI6IHsKICAgICJ0aXRsZSI6ICJTd2FnZ2VyIFBldHN0b3JlIC0gT3BlbkFQSSAzLjAiLAogICAgImRlc2NyaXB0aW9uIjogIlRoaXMgaXMgYSBzYW1wbGUgUGV0IFN0b3JlIFNlcnZlciBiYXNlZCBvbiB0aGUgT3BlbkFQSSAzLjAgc3BlY2lmaWNhdGlvbi4gIFlvdSBjYW4gZmluZCBvdXQgbW9yZSBhYm91dFxuU3dhZ2dlciBhdCBbaHR0cDovL3N3YWdnZXIuaW9dKGh0dHA6Ly9zd2FnZ2VyLmlvKS4gSW4gdGhlIHRoaXJkIGl0ZXJhdGlvbiBvZiB0aGUgcGV0IHN0b3JlLCB3ZSd2ZSBzd2l0Y2hlZCB0byB0aGUgZGVzaWduIGZpcnN0IGFwcHJvYWNoIVxuWW91IGNhbiBub3cgaGVscCB1cyBpbXByb3ZlIHRoZSBBUEkgd2hldGhlciBpdCdzIGJ5IG1ha2luZyBjaGFuZ2VzIHRvIHRoZSBkZWZpbml0aW9uIGl0c2VsZiBvciB0byB0aGUgY29kZS5cblRoYXQgd2F5LCB3aXRoIHRpbWUsIHdlIGNhbiBpbXByb3ZlIHRoZSBBUEkgaW4gZ2VuZXJhbCwgYW5kIGV4cG9zZSBzb21lIG9mIHRoZSBuZXcgZmVhdHVyZXMgaW4gT0FTMy5cblNvbWUgdXNlZnVsIGxpbmtzOlxuLSBbVGhlIFBldCBTdG9yZSByZXBvc2l0b3J5XShodHRwczovL2dpdGh1Yi5jb20vc3dhZ2dlci1hcGkvc3dhZ2dlci1wZXRzdG9yZSlcbi0gW1RoZSBzb3VyY2UgQVBJIGRlZmluaXRpb24gZm9yIHRoZSBQZXQgU3RvcmVdKGh0dHBzOi8vZ2l0aHViLmNvbS9zd2FnZ2VyLWFwaS9zd2FnZ2VyLXBldHN0b3JlL2Jsb2IvbWFzdGVyL3NyYy9tYWluL3Jlc291cmNlcy9vcGVuYXBpLnlhbWwpIiwKICAgICJ0ZXJtc09mU2VydmljZSI6ICJodHRwOi8vc3dhZ2dlci5pby90ZXJtcy8iLAogICAgImNvbnRhY3QiOiB7CiAgICAgICJlbWFpbCI6ICJhcGl0ZWFtQHN3YWdnZXIuaW8iCiAgICB9LAogICAgImxpY2Vuc2UiOiB7CiAgICAgICJuYW1lIjogIkFwYWNoZSAyLjAiLAogICAgICAidXJsIjogImh0dHA6Ly93d3cuYXBhY2hlLm9yZy9saWNlbnNlcy9MSUNFTlNFLTIuMC5odG1sIgogICAgfSwKICAgICJ2ZXJzaW9uIjogMQogIH0sCiAgImV4dGVybmFsRG9jcyI6IHsKICAgICJkZXNjcmlwdGlvbiI6ICJGaW5kIG91dCBtb3JlIGFib3V0IFN3YWdnZXIiLAogICAgInVybCI6ICJodHRwOi8vc3dhZ2dlci5pbyIKICB9LAogICJzZXJ2ZXJzIjogWwogICAgewogICAgICAidXJsIjogImh0dHBzOi8vcGV0c3RvcmUzLnN3YWdnZXIuaW8vYXBpL3YzLyIKICAgIH0KICBdLAogICJ0YWdzIjogWwogICAgewogICAgICAibmFtZSI6ICJwZXQiLAogICAgICAiZGVzY3JpcHRpb24iOiAiRXZlcnl0aGluZyBhYm91dCB5b3VyIFBldHMiLAogICAgICAiZXh0ZXJuYWxEb2NzIjogewogICAgICAgICJkZXNjcmlwdGlvbiI6ICJGaW5kIG91dCBtb3JlIiwKICAgICAgICAidXJsIjogImh0dHA6Ly9zd2FnZ2VyLmlvIgogICAgICB9CiAgICB9LAogICAgewogICAgICAibmFtZSI6ICJzdG9yZSIsCiAgICAgICJkZXNjcmlwdGlvbiI6ICJPcGVyYXRpb25zIGFib3V0IHVzZXIiCiAgICB9LAogICAgewogICAgICAibmFtZSI6ICJ1c2VyIiwKICAgICAgImRlc2NyaXB0aW9uIjogIkFjY2VzcyB0byBQZXRzdG9yZSBvcmRlcnMiLAogICAgICAiZXh0ZXJuYWxEb2NzIjogewogICAgICAgICJkZXNjcmlwdGlvbiI6ICJGaW5kIG91dCBtb3JlIGFib3V0IG91ciBzdG9yZSIsCiAgICAgICAgInVybCI6ICJodHRwOi8vc3dhZ2dlci5pbyIKICAgICAgfQogICAgfQogIF0sCiAgInBhdGhzIjogewogICAgIi9wZXQiOiB7CiAgICAgICJwb3N0IjogewogICAgICAgICJ0YWdzIjogWwogICAgICAgICAgInBldCIKICAgICAgICBdLAogICAgICAgICJzdW1tYXJ5IjogInFzZHFzZHQiLAogICAgICAgICJkZXNjcmlwdGlvbiI6ICJVcGRhdHFzZHFzZCBwZXQgYnkgSWQiLAogICAgICAgICJvcGVyYXRpb25JZCI6ICJ1cGRhdHFzZHFzZHQiLAogICAgICAgICJyZXF1ZXN0Qm9keSI6IHsKICAgICAgICAgICJkZXNjcmlwdGlvbiI6ICJVcGRhdGUgYW4gcXNkcXNkdGhlIHN0b3JlIiwKICAgICAgICAgICJjb250ZW50IjogewogICAgICAgICAgICAiYXBwbGljYXRpb24vanNvbiI6IHsKICAgICAgICAgICAgICAic2NoZW1hIjogewogICAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0IgogICAgICAgICAgICAgIH0KICAgICAgICAgICAgfSwKICAgICAgICAgICAgImFwcGxpY2F0aW9uL3htbCI6IHsKICAgICAgICAgICAgICAic2NoZW1hIjogewogICAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0IgogICAgICAgICAgICAgIH0KICAgICAgICAgICAgfSwKICAgICAgICAgICAgImFwcGxpY2F0aW9uL3gtd3d3LWZvcm0tdXJsZW5jb2RlZCI6IHsKICAgICAgICAgICAgICAic2NoZW1hIjogewogICAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0IgogICAgICAgICAgICAgIH0KICAgICAgICAgICAgfQogICAgICAgICAgfSwKICAgICAgICAgICJyZXF1aXJlZCI6IHRydWUKICAgICAgICB9LAogICAgICAgICJyZXNwb25zZXMiOiB7CiAgICAgICAgICAiMjAwIjogewogICAgICAgICAgICAiZGVzY3JpcHRpb24iOiAiU3VjY2Vzc2Z1bCBvcGVyYXRpb24iLAogICAgICAgICAgICAiY29udGVudCI6IHsKICAgICAgICAgICAgICAiYXBwbGljYXRpb24veG1sIjogewogICAgICAgICAgICAgICAgInNjaGVtYSI6IHsKICAgICAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0IgogICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgImFwcGxpY2F0aW9uL2pzb24iOiB7CiAgICAgICAgICAgICAgICAic2NoZW1hIjogewogICAgICAgICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiCiAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgICAgfQogICAgICAgICAgICB9CiAgICAgICAgICB9LAogICAgICAgICAgIjQwMCI6IHsKICAgICAgICAgICAgImRlc2NyaXB0aW9uIjogIkludmFsaWQgSUQgc3VwcGxpZWQiCiAgICAgICAgICB9LAogICAgICAgICAgIjQwNCI6IHsKICAgICAgICAgICAgImRlc2NyaXB0aW9uIjogIlBldCBub3QgZm91bmQiCiAgICAgICAgICB9LAogICAgICAgICAgIjQwNSI6IHsKICAgICAgICAgICAgImRlc2NyaXB0aW9uIjogIlZhbGlkYXRpb24gZXhjZXB0aW9uIgogICAgICAgICAgfQogICAgICAgIH0sCiAgICAgICAgInNlY3VyaXR5IjogWwogICAgICAgICAgewogICAgICAgICAgICAicGV0c3RvcmVfYXV0aCI6IFsKICAgICAgICAgICAgICAid3JpdGU6cGV0cyIsCiAgICAgICAgICAgICAgInJlYWQ6cGV0cyIKICAgICAgICAgICAgXQogICAgICAgICAgfQogICAgICAgIF0KICAgICAgfQogICAgfQogIH0sCiAgImNvbXBvbmVudHMiOiB7CiAgICAic2NoZW1hcyI6IHsKICAgICAgIk9yZGVyIjogewogICAgICAgICJ0eXBlIjogIm9iamVjdCIsCiAgICAgICAgInByb3BlcnRpZXMiOiB7CiAgICAgICAgICAiaWQiOiB7CiAgICAgICAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAgICAgICAiZm9ybWF0IjogImludDY0IiwKICAgICAgICAgICAgImV4YW1wbGUiOiAxMAogICAgICAgICAgfSwKICAgICAgICAgICJwZXRJZCI6IHsKICAgICAgICAgICAgInR5cGUiOiAiaW50ZWdlciIsCiAgICAgICAgICAgICJmb3JtYXQiOiAiaW50NjQiLAogICAgICAgICAgICAiZXhhbXBsZSI6IDE5ODc3MgogICAgICAgICAgfSwKICAgICAgICAgICJxdWFudGl0eSI6IHsKICAgICAgICAgICAgInR5cGUiOiAiaW50ZWdlciIsCiAgICAgICAgICAgICJmb3JtYXQiOiAiaW50MzIiLAogICAgICAgICAgICAiZXhhbXBsZSI6IDcKICAgICAgICAgIH0sCiAgICAgICAgICAic2hpcERhdGUiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICAgICAgICJmb3JtYXQiOiAiZGF0ZS10aW1lIgogICAgICAgICAgfSwKICAgICAgICAgICJzdGF0dXMiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICAgICAgICJkZXNjcmlwdGlvbiI6ICJPcmRlciBTdGF0dXMiLAogICAgICAgICAgICAiZXhhbXBsZSI6ICJhcHByb3ZlZCIsCiAgICAgICAgICAgICJlbnVtIjogWwogICAgICAgICAgICAgICJwbGFjZWQiLAogICAgICAgICAgICAgICJhcHByb3ZlZCIsCiAgICAgICAgICAgICAgImRlbGl2ZXJlZCIKICAgICAgICAgICAgXQogICAgICAgICAgfSwKICAgICAgICAgICJjb21wbGV0ZSI6IHsKICAgICAgICAgICAgInR5cGUiOiAiYm9vbGVhbiIKICAgICAgICAgIH0KICAgICAgICB9LAogICAgICAgICJ4bWwiOiB7CiAgICAgICAgICAibmFtZSI6ICJvcmRlciIKICAgICAgICB9CiAgICAgIH0sCiAgICAgICJDdXN0b21lciI6IHsKICAgICAgICAidHlwZSI6ICJvYmplY3QiLAogICAgICAgICJwcm9wZXJ0aWVzIjogewogICAgICAgICAgImlkIjogewogICAgICAgICAgICAidHlwZSI6ICJpbnRlZ2VyIiwKICAgICAgICAgICAgImZvcm1hdCI6ICJpbnQ2NCIsCiAgICAgICAgICAgICJleGFtcGxlIjogMTAwMDAwCiAgICAgICAgICB9LAogICAgICAgICAgInVzZXJuYW1lIjogewogICAgICAgICAgICAidHlwZSI6ICJzdHJpbmciLAogICAgICAgICAgICAiZXhhbXBsZSI6ICJmZWhndXkiCiAgICAgICAgICB9LAogICAgICAgICAgImFkZHJlc3MiOiB7CiAgICAgICAgICAgICJ0eXBlIjogImFycmF5IiwKICAgICAgICAgICAgInhtbCI6IHsKICAgICAgICAgICAgICAibmFtZSI6ICJhZGRyZXNzZXMiLAogICAgICAgICAgICAgICJ3cmFwcGVkIjogdHJ1ZQogICAgICAgICAgICB9LAogICAgICAgICAgICAiaXRlbXMiOiB7CiAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvQWRkcmVzcyIKICAgICAgICAgICAgfQogICAgICAgICAgfQogICAgICAgIH0sCiAgICAgICAgInhtbCI6IHsKICAgICAgICAgICJuYW1lIjogImN1c3RvbWVyIgogICAgICAgIH0KICAgICAgfSwKICAgICAgIkFkZHJlc3MiOiB7CiAgICAgICAgInR5cGUiOiAib2JqZWN0IiwKICAgICAgICAicHJvcGVydGllcyI6IHsKICAgICAgICAgICJzdHJlZXQiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICAgICAgICJleGFtcGxlIjogIjQzNyBMeXR0b24iCiAgICAgICAgICB9LAogICAgICAgICAgImNpdHkiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICAgICAgICJleGFtcGxlIjogIlBhbG8gQWx0byIKICAgICAgICAgIH0sCiAgICAgICAgICAic3RhdGUiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICAgICAgICJleGFtcGxlIjogIkNBIgogICAgICAgICAgfSwKICAgICAgICAgICJ6aXAiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICAgICAgICJleGFtcGxlIjogIjk0MzAxIgogICAgICAgICAgfQogICAgICAgIH0sCiAgICAgICAgInhtbCI6IHsKICAgICAgICAgICJuYW1lIjogImFkZHJlc3MiCiAgICAgICAgfQogICAgICB9LAogICAgICAiQ2F0ZWdvcnkiOiB7CiAgICAgICAgInR5cGUiOiAib2JqZWN0IiwKICAgICAgICAicHJvcGVydGllcyI6IHsKICAgICAgICAgICJpZCI6IHsKICAgICAgICAgICAgInR5cGUiOiAiaW50ZWdlciIsCiAgICAgICAgICAgICJmb3JtYXQiOiAiaW50NjQiLAogICAgICAgICAgICAiZXhhbXBsZSI6IDEKICAgICAgICAgIH0sCiAgICAgICAgICAibmFtZSI6IHsKICAgICAgICAgICAgInR5cGUiOiAic3RyaW5nIiwKICAgICAgICAgICAgImV4YW1wbGUiOiAiRG9ncyIKICAgICAgICAgIH0KICAgICAgICB9LAogICAgICAgICJ4bWwiOiB7CiAgICAgICAgICAibmFtZSI6ICJjYXRlZ29yeSIKICAgICAgICB9CiAgICAgIH0sCiAgICAgICJVc2VyIjogewogICAgICAgICJ0eXBlIjogIm9iamVjdCIsCiAgICAgICAgInByb3BlcnRpZXMiOiB7CiAgICAgICAgICAiaWQiOiB7CiAgICAgICAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAgICAgICAiZm9ybWF0IjogImludDY0IiwKICAgICAgICAgICAgImV4YW1wbGUiOiAxMAogICAgICAgICAgfSwKICAgICAgICAgICJ1c2VybmFtZSI6IHsKICAgICAgICAgICAgInR5cGUiOiAic3RyaW5nIiwKICAgICAgICAgICAgImV4YW1wbGUiOiAidGhlVXNlciIKICAgICAgICAgIH0sCiAgICAgICAgICAiZmlyc3ROYW1lIjogewogICAgICAgICAgICAidHlwZSI6ICJzdHJpbmciLAogICAgICAgICAgICAiZXhhbXBsZSI6ICJKb2huIgogICAgICAgICAgfSwKICAgICAgICAgICJsYXN0TmFtZSI6IHsKICAgICAgICAgICAgInR5cGUiOiAic3RyaW5nIiwKICAgICAgICAgICAgImV4YW1wbGUiOiAiSmFtZXMiCiAgICAgICAgICB9LAogICAgICAgICAgImVtYWlsIjogewogICAgICAgICAgICAidHlwZSI6ICJzdHJpbmciLAogICAgICAgICAgICAiZXhhbXBsZSI6ICJqb2huQGVtYWlsLmNvbSIKICAgICAgICAgIH0sCiAgICAgICAgICAicGFzc3dvcmQiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICAgICAgICJleGFtcGxlIjogIjEyMzQ1IgogICAgICAgICAgfSwKICAgICAgICAgICJwaG9uZSI6IHsKICAgICAgICAgICAgInR5cGUiOiAic3RyaW5nIiwKICAgICAgICAgICAgImV4YW1wbGUiOiAiMTIzNDUiCiAgICAgICAgICB9LAogICAgICAgICAgInVzZXJTdGF0dXMiOiB7CiAgICAgICAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAgICAgICAiZGVzY3JpcHRpb24iOiAiVXNlciBTdGF0dXMiLAogICAgICAgICAgICAiZm9ybWF0IjogImludDMyIiwKICAgICAgICAgICAgImV4YW1wbGUiOiAxCiAgICAgICAgICB9CiAgICAgICAgfSwKICAgICAgICAieG1sIjogewogICAgICAgICAgIm5hbWUiOiAidXNlciIKICAgICAgICB9CiAgICAgIH0sCiAgICAgICJUYWciOiB7CiAgICAgICAgInR5cGUiOiAib2JqZWN0IiwKICAgICAgICAicHJvcGVydGllcyI6IHsKICAgICAgICAgICJpZCI6IHsKICAgICAgICAgICAgInR5cGUiOiAiaW50ZWdlciIsCiAgICAgICAgICAgICJmb3JtYXQiOiAiaW50NjQiCiAgICAgICAgICB9LAogICAgICAgICAgIm5hbWUiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIKICAgICAgICAgIH0KICAgICAgICB9LAogICAgICAgICJ4bWwiOiB7CiAgICAgICAgICAibmFtZSI6ICJ0YWciCiAgICAgICAgfQogICAgICB9LAogICAgICAiUGV0IjogewogICAgICAgICJyZXF1aXJlZCI6IFsKICAgICAgICAgICJuYW1lIiwKICAgICAgICAgICJwaG90b1VybHMiCiAgICAgICAgXSwKICAgICAgICAidHlwZSI6ICJvYmplY3QiLAogICAgICAgICJwcm9wZXJ0aWVzIjogewogICAgICAgICAgImlkIjogewogICAgICAgICAgICAidHlwZSI6ICJpbnRlZ2VyIiwKICAgICAgICAgICAgImZvcm1hdCI6ICJpbnQ2NCIsCiAgICAgICAgICAgICJleGFtcGxlIjogMTAKICAgICAgICAgIH0sCiAgICAgICAgICAibmFtZSI6IHsKICAgICAgICAgICAgInR5cGUiOiAic3RyaW5nIiwKICAgICAgICAgICAgImV4YW1wbGUiOiAiZG9nZ2llIgogICAgICAgICAgfSwKICAgICAgICAgICJjYXRlZ29yeSI6IHsKICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvQ2F0ZWdvcnkiCiAgICAgICAgICB9LAogICAgICAgICAgInBob3RvVXJscyI6IHsKICAgICAgICAgICAgInR5cGUiOiAiYXJyYXkiLAogICAgICAgICAgICAieG1sIjogewogICAgICAgICAgICAgICJ3cmFwcGVkIjogdHJ1ZQogICAgICAgICAgICB9LAogICAgICAgICAgICAiaXRlbXMiOiB7CiAgICAgICAgICAgICAgInR5cGUiOiAic3RyaW5nIiwKICAgICAgICAgICAgICAieG1sIjogewogICAgICAgICAgICAgICAgIm5hbWUiOiAicGhvdG9VcmwiCiAgICAgICAgICAgICAgfQogICAgICAgICAgICB9CiAgICAgICAgICB9LAogICAgICAgICAgInRhZ3MiOiB7CiAgICAgICAgICAgICJ0eXBlIjogImFycmF5IiwKICAgICAgICAgICAgInhtbCI6IHsKICAgICAgICAgICAgICAid3JhcHBlZCI6IHRydWUKICAgICAgICAgICAgfSwKICAgICAgICAgICAgIml0ZW1zIjogewogICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1RhZyIKICAgICAgICAgICAgfQogICAgICAgICAgfSwKICAgICAgICAgICJzdGF0dXMiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICAgICAgICJkZXNjcmlwdGlvbiI6ICJwZXQgc3RhdHVzIGluIHRoZSBzdG9yZSIsCiAgICAgICAgICAgICJlbnVtIjogWwogICAgICAgICAgICAgICJhdmFpbGFibGUiLAogICAgICAgICAgICAgICJwZW5kaW5nIiwKICAgICAgICAgICAgICAic29sZCIKICAgICAgICAgICAgXQogICAgICAgICAgfQogICAgICAgIH0sCiAgICAgICAgInhtbCI6IHsKICAgICAgICAgICJuYW1lIjogInBldCIKICAgICAgICB9CiAgICAgIH0sCiAgICAgICJBcGlSZXNwb25zZSI6IHsKICAgICAgICAidHlwZSI6ICJvYmplY3QiLAogICAgICAgICJwcm9wZXJ0aWVzIjogewogICAgICAgICAgImNvZGUiOiB7CiAgICAgICAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAgICAgICAiZm9ybWF0IjogImludDMyIgogICAgICAgICAgfSwKICAgICAgICAgICJ0eXBlIjogewogICAgICAgICAgICAidHlwZSI6ICJzdHJpbmciCiAgICAgICAgICB9LAogICAgICAgICAgIm1lc3NhZ2UiOiB7CiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIKICAgICAgICAgIH0KICAgICAgICB9LAogICAgICAgICJ4bWwiOiB7CiAgICAgICAgICAibmFtZSI6ICIjI2RlZmF1bHQiCiAgICAgICAgfQogICAgICB9CiAgICB9LAogICAgInJlcXVlc3RCb2RpZXMiOiB7CiAgICAgICJQZXQiOiB7CiAgICAgICAgImRlc2NyaXB0aW9uIjogIlBldCBvYmplY3QgdGhhdCBuZWVkcyB0byBiZSBhZGRlZCB0byB0aGUgc3RvcmUiLAogICAgICAgICJjb250ZW50IjogewogICAgICAgICAgImFwcGxpY2F0aW9uL2pzb24iOiB7CiAgICAgICAgICAgICJzY2hlbWEiOiB7CiAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0IgogICAgICAgICAgICB9CiAgICAgICAgICB9LAogICAgICAgICAgImFwcGxpY2F0aW9uL3htbCI6IHsKICAgICAgICAgICAgInNjaGVtYSI6IHsKICAgICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiCiAgICAgICAgICAgIH0KICAgICAgICAgIH0KICAgICAgICB9CiAgICAgIH0sCiAgICAgICJVc2VyQXJyYXkiOiB7CiAgICAgICAgImRlc2NyaXB0aW9uIjogIkxpc3Qgb2YgdXNlciBvYmplY3QiLAogICAgICAgICJjb250ZW50IjogewogICAgICAgICAgImFwcGxpY2F0aW9uL2pzb24iOiB7CiAgICAgICAgICAgICJzY2hlbWEiOiB7CiAgICAgICAgICAgICAgInR5cGUiOiAiYXJyYXkiLAogICAgICAgICAgICAgICJpdGVtcyI6IHsKICAgICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1VzZXIiCiAgICAgICAgICAgICAgfQogICAgICAgICAgICB9CiAgICAgICAgICB9CiAgICAgICAgfQogICAgICB9CiAgICB9LAogICAgInNlY3VyaXR5U2NoZW1lcyI6IHsKICAgICAgInBldHN0b3JlX2F1dGgiOiB7CiAgICAgICAgInR5cGUiOiAib2F1dGgyIiwKICAgICAgICAiZmxvd3MiOiB7CiAgICAgICAgICAiaW1wbGljaXQiOiB7CiAgICAgICAgICAgICJhdXRob3JpemF0aW9uVXJsIjogImh0dHBzOi8vcGV0c3RvcmUzLnN3YWdnZXIuaW8vb2F1dGgvYXV0aG9yaXplIiwKICAgICAgICAgICAgInNjb3BlcyI6IHsKICAgICAgICAgICAgICAid3JpdGU6cGV0cyI6ICJtb2RpZnkgcGV0cyBpbiB5b3VyIGFjY291bnQiLAogICAgICAgICAgICAgICJyZWFkOnBldHMiOiAicmVhZCB5b3VyIHBldHMiCiAgICAgICAgICAgIH0KICAgICAgICAgIH0KICAgICAgICB9CiAgICAgIH0sCiAgICAgICJhcGlfa2V5IjogewogICAgICAgICJ0eXBlIjogImFwaUtleSIsCiAgICAgICAgIm5hbWUiOiAiYXBpX2tleSIsCiAgICAgICAgImluIjogImhlYWRlciIKICAgICAgfQogICAgfQogIH0KfQ==";
        yamlFile = Base64.getDecoder().decode(ymlBase64.getBytes(StandardCharsets.UTF_8));
        jsonFile = Base64.getDecoder().decode(jsonBase64.getBytes(StandardCharsets.UTF_8));
        title = "Swagger Petstore - OpenAPI 3.22";
        version = "1";
    }

    @Test
    void transformFromYAML() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", yamlFile);
        given(openAPIService.transform(multipartFile)).willReturn(getTransformResult());
        given(transformResultDtoResourceAssembler.toModel(getTransformResult())).willReturn(getTransformResultDto());

        mvc.perform(
                multipart("/openapi/transform")
                        .file(multipartFile)
                        .header("Content-Type", "multipart/form-data")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.version").isString())
                .andExpect(jsonPath("$.connections").isArray())
                .andExpect(jsonPath("$.components").isArray())
                .andExpect(jsonPath("$.connections[0]").isMap())
                .andExpect(jsonPath("$.components[0]").isMap());
    }

    @Test
    void transformFromJSON() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", jsonFile);

        given(openAPIService.transform(multipartFile)).willReturn(getTransformResult());
        given(transformResultDtoResourceAssembler.toModel(getTransformResult())).willReturn(getTransformResultDto());

        mvc.perform(
                multipart("/openapi/transform")
                        .file(multipartFile)
                        .header("Content-Type", "multipart/form-data")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.version").isString())
                .andExpect(jsonPath("$.connections").isArray())
                .andExpect(jsonPath("$.components").isArray())
                .andExpect(jsonPath("$.connections[0]").isMap())
                .andExpect(jsonPath("$.components[0]").isMap());
    }

    @Test
    void transformFromBody() throws Exception {
        String jsonContent = new String(jsonFile);

        given(openAPIService.transform(jsonContent)).willReturn(getTransformResult());
        given(transformResultDtoResourceAssembler.toModel(getTransformResult())).willReturn(getTransformResultDto());

        mvc.perform(
                post("/openapi/transform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
        )
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.version").isString())
                .andExpect(jsonPath("$.connections").isArray())
                .andExpect(jsonPath("$.components").isArray())
                .andExpect(jsonPath("$.connections[0]").isMap())
                .andExpect(jsonPath("$.components[0]").isMap());
    }

    private TransformResultDto getTransformResultDto() {
        ConnectionParameterDto host = new ConnectionParameterDto("host", "petstore3.swagger.io");
        ConnectionParameterDto baseUrl = new ConnectionParameterDto("baseUrl", "/api/v3");
        ConnectionParameterDto tls = new ConnectionParameterDto("tls", "Y");
        ConnectionDto connectionDto = new ConnectionDto(
                "Swagger Petstore - OpenAPI 3.22",
                "http", "small description",
                Stream.of(
                        new ConnectionEnvironmentDto(
                                "env0",
                                Stream.of(host, baseUrl, tls).collect(Collectors.toSet())
                        )
                ).collect(Collectors.toSet())
        );

        ComponentParameterDto endpoint = new ComponentParameterDto("endpoint", "/pet");
        ComponentParameterDto type = new ComponentParameterDto("type", "PUT");
        ComponentParameterDto connectionParam = new ComponentParameterDto("connection", "Swagger Petstore - OpenAPI 3.22");
        ComponentVersionDto componentVersionDto = new ComponentVersionDto(1L, "Update an existing pet by Id");
        ComponentDto componentDto = new ComponentDto(
                "http.request",
                "updatePet",
                "Update an existing pet by Id",
                componentVersionDto,
                Stream.of(endpoint, type, connectionParam)
                        .collect(Collectors.toSet()),
                new HashSet<>()
        );

        return new TransformResultDto(
                Collections.singletonList(connectionDto),
                Collections.singletonList(componentDto),
                title,
                version
        );
    }

    private TransformResult getTransformResult() {
        EnvironmentKey environmentKey = new EnvironmentKey("0");
        ConnectionKey connectionKey = new ConnectionKey(
                IdentifierTools.getComponentIdentifier("Swagger Petstore - OpenAPI 3.22"),
                environmentKey);
        ComponentKey componentKey = new ComponentKey(
                IdentifierTools.getComponentIdentifier("updatePet"),
                1L);
        ComponentVersionKey componentVersionKey = new ComponentVersionKey(componentKey);
        ConnectionParameter host = new ConnectionParameter(
                new ConnectionParameterKey(connectionKey, "host"),
                "petstore3.swagger.io");
        ConnectionParameter baseUrl = new ConnectionParameter(
                new ConnectionParameterKey(connectionKey, "baseUrl"),
                "/api/v3");
        ConnectionParameter tls = new ConnectionParameter(
                new ConnectionParameterKey(connectionKey, "tls"),
                "Y");
        Connection connection = new Connection(connectionKey, "http", "small description", Arrays.asList(baseUrl, host, tls));


        ComponentParameter endpoint = new ComponentParameter(
                new ComponentParameterKey(componentKey, "endpoint"),
                "endpoint"
        );
        ComponentParameter type = new ComponentParameter(
                new ComponentParameterKey(componentKey, "type"),
                "PUT"
        );
        ComponentParameter connectionParam = new ComponentParameter(
                new ComponentParameterKey(componentKey, "connection"),
                "Swagger Petstore - OpenAPI 3.22"
        );
        Component component = new Component(
                componentKey,
                "http.request",
                "updatePet",
                "Update an existing pet by Id",
                new ComponentVersion(componentVersionKey, "Update an existing pet by Id"),
                Arrays.asList(endpoint, type, connectionParam),
                new ArrayList<>()
        );

        return new TransformResult(
                Collections.singletonList(connection),
                Collections.singletonList(component),
                title,
                version
        );
    }
}
